package fr.xStagg.GraphFloydWarshall;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Graph.Node;
import fr.xStagg.GraphFloydWarshall.Visualization.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Node n1 = new Node(1);
        Node n2 = new Node(2);

        Graph g = new Graph();
        g.addNode(n1);
        g.addNode(n2);
        g.createEdge(n1, n2);

        for(Node n : g.getNodes()) {
            System.out.println(n);
        }

        Visualization v = new Visualization(g);
        v.visualize();
    }
}
