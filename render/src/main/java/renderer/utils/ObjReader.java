package renderer.utils;

import renderer.utils.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import renderer.objects.Polyhedron;
import renderer.objects.Triangle;
import renderer.objects.TriangleNode;
import renderer.tracer.RayTracerDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static renderer.utils.Vec3d.EPS;

public class ObjReader {

    Polyhedron polyhedron;
    int vSize, vtSize, vnSize, fSize, matSize;
    Vec3d[] v;
    ArrayList<Integer>[] vec2F;
    Pair[] vt;
    Vec3d[] vn;
    Triangle[] tris;
    Material[] mat;
    Map<String, Integer> matMap;

    public ObjReader() {
        polyhedron = null;
        vSize = vtSize = vnSize = fSize = matSize = 0;
        matMap = new HashMap<String, Integer>();
    }

    public void setPolyhedron(Polyhedron _polyhedron) {
        polyhedron = _polyhedron;
    }

    private BufferedReader getReader(String filePath) throws IOException {
        BufferedReader br = null;
        switch (RayTracerDriver.rtEnv) {
            case NATIVE:
                br = new BufferedReader(new FileReader(filePath));
                break;
            case HDFS:
                Configuration conf = new Configuration();
                FileSystem fs = FileSystem.get(conf);
                Path path = new Path(filePath);
                br = new BufferedReader(new InputStreamReader(fs.open(path)));
                break;
        }
        return br;
    }

    public void readMtlSize(String file) throws IOException {
        BufferedReader br = getReader(file);
        String str = null;
        while ((str = br.readLine()) != null) {
            StringTokenizer tk = new StringTokenizer(str);
            if (!tk.hasMoreTokens()) continue;
            String var = tk.nextToken();
            if (var.equals("newmtl")) matSize++;
        }

        mat = new Material[matSize + 1];
        for (int i = 0; i < matSize + 1; i++) {
            mat[i] = new Material();
        }
    }

    public void readObjSize(String file) throws IOException {
        BufferedReader br = getReader(file);
        String str = null;
        while ((str = br.readLine()) != null) {
            StringTokenizer tk = new StringTokenizer(str);
            if (!tk.hasMoreTokens()) continue;
            String var = tk.nextToken();
            if (var.equals("mtllib")) {
                if (!tk.hasMoreTokens()) continue;
                String mtlFile = tk.nextToken();
                readMtlSize(mtlFile);
            }
            if (var.equals("v")) vSize++;
            if (var.equals("vt")) vtSize++;
            if (var.equals("vn")) vnSize++;
            if (var.equals("f")) {
                int vertexCnt = 0;
                String tmp;
                while (tk.hasMoreTokens()) {
                    tmp = tk.nextToken();
                    vertexCnt++;
                }
                fSize += Math.max(0, vertexCnt - 2);
            }
        }

        v = new Vec3d[vSize + 1];
        vec2F = new ArrayList[vSize + 1];
        for (int i = 0; i < vSize + 1; i++) {
            v[i] = new Vec3d();
            vec2F[i] = new ArrayList<Integer>();
        }

        vt = new Pair[vtSize + 1];
        if (vnSize == 0) {
            vn = new Vec3d[vSize + 1];
            for (int i = 0; i < vSize + 1; i++) {
                vn[i] = new Vec3d();
            }
        }
        else {
            vn = new Vec3d[vnSize + 1];
            for (int i = 0; i < vnSize + 1; i++) {
                vn[i] = new Vec3d();
            }
        }
        tris = new Triangle[fSize];
        for (int i = 0; i < fSize; i++) {
            tris[i] = new Triangle();
        }
    }

    public void readMtl(String file) throws IOException {
        BufferedReader br = getReader(file);
        String str = null;
        int matCnt= 0;
        while ((str = br.readLine()) != null) {
            StringTokenizer tk = new StringTokenizer(str);
            if (!tk.hasMoreTokens()) continue;
            String var = tk.nextToken();
            int index = str.indexOf(var) + var.length();
            String value = str.substring(index);
            if (var.equals("newmtl")) {
                if (!tk.hasMoreTokens()) continue;
                String matName = tk.nextToken();
                matMap.put(matName, ++matCnt);
                mat[matCnt] = new Material();
            }
            if (var.equals("Ka")) {

            }
            if (var.equals("Kd")) {
                mat[matCnt].color.input(value);
                mat[matCnt].diff = mat[matCnt].color.RGBMax();
                mat[matCnt].color.divToThis(mat[matCnt].diff);
            }
//            if (var.equals("Ks")) {
//                if (!tk.hasMoreTokens()) continue;
//                mat[matCnt].refl = Double.valueOf(tk.nextToken());
//            }
//            if (var.equals("Tf")) {
//                mat[matCnt].absor.input(value);
//                if (mat[matCnt].absor.power() < 1 - EPS) {
//                    mat[matCnt].refr = 1;
//                    mat[matCnt].diff = mat[matCnt].spec = mat[matCnt].refl = 0;
//                }
//            }
            if (var.equals("Ni")) {
                if (!tk.hasMoreTokens()) continue;
                mat[matCnt].rindex = Double.parseDouble(tk.nextToken());
            }
            if (var.equals("map_Kd")) {
                if (!tk.hasMoreTokens()) continue;
                mat[matCnt].texture = new Bmp();
                mat[matCnt].texture.input(tk.nextToken());
            }
            if (var.equals("map_bump")) {
                if (!tk.hasMoreTokens()) continue;
                mat[matCnt].bump = new Bmp();
                mat[matCnt].bump.input(tk.nextToken());
            }
        }
    }

    public void calnVn() {
        if (vnSize > 0) {
            for (int i = 1; i <= vnSize; i++) {
                vn[i] = vn[i].rotate(new Vec3d(1, 0, 0), polyhedron.getAngles().getCoord(0));
                vn[i] = vn[i].rotate(new Vec3d(0, 1, 0), polyhedron.getAngles().getCoord(1));
                vn[i] = vn[i].rotate(new Vec3d(0, 0, 1), polyhedron.getAngles().getCoord(2));
            }
        }
    }

    public void readObj(String filePath) throws IOException {
        readObjSize(filePath);
        BufferedReader br = getReader(filePath);
        String str = null;

        int matID = -1;
        int vCnt = 0, vtCnt = 0, vnCnt = 0, fCnt = 0;
        while ((str = br.readLine()) != null) {
            StringTokenizer tk = new StringTokenizer(str);
            if (!tk.hasMoreTokens()) continue;
            String var = tk.nextToken();
            int index = str.indexOf(var) + var.length();
            String value = str.substring(index);

            if (var.equals("mtllib")) {
                if (!tk.hasMoreTokens()) continue;
                readMtl(tk.nextToken());
            }
            if (var.equals("usemtl")) {
                if (!tk.hasMoreTokens()) continue;
                matID = matMap.get(tk.nextToken());
            }
            if (var.equals("v")) {
                vCnt++;
                v[vCnt].input(value);
            }
            if (var.equals("vt")) {
                vtCnt++;
                if (!tk.hasMoreTokens()) continue;
                // FIXME: is it wrong?
                Double second = Double.parseDouble(tk.nextToken());
                if (!tk.hasMoreTokens()) continue;
                Double first = Double.parseDouble(tk.nextToken());
                vt[vtCnt] = new Pair(first, second);
            }
            if (var.equals("vn")) {
                vnCnt++;
                vn[vnCnt].input(value);
            }
            if (var.equals("f")) {
                Triangle tri = tris[fCnt] = new Triangle();
                tri.setParent(polyhedron);
                if (matID != -1)
                    tri.setMaterial(mat[matID]);
                else
                    tri.setMaterial(polyhedron.getMaterial());
                String tmp;
                for (int i = 0; tk.hasMoreTokens(); i++) {
                    tmp = tk.nextToken();
                    int bufferLen = 0;
                    int[] buffer = new int[3];
                    buffer[0] = buffer[1] = buffer[2] = -1;
                    for (int s = 0, t = 0; t < tmp.length(); t++) {
                        if (t + 1 >= tmp.length() || tmp.indexOf(t + 1) == '/') {
                            buffer[bufferLen++] = Integer.parseInt(tmp.substring(s, t - s + 1));
                            s = t + 2;
                        }
                    }
                    int vertexID = i;
                    if (i >= 3) {
                        // FIXME: not finished.
                        System.out.println("wrong f!!!");
                        throw new IOException("wrong f!!");
                    }
                    if (buffer[0] > 0) {
                        tri.getVertex(vertexID).set(buffer[0]);
                        Vec3d vertexPos = new Vec3d(v[buffer[0]]);
                        vertexPos = vertexPos.rotate(new Vec3d(1, 0, 0), polyhedron.getAngles().getCoord(0));
                        vertexPos = vertexPos.rotate(new Vec3d(0, 1, 0), polyhedron.getAngles().getCoord(1));
                        vertexPos = vertexPos.rotate(new Vec3d(0, 0, 1), polyhedron.getAngles().getCoord(2));
                        vertexPos = polyhedron.getO().add(vertexPos.mul(polyhedron.getSize()));
                        tri.setPos(vertexID, vertexPos);

                        vec2F[buffer[0]].add(fCnt);
                    }
                    if (buffer[1] > 0) {
                        tri.getTextureVertex(vertexID).set(buffer[1]);
                    }
                    if (buffer[2] > 0) {
                        tri.getNormalVecctorID(vertexID).set(buffer[2]);
                    }
                    if (i >= 2) {
                        tri.preTreatment();
                        fCnt++;
                    }
                }
            }
        }

        for (int i = 1; i <= vCnt; i++) {
            Vec3d thisVn = new Vec3d(0, 0, 0);
            for (Integer f : vec2F[i]) {
                int x = -1, y = -1;
                for (int j = 0; j < 3; j++) {
                    if (tris[f].getVertex(j).get() == i) {
                        x = tris[f].getVertex ((j + 1) % 3).get();
                        y = tris[f].getVertex ((j + 2) % 3).get();
                        break;
                    }
                }
                Vec3d ix = v[x].sub(v[i]), iy = v[y].sub(v[i]);
                double angle = Math.abs(ix.dot(iy) / ix.module() / iy.module());
                thisVn.addToThis(tris[f].getN().mul(angle));
            }
            thisVn = thisVn.normalize();
            if (thisVn.isZeroVector()) thisVn = new Vec3d(0, 0, 1);
            vnCnt++;
            vn[i] = thisVn;
        }

        for (int i = 0; i < fCnt; i++) {
            for (int j = 0; j < 3; j++) {
                tris[i].getNormalVecctorID(j).set(tris[i].getVertex(j).get());
            }
        }

        calnVn();

        TriangleNode root = polyhedron.getTree().getRoot();
        root.size = fCnt;
        root.tris = new Triangle[root.size];
        for (int i = 0; i < root.size; i++) {
            root.tris[i] = tris[i];
            root.box.update(tris[i]);
        }
        polyhedron.getTree().buildTree();
        polyhedron.setVertexN(vn);
        polyhedron.setPixel(vt);
    }
}
