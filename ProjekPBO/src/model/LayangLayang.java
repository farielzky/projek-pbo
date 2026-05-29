package model;

import view.JendelaUtama;
import java.awt.Color;
import java.util.List;
import java.util.Random;

// GENERALIZATION
public class LayangLayang extends BangunGeometri {
    protected double d1, d2; // Gunakan 'protected' agar bisa dibaca kelas anak
    protected double luas;
    protected double keliling;
    protected boolean bisaInterupsi;

    // Konstruktor untuk objek LayangLayang (2D) yang akan di lempar ke Main
    public LayangLayang(JendelaUtama jendela, int jumlahData, Color warna, int idThread, List<Thread> daftarThread, boolean bisaInterupsi) {
        super("LAYANG", jendela, jumlahData, warna, idThread, daftarThread);
        this.bisaInterupsi = bisaInterupsi;
    }

    // Konstruktor khusus untuk dipanggil oleh kelas spesialisasi (Prisma/Limas)
    protected LayangLayang(String namaGeometri, JendelaUtama jendela, int jumlahData, Color warna, int idThread, List<Thread> daftarThread, boolean bisaInterupsi) {
        super(namaGeometri, jendela, jumlahData, warna, idThread, daftarThread);
        this.bisaInterupsi = bisaInterupsi;
    }

    public void setDimensi(double d1, double d2) {
        this.d1 = d1;
        this.d2 = d2;
        this.luas     = 0.5 * d1 * d2;
        this.keliling = 2 * (Math.sqrt(Math.pow(d1 / 2, 2) + Math.pow(d2 / 2, 2))
                       + Math.sqrt(Math.pow(d1 / 2, 2) + Math.pow(d2 / 2, 2)));
    }

    @Override public double hitungLuas() { return luas; }
    public double hitungKeliling(){ return keliling; }
    @Override public double hitungVolume() { return 0; } // 2D tidak punya volume


    // Method ini dipisahkan agar BISA DI-OVERRIDE oleh kelas spesialisasi
    protected void setDimensiAcakDanCetakLog(int i) {
        setDimensi((Math.random() * 50) + 1, (Math.random() * 50) + 1);

        // Format 8 Kolom untuk 2D
        String row = String.format("| %-12s | %-9d | %-6.2f | %-6.2f | %-8.2f | %-8.2f | %-8s | %-8s |", 
            nama + "-" + idThread, i, d1, d2, luas, keliling, "-", "-");

        jendela.tambahLog(row, warna);
    }

    @Override
    public void run() {
        String key = nama + "-" + idThread;
        Random rand = new Random();
        int i = 0;
        try {
            for (i = 1; i <= jumlahData; i++) {
                
                // Polimorfisme: Saat Prisma/Limas yang jalan, fungsi bawah ini akan ikut berubah otomatis
                setDimensiAcakDanCetakLog(i);
                jendela.updateThreadCard(key, i, jumlahData, "RUNNING");
                jendela.tambahProgress();

                // Logika Interupsi Thread
                if (bisaInterupsi && rand.nextInt(100) < 5 && daftarThread.size() > 1) {
                    Thread target = daftarThread.get(rand.nextInt(daftarThread.size()));
                    if (target != Thread.currentThread() && target.isAlive()) {
                        jendela.tambahLog(">>> [INTERUPSI] " + Thread.currentThread().getName()
                                + " mematikan " + target.getName(), new Color(220, 165, 60));
                        target.interrupt();
                    }
                }
                // Delay animasi
                Thread.sleep(5 + rand.nextInt(20));
            }
            jendela.tambahLog("[" + nama + "-" + idThread + "] SELESAI", warna);
            jendela.updateThreadCard(key, jumlahData, jumlahData, "DONE");
        } catch (InterruptedException e) {
            int sisa = jumlahData - i;
            for (int s = 0; s < sisa; s++) jendela.tambahProgress();

            // Tambahkan baris penanda di tabel bahwa sisa data hancur
            String rowGagal = String.format("| %-12s | %-9s | %-6s | %-6s | %-8s | %-8s | %-8s |", 
                nama + "-" + idThread, "GAGAL x" + sisa, "---", "---", "---", "---", "---");
            jendela.tambahLog(rowGagal, new Color(220, 165, 60)); // Cetak dengan warna amber

            jendela.tambahLog("[" + nama + "-" + idThread + "] K.O. KARENA DIINTERUPSI!", new Color(220, 165, 60));
            jendela.updateThreadCard(key, i, jumlahData, "INTERRUPTED"); // Tampilkan progress terakhir di kartu
        }
    }
}