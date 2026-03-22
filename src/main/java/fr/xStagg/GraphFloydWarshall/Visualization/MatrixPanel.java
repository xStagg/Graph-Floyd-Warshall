package fr.xStagg.GraphFloydWarshall.Visualization;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau Swing responsable de l'affichage d'une matrice entière (adjacence,
 * L^k, P^k, etc.) associée à un graphe, avec en-têtes de lignes/colonnes,
 * surlignage optionnel des cellules modifiées, et affichage des degrés.
 */
public class MatrixPanel extends JPanel {

    private Graph graph;
    private final int CELL_SIZE = 50;

    /**
     * Matrice actuellement affichée (adjacence, L^k, P^k...).
     * On suppose une matrice carrée n×n.
     */
    private int[][] matrix;

    /**
     * Matrice de surlignage : {@code highlight[i][j] == true} si la cellule
     * (i, j) doit être mise en évidence (par exemple, modifiée à l'étape k).
     */
    private boolean[][] highlight;

    private boolean drawDegrees = false;

    /**
     * Crée un panneau de matrice pour le graphe donné.
     * Par défaut, affiche la matrice d'adjacence.
     *
     * @param graph graphe dont la matrice sera affichée (peut être {@code null})
     */
    public MatrixPanel(Graph graph) {
        this.graph = graph;
        this.matrix = graph != null ? graph.getGraphMatrix() : null;
        if (matrix != null) {
            this.highlight = new boolean[matrix.length][matrix.length];
        }
        setBackground(Color.WHITE);
    }

    /**
     * Active l'affichage des degrés entrants et sortants autour de la matrice
     * et déclenche un repaint.
     */
    public void showDegrees() {
        drawDegrees = true;
        repaint();
    }

    /**
     * Définit la matrice à afficher ainsi que la matrice de surlignage.
     * Si {@code highlight} est {@code null} ou de dimensions incorrectes,
     * une matrice de surlignage vide est utilisée.
     *
     * @param matrix    matrice n×n à afficher
     * @param highlight matrice n×n de booléens indiquant les cellules à mettre en évidence
     *                  (peut être {@code null})
     */
    public void setMatrix(int[][] matrix, boolean[][] highlight) {
        this.matrix = matrix;
        if (matrix != null) {
            if (highlight != null &&
                    highlight.length == matrix.length &&
                    highlight[0].length == matrix[0].length) {
                this.highlight = highlight;
            } else {
                this.highlight = new boolean[matrix.length][matrix.length];
            }
        } else {
            this.highlight = null;
        }
        repaint();
    }

    /**
     * Remplace le graphe associé à ce panneau et réinitialise l'affichage
     * avec la nouvelle matrice d'adjacence.
     *
     * @param graph nouveau graphe (peut être {@code null})
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        if (graph != null) {
            this.matrix = graph.getGraphMatrix();
            if (matrix != null) {
                this.highlight = new boolean[matrix.length][matrix.length];
            }
        }
        repaint();
    }

    /**
     * {@inheritDoc}
     * Active l'antialiasing et délègue l'affichage à {@link #drawMatrix(Graphics2D)}
     * ainsi qu'à {@link #drawDegreesMatrix(Graphics2D)} si activé.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawMatrix(g2d);
        if (drawDegrees && graph != null && matrix != null) {
            drawDegreesMatrix(g2d);
        }
    }

    /**
     * Dessine la matrice courante centrée dans le panneau, avec les en-têtes
     * de lignes et de colonnes issus des identifiants des nœuds.
     * Les valeurs égales à {@code 100000} sont affichées comme {@code INF}.
     * Les cellules surlignées apparaissent en jaune clair.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawMatrix(Graphics2D g2d) {
        if (matrix == null || graph == null) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 20));
            String msg = "Aucune matrice à afficher";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
            return;
        }

        int n = matrix.length;

        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                int x = j * CELL_SIZE + getWidth() / 2 - (n + 1) * CELL_SIZE / 2;
                int y = i * CELL_SIZE + getHeight() / 2 - (n + 1) * CELL_SIZE / 2;

                String val = "";
                Color greyColor = new Color(153, 153, 153, 75);
                Color cellColor = Color.WHITE;

                if (i != 0 || j != 0) {
                    if (i != 0 && j != 0) {
                        int mVal = matrix[i - 1][j - 1];
                        if (mVal > 99000) {
                            val = "INF";
                        } else {
                            val = Integer.toString(mVal);
                        }
                        if (highlight != null &&
                                i - 1 < highlight.length &&
                                j - 1 < highlight[0].length &&
                                highlight[i - 1][j - 1]) {
                            cellColor = new Color(255, 255, 150);
                        }
                    }

                    if (i == 0) {
                        val = String.valueOf(graph.getNodes().get(j - 1).getId());
                        cellColor = greyColor;
                    }
                    if (j == 0) {
                        val = String.valueOf(graph.getNodes().get(i - 1).getId());
                        cellColor = greyColor;
                    }

                    g2d.setColor(cellColor);
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }

                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (CELL_SIZE - fm.stringWidth(val)) / 2;
                int textY = y + (CELL_SIZE - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(val, textX, textY);
            }
        }
    }

    /**
     * Dessine les degrés entrants ({@code d°+}) en bas de la matrice
     * et les degrés sortants ({@code d°-}) à droite de la matrice.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawDegreesMatrix(Graphics2D g2d) {
        if (matrix == null) return;

        int n = matrix.length;

        int TOP_RIGHT_X = getWidth() / 2 + (n + 1) * CELL_SIZE / 2;
        int TOP_RIGHT_Y = getHeight() / 2 - (n + 1) * CELL_SIZE / 2;
        int BOT_LEFT_X = getWidth() / 2 - (n + 1) * CELL_SIZE / 2;
        int BOT_LEFT_Y = getHeight() / 2 + (n + 1) * CELL_SIZE / 2;

        Color greyColor = new Color(153, 153, 153, 75);
        String val;

        for (int i = 0; i < n + 1; i++) {
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
            int textY = BOT_LEFT_Y + (CELL_SIZE - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(val, textX, textY);
        }

        for (int j = 0; j < n + 1; j++) {
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
            int textY = y + (CELL_SIZE - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(val, textX, textY);
        }
    }
}