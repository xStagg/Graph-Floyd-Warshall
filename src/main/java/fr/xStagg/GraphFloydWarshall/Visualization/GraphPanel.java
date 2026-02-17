package fr.xStagg.GraphFloydWarshall.Visualization;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Graph.Node;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GraphPanel extends JPanel {

    private Graph graph;
    private static final int RADIUS = 30;
    private Random random = new Random();

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);

        if (graph != null && graph.getNodes() != null) {
            initialiserPositions();
        }
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        if (graph != null && graph.getNodes() != null) {
            initialiserPositions();
        } else {
            System.out.println("No graph found");
        }
        repaint();
    }

    private void initialiserPositions() {
        if (graph == null || graph.getNodes() == null) return;

        for (Node n : graph.getNodes()) {
            n.setGraphicsX(random.nextInt(700) + 50);
            n.setGraphicsY(random.nextInt(500) + 50);
        }
    }

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
        drawGraph(g2d);
    }

    private void drawGraph(Graphics2D g2d) {
        if (graph == null || graph.getNodes() == null || graph.getNodes().isEmpty()) {
            // Message si pas de graphe
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 20));
            String msg = "Aucun graphe à afficher";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
            return;
        }

        // Dessiner les arêtes d'abord
        drawEdges(g2d);

        // Puis les nœuds
        drawNodes(g2d);
    }

    private void drawEdges(Graphics2D g2d) {
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));

        Node[] nodes = graph.getNodes().toArray(new Node[0]);

        // Parcourir toutes les paires de nœuds
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                Node n1 = nodes[i];
                Node n2 = nodes[j];

                // Vérifier s'il existe une arête entre n1 et n2
                // À ADAPTER selon votre implémentation de Graph
                if (graph.areConnected(n1, n2)) {
                    if (positionsValides(n1, n2)) {
                        g2d.drawLine(n1.getGraphicsX(), n1.getGraphicsY(),
                                n2.getGraphicsX(), n2.getGraphicsY());
                    }
                }
            }
        }
    }

    private boolean positionsValides(Node n1, Node n2) {
        return n1.getGraphicsX() > 0 && n1.getGraphicsY() > 0 &&
                n2.getGraphicsX() > 0 && n2.getGraphicsY() > 0;
    }

    private void drawNodes(Graphics2D g2d) {
        for (Node n : graph.getNodes()) {
            int x = n.getGraphicsX();
            int y = n.getGraphicsY();

            if (x <= 0 || y <= 0) continue;

            // Ombre
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(x - RADIUS + 3, y - RADIUS + 3, RADIUS * 2, RADIUS * 2);

            // Remplissage du nœud
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

            // Bordure du nœud
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

            // Étiquette du nœud
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String label = String.valueOf((char) ('A' + n.getId()-1));
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = x - fm.stringWidth(label) / 2;
            int labelY = y + fm.getAscent() / 3;
            g2d.drawString(label, labelX, labelY);
        }
    }

    public void randomizePositions() {
        if (graph != null && graph.getNodes() != null) {
            initialiserPositions();
            repaint();
        }
    }
}