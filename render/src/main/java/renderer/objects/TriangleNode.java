package renderer.objects;

public class TriangleNode {

    public Triangle[] tris;
    public int size, plane;
    public double split;
    public TriangleBox box;
    TriangleNode leftNode;
    TriangleNode rightNode;

    public TriangleNode() {
        size = 0;
        plane = -1;
        split = 0;
        leftNode = null;
        rightNode = null;
        box = new TriangleBox();
    }
}
