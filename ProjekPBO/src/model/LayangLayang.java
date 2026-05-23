package model;

import view.JendelaUtama;
import java.awt.Color;
import java.util.List;
import java.util.Random;

// GENERALIZATION
public class LayangLayang extends BangunGeometri {
    protected double d1, d2; // Gunakan 'protected' agar bisa dibaca kelas anak
    protected boolean bisaInterupsi;

    // Konstruktor untuk objek LayangLayang (2D)
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
    }

    @Override public double hitungLuas() { return 0.5 * d1 * d2; }
    @Override public double hitungVolume() { return 0; } // 2D tidak punya volume

    // Method ini dipisahkan agar BISA DI-OVERRIDE oleh kelas spesialisasi
    protected void setDimensiAcakDanCetakLog(int i) {
        setDimensi((Math.random() * 50) + 1, (Math.random() * 50) + 1);
        jendela.tambahLog(String.format("[%s-%d] Data %d | Luas: %.2f", nama, idThread, i, hitungLuas()), warna);
    }

    @Override
    public void run() {
        String key = nama + "-" + idThread;
        Random rand = new Random();
        try {
            for (int i = 1; i <= jumlahData; i++) {
                
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
            jendela.tambahLog("[" + nama + "-" + idThread + "] BERHENTI KARENA DIINTERUPSI!", new Color(220, 165, 60));
            jendela.updateThreadCard(key, 0, jumlahData, "INTERRUPTED");
        }
    }
}