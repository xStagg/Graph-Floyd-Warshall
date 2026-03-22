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
 * et le déplacement interactif des nœuds à la souris.
 * <p>
 * Les nœuds sont représentés par des cercles bleus étiquetés avec leur identifiant.
 * Les arêtes sont dessinées avec des flèches ; les arêtes bidirectionnelles sont
 * décalées perpendiculairement pour rester lisibles.
 * </p>
 */
public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener {

    private Graph graph;
    private static final int RADIUS = 30;
    private static final int ARROW_SIZE = 20;
    private final Random random = new Random();
    private Node selectedNode;

    /**
     * Crée un panneau d'affichage pour le graphe donné.
     * Les positions des nœuds sont initialisées aléatoirement si le graphe est non nul.
     *
     * @param graph graphe à afficher (peut être {@code null})
     */
    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);

        if (graph != null && graph.getNodes() != null) {
            initialiserPositions();
        }

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Remplace le graphe affiché et déclenche un repaint.
     * Si la propriété {@code randomPosition} du graphe est activée,
     * les positions des nœuds sont réinitialisées aléatoirement.
     *
     * @param graph nouveau graphe à afficher (peut être {@code null})
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        if (graph != null && graph.getNodes() != null && graph.isRandomPosition()) {
            initialiserPositions();
        }
        repaint();
    }

    /**
     * Initialise aléatoirement les positions graphiques (X, Y) de tous les nœuds
     * du graphe dans une zone de 700×500 pixels décalée de 50.
     */
    private void initialiserPositions() {
        if (graph == null || graph.getNodes() == null) return;
        for (Node n : graph.getNodes()) {
            n.setGraphicsX(random.nextInt(700) + 50);
            n.setGraphicsY(random.nextInt(500) + 50);
        }
    }

    /**
     * {@inheritDoc}
     * Active l'antialiasing et délègue le dessin à {@link #drawGraph(Graphics2D)}.
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

        drawGraph(g2d);
    }

    /**
     * Dessine le graphe complet (arêtes puis nœuds) ou affiche un message
     * si le graphe est vide ou nul.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawGraph(Graphics2D g2d) {
        if (graph == null || graph.getNodes() == null || graph.getNodes().isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 20));
            String msg = "Aucun graphe à afficher";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
            return;
        }

        drawEdges(g2d);
        drawNodes(g2d);
    }

    /**
     * Dessine toutes les arêtes du graphe avec leurs flèches directionnelles et poids.
     * Les boucles (arêtes d'un nœud vers lui-même) sont dessinées comme des courbes.
     * Les arêtes bidirectionnelles sont décalées latéralement.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawEdges(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2));

        for (Edge e : graph.getEdges()) {
            Node srcNode = e.getSource();
            Node trgNode = e.getTarget();

            if (srcNode == trgNode) {
                drawSelfLoop(g2d, srcNode, e.getWeight());
                continue;
            }

            Point source = new Point(srcNode.getGraphicsX(), srcNode.getGraphicsY());
            Point target = new Point(trgNode.getGraphicsX(), trgNode.getGraphicsY());
            if (source.x <= 0 || source.y <= 0 || target.x <= 0 || target.y <= 0) continue;

            boolean hasBackEdge = graph.areConnected(trgNode, srcNode);

            Node a = (srcNode.getId() < trgNode.getId()) ? srcNode : trgNode;
            Node b = (srcNode.getId() < trgNode.getId()) ? trgNode : srcNode;
            double dxBase = b.getGraphicsX() - a.getGraphicsX();
            double dyBase = b.getGraphicsY() - a.getGraphicsY();
            double lenBase = Math.sqrt(dxBase * dxBase + dyBase * dyBase);
            double nxBase = -dyBase / lenBase;
            double nyBase = dxBase / lenBase;

            int side = 0;
            if (hasBackEdge) {
                boolean fromAtoB = (srcNode == a);
                side = fromAtoB ? +1 : -1;
            }

            drawArrow(g2d, source, target, hasBackEdge, side, nxBase, nyBase);
            drawWeight(g2d, source, target, e.getWeight(), hasBackEdge, side, nxBase, nyBase);
        }
    }

    /**
     * Dessine une courbe quadratique entre deux points de contrôle.
     *
     * @param g2d contexte graphique 2D
     * @param x1  coordonnée X du point de départ
     * @param y1  coordonnée Y du point de départ
     * @param cx  coordonnée X du point de contrôle
     * @param cy  coordonnée Y du point de contrôle
     * @param x2  coordonnée X du point d'arrivée
     * @param y2  coordonnée Y du point d'arrivée
     */
    private void drawQuadCurve(Graphics2D g2d,
                               double x1, double y1,
                               double cx, double cy,
                               double x2, double y2) {

        Path2D path = new Path2D.Double();
        path.moveTo(x1, y1);
        path.quadTo(cx, cy, x2, y2);
        g2d.draw(path);
    }

    /**
     * Dessine une boucle (arête réflexive) sur le nœud donné,
     * représentée par une courbe quadratique au-dessus du cercle du nœud,
     * avec une flèche et le poids de l'arête.
     *
     * @param g2d    contexte graphique 2D
     * @param node   nœud sur lequel dessiner la boucle
     * @param weight poids de l'arête réflexive
     */
    private void drawSelfLoop(Graphics2D g2d, Node node, double weight) {
        int x = node.getGraphicsX();
        int y = node.getGraphicsY();
        double r = RADIUS;
        double controlHeight = r * 3;

        double leftAngle = Math.toRadians(210);
        double rightAngle = Math.toRadians(-30);

        double ax = x + r * Math.cos(leftAngle);
        double ay = y + r * Math.sin(leftAngle);

        double bx = x + r * Math.cos(rightAngle);
        double by = y + r * Math.sin(rightAngle);

        double cx = x;
        double cy = y - controlHeight;

        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3));
        drawQuadCurve(g2d, ax, ay, cx, cy, bx, by);
        g2d.setStroke(oldStroke);

        double dirX = bx - cx;
        double dirY = by - cy;
        double angleDir = Math.atan2(dirY, dirX);
        drawArrowHead(g2d, new Point((int) bx, (int) by), angleDir);

        g2d.setColor(Color.BLACK);
        String txt = String.valueOf(weight);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (int) (x - fm.stringWidth(txt) / 2);
        int textY = (int) (cy + 10);
        g2d.drawString(txt, textX, textY);
    }

    /**
     * Dessine le poids d'une arête, centré sur le segment et correctement orienté.
     * Applique un décalage perpendiculaire si l'arête est bidirectionnelle.
     *
     * @param g2d           contexte graphique 2D
     * @param source        point source du segment
     * @param target        point cible du segment
     * @param weight        poids à afficher
     * @param bidirectional {@code true} si l'arête inverse existe
     * @param side          côté de décalage (+1 ou -1) pour les arêtes bidirectionnelles
     * @param nxBase        composante X de la normale au segment de référence
     * @param nyBase        composante Y de la normale au segment de référence
     */
    private void drawWeight(Graphics2D g2d,
                            Point source, Point target,
                            double weight,
                            boolean bidirectional, int side,
                            double nxBase, double nyBase) {

        String txt = String.valueOf(weight);
        int stringWidth = g2d.getFontMetrics().stringWidth(txt);

        double offsetX = 0;
        double offsetY = 0;
        if (bidirectional && side != 0) {
            int offset = 16;
            offsetX = nxBase * offset * side;
            offsetY = nyBase * offset * side;
        }

        double sx = source.x + offsetX;
        double sy = source.y + offsetY;
        double tx = target.x + offsetX;
        double ty = target.y + offsetY;

        double angle = Math.atan2(ty - sy, tx - sx);
        if (sx > tx) {
            angle += Math.PI;
        }

        float midX = (float) ((tx + sx) / 2.0);
        float midY = (float) ((ty + sy) / 2.0);

        AffineTransform defaultTransform = g2d.getTransform();
        g2d.rotate(angle, midX, midY);
        g2d.drawString(txt,
                midX - stringWidth / 2f,
                midY - g2d.getFontMetrics().getAscent() / 2f);
        g2d.setTransform(defaultTransform);
    }

    /**
     * Dessine tous les nœuds du graphe sous forme de cercles bleus
     * avec leur identifiant affiché en blanc au centre.
     *
     * @param g2d contexte graphique 2D
     */
    private void drawNodes(Graphics2D g2d) {
        for (Node n : graph.getNodes()) {
            int x = n.getGraphicsX();
            int y = n.getGraphicsY();
            if (x <= 0 || y <= 0) continue;

            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(x - RADIUS + 3, y - RADIUS + 3, RADIUS * 2, RADIUS * 2);

            g2d.setColor(new Color(70, 130, 180));
            g2d.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

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
     * Réinitialise aléatoirement les positions de tous les nœuds du graphe
     * et déclenche un repaint.
     */
    public void randomizePositions() {
        if (graph != null && graph.getNodes() != null) {
            initialiserPositions();
            repaint();
        }
    }

    /**
     * Dessine une flèche entre deux points (segment droit), avec un décalage
     * perpendiculaire optionnel pour les arêtes bidirectionnelles.
     *
     * @param g2d           contexte graphique 2D
     * @param source        point de départ de la flèche
     * @param target        point d'arrivée de la flèche
     * @param bidirectional {@code true} si l'arête inverse existe
     * @param side          côté de décalage (+1 ou -1)
     * @param nxBase        composante X de la normale au segment de référence
     * @param nyBase        composante Y de la normale au segment de référence
     */
    private void drawArrow(Graphics2D g2d, Point source, Point target,
                           boolean bidirectional, int side,
                           double nxBase, double nyBase) {

        double offsetX = 0;
        double offsetY = 0;
        if (bidirectional && side != 0) {
            int offset = 10;
            offsetX = nxBase * offset * side;
            offsetY = nyBase * offset * side;
        }

        double sx = source.x + offsetX;
        double sy = source.y + offsetY;
        double tx = target.x + offsetX;
        double ty = target.y + offsetY;

        double angle = Math.atan2(ty - sy, tx - sx);

        Point start = getPointOnCircle(new Point((int) sx, (int) sy), angle, RADIUS);
        Point end = getPointOnCircle(new Point((int) tx, (int) ty), angle + Math.PI, RADIUS);

        g2d.setColor(Color.BLACK);
        g2d.drawLine(start.x, start.y, end.x, end.y);
        drawArrowHead(g2d, end, angle);
    }

    /**
     * Calcule le point situé sur le bord d'un cercle de centre donné,
     * dans la direction spécifiée par {@code angle}.
     *
     * @param center centre du cercle
     * @param angle  angle de direction en radians
     * @param radius rayon du cercle
     * @return point sur le bord du cercle
     */
    private Point getPointOnCircle(Point center, double angle, int radius) {
        int x = center.x + (int) (radius * Math.cos(angle));
        int y = center.y + (int) (radius * Math.sin(angle));
        return new Point(x, y);
    }

    /**
     * Dessine une tête de flèche triangulaire remplie au point {@code tip},
     * orientée selon {@code directionAngle}.
     *
     * @param g2d            contexte graphique 2D
     * @param tip            point de la pointe de la flèche
     * @param directionAngle angle de direction de la flèche en radians
     */
    private void drawArrowHead(Graphics2D g2d, Point tip, double directionAngle) {
        int arrowSize = ARROW_SIZE;
        int arrowAngle = 25;

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

        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(tip.x, tip.y);
        arrowHead.lineTo(base1.x, base1.y);
        arrowHead.lineTo(base2.x, base2.y);
        arrowHead.closePath();

        g2d.setColor(Color.RED);
        g2d.fill(arrowHead);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(arrowHead);
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(MouseEvent e) {}

    /**
     * {@inheritDoc}
     * Sélectionne le nœud sur lequel l'utilisateur a cliqué (dans un rayon {@code RADIUS}).
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Point clickedPoint = e.getPoint();
        for (Node n : graph.getNodes()) {
            Point nodePoint = new Point(n.getGraphicsX(), n.getGraphicsY());
            if (clickedPoint.distance(nodePoint) < RADIUS) {
                selectedNode = n;
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     * Désélectionne le nœud en cours de déplacement.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        selectedNode = null;
    }

    /** {@inheritDoc} */
    @Override public void mouseEntered(MouseEvent e) {}

    /** {@inheritDoc} */
    @Override public void mouseExited(MouseEvent e) {}

    /**
     * {@inheritDoc}
     * Déplace le nœud sélectionné vers la position courante de la souris et repeint.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedNode != null) {
            Point p = e.getPoint();
            selectedNode.setGraphicsX(p.x);
            selectedNode.setGraphicsY(p.y);
            repaint();
        }
    }

    /** {@inheritDoc} */
    @Override public void mouseMoved(MouseEvent e) {}
}