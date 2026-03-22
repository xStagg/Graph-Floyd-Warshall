package fr.xStagg.GraphFloydWarshall;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Utils.JSONLoader;
import fr.xStagg.GraphFloydWarshall.Utils.LoadingMethod;
import fr.xStagg.GraphFloydWarshall.Visualization.*;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws UnsupportedEncodingException {

        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));

        Visualization v = new Visualization();
        v.visualize();

    }
}
