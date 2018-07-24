package renderer.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import static renderer.utils.Vec3d.EPS;

public class Bmp {

    public BufferedImage ima;

    public Bmp(int H, int W) {
        ima = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
    }

    public Bmp() {}

    public void set(BufferedImage _ima) {
        ima = _ima;
    }

    public int getH() {
        return ima.getHeight();
    }

    public int getW() {
        return ima.getWidth();
    }

    public Color getColor(int x, int y) {
        return new Color(ima.getRGB(x, getH() - 1 - y));
    }

    public void setColor(int x, int y, Color c) {
        ima.setRGB(x, getH() - 1 - y, c.getRGB());
    }

    public void input(String value) throws IOException {
        StringTokenizer tk = new StringTokenizer(value);
        if (tk.hasMoreTokens()) {
            value = tk.nextToken();
        }
        ima = ImageIO.read(new FileInputStream(value));
    }

    public void output(String filename) {
        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(1f);

        try {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            // specifies where the jpg image has to be written
            FileImageOutputStream fios = new FileImageOutputStream(new File(filename));
            writer.setOutput(fios);

            // writes the file with given compression level
            // from your JPEGImageWriteParam instance
            writer.write(null, new IIOImage(ima, null, null), jpegParams);
            writer.reset();
            fios.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Color getSmoothColor(double u, double v) {
        double U = (u + EPS - Math.floor(u + EPS)) * ima.getHeight();
        double V = (v + EPS - Math.floor(v + EPS)) * ima.getWidth();
        int U1 = (int) Math.floor(U + EPS), U2 = U1 + 1;
        int V1 = (int) Math.floor(V + EPS), V2 = V1 + 1;
        double rat_U = U2 - U;
        double rat_V = V2 - V;
        if (U1 < 0)
            U1 = ima.getHeight() - 1;
        if (U2 == ima.getHeight())
            U2 = 0;
        if (V1 < 0)
            V1 = ima.getWidth() - 1;
        if (V2 == ima.getWidth())
            V2 = 0;
        Color ret = new Color();
        Color tmp = new Color(ima.getRGB(V1, getH() - 1 - U1));
        ret.addToThis(tmp.mulToThis(rat_U * rat_V));
        tmp = new Color(ima.getRGB(V2, getH() - 1 - U1));
        ret.addToThis(tmp.mulToThis(rat_U * (1 - rat_V)));
        tmp = new Color(ima.getRGB(V1, getH() - 1 - U2));
        ret.addToThis(tmp.mulToThis((1 - rat_U) * rat_V));
        tmp = new Color(ima.getRGB(V2, getH() - 1 - U2));
        ret.addToThis(tmp.mulToThis((1 - rat_U) * (1 - rat_V)));
        return ret;
    }

    public Color getUVColor(double u, double v) {
        int U = (int)(u * ima.getHeight());
        int V = (int)(v * ima.getWidth());
        return new Color(ima.getRGB(V, getH() - 1 - U));
    }
}
