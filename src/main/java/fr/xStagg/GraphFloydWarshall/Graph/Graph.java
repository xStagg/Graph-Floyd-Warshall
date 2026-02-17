package fr.xStagg.GraphFloydWarshall.Graph;

import fr.xStagg.GraphFloydWarshall.Graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Graph {

    // ATTRIBUTES
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

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
        return nodes.get(id);
    }

    public Edge getEdge(int id) {
        return edges.get(id);
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

}
