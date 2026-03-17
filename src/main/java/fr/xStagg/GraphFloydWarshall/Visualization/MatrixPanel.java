package fr.xStagg.GraphFloydWarshall.Visualization;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau Swing responsable de l'affichage de la matrice d'adjacence
 * d'un graphe et, optionnellement, des degrés entrants et sortants.
 */
public class MatrixPanel extends JPanel {

    private Graph graph;
    private final int CELL_SIZE = 50;
    private final int[][] matrix;
    private boolean drawDegrees = false;

    /**
     * Crée un panneau de matrice pour le graphe donné.
     *
     * @param graph graphe dont la matrice d'adjacence sera affichée
     */
    public MatrixPanel(Graph graph) {
        this.graph = graph;
        this.matrix = graph.getAdjacencyMatrix();
        setBackground(Color.WHITE);
    }

    /**
     * Active l'affichage des matrices de degrés entrants et sortants.
     */
    public void showDegrees() {
        drawDegrees = true;
    }

    /**
     * Redessine le panneau avec la matrice (et éventuellement les degrés).
     *
     * @param g contexte graphique
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Activer l'anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dessiner le graphe
        drawMatrix(g2d);
        if (drawDegrees) {
            drawDegreesMatrix(g2d);
        }
    }

    /**
     * Dessine la matrice d'adjacence du graphe.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawMatrix(Graphics2D g2d) {
        if (graph.getAdjacencyMatrix() == null) {
            // Message si pas de graphe
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 20));
            String msg = "Aucune matrice à afficher";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
            return;
        }

        for (int i = 0; i < matrix.length + 1; i++) {
            for (int j = 0; j < matrix.length + 1; j++) {
                int x = j * CELL_SIZE + getWidth() / 2 - (matrix.length + 1) * CELL_SIZE / 2;
                int y = i * CELL_SIZE + getHeight() / 2 - (matrix.length + 1) * CELL_SIZE / 2;

                String val = "";
                Color greyColor = new Color(153, 153, 153, 75);
                Color color = Color.WHITE;

                if (i != 0 || j != 0) {
                    if (i != 0 && j != 0) {
                        if (matrix[i - 1][j - 1] == 1) {
                            g2d.setColor(Color.RED);
                        } else {
                            g2d.setColor(Color.BLACK);
                        }
                        val = Integer.toString(matrix[i - 1][j - 1]);
                    }

                    if (i == 0) {
                        val = String.valueOf(graph.getNodes().get(j - 1).getId());
                        color = greyColor;
                    }
                    if (j == 0) {
                        val = String.valueOf(graph.getNodes().get(i - 1).getId());
                        color = greyColor;
                    }

                    g2d.setColor(color);
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }

                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (CELL_SIZE - fm.stringWidth(val)) / 2;
                int textY = y + (CELL_SIZE - fm.stringWidth(val)) / 2 + fm.getAscent();
                g2d.drawString(val, textX, textY);

            }
        }
    }

    /**
     * Dessine, autour de la matrice d'adjacence, les degrés entrants et sortants
     * de chaque nœud.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawDegreesMatrix(Graphics2D g2d) {
        int TOP_LEFT_X = getWidth() / 2 - (matrix.length + 1) * CELL_SIZE / 2;
        int TOP_LEFT_Y = getHeight() / 2 - (matrix.length + 1) * CELL_SIZE / 2;
        int TOP_RIGHT_X = getWidth() / 2 + (matrix.length + 1) * CELL_SIZE / 2;
        int TOP_RIGHT_Y = getHeight() / 2 - (matrix.length + 1) * CELL_SIZE / 2;
        int BOT_LEFT_X = getWidth() / 2 - (matrix.length + 1) * CELL_SIZE / 2;
        int BOT_LEFT_Y = getHeight() / 2 + (matrix.length + 1) * CELL_SIZE / 2;

        Color greyColor = new Color(153, 153, 153, 75);
        String val;

        for (int i = 0; i < matrix.length + 1; i++) {

            int x = i * CELL_SIZE + BOT_LEFT_X;

            if (i == 0) {
                g2d.setColor(greyColor);
                g2d.fillRect(x, BOT_LEFT_Y, CELL_SIZE, CELL_SIZE);
                val = "d°+";
            } else {
                val = String.valueOf(graph.getInDegrees()[i - 1]);
            }
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, BOT_LEFT_Y, CELL_SIZE, CELL_SIZE);

            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (CELL_SIZE - fm.stringWidth(val)) / 2;
            int textY = BOT_LEFT_Y + (CELL_SIZE - fm.stringWidth(val)) / 2 + fm.getAscent();
            g2d.drawString(val, textX, textY);
        }
        for (int j = 0; j < matrix.length + 1; j++) {

            int y = j * CELL_SIZE + TOP_RIGHT_Y;

            if (j == 0) {
                g2d.setColor(greyColor);
                g2d.fillRect(TOP_RIGHT_X, y, CELL_SIZE, CELL_SIZE);
                val = "d°-";
            } else {
                val = String.valueOf(graph.getOutDegrees()[j - 1]);
            }
            g2d.setColor(Color.BLACK);
            g2d.drawRect(TOP_RIGHT_X, y, CELL_SIZE, CELL_SIZE);

            FontMetrics fm = g2d.getFontMetrics();
            int textX = TOP_RIGHT_X + (CELL_SIZE - fm.stringWidth(val)) / 2;
            int textY = y + (CELL_SIZE - fm.stringWidth(val)) / 2 + fm.getAscent();
            g2d.drawString(val, textX, textY);
        }
    }

    /**
     * Met à jour le graphe affiché par ce panneau.
     *
     * @param graph nouveau graphe
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
