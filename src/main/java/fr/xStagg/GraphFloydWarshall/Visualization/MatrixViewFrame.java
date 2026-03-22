package fr.xStagg.GraphFloydWarshall.Visualization;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Fenêtre de visualisation des matrices L^k et P^k produites par l'algorithme
 * de Floyd-Warshall, avec navigation pas à pas via un curseur ({@link JSlider}).
 * <p>
 * Structure du tableau {@code result} :
 * <ul>
 *   <li>{@code result[k][0]} = L^k (matrice des distances à l'étape k)</li>
 *   <li>{@code result[k][1]} = P^k (matrice des prédécesseurs à l'étape k)</li>
 *   <li>{@code updated[k][i][j] = true} si la cellule (i,j) a été modifiée à l'étape k</li>
 * </ul>
 * </p>
 */
public class MatrixViewFrame extends JFrame {

    private final Graph graph;
    private final int[][][][] result;
    private final boolean[][][] updated;
    private final boolean hasNegativeCycle;

    private final MatrixPanel matrixPanel;
    private final JSlider stepSlider;
    private final JToggleButton toggleL;

    private final JLabel stateLabel;
    private final JLabel cycleLabel;
    private final JPanel pathsPanel;
    private final JTextArea pathsArea;

    /**
     * Crée la fenêtre de visualisation Floyd-Warshall.
     *
     * @param graph            graphe analysé
     * @param result           tableau 4D des matrices L^k et P^k pour chaque étape k
     * @param updated          tableau 3D indiquant les cellules modifiées à chaque étape
     * @param hasNegativeCycle {@code true} si le graphe contient un circuit absorbant
     */
    public MatrixViewFrame(Graph graph,
                           int[][][][] result,
                           boolean[][][] updated,
                           boolean hasNegativeCycle) {

        super("Floyd-Warshall - Matrices");

        this.graph = graph;
        this.result = result;
        this.updated = updated;
        this.hasNegativeCycle = hasNegativeCycle;

        this.matrixPanel = new MatrixPanel(graph);

        int maxStep = result.length - 1;
        this.stepSlider = new JSlider(0, maxStep, 0);
        stepSlider.setPaintTicks(true);
        stepSlider.setPaintLabels(true);
        stepSlider.setMajorTickSpacing(1);

        this.toggleL = new JToggleButton("L");
        toggleL.setSelected(true);

        JPanel controlPanel = createControlPanel(maxStep);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setPreferredSize(new Dimension(350, 0));

        JPanel statePanel = new JPanel(new BorderLayout());
        statePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "État de l'algorithme",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        stateLabel = new JLabel();
        stateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statePanel.add(stateLabel, BorderLayout.CENTER);

        JPanel cyclePanel = new JPanel(new BorderLayout());
        cyclePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Détection de circuit",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        cycleLabel = new JLabel();
        cycleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cyclePanel.add(cycleLabel, BorderLayout.CENTER);

        pathsPanel = new JPanel(new BorderLayout());
        pathsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Plus courts chemins",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        pathsArea = new JTextArea();
        pathsArea.setEditable(false);
        pathsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane pathsScroll = new JScrollPane(pathsArea);
        pathsPanel.add(pathsScroll, BorderLayout.CENTER);
        pathsPanel.setVisible(false);

        infoPanel.add(statePanel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(cyclePanel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(pathsPanel);
        infoPanel.add(Box.createVerticalGlue());

        JLabel statusBar = new JLabel(" ");
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(matrixPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);

        stepSlider.addChangeListener(e -> {
            refreshMatrix();
            statusBar.setText("Étape k = " + stepSlider.getValue());
        });

        toggleL.addActionListener(e -> {
            toggleL.setText(toggleL.isSelected() ? "L" : "P");
            refreshMatrix();
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        refreshMatrix();
    }

    /**
     * Crée le panneau de contrôle en haut de la fenêtre, contenant
     * la navigation par étapes (boutons Précédent/Suivant + curseur)
     * et le choix de matrice (L ou P).
     *
     * @param maxStep nombre maximum d'étapes de l'algorithme
     * @return panneau de contrôle configuré
     */
    private JPanel createControlPanel(int maxStep) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel stepPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stepPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));

        JButton prevButton = new JButton("◀ Précédent");
        JButton nextButton = new JButton("Suivant ▶");
        JLabel stepLabel = new JLabel("k = 0 / " + maxStep);

        prevButton.addActionListener(e -> {
            int k = stepSlider.getValue();
            if (k > 0) stepSlider.setValue(k - 1);
        });
        nextButton.addActionListener(e -> {
            int k = stepSlider.getValue();
            if (k < maxStep) stepSlider.setValue(k + 1);
        });

        stepSlider.addChangeListener(e ->
                stepLabel.setText("k = " + stepSlider.getValue() + " / " + maxStep));

        stepPanel.add(prevButton);
        stepPanel.add(stepSlider);
        stepPanel.add(nextButton);
        stepPanel.add(stepLabel);

        JPanel matrixChoicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        matrixChoicePanel.setBorder(BorderFactory.createTitledBorder("Matrice"));

        ButtonGroup group = new ButtonGroup();
        JRadioButton radioL = new JRadioButton("L (Distances)", true);
        JRadioButton radioP = new JRadioButton("P (Prédécesseurs)", false);
        group.add(radioL);
        group.add(radioP);

        radioL.addActionListener(e -> {
            if (!toggleL.isSelected()) toggleL.setSelected(true);
            refreshMatrix();
        });
        radioP.addActionListener(e -> {
            if (toggleL.isSelected()) toggleL.setSelected(false);
            refreshMatrix();
        });

        matrixChoicePanel.add(radioL);
        matrixChoicePanel.add(radioP);

        panel.add(stepPanel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(matrixChoicePanel);

        return panel;
    }

    /**
     * Met à jour l'affichage de la matrice, le titre de la fenêtre
     * et les panneaux d'information selon l'étape et la matrice courantes.
     */
    private void refreshMatrix() {
        int k = stepSlider.getValue();
        int which = toggleL.isSelected() ? 0 : 1;
        int n = result.length - 1;

        int[][] mat = result[k][which];
        boolean[][] hl = (updated != null && k < updated.length) ? updated[k] : null;
        matrixPanel.setMatrix(mat, hl);

        String type = (which == 0) ? "L (Distances)" : "P (Prédécesseurs)";
        setTitle("Floyd-Warshall - " + type + " - Étape " + k + "/" + n);

        updateStateLabel(k, n);
        updateCycleLabel(k, n);
        updatePathsPanel(k, n);
    }

    /**
     * Met à jour le label décrivant l'état de l'algorithme à l'étape k.
     *
     * @param k étape courante
     * @param n nombre total d'étapes
     */
    private void updateStateLabel(int k, int n) {
        if (k == 0) {
            stateLabel.setText("<html>Étape initiale (k = 0)<br>Matrices initialisées à partir du graphe (arêtes directes).</html>");
            stateLabel.setForeground(Color.BLACK);
        } else if (k == n) {
            stateLabel.setText("<html>Étape finale (k = " + n + ")<br>Toutes les distances minimales ont été calculées.</html>");
            stateLabel.setForeground(new Color(0, 100, 0));
        } else {
            int nodeId = graph.getNodes().get(k - 1).getId();
            stateLabel.setText("<html>Étape " + k + " / " + n + "<br>Autorisation du sommet " + nodeId + " comme sommet intermédiaire.</html>");
            stateLabel.setForeground(Color.BLACK);
        }
    }

    /**
     * Met à jour le label de détection de circuit absorbant selon l'étape k.
     *
     * @param k étape courante
     * @param n nombre total d'étapes
     */
    private void updateCycleLabel(int k, int n) {
        if (k < n) {
            cycleLabel.setText("Analyse des circuits absorbants en cours (étape " + k + " / " + n + ")...");
            cycleLabel.setForeground(Color.GRAY);
        } else {
            if (hasNegativeCycle) {
                cycleLabel.setText("<html>⚠ Circuit absorbant détecté<br>Le graphe contient au moins un cycle de poids négatif.</html>");
                cycleLabel.setForeground(Color.RED);
            } else {
                cycleLabel.setText("<html>✓ Aucun circuit absorbant détecté<br>Les plus courts chemins sont bien définis.</html>");
                cycleLabel.setForeground(new Color(0, 128, 0));
            }
        }
    }

    /**
     * Affiche le panneau des plus courts chemins uniquement à l'étape finale
     * et en l'absence de circuit absorbant.
     *
     * @param k étape courante
     * @param n nombre total d'étapes
     */
    private void updatePathsPanel(int k, int n) {
        if (k == n && !hasNegativeCycle) {
            pathsPanel.setVisible(true);

            List<String> paths = graph.getAllShortestPathsDescriptions();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Total : %d chemins trouvés\n", paths.size()));
            sb.append("────────────────────────────────────────\n\n");
            for (String s : paths) {
                sb.append(s).append("\n");
            }
            pathsArea.setText(sb.toString());
            pathsArea.setCaretPosition(0);
        } else {
            pathsPanel.setVisible(false);
            pathsArea.setText("");
        }
    }

    /**
     * Méthode utilitaire pour ouvrir la fenêtre de visualisation Floyd-Warshall
     * dans l'Event Dispatch Thread de Swing.
     *
     * @param graph            graphe analysé
     * @param result           tableau 4D des matrices L^k et P^k
     * @param updated          tableau 3D de surlignage des cellules modifiées
     * @param hasNegativeCycle {@code true} si le graphe contient un circuit absorbant
     */
    public static void showMatrixView(Graph graph,
                                      int[][][][] result,
                                      boolean[][][] updated,
                                      boolean hasNegativeCycle) {
        SwingUtilities.invokeLater(() -> {
            MatrixViewFrame frame =
                    new MatrixViewFrame(graph, result, updated, hasNegativeCycle);
            frame.setVisible(true);
        });
    }
}