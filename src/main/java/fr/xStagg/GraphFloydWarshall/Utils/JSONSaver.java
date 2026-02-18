package fr.xStagg.GraphFloydWarshall.Utils;

import com.google.gson.*;
import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Graph.Node;

import java.io.*;

import static fr.xStagg.GraphFloydWarshall.Utils.JSONLoader.readFileFromFolders;
import static fr.xStagg.GraphFloydWarshall.Utils.JSONLoader.readFileFromResources;

public class JSONSaver {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveGraph(Graph graph) {
        // Lire le fichier JSON
        String jsonContent = "";

        try {
            if(graph.getLoadingMethod() == LoadingMethod.RESOURCES) jsonContent = readFileFromResources(graph.getPath());
            if(graph.getLoadingMethod() == LoadingMethod.FOLDERS) jsonContent = readFileFromFolders(graph.getPath());

            // Parser le JSON en JsonObject
            JsonObject jsonObject = gson.fromJson(jsonContent, JsonObject.class);

            // Modifier des champs simples
            if (jsonObject.has("randomPosition")) {
                jsonObject.addProperty("randomPosition", false);
            }

            JsonArray nodesArray = jsonObject.getAsJsonArray("nodes");
            for(JsonElement nodeJson : nodesArray) {
                JsonObject nodeObject = nodeJson.getAsJsonObject();
                Node node = graph.getNode(nodeObject.get("id").getAsInt());
                nodeObject.addProperty("graphicsX", node.getGraphicsX());
                nodeObject.addProperty("graphicsY", node.getGraphicsY());
            }

            // Sauvegarder le JSON modifié
            try (Writer writer = new FileWriter(graph.getPath())) {
                gson.toJson(jsonObject, writer);
            }

            System.out.println("JSON modifié avec succès!");

            // Afficher le résultat
            System.out.println("JSON modifié:\n" +
                    gson.toJson(jsonObject));
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            throw new RuntimeException(e);
        }

    }
}

