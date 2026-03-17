package fr.xStagg.GraphFloydWarshall.Visualization;

import fr.xStagg.GraphFloydWarshall.Graph.Edge;
import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Graph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Random;

/**
 * Panneau Swing permettant l'affichage graphique d'un graphe orienté
 * et le déplacement des nœuds à la souris.
 */
public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener {

    private Graph graph;
    private static final int RADIUS = 30;
    private static final int ARROW_SIZE = 20; // Taille de la flèche
    private Random random = new Random();
    private Node selectedNode;

    /**
     * Crée un panneau d'affichage pour le graphe donné.
     *
     * @param graph graphe à afficher
     */
    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);

        if (graph != null && graph.getNodes() != null) {
            initialiserPositions();
        }

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Met à jour le graphe à afficher et, si demandé, initialise
     * des positions aléatoires pour ses nœuds.
     *
     * @param graph nouveau graphe à afficher
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        if (graph != null && graph.getNodes() != null && graph.isRandomPosition()) {
            initialiserPositions();
        } else {
            System.out.println("No graph found");
        }
        repaint();
    }

    /**
     * Initialise de manière aléatoire les positions graphiques des nœuds du graphe.
     */
    private void initialiserPositions() {
        if (graph == null || graph.getNodes() == null) return;

        for (Node n : graph.getNodes()) {
            n.setGraphicsX(random.nextInt(700) + 50);
            n.setGraphicsY(random.nextInt(500) + 50);
        }
    }

    /**
     * Redessine le contenu du panneau (nœuds et arêtes).
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
        drawGraph(g2d);
    }

    /**
     * Dessine les arêtes puis les nœuds du graphe si celui-ci existe.
     *
     * @param g2d contexte graphique 2D
     */
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

    /**
     * Dessine toutes les arêtes du graphe, avec flèches et poids.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawEdges(Graphics2D g2d) {
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));

        for (Edge e : graph.getEdges()) {
            Point source = new Point(e.getSource().getGraphicsX(), e.getSource().getGraphicsY());
            Point target = new Point(e.getTarget().getGraphicsX(), e.getTarget().getGraphicsY());

            // Vérifier que les positions sont valides
            if (source.x <= 0 || source.y <= 0 || target.x <= 0 || target.y <= 0) continue;

            // Dessiner la ligne et la flèche ajustées au bord des cercles
            drawArrow(g2d, source, target);
            drawWeight(g2d, source, target, e.getWeight());
        }
    }

    /**
     * Dessine le poids d'une arête au milieu du segment qui relie ses nœuds.
     *
     * @param g2d   contexte graphique 2D
     * @param source point source
     * @param target point cible
     * @param weight poids à afficher
     */
    private void drawWeight(Graphics2D g2d, Point source, Point target, double weight) {
        int stringWidth = g2d.getFontMetrics().stringWidth(String.valueOf(weight));
        double angle = Math.atan2(target.y - source.y, target.x - source.x);
        if (source.x > target.x) {
            angle += Math.PI;
        }
        float midX = (float) (target.x + source.x) / 2;
        float midY = (float) (target.y + source.y) / 2;

        AffineTransform defaultTransform = g2d.getTransform();
        g2d.rotate(angle, midX, midY);
        g2d.drawString(String.valueOf(weight), midX - stringWidth / 2, midY - g2d.getFontMetrics().getAscent() / 2);
        g2d.setTransform(defaultTransform);

    }

    /**
     * Dessine tous les nœuds du graphe (cercle, bordure, ombre, étiquette).
     *
     * @param g2d contexte graphique 2D
     */
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
            String label = String.valueOf(n.getId());
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = x - fm.stringWidth(label) / 2;
            int labelY = y + fm.getAscent() / 3;
            g2d.drawString(label, labelX, labelY);
        }
    }

    /**
     * Randomise les positions des nœuds affichés, puis redessine le graphe.
     */
    public void randomizePositions() {
        if (graph != null && graph.getNodes() != null) {
            initialiserPositions();
            repaint();
        }
    }

    /**
     * Dessine une flèche qui part du bord du cercle source et arrive au bord du cercle cible.
     *
     * @param g2d    contexte graphique 2D
     * @param source centre du nœud source
     * @param target centre du nœud cible
     */
    private void drawArrow(Graphics2D g2d, Point source, Point target) {
        // Calculer l'angle entre les deux centres
        double angle = Math.atan2(target.y - source.y, target.x - source.x);

        // Calculer le point de départ (bord du cercle source)
        Point start = getPointOnCircle(source, angle, RADIUS);

        // Calculer le point d'arrivée (bord du cercle cible)
        // Pour le point d'arrivée, on utilise l'angle opposé (source -> target)
        Point end = getPointOnCircle(target, angle + Math.PI, RADIUS);

        // Dessiner la ligne du bord du cercle source au bord du cercle cible
        g2d.setColor(Color.BLACK);
        g2d.drawLine(start.x, start.y, end.x, end.y);

        // Dessiner la tête de flèche au point d'arrivée
        drawArrowHead(g2d, end, angle);
    }

    /**
     * Calcule un point sur le cercle à un angle donné.
     *
     * @param center centre du cercle
     * @param angle  angle en radians
     * @param radius rayon du cercle
     * @return point sur le cercle
     */
    private Point getPointOnCircle(Point center, double angle, int radius) {
        int x = center.x + (int) (radius * Math.cos(angle));
        int y = center.y + (int) (radius * Math.sin(angle));
        return new Point(x, y);
    }

    /**
     * Dessine la tête de flèche orientée à partir de la pointe et de la direction.
     *
     * @param g2d            contexte graphique 2D
     * @param tip            point de la pointe de la flèche
     * @param directionAngle angle de la direction de l'arête en radians
     */
    private void drawArrowHead(Graphics2D g2d, Point tip, double directionAngle) {
        int arrowSize = ARROW_SIZE;
        int arrowAngle = 25; // degrés

        // Calculer les deux points de la base de la flèche
        double angle1 = directionAngle + Math.toRadians(180 - arrowAngle);
        double angle2 = directionAngle + Math.toRadians(180 + arrowAngle);

        Point base1 = new Point(
                tip.x + (int) (arrowSize * Math.cos(angle1)),
                tip.y + (int) (arrowSize * Math.sin(angle1))
        );

        Point base2 = new Point(
                tip.x + (int) (arrowSize * Math.cos(angle2)),
                tip.y + (int) (arrowSize * Math.sin(angle2))
        );

        // Créer le triangle de la flèche
        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(tip.x, tip.y);
        arrowHead.lineTo(base1.x, base1.y);
        arrowHead.lineTo(base2.x, base2.y);
        arrowHead.closePath();

        // Remplir la flèche
        g2d.setColor(Color.RED);
        g2d.fill(arrowHead);

        // Optionnel : ajouter un contour noir
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(arrowHead);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Sélectionne un nœud lorsqu'on presse la souris à proximité de lui.
     *
     * @param e événement de souris
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Point clickedPoint = e.getPoint();
        System.out.println("clickedPoint: " + clickedPoint);
        for (Node n : graph.getNodes()) {
            Point nodePoint = new Point(n.getGraphicsX(), n.getGraphicsY());
            if (clickedPoint.distance(nodePoint) < RADIUS) {
                selectedNode = n;
            }
        }
    }

    /**
     * Libère le nœud sélectionné lorsque le bouton de la souris est relâché.
     *
     * @param e événement de souris
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        selectedNode = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Déplace le nœud sélectionné en suivant la position de la souris,
     * puis redessine le graphe.
     *
     * @param e événement de souris
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedNode != null) {
            Point dragPoint = e.getPoint();
            selectedNode.setGraphicsX(dragPoint.x);
            selectedNode.setGraphicsY(dragPoint.y);
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
