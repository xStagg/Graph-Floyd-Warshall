package fr.xStagg.GraphFloydWarshall.Graph;

import fr.xStagg.GraphFloydWarshall.Utils.LoadingMethod;

import java.util.ArrayList;

public class Graph {

    // ATTRIBUTES
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private String path;
    private LoadingMethod loadingMethod;
    private boolean randomPosition;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public Node getNode(int id) {
        for(Node node : nodes) {
            if(node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    public Edge getEdge(int id) {
        for(Edge edge : edges) {
            if(edge.getId() == id) {
                return edge;
            }
        }
        return null;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public Node removeNode(int id) {
        Node node = nodes.get(id);
        nodes.remove(node.getId());
        return node;
    }

    public Edge removeEdge(int id) {
        Edge edge = edges.get(id);
        edges.remove(id);
        return edge;
    }

    public Edge createEdge(Node source, Node target) {
        Edge edge = new Edge(source, target);
        source.addSuccessor(target);
        target.addPredecessor(source);
        edges.add(edge);
        return edge;
    }

    public boolean areConnected(Node source, Node target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getTarget().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LoadingMethod getLoadingMethod() {
        return loadingMethod;
    }

    public void setLoadingMethod(LoadingMethod loadingMethod) {
        this.loadingMethod = loadingMethod;
    }

    public boolean isRandomPosition() {
        return randomPosition;
    }

    public void setRandomPosition(boolean randomPosition) {
        this.randomPosition = randomPosition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            sb.append(node.toString() + "\n");
        }
        for (Edge edge : edges) {
            System.out.println(edge);
            sb.append(edge.toString() + "\n");
        }
        return sb.toString();
    }

}
