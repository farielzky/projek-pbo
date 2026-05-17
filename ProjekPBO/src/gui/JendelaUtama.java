package gui;

import util.KalkulasiThread;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class JendelaUtama extends JFrame {

    public JendelaUtama() {
        setTitle("Geometry Compute Engine");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Layang-Layang", buatPanel("LAYANG"));
        tabs.addTab("Prisma Layang",  buatPanel("PRISMA"));
        tabs.addTab("Limas Layang",   buatPanel("LIMAS"));

        add(tabs);
    }

    private JPanel buatPanel(String jenis) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Kolom tabel: No, D1, D2, T (tinggi), Hasil
        String[] kolom = {"No", "D1", "D2", "Tinggi (t)", "Hasil"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabel = new JTable(model);
        tabel.getTableHeader().setReorderingAllowed(false);

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);

        // Baris bawah
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label        = new JLabel("Jumlah data:");
        JTextField input    = new JTextField("1000", 8);
        JButton btnJalankan = new JButton("Jalankan");
        JButton btnReset    = new JButton("Reset");

        bawah.add(label);
        bawah.add(input);
        bawah.add(btnJalankan);
        bawah.add(btnReset);
        panel.add(bawah, BorderLayout.SOUTH);

        // Aksi Jalankan
        btnJalankan.addActionListener(e -> {
            try {
                int n = Integer.parseInt(input.getText().trim());
                if (n <= 0) throw new NumberFormatException();

                btnJalankan.setText("Memproses...");
                btnJalankan.setEnabled(false);
                btnReset.setEnabled(false);

                new Thread(() -> {
                    KalkulasiThread kt = new KalkulasiThread(model, n, jenis, btnJalankan);
                    kt.start();
                    try { kt.join(); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                    SwingUtilities.invokeLater(() -> {
                        btnJalankan.setText("Jalankan");
                        btnJalankan.setEnabled(true);
                        btnReset.setEnabled(true);
                    });
                }).start();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Masukkan bilangan bulat positif.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Aksi Reset
        btnReset.addActionListener(e -> {
            model.setRowCount(0);
            input.setText("1000");
        });

        return panel;
    }
}