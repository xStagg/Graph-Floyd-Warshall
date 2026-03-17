package fr.xStagg.GraphFloydWarshall.Visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;
import fr.xStagg.GraphFloydWarshall.Utils.JSONLoader;
import fr.xStagg.GraphFloydWarshall.Utils.JSONSaver;
import fr.xStagg.GraphFloydWarshall.Utils.LoadingMethod;

/**
 * Fenêtre principale de visualisation d'un graphe, permettant d'afficher
 * soit le graphe, soit sa matrice d'adjacence, et de charger/sauvegarder
 * le graphe depuis/vers un fichier JSON.
 */
public class Visualization extends JFrame {

    private GraphPanel graphPanel;
    private MatrixPanel matrixPanel;
    private Graph activeGraph;
    private JPanel currentPanel;

    /**
     * Crée une fenêtre de visualisation pour le graphe donné.
     *
     * @param graph graphe à visualiser
     */
    public Visualization(Graph graph) {
        super("Floyd Warshall");

        this.activeGraph = graph;

        // Gestionnaire de fermeture
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        Visualization.this,
                        "Voulez-vous vraiment quitter ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Charger l'icône
        URL iconURL = Visualization.class.getResource("/knowledge-graph.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            setIconImage(icon.getImage());
        }

        // === menu bar ===
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // === file menu ===
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // === file menu -> opener ===
        JMenuItem openItem = new JMenuItem("Open");
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String pathFile = chooser.getSelectedFile().getPath();
                activeGraph = JSONLoader.loadGraph(pathFile, LoadingMethod.FOLDERS);
                if (getGraphPanel() != null) {
                    getGraphPanel().setGraph(activeGraph);
                }
                if (getMatrixPanel() != null) {
                    getMatrixPanel().setGraph(activeGraph);
                }
            }
        });

        // === file menu -> saver ===
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.addActionListener(e -> {
            JSONSaver.saveGraph(activeGraph);
        });

        // === view menu ===
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_E);

        // === view menu -> graph view ===
        JMenuItem graphViewItem = new JMenuItem("Graph View");
        graphViewItem.setMnemonic(KeyEvent.VK_V);
        graphViewItem.addActionListener(e -> {
            // Vérifier si le panel est déjà initialisé
            if (graphPanel == null) {
                // Afficher un message de chargement
                JLabel loadingLabel = new JLabel("Chargement du graphe...", SwingConstants.CENTER);
                loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
                setContentPane(loadingLabel);
                revalidate();

                // Charger en arrière-plan
                new Thread(() -> {
                    graphPanel = new GraphPanel(activeGraph);
                    SwingUtilities.invokeLater(() -> {
                        setContentPane(graphPanel);
                        revalidate();
                        repaint();
                    });
                }).start();
            } else {
                setContentPane(graphPanel);
                graphPanel.repaint();
                revalidate();
            }
        });

        // === view menu -> matrix view ===
        JMenuItem matrixViewItem = new JMenuItem("Matrix View");
        matrixViewItem.setMnemonic(KeyEvent.VK_T);
        matrixViewItem.addActionListener(e -> {
            // Vérifier si le panel est déjà initialisé
            if (matrixPanel == null) {
                // Afficher un message de chargement
                JLabel loadingLabel = new JLabel("Chargement de la matrice...", SwingConstants.CENTER);
                loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
                setContentPane(loadingLabel);
                revalidate();

                // Charger en arrière-plan
                new Thread(() -> {
                    matrixPanel = new MatrixPanel(activeGraph);
                    SwingUtilities.invokeLater(() -> {
                        setContentPane(matrixPanel);
                        revalidate();
                        repaint();
                    });
                }).start();
            } else {
                setContentPane(matrixPanel);
                matrixPanel.repaint();
                revalidate();
            }
        });

        // === maths menu ===
        JMenu mathsMenu = new JMenu("Maths");
        mathsMenu.setMnemonic(KeyEvent.VK_M);

        // === maths menu -> show degrees ===
        JMenuItem degreesMathsItem = new JMenuItem("show degrees");
        degreesMathsItem.setMnemonic(KeyEvent.VK_D);
        degreesMathsItem.addActionListener(e -> {
            matrixPanel.showDegrees();
            matrixPanel.repaint();
            revalidate();
        });

        // === maths menu -> apply floyd-warshall
        JMenuItem floydMathsItem = new JMenuItem("Apply Floyd Warshall");
        floydMathsItem.setMnemonic(KeyEvent.VK_F);
        floydMathsItem.addActionListener(e -> {
            // TODO: implémenter l'appel à l'algorithme de Floyd-Warshall
        });

        mathsMenu.add(degreesMathsItem);

        viewMenu.add(matrixViewItem);
        viewMenu.add(graphViewItem);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(mathsMenu);

        // Ne créer qu'un seul panel au début (celui par défaut)
        matrixPanel = new MatrixPanel(activeGraph);
        setContentPane(matrixPanel);
        currentPanel = matrixPanel;
    }

    /**
     * Affiche la fenêtre de visualisation avec une taille par défaut.
     */
    public void visualize() {
        SwingUtilities.invokeLater(() -> {
            setSize(800, 600);
            setLocationRelativeTo(null); // Centrer la fenêtre
            setVisible(true);
        });
    }

    /**
     * Retourne le graphe actuellement affiché.
     *
     * @return graphe actif
     */
    public Graph getActiveGraph() {
        return activeGraph;
    }

    /**
     * Modifie le graphe actif et met à jour les panneaux associés.
     *
     * @param graph nouveau graphe
     */
    public void setActiveGraph(Graph graph) {
        activeGraph = graph;
        // Mettre à jour les panels s'ils existent
        if (graphPanel != null) graphPanel.setGraph(graph);
        if (matrixPanel != null) matrixPanel.setGraph(graph);
    }

    /**
     * Retourne le panneau d'affichage du graphe.
     *
     * @return panneau du graphe ou {@code null} s'il n'a pas encore été créé
     */
    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    /**
     * Retourne le panneau d'affichage de la matrice.
     *
     * @return panneau de matrice ou {@code null} s'il n'a pas encore été créé
     */
    public MatrixPanel getMatrixPanel() {
        return matrixPanel;
    }
}
