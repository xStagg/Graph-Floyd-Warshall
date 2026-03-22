package fr.xStagg.GraphFloydWarshall.Utils;

import com.google.gson.*;
import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Graph.Node;

import java.io.*;

import static fr.xStagg.GraphFloydWarshall.Utils.JSONLoader.readFileFromFolders;
import static fr.xStagg.GraphFloydWarshall.Utils.JSONLoader.readFileFromResources;

/**
 * Utilitaire pour sauvegarder l'état d'un graphe dans le fichier JSON
 * à partir duquel il a été chargé.
 * <p>
 * Les coordonnées graphiques ({@code graphicsX}, {@code graphicsY}) de chaque nœud
 * sont mises à jour, et le champ {@code randomPosition} est passé à {@code false}.
 * </p>
 */
public class JSONSaver {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Sauvegarde le graphe donné dans son fichier JSON d'origine.
     * <p>
     * Le fichier JSON est relu, les positions des nœuds sont mises à jour,
     * puis le fichier est réécrit à la même adresse.
     * </p>
     *
     * @param graph graphe à sauvegarder ; doit avoir un chemin ({@link Graph#getPath()})
     *              et une méthode de chargement ({@link Graph#getLoadingMethod()}) valides
     * @throws RuntimeException encapsulant une {@link IOException} ou une erreur de parsing JSON
     */
    public static void saveGraph(Graph graph) {
        String jsonContent = "";

        try {
            if (graph.getLoadingMethod() == LoadingMethod.RESOURCES)
                jsonContent = readFileFromResources(graph.getPath());
            if (graph.getLoadingMethod() == LoadingMethod.FOLDERS)
                jsonContent = readFileFromFolders(graph.getPath());

            JsonObject jsonObject = gson.fromJson(jsonContent, JsonObject.class);

            if (jsonObject.has("randomPosition")) {
                jsonObject.addProperty("randomPosition", false);
            }

            JsonArray nodesArray = jsonObject.getAsJsonArray("nodes");
            for (JsonElement nodeJson : nodesArray) {
                JsonObject nodeObject = nodeJson.getAsJsonObject();
                Node node = graph.getNode(nodeObject.get("id").getAsInt());
                nodeObject.addProperty("graphicsX", node.getGraphicsX());
                nodeObject.addProperty("graphicsY", node.getGraphicsY());
            }

            try (Writer writer = new FileWriter(graph.getPath())) {
                gson.toJson(jsonObject, writer);
            }

            System.out.println("JSON modifié avec succès!");
            System.out.println("JSON modifié:\n" + gson.toJson(jsonObject));

        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            throw new RuntimeException(e);
        }
    }
}