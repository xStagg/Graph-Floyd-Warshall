package fr.xStagg.GraphFloydWarshall.Graph;

public class Edge {
    static int ID;
    // ATTRIBUTES
    private int id;
    private Node source;
    private Node target;

    public Edge(Node source, Node target) {
        this.id = ID++;
        this.source = source;
        this.target = target;
    }

    public int getId() {
        return id;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }
}
