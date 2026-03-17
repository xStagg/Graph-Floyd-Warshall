package fr.xStagg.GraphFloydWarshall.Graph;

import fr.xStagg.GraphFloydWarshall.Utils.LoadingMethod;

import java.util.ArrayList;

/**
 * Représente un graphe orienté composé de nœuds et d'arêtes, avec des
 * structures associées comme la matrice d'adjacence et les degrés.
 */
public class Graph {

    // ATTRIBUTES
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private String path;
    private LoadingMethod loadingMethod;
    private boolean randomPosition;
    private int[][] adjacencyMatrix;
    private int[] inDegrees;
    private int[] outDegrees;

    /**
     * Crée un graphe vide sans nœuds ni arêtes.
     */
    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    /**
     * Ajoute un nœud au graphe.
     *
     * @param node nœud à ajouter
     */
    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Ajoute une arête au graphe.
     *
     * @param edge arête à ajouter
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    /**
     * Recherche un nœud par identifiant.
     *
     * @param id identifiant du nœud recherché
     * @return nœud correspondant ou {@code null} s'il n'existe pas
     */
    public Node getNode(int id) {
        for (Node node : nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    /**
     * Recherche une arête par identifiant.
     *
     * @param id identifiant de l'arête recherchée
     * @return arête correspondante ou {@code null} si elle n'existe pas
     */
    public Edge getEdge(int id) {
        for (Edge edge : edges) {
            if (edge.getId() == id) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Retourne la liste des nœuds du graphe.
     *
     * @return liste des nœuds
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * Retourne la liste des arêtes du graphe.
     *
     * @return liste des arêtes
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    /**
     * Supprime un nœud par son identifiant et enlève toutes les arêtes
     * qui y sont incidentes.
     *
     * @param id identifiant du nœud à supprimer
     * @return nœud supprimé
     */
    public Node removeNode(int id) {
        Node node = nodes.get(id);
        nodes.remove(node.getId());
        edges.removeIf(edge -> edge.getSource().getId() == id || edge.getTarget().getId() == id);
        return node;
    }

    /**
     * Supprime une arête par son identifiant.
     *
     * @param id identifiant de l'arête à supprimer
     * @return arête supprimée
     */
    public Edge removeEdge(int id) {
        Edge edge = edges.get(id);
        edges.remove(id);
        return edge;
    }

    /**
     * Crée et ajoute au graphe une nouvelle arête pondérée entre deux nœuds,
     * en mettant à jour leurs successeurs et prédécesseurs.
     *
     * @param source nœud source
     * @param target nœud cible
     * @param weight poids de l'arête
     * @return arête créée
     */
    public Edge createEdge(Node source, Node target, double weight) {
        Edge edge = new Edge(source, target, weight);
        source.addSuccessor(target);
        target.addPredecessor(source);
        edges.add(edge);
        return edge;
    }

    /**
     * Indique si deux nœuds sont directement connectés par une arête
     * orientée de source vers cible.
     *
     * @param source nœud source
     * @param target nœud cible
     * @return {@code true} si une arête existe de source vers cible, sinon {@code false}
     */
    public boolean areConnected(Node source, Node target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getTarget().equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne le chemin du fichier JSON associé à ce graphe.
     *
     * @return chemin du fichier
     */
    public String getPath() {
        return path;
    }

    /**
     * Définit le chemin du fichier JSON associé à ce graphe.
     *
     * @param path chemin du fichier
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Retourne la méthode de chargement utilisée pour ce graphe.
     *
     * @return méthode de chargement
     */
    public LoadingMethod getLoadingMethod() {
        return loadingMethod;
    }

    /**
     * Définit la méthode de chargement utilisée pour ce graphe.
     *
     * @param loadingMethod méthode de chargement
     */
    public void setLoadingMethod(LoadingMethod loadingMethod) {
        this.loadingMethod = loadingMethod;
    }

    /**
     * Indique si la position des nœuds doit être randomisée à l'affichage.
     *
     * @return {@code true} si la position doit être aléatoire, sinon {@code false}
     */
    public boolean isRandomPosition() {
        return randomPosition;
    }

    /**
     * Définit si la position des nœuds doit être randomisée à l'affichage.
     *
     * @param randomPosition {@code true} pour activer la randomisation
     */
    public void setRandomPosition(boolean randomPosition) {
        this.randomPosition = randomPosition;
    }

    /**
     * Calcule la matrice d'adjacence du graphe à partir des successeurs
     * des nœuds.
     */
    private void computeAdjacencyMatrix() {
        adjacencyMatrix = new int[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (nodes.get(i).getSuccessors().contains(nodes.get(j))) {
                    adjacencyMatrix[i][j] = 1;
                } else {
                    adjacencyMatrix[i][j] = 0;
                }
            }
        }
    }

    /**
     * Retourne la matrice d'adjacence du graphe, en la calculant si nécessaire.
     *
     * @return matrice d'adjacence
     */
    public int[][] getAdjacencyMatrix() {
        if (adjacencyMatrix == null) {
            computeAdjacencyMatrix();
        }
        return adjacencyMatrix;
    }

    /**
     * Calcule les degrés entrants et sortants de chaque nœud du graphe.
     */
    public void computeDegreesMatrix() {
        inDegrees = new int[nodes.size()];
        outDegrees = new int[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            inDegrees[i] = nodes.get(i).getPredecessors().size();
            outDegrees[i] = nodes.get(i).getSuccessors().size();
        }
    }

    /**
     * Retourne le tableau des degrés entrants des nœuds,
     * en le calculant si nécessaire.
     *
     * @return tableau des degrés entrants
     */
    public int[] getInDegrees() {
        if (inDegrees == null) {
            computeDegreesMatrix();
        }
        return inDegrees;
    }

    /**
     * Retourne le tableau des degrés sortants des nœuds,
     * en le calculant si nécessaire.
     *
     * @return tableau des degrés sortants
     */
    public int[] getOutDegrees() {
        if (outDegrees == null) {
            computeDegreesMatrix();
        }
        return outDegrees;
    }

    /**
     * Applique l'algorithme de Floyd-Warshall sur le graphe.
     * (Méthode à implémenter).
     *
     * @return graphe éventuellement transformé
     */
    public Graph floydWarshall() {
        return this;
    }

    /**
     * Retourne une représentation textuelle du graphe listant les nœuds
     * puis les arêtes.
     *
     * @return chaîne représentant le graphe
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            sb.append(node.toString()).append("\n");
        }
        for (Edge edge : edges) {
            System.out.println(edge);
            sb.append(edge.toString()).append("\n");
        }
        return sb.toString();
    }
}
