package fr.xStagg.GraphFloydWarshall.Graph;

import java.util.ArrayList;

public class Node {

    // ATTRIBUTES

    private int id;
    private ArrayList<Node> predecessors;
    private ArrayList<Node> successors;
    private int graphicsX;
    private int graphicsY;

    public Node(int id) {
        this.id = id;
        predecessors = new ArrayList<Node>();
        successors = new ArrayList<Node>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Node> getPredecessors() {
        return predecessors;
    }

    public ArrayList<Node> getSuccessors() {
        return successors;
    }

    public void addPredecessor(Node n) {
        if (!predecessors.contains(n)) {
            if (!n.getSuccessors().contains(this)) {
                n.getSuccessors().add(this);
            }
            predecessors.add(n);
        }
    }

    public void addSuccessor(Node n) {
        if (!successors.contains(n)) {
            if (!n.getPredecessors().contains(this)) {
                n.getPredecessors().add(this);
            }
            successors.add(n);
        }
    }

    public void removePredecessor(Node n) {
        if (predecessors.contains(n)) {
            n.getSuccessors().remove(this);
            predecessors.remove(n);
        }
    }

    public void removeSuccessor(Node n) {
        if (successors.contains(n)) {
            n.getPredecessors().remove(this);
            successors.remove(n);
        }
    }

    public Node removePredecessor(int id) {
        Node n = predecessors.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        if (n != null) {
            removePredecessor(n);
        }
        return n;
    }

    public Node removeSuccessor(int id) {
        Node n = successors.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        if (n != null) {
            removeSuccessor(n);
        }
        return n;
    }

    public int getGraphicsX() {
        return graphicsX;
    }

    public int getGraphicsY() {
        return graphicsY;
    }

    public void setGraphicsX(int x) {
        this.graphicsX = x;
    }

    public void setGraphicsY(int y) {
        this.graphicsY = y;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < predecessors.size()-1; i++) {
            sb.append(predecessors.get(i).getId());
            sb.append("-");
        }
        if(!predecessors.isEmpty()) {
            sb.append(predecessors.getLast().getId());
        }
        sb.append("]");
        sb.append(" -> ");
        sb.append(id);
        sb.append(" -> ");
        sb.append("[");
        for(int i = 0; i < successors.size()-1; i++) {
            sb.append(successors.get(i).getId());
            sb.append("-");
        }
        if(!successors.isEmpty()) {
            sb.append(successors.getLast().getId());
        };
        sb.append("]");
        return sb.toString();
    }

}
