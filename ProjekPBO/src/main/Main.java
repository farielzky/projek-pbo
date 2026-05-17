package main;

import gui.JendelaUtama;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JendelaUtama().setVisible(true);
        });
    }
}