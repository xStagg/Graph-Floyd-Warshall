package fr.xStagg.GraphFloydWarshall.Graph;

/**
 * Représente une arête orientée d'un graphe, reliant un nœud source à un nœud cible,
 * éventuellement pondérée par un poids.
 */
public class Edge {
    static int ID;
    // ATTRIBUTES
    private int id;
    private Node source;
    private Node target;
    private double weight;

    /**
     * Crée une nouvelle arête non pondérée entre deux nœuds.
     *
     * @param source nœud source de l'arête
     * @param target nœud cible de l'arête
     */
    public Edge(Node source, Node target) {
        this.id = ID++;
        this.source = source;
        this.target = target;
    }

    /**
     * Crée une nouvelle arête pondérée entre deux nœuds.
     *
     * @param source nœud source de l'arête
     * @param target nœud cible de l'arête
     * @param weight poids associé à l'arête
     */
    public Edge(Node source, Node target, double weight) {
        this.id = ID++;
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    /**
     * Retourne l'identifiant unique de l'arête.
     *
     * @return identifiant de l'arête
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne le nœud source de l'arête.
     *
     * @return nœud source
     */
    public Node getSource() {
        return source;
    }

    /**
     * Retourne le nœud cible de l'arête.
     *
     * @return nœud cible
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Retourne le poids de l'arête.
     *
     * @return poids de l'arête
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Modifie le poids de l'arête.
     *
     * @param weight nouveau poids
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Retourne une représentation textuelle de l'arête.
     *
     * @return chaîne représentant l'arête avec son id, sa source et sa cible
     */
    @Override
    public String toString() {
        return "Edge [id=" + id + ", source=" + source.getId() + ", target=" + target.getId() + "]";
    }
}
