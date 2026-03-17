package fr.xStagg.GraphFloydWarshall;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Utils.JSONLoader;
import fr.xStagg.GraphFloydWarshall.Utils.LoadingMethod;
import fr.xStagg.GraphFloydWarshall.Visualization.*;


public class Main {
    public static void main(String[] args) {

        Graph g = JSONLoader.loadGraph("G1.json", LoadingMethod.FOLDERS);
        System.out.println(g);

        Visualization v = new Visualization(g);
        v.visualize();
    }
}
