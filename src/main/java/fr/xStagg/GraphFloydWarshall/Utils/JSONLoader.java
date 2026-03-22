package fr.xStagg.GraphFloydWarshall.Utils;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import java.io.*;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;
import fr.xStagg.GraphFloydWarshall.Graph.Node;

/**
 * Utilitaire pour charger un graphe depuis un fichier JSON, que ce soit
 * depuis le système de fichiers ou depuis les ressources du classpath.
 */
public class JSONLoader {

    /**
     * Charge un graphe depuis un fichier JSON situé au chemin donné,
     * en utilisant la méthode de chargement spécifiée.
     * <p>
     * Le fichier JSON doit contenir les champs {@code nodes}, {@code edges}
     * et {@code randomPosition}. Chaque arête peut optionnellement avoir
     * un champ {@code weight} (1 par défaut).
     * </p>
     *
     * @param path          chemin du fichier JSON (relatif ou absolu selon {@code loadingMethod})
     * @param loadingMethod méthode de chargement ({@link LoadingMethod#RESOURCES} ou {@link LoadingMethod#FOLDERS})
     * @return graphe chargé, ou {@code null} en cas d'erreur d'entrée/sortie
     */
    public static Graph loadGraph(String path, LoadingMethod loadingMethod) {
        try {
            String jsonContent = "";
            if (loadingMethod == LoadingMethod.RESOURCES) jsonContent = readFileFromResources(path);
            if (loadingMethod == LoadingMethod.FOLDERS) jsonContent = readFileFromFolders(path);
            JsonObject jsonGraph = JsonParser.parseString(jsonContent).getAsJsonObject();

            Graph loadedGraph = new Graph();
            loadedGraph.setPath(path);
            loadedGraph.setLoadingMethod(loadingMethod);
            loadedGraph.setRandomPosition(jsonGraph.get("randomPosition").getAsBoolean());

            for (JsonElement jsonNode : jsonGraph.get("nodes").getAsJsonArray().asList()) {
                int id = jsonNode.getAsJsonObject().get("id").getAsInt();
                Node newNode = new Node(id);
                newNode.setGraphicsX(jsonNode.getAsJsonObject().get("graphicsX").getAsInt());
                newNode.setGraphicsY(jsonNode.getAsJsonObject().get("graphicsY").getAsInt());
                loadedGraph.addNode(newNode);
            }
            for (JsonElement jsonEdge : jsonGraph.get("edges").getAsJsonArray().asList()) {
                int source = jsonEdge.getAsJsonObject().get("from").getAsInt();
                int target = jsonEdge.getAsJsonObject().get("to").getAsInt();
                int weight;
                if (jsonEdge.getAsJsonObject().has("weight")) {
                    weight = jsonEdge.getAsJsonObject().get("weight").getAsInt();
                } else {
                    weight = 1;
                }
                Node sourceNode = loadedGraph.getNode(source);
                Node targetNode = loadedGraph.getNode(target);
                if (sourceNode != null && targetNode != null) {
                    loadedGraph.createEdge(sourceNode, targetNode, weight);
                } else {
                    System.out.println("Node " + sourceNode + " and Node " + targetNode + " are null");
                }
            }

            return loadedGraph;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lit le contenu d'un fichier texte depuis le système de fichiers.
     *
     * @param path chemin du fichier sur le disque
     * @return contenu du fichier sous forme de chaîne
     * @throws IOException en cas d'erreur de lecture ou si le fichier est introuvable
     */
    protected static String readFileFromFolders(String path) throws IOException {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }

    /**
     * Lit le contenu d'un fichier texte situé dans le classpath (resources).
     *
     * @param path chemin du fichier dans les ressources (avec ou sans slash initial)
     * @return contenu du fichier sous forme de chaîne
     * @throws IOException si le fichier n'est pas trouvé ou en cas d'erreur de lecture
     */
    protected static String readFileFromResources(String path) throws IOException {
        String resourcePath = path;
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        try (InputStream inputStream = JSONLoader.class.getClassLoader()
                .getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            if (inputStream == null) {
                throw new FileNotFoundException("Fichier non trouvé: " + resourcePath);
            }

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }
    }
}