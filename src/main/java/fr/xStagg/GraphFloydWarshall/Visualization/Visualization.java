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

public class Visualization extends JFrame {

    private GraphPanel graphPanel;
    private String activeGraphPath = "";

    public Visualization(Graph graph) {
        super("Floyd Warshall");

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
                getGraphPanel().setGraph(JSONLoader.loadGraph(pathFile, LoadingMethod.FOLDERS));
            }
        });

        // === file menu -> saver ===
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.addActionListener(e -> {
            JSONSaver.saveGraph(getGraphPanel().getGraph());
        });


        // === edit menu ===
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        // === edit menu -> to matrix ===
        JMenuItem toMatrixItem = new JMenuItem("To Matrix");
        toMatrixItem.setMnemonic(KeyEvent.VK_T);
        toMatrixItem.addActionListener(e -> {});

        editMenu.add(toMatrixItem);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);


        // Créer le panneau de dessin AVEC le graphe
        graphPanel = new GraphPanel(graph);
        setContentPane(graphPanel);
    }

    public void visualize() {
        SwingUtilities.invokeLater(() -> {
            setSize(800, 600);
            setLocationRelativeTo(null); // Centrer la fenêtre
            setVisible(true);
        });
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}