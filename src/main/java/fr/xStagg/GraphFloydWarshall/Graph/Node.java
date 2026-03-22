package fr.xStagg.GraphFloydWarshall.Graph;

import java.util.ArrayList;

/**
 * Représente un nœud d'un graphe orienté avec ses prédécesseurs, successeurs
 * et informations de position graphique.
 */
public class Node {

    // ATTRIBUTES
    private int id;
    private ArrayList<Node> predecessors;
    private ArrayList<Node> successors;
    private int graphicsX;
    private int graphicsY;

    /**
     * Crée un nœud avec l'identifiant donné.
     *
     * @param id identifiant unique du nœud
     */
    public Node(int id) {
        this.id = id;
        predecessors = new ArrayList<>();
        successors = new ArrayList<>();
    }

    /**
     * Retourne l'identifiant du nœud.
     *
     * @return identifiant du nœud
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne la liste des prédécesseurs du nœud.
     *
     * @return liste des nœuds prédécesseurs
     */
    public ArrayList<Node> getPredecessors() {
        return predecessors;
    }

    /**
     * Retourne la liste des successeurs du nœud.
     *
     * @return liste des nœuds successeurs
     */
    public ArrayList<Node> getSuccessors() {
        return successors;
    }

    /**
     * Ajoute un prédécesseur au nœud et met à jour la liste des successeurs
     * du nœud passé en paramètre si nécessaire.
     *
     * @param n nœud à ajouter comme prédécesseur
     */
    public void addPredecessor(Node n) {
        if (!predecessors.contains(n)) {
            if (!n.getSuccessors().contains(this)) {
                n.getSuccessors().add(this);
            }
            predecessors.add(n);
        }
    }

    /**
     * Ajoute un successeur au nœud et met à jour la liste des prédécesseurs
     * du nœud passé en paramètre si nécessaire.
     *
     * @param n nœud à ajouter comme successeur
     */
    public void addSuccessor(Node n) {
        if (!successors.contains(n)) {
            if (!n.getPredecessors().contains(this)) {
                n.getPredecessors().add(this);
            }
            successors.add(n);
        }
    }

    /**
     * Supprime un prédécesseur du nœud et met à jour la liste des successeurs
     * du nœud passé en paramètre.
     *
     * @param n nœud à retirer de la liste des prédécesseurs
     */
    public void removePredecessor(Node n) {
        if (predecessors.contains(n)) {
            n.getSuccessors().remove(this);
            predecessors.remove(n);
        }
    }

    /**
     * Supprime un successeur du nœud et met à jour la liste des prédécesseurs
     * du nœud passé en paramètre.
     *
     * @param n nœud à retirer de la liste des successeurs
     */
    public void removeSuccessor(Node n) {
        if (successors.contains(n)) {
            n.getPredecessors().remove(this);
            successors.remove(n);
        }
    }

    /**
     * Supprime et retourne le prédécesseur ayant l'identifiant donné.
     *
     * @param id identifiant du prédécesseur à retirer
     * @return nœud retiré ou {@code null} s'il n'existe pas
     */
    public Node removePredecessor(int id) {
        Node n = predecessors.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        if (n != null) {
            removePredecessor(n);
        }
        return n;
    }

    /**
     * Supprime et retourne le successeur ayant l'identifiant donné.
     *
     * @param id identifiant du successeur à retirer
     * @return nœud retiré ou {@code null} s'il n'existe pas
     */
    public Node removeSuccessor(int id) {
        Node n = successors.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        if (n != null) {
            removeSuccessor(n);
        }
        return n;
    }

    /**
     * Retourne la coordonnée X utilisée pour l'affichage graphique du nœud.
     *
     * @return position X du nœud
     */
    public int getGraphicsX() {
        return graphicsX;
    }

    /**
     * Retourne la coordonnée Y utilisée pour l'affichage graphique du nœud.
     *
     * @return position Y du nœud
     */
    public int getGraphicsY() {
        return graphicsY;
    }

    /**
     * Modifie la coordonnée X utilisée pour l'affichage graphique du nœud.
     *
     * @param x nouvelle coordonnée X
     */
    public void setGraphicsX(int x) {
        this.graphicsX = x;
    }

    /**
     * Modifie la coordonnée Y utilisée pour l'affichage graphique du nœud.
     *
     * @param y nouvelle coordonnée Y
     */
    public void setGraphicsY(int y) {
        this.graphicsY = y;
    }

    /**
     * Indique si le nœud est un point de départ (aucun prédécesseur).
     *
     * @return {@code true} si le nœud n'a pas de prédécesseurs, sinon {@code false}
     */
    public boolean isStartingPoint() {
        return predecessors.isEmpty();
    }

    /**
     * Retourne une représentation textuelle du nœud montrant ses prédécesseurs
     * et ses successeurs.
     *
     * @return chaîne représentant le nœud
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < predecessors.size() - 1; i++) {
            sb.append(predecessors.get(i).getId());
            sb.append("-");
        }
        if (!predecessors.isEmpty()) {
            sb.append(predecessors.getLast().getId());
        }
        sb.append("]");
        sb.append(" -> ");
        sb.append(id);
        sb.append(" -> ");
        sb.append("[");
        for (int i = 0; i < successors.size() - 1; i++) {
            sb.append(successors.get(i).getId());
            sb.append("-");
        }
        if (!successors.isEmpty()) {
            sb.append(successors.getLast().getId());
        }
        sb.append("]");
        return sb.toString();
    }
}