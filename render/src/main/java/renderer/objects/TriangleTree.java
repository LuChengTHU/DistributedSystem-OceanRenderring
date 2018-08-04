package renderer.objects;

import renderer.utils.Vec3d;

import static renderer.utils.Vec3d.EPS;

public class TriangleTree {

    TriangleNode root;

    public TriangleNode getRoot() {
        return root;
    }

    public TriangleTree() {
        root = new TriangleNode();
    }

    public void sortTriangle(Triangle[] tris, int l, int r, int coord, boolean minCoord) {
        if (l >= r) return;
        int i = l, j = r;
        Triangle key = tris[(l + r) >> 1];
        while (i <= j) {
            if (minCoord) {
                while (j >= l && (key.getMinCoord(coord) < tris[j].getMinCoord(coord))) j--;
                while (i <= r && (tris[i].getMinCoord(coord) < key.getMinCoord(coord))) i++;
                if (i <= j) {
                    Triangle tmp = tris[i];
                    tris[i] = tris[j];
                    tris[j] = tmp;
                    i++;
                    j--;
                }
            } else {
                while (j >= l && (key.getMaxCoord(coord) < tris[j].getMaxCoord(coord))) j--;
                while (i <= r && (tris[i].getMaxCoord(coord) < key.getMaxCoord(coord))) i++;
                if (i <= j) {
                    Triangle tmp = tris[i];
                    tris[i] = tris[j];
                    tris[j] = tmp;
                    i++;
                    j--;
                }
            }
        }
        sortTriangle(tris, i, r, coord, minCoord);
        sortTriangle(tris, l, j, coord, minCoord);
    }

    public void divideNode(TriangleNode node) {
        if (node.size == root.size) {
            System.out.println("Building subKDtree, size = " + node.size);
        }
        Triangle[] minNode = new Triangle[node.size];
        Triangle[] maxNode = new Triangle[node.size];
        for (int i = 0; i < node.size; i++) {
            minNode[i] = node.tris[i];
            maxNode[i] = node.tris[i];
        }

        double thisCost = node.box.calnArea() * (node.size - 1);
        double minCost = thisCost;
        int bestCoord = -1, leftSize = 0, rightSize = 0;
        double bestSplit = 0.0;
        for (int coord = 0; coord < 3; coord++) {
            sortTriangle(minNode, 0, node.size - 1, coord, true);
            sortTriangle(maxNode, 0, node.size - 1, coord, false);
            TriangleBox leftBox = new TriangleBox(node.box);
            TriangleBox rightBox = new TriangleBox(node.box);

            int j = 0;
            for (int i = 0; i < node.size; i++) {
                double split = minNode[i].getMinCoord(coord);
                leftBox.maxPos.setCoord(coord, split);
                rightBox.minPos.setCoord(coord, split);
                for (; j < node.size && maxNode[j].getMaxCoord(coord) <= split + EPS; j++);
                double cost = leftBox.calnArea() * i + rightBox.calnArea() * (node.size - j);
                if (cost < minCost) {
                    minCost = cost;
                    bestCoord = coord;
                    bestSplit = split;
                    leftSize = i;
                    rightSize = node.size - j;
                }
            }

            j = 0;
            for (int i = 0; i < node.size; i++) {
                double split = maxNode[i].getMaxCoord(coord);
                leftBox.maxPos.setCoord(coord, split);
                rightBox.minPos.setCoord(coord, split);
                for (; j < node.size && minNode[j].getMinCoord(coord) <= split - EPS; j++);
                double cost = leftBox.calnArea() * j + rightBox.calnArea() * (node.size - i);
                if (cost < minCost) {
                    minCost = cost;
                    bestCoord = coord;
                    bestSplit = split;
                    leftSize = j;
                    rightSize = node.size - i;
                }

            }
        }

        if (bestCoord != -1) {
            leftSize = rightSize = 0;
            for (int i = 0; i < node.size; i++) {
                if (node.tris[i].getMinCoord(bestCoord) <= bestSplit - EPS || node.tris[i].getMaxCoord(bestCoord) <= bestSplit + EPS)
                    leftSize++;
                if (node.tris[i].getMaxCoord(bestCoord) >= bestSplit + EPS || node.tris[i].getMinCoord(bestCoord) >= bestSplit - EPS)
                    rightSize++;
            }
            TriangleBox leftBox = new TriangleBox(node.box);
            TriangleBox rightBox = new TriangleBox(node.box);
            leftBox.maxPos.setCoord(bestCoord, bestSplit);
            rightBox.minPos.setCoord(bestCoord, bestSplit);
            double cost = leftBox.calnArea() * leftSize + rightBox.calnArea() * rightSize;

            if (cost < thisCost) {
                node.plane = bestCoord;
                node.split = bestSplit;

                node.leftNode = new TriangleNode();
                node.leftNode.box = new TriangleBox(node.box);
                node.leftNode.box.maxPos.setCoord(node.plane, node.split);

                node.rightNode = new TriangleNode();
                node.rightNode.box = new TriangleBox(node.box);
                node.rightNode.box.minPos.setCoord(node.plane, node.split);

                node.leftNode.tris = new Triangle[leftSize];
                node.rightNode.tris = new Triangle[rightSize];
                int leftCnt = 0, rightCnt = 0;
                for (int i = 0; i < node.size; i++) {
                    if (node.tris[i].getMinCoord(node.plane) <= node.split - EPS || node.tris[i].getMaxCoord(node.plane) <= node.split + EPS)
                        node.leftNode.tris[leftCnt++] = node.tris[i];
                    if (node.tris[i].getMaxCoord(node.plane) >= node.split + EPS || node.tris[i].getMinCoord(node.plane) >= node.split - EPS)
                        node.rightNode.tris[rightCnt++] = node.tris[i];
                }
                node.leftNode.size = leftSize;
                node.rightNode.size = rightSize;

                divideNode(node.leftNode);
                divideNode(node.rightNode);
            }
        }
    }

    public Collider travelTree(TriangleNode node, Vec3d ray_O, Vec3d ray_V) {
        if (!node.box.contain(ray_O) && node.box.collide(ray_O, ray_V) <= -EPS)
            return new Collider();

        if (node.leftNode == null || node.rightNode == null) {
            Collider ret = new Collider();
            for (int i = 0; i < node.size; i++) {
                Collider collider = node.tris[i].collide(ray_O, ray_V);
                if (collider.crash && node.box.contain(collider.C) && (!ret.crash || collider.dist < ret.dist))
                    ret = collider;
            }
            return ret;
        }

        if (node.leftNode.box.contain(ray_O)) {
            Collider collider = travelTree(node.leftNode, ray_O, ray_V);
            if (collider.crash) return collider;
            return travelTree(node.rightNode, ray_O, ray_V);
        }

        if (node.rightNode.box.contain(ray_O)) {
            Collider collider = travelTree(node.rightNode, ray_O, ray_V);
            if (collider.crash) return collider;
            return travelTree(node.leftNode, ray_O, ray_V);
        }

        double leftDist = node.leftNode.box.collide(ray_O, ray_V);
        double rightDist = node.rightNode.box.collide(ray_O, ray_V);
        if (rightDist <= -EPS)
            return travelTree(node.leftNode, ray_O, ray_V);
        if (leftDist <= -EPS)
            return travelTree(node.rightNode, ray_O, ray_V);

        if (leftDist < rightDist) {
            Collider collider = travelTree(node.leftNode, ray_O, ray_V);
            if (collider.crash) return collider;
            return travelTree(node.rightNode, ray_O, ray_V);
        }

        Collider collider = travelTree(node.rightNode, ray_O, ray_V);
        if (collider.crash) return collider;
        return travelTree(node.leftNode, ray_O, ray_V);
    }

    public void buildTree() {
        divideNode(root);
    }

    public Collider collide(Vec3d ray_O, Vec3d ray_V) {
        return travelTree(root, ray_O, ray_V);
    }
}
