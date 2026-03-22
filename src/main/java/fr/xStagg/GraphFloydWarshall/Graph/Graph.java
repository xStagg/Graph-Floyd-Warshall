package fr.xStagg.GraphFloydWarshall.Graph;

import fr.xStagg.GraphFloydWarshall.Utils.LoadingMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.xStagg.GraphFloydWarshall.Utils.MatrixUtils.copyMatrix;

/**
 * Représente un graphe orienté pondéré composé de nœuds ({@link Node}) et d'arêtes ({@link Edge}).
 * <p>
 * Fournit les structures associées (matrice d'adjacence, degrés entrants/sortants)
 * ainsi que l'algorithme de Floyd-Warshall pour le calcul des plus courts chemins.
 * </p>
 */
public class Graph {

    // ATTRIBUTES
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private String path;
    private LoadingMethod loadingMethod;
    private boolean randomPosition;
    private int[][] adjacencyMatrix;
    private int[][] graphMatrix;
    private int[] inDegrees;
    private int[] outDegrees;
    private int[][][][] floydResult;
    private boolean[][][] floydUpdated;

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
     * Recherche une arête par ses nœuds source et cible.
     *
     * @param id_source identifiant du nœud source
     * @param id_dest   identifiant du nœud cible
     * @return arête correspondante ou {@code null} si elle n'existe pas
     */
    public Edge getEdgeBySrcTrg(int id_source, int id_dest) {
        for (Edge edge : edges) {
            if (edge.getSource().getId() == id_source && edge.getTarget().getId() == id_dest) {
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
    public Edge createEdge(Node source, Node target, int weight) {
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
        int s = source.getId();
        int t = target.getId();
        for (Edge edge : edges) {
            if (edge.getSource().getId() == s && edge.getTarget().getId() == t) {
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
     * Calcule la matrice d'adjacence du graphe à partir des successeurs des nœuds.
     * La valeur {@code adjacencyMatrix[i][j]} vaut 1 si le nœud i a le nœud j comme successeur,
     * 0 sinon.
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
     * Retourne une copie pour éviter toute modification externe.
     *
     * @return copie de la matrice d'adjacence
     */
    public int[][] getAdjacencyMatrix() {
        if (adjacencyMatrix == null) {
            computeAdjacencyMatrix();
        }
        return copyMatrix(adjacencyMatrix);
    }

    private void computeGraphMatrix() {
        graphMatrix = new int[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (nodes.get(i).getSuccessors().contains(nodes.get(j))) {
                    graphMatrix[i][j] = getEdgeBySrcTrg(i, j).getWeight();
                } else {
                    graphMatrix[i][j] = 0;
                }
            }
        }
    }

    public int[][] getGraphMatrix() {
        if (graphMatrix == null) {
            computeGraphMatrix();
        }
        return copyMatrix(graphMatrix);
    }

    /**
     * Calcule les degrés entrants et sortants de chaque nœud du graphe.
     * Le degré entrant d'un nœud est le nombre de ses prédécesseurs,
     * le degré sortant est le nombre de ses successeurs.
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
     * @return tableau des degrés entrants indexé par position dans la liste des nœuds
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
     * @return tableau des degrés sortants indexé par position dans la liste des nœuds
     */
    public int[] getOutDegrees() {
        if (outDegrees == null) {
            computeDegreesMatrix();
        }
        return outDegrees;
    }

    /**
     * Applique l'algorithme de Floyd-Warshall sur le graphe et retourne
     * toutes les matrices intermédiaires L^k et P^k.
     * <p>
     * Le tableau retourné {@code result} est de dimension {@code [n+1][2][n][n]} où :
     * <ul>
     *   <li>{@code result[k][0]} = matrice des distances L à l'étape k</li>
     *   <li>{@code result[k][1]} = matrice des prédécesseurs P à l'étape k</li>
     * </ul>
     * Les distances inatteignables sont codées par {@code 100000} (∞).
     * Chaque étape est tracée dans la console pour le suivi de l'exécution.
     * </p>
     *
     * @return tableau 4D contenant toutes les étapes de l'algorithme
     */
    public int[][][][] floydWarshall() {
        int n = nodes.size();
        int[][][][] result = new int[n+1][2][n][n];

        int[][] L = getAdjacencyMatrix().clone();
        int[][] P = new int[n][n];

        for (int i = 0; i < L.length; i++) {
            for (int j = 0; j < L[i].length; j++) {
                if (i == j) {
                    L[i][j] = 0;
                    P[i][j] = -1;
                } else if (L[i][j] == 1) {
                    L[i][j] = getEdgeBySrcTrg(nodes.get(i).getId(), nodes.get(j).getId()).getWeight();
                    P[i][j] = i;
                } else {
                    L[i][j] = 100000;
                    P[i][j] = -1;
                }
            }
        }

        result[0] = new int[][][]{copyMatrix(L), copyMatrix(P)};

        // --- Trace initiale ---
        System.out.println("=".repeat(60));
        System.out.println("  ALGORITHME DE FLOYD-WARSHALL");
        System.out.println("  Graphe : " + n + " sommets, " + edges.size() + " arêtes");
        System.out.println("=".repeat(60));
        printFloydStep(L, P, 0, -1, n);

        boolean[][][] updated = new boolean[n+1][n][n];

        for (int k = 0; k < n; k++) {
            int[][] L_new = copyMatrix(L);
            int[][] P_new = copyMatrix(P);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("  Étape k = " + (k+1) + " — Sommet intermédiaire autorisé : " + nodes.get(k).getId());
            System.out.println("=".repeat(60));

            boolean anyUpdate = false;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (L[i][k] != 100000 && L[k][j] != 100000 &&
                            L[i][k] + L[k][j] < L[i][j]) {

                        int oldVal = L[i][j];
                        L_new[i][j] = L[i][k] + L[k][j];
                        P_new[i][j] = P[k][j];
                        updated[k+1][i][j] = true;
                        anyUpdate = true;

                        // Trace de chaque mise à jour
                        System.out.printf("  Mise à jour [%d][%d] : %s + %d = %d  (ancien : %s)  → P[%d][%d] = %d%n",
                                nodes.get(i).getId(),
                                nodes.get(j).getId(),
                                oldVal == 100000 ? "INF" : String.valueOf(oldVal),
                                L[i][k] + L[k][j] - (oldVal == 100000 ? 0 : 0), // juste pour la syntaxe
                                L[i][k] + L[k][j],
                                oldVal == 100000 ? "INF" : String.valueOf(oldVal),
                                nodes.get(i).getId(),
                                nodes.get(j).getId(),
                                nodes.get(P[k][j]).getId()
                        );
                    }
                }
            }

            if (!anyUpdate) {
                System.out.println("  (aucune mise à jour à cette étape)");
            }

            L = L_new;
            P = P_new;
            result[k+1] = new int[][][]{copyMatrix(L), copyMatrix(P)};

            printFloydStep(L, P, k+1, nodes.get(k).getId(), n);
        }

        // --- Résumé final ---
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  RÉSULTAT FINAL");
        System.out.println("=".repeat(60));

        boolean hasNeg = false;
        for (int i = 0; i < n; i++) {
            if (L[i][i] < 0) { hasNeg = true; break; }
        }
        System.out.println("  Circuit absorbant : " + (hasNeg ? "OUI ⚠" : "NON ✓"));

        if (!hasNeg) {
            System.out.println("\n  Plus courts chemins :");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) continue;
                    if (L[i][j] >= 100000) continue;
                    System.out.printf("    %d -> %d  |  distance = %d%n",
                            nodes.get(i).getId(), nodes.get(j).getId(), L[i][j]);
                }
            }
        }

        floydResult = result;
        floydUpdated = updated;
        return result;
    }

    /**
     * Affiche dans la console les matrices L^k et P^k à une étape donnée
     * de l'algorithme de Floyd-Warshall, avec en-têtes et formatage aligné.
     *
     * @param L            matrice des distances à afficher
     * @param P            matrice des prédécesseurs à afficher
     * @param k            indice de l'étape courante (0 = initialisation)
     * @param intermediate identifiant du sommet intermédiaire autorisé (-1 pour l'initialisation)
     * @param n            nombre de sommets du graphe
     */
    private void printFloydStep(int[][] L, int[][] P, int k, int intermediate, int n) {
        String title = (k == 0)
                ? "\n  --- L^0 et P^0 (initialisation) ---"
                : "\n  --- L^" + k + " et P^" + k + " (après sommet " + intermediate + ") ---";
        System.out.println(title);

        // En-tête colonnes
        System.out.print("       ");
        for (int j = 0; j < n; j++) {
            System.out.printf("%6d", nodes.get(j).getId());
        }
        System.out.println();

        // Séparateur
        System.out.println("  L^" + k + " :");
        for (int i = 0; i < n; i++) {
            System.out.printf("    %2d |", nodes.get(i).getId());
            for (int j = 0; j < n; j++) {
                if (L[i][j] == 100000) System.out.printf("%6s", "INF");
                else                   System.out.printf("%6d", L[i][j]);
            }
            System.out.println();
        }

        System.out.println("  P^" + k + " :");
        for (int i = 0; i < n; i++) {
            System.out.printf("    %2d |", nodes.get(i).getId());
            for (int j = 0; j < n; j++) {
                if (P[i][j] == -1) System.out.printf("%6s", "-1");
                else               System.out.printf("%6d", nodes.get(P[i][j]).getId());
            }
            System.out.println();
        }
    }

    /**
     * Retourne le résultat complet de Floyd-Warshall (toutes les étapes),
     * en déclenchant le calcul si nécessaire.
     *
     * @return tableau 4D des matrices L^k et P^k pour chaque étape k
     */
    public int[][][][] getFloydResult() {
        if(floydResult == null) {
            floydWarshall();
        }
        return floydResult;
    }

    /**
     * Retourne la matrice de surlignage indiquant quelles cellules ont été
     * modifiées à chaque étape de Floyd-Warshall.
     * Déclenche le calcul si nécessaire.
     *
     * @return tableau 3D de booléens {@code updated[k][i][j]}
     */
    public boolean[][][] getFloydUpdated() {
        if(floydUpdated == null) {
            floydWarshall();
        }
        return floydUpdated;
    }

    /**
     * Indique si le graphe contient un circuit absorbant (cycle de poids négatif).
     * Un circuit absorbant est détecté si la diagonale de la matrice finale L^n
     * contient une valeur strictement négative.
     * Déclenche Floyd-Warshall si le résultat n'est pas encore calculé.
     *
     * @return {@code true} si au moins un circuit absorbant existe, sinon {@code false}
     */
    public boolean hasNegativeCycle() {
        if(floydResult == null) {
            floydWarshall();
        }
        boolean hasNegativeCycle = false;
        for (int i = 0; i < nodes.size(); i++) {
            if (floydResult[floydResult.length-1][0][i][i] < 0) {
                hasNegativeCycle = true;
                break;
            }
        }
        return hasNegativeCycle;
    }

    /**
     * Retourne la liste textuelle de tous les plus courts chemins entre chaque
     * paire de nœuds (i, j) avec i ≠ j, à partir du résultat de Floyd-Warshall.
     * Les chemins sont reconstruits via la matrice P finale.
     * Déclenche Floyd-Warshall si le résultat n'est pas encore calculé.
     *
     * @return liste de chaînes au format {@code "src -> dst : n1 -> n2 -> ... (longueur = d)"}
     */
    public java.util.List<String> getAllShortestPathsDescriptions() {
        if (floydResult == null) {
            floydWarshall();
        }
        int[][] L = floydResult[floydResult.length - 1][0];
        int[][] P = floydResult[floydResult.length - 1][1];

        int n = nodes.size();
        java.util.List<String> result = new java.util.ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (L[i][j] >= 100000) continue;

                java.util.List<Integer> path = reconstructPathByIndex(i, j, P);
                if (!path.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(nodes.get(i).getId())
                            .append(" -> ")
                            .append(nodes.get(j).getId())
                            .append(" : ");

                    for (int k = 0; k < path.size(); k++) {
                        int idx = path.get(k);
                        sb.append(nodes.get(idx).getId());
                        if (k < path.size() - 1) sb.append(" -> ");
                    }
                    sb.append(" (longueur = ").append(L[i][j]).append(")");
                    result.add(sb.toString());
                }
            }
        }
        return result;
    }

    /**
     * Reconstruit le chemin entre deux nœuds (par indices 0..n-1)
     * à partir de la matrice des prédécesseurs P.
     *
     * @param start indice du nœud de départ dans la liste des nœuds
     * @param end   indice du nœud d'arrivée dans la liste des nœuds
     * @param P     matrice des prédécesseurs issue de Floyd-Warshall
     * @return liste ordonnée des indices de nœuds formant le chemin,
     *         ou liste vide si aucun chemin n'existe
     */
    private java.util.List<Integer> reconstructPathByIndex(int start, int end, int[][] P) {
        if (start == end) {
            return java.util.List.of(start);
        }
        if (P[start][end] == -1) {
            return java.util.Collections.emptyList();
        }
        java.util.List<Integer> path = new java.util.ArrayList<>();
        int current = end;
        while (current != start) {
            path.add(current);
            current = P[start][current];
            if (current == -1) {
                return java.util.Collections.emptyList();
            }
        }
        path.add(start);
        java.util.Collections.reverse(path);
        return path;
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