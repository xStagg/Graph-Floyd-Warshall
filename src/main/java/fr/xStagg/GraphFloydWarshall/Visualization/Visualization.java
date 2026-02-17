package fr.xStagg.GraphFloydWarshall.Visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import fr.xStagg.GraphFloydWarshall.Graph.Graph;

public class Visualization extends JFrame {

    private GraphPanel graphPanel;

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