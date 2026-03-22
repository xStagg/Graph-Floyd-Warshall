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
 * Fenêtre principale de l'application de visualisation de graphes Floyd-Warshall.
 * <p>
 * Permet d'afficher soit le graphe (via {@link GraphPanel}), soit sa matrice d'adjacence
 * (via {@link MatrixPanel}), et de charger/sauvegarder le graphe depuis/vers un fichier JSON.
 * La barre de menus propose les actions suivantes :
 * <ul>
 *   <li><b>File &gt; Open</b> : charge un graphe depuis un fichier JSON</li>
 *   <li><b>File &gt; Save</b> : sauvegarde le graphe actif</li>
 *   <li><b>View &gt; Graph View</b> : affiche le graphe visuellement</li>
 *   <li><b>View &gt; Matrix View</b> : affiche la matrice d'adjacence</li>
 *   <li><b>Maths &gt; Show Degrees</b> : affiche les degrés autour de la matrice</li>
 *   <li><b>Maths &gt; Apply Floyd Warshall</b> : ouvre {@link MatrixViewFrame}</li>
 * </ul>
 * </p>
 */
public class Visualization extends JFrame {

    private GraphPanel graphPanel;
    private MatrixPanel matrixPanel;
    private Graph activeGraph;
    private JPanel currentPanel;

    /**
     * Crée et configure la fenêtre principale de l'application.
     * Initialise la barre de menus, les gestionnaires d'événements
     * et affiche par défaut le panneau de matrice.
     */
    public Visualization() {
        super("Floyd Warshall");

        this.activeGraph = null;

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

        URL iconURL = Visualization.class.getResource("/knowledge-graph.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            setIconImage(icon.getImage());
        }

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openItem.addActionListener(e -> {
            File workingDirectory = new File(System.getProperty("user.dir"));
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(workingDirectory);
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

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.addActionListener(e -> {
            JSONSaver.saveGraph(activeGraph);
        });

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem graphViewItem = new JMenuItem("Graph View");
        graphViewItem.setMnemonic(KeyEvent.VK_V);
        graphViewItem.addActionListener(e -> {
            if (graphPanel == null) {
                JLabel loadingLabel = new JLabel("Chargement du graphe...", SwingConstants.CENTER);
                loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
                setContentPane(loadingLabel);
                revalidate();

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

        JMenuItem matrixViewItem = new JMenuItem("Matrix View");
        matrixViewItem.setMnemonic(KeyEvent.VK_T);
        matrixViewItem.addActionListener(e -> {
            if (matrixPanel == null) {
                JLabel loadingLabel = new JLabel("Chargement de la matrice...", SwingConstants.CENTER);
                loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
                setContentPane(loadingLabel);
                revalidate();

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

        JMenu mathsMenu = new JMenu("Maths");
        mathsMenu.setMnemonic(KeyEvent.VK_M);

        JMenuItem degreesMathsItem = new JMenuItem("show degrees");
        degreesMathsItem.setMnemonic(KeyEvent.VK_D);
        degreesMathsItem.addActionListener(e -> {
            matrixPanel.showDegrees();
            matrixPanel.repaint();
            revalidate();
        });

        JMenuItem floydMathsItem = new JMenuItem("Apply Floyd Warshall");
        floydMathsItem.setMnemonic(KeyEvent.VK_F);
        floydMathsItem.addActionListener(e -> {
            MatrixViewFrame.showMatrixView(activeGraph, activeGraph.getFloydResult(), activeGraph.getFloydUpdated(), activeGraph.hasNegativeCycle());
        });

        mathsMenu.add(floydMathsItem);
        mathsMenu.add(degreesMathsItem);

        viewMenu.add(matrixViewItem);
        viewMenu.add(graphViewItem);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(mathsMenu);

        matrixPanel = new MatrixPanel(activeGraph);
        setContentPane(matrixPanel);
        currentPanel = matrixPanel;
    }

    /**
     * Affiche la fenêtre de visualisation centrée à l'écran avec une taille de 800×600.
     * Cette méthode est thread-safe (utilise {@link SwingUtilities#invokeLater}).
     */
    public void visualize() {
        SwingUtilities.invokeLater(() -> {
            setSize(800, 600);
            setLocationRelativeTo(null);
            setVisible(true);
        });
    }

    /**
     * Retourne le graphe actuellement actif dans la fenêtre.
     *
     * @return graphe actif, ou {@code null} si aucun graphe n'a été chargé
     */
    public Graph getActiveGraph() {
        return activeGraph;
    }

    /**
     * Remplace le graphe actif et met à jour les panneaux d'affichage si initialisés.
     *
     * @param graph nouveau graphe à afficher
     */
    public void setActiveGraph(Graph graph) {
        activeGraph = graph;
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