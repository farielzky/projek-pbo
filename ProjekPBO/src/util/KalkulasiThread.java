package util;

import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class KalkulasiThread extends Thread {
    private DefaultTableModel tableModel;
    private int jumlahData;
    private String jenisBangun;
    private JButton tombol;

    public KalkulasiThread(DefaultTableModel tableModel, int jumlahData, String jenisBangun, JButton tombol) {
        this.tableModel = tableModel;
        this.jumlahData = jumlahData;
        this.jenisBangun = jenisBangun;
        this.tombol = tombol;
    }

    @Override
    public void run() {
        try {
            SwingUtilities.invokeLater(() -> {
                tombol.setEnabled(false);
                tableModel.setRowCount(0); // bersihkan tabel sebelum mulai
            });

            for (int i = 1; i <= jumlahData; i++) {
                double d1 = (Math.random() * 100) + 1;
                double d2 = (Math.random() * 100) + 1;
                double t  = (Math.random() * 50)  + 1;

                Object[] row;
                if (jenisBangun.equals("LAYANG")) {
                    LayangLayang b = new LayangLayang(d1, d2);
                    row = new Object[]{
                        i,
                        String.format("%.2f", d1),
                        String.format("%.2f", d2),
                        "-",
                        String.format("%.2f", b.hitungLuas())
                    };
                } else if (jenisBangun.equals("PRISMA")) {
                    PrismaLayangLayang b = new PrismaLayangLayang(d1, d2, t);
                    row = new Object[]{
                        i,
                        String.format("%.2f", d1),
                        String.format("%.2f", d2),
                        String.format("%.2f", t),
                        String.format("%.2f", b.hitungVolume())
                    };
                } else { // LIMAS
                    LimasLayangLayang b = new LimasLayangLayang(d1, d2, t);
                    row = new Object[]{
                        i,
                        String.format("%.2f", d1),
                        String.format("%.2f", d2),
                        String.format("%.2f", t),
                        String.format("%.2f", b.hitungVolume())
                    };
                }

                final Object[] finalRow = row;
                SwingUtilities.invokeLater(() -> tableModel.addRow(finalRow));

                long delay = 10 + (i / 20);
                Thread.sleep(delay);
            }

        } catch (InterruptedException e) {
            // thread dihentikan, tidak perlu tindakan khusus
        } finally {
            SwingUtilities.invokeLater(() -> tombol.setEnabled(true));
        }
    }
}