package model;

import view.JendelaUtama;
import java.awt.Color;
import java.util.List;

// SPECIALIZATION dari LayangLayang
public class PrismaLayangLayang extends LayangLayang {
    private double tinggi; // Atribut tambahan hasil spesialisasi
    private double volume;

    public PrismaLayangLayang(JendelaUtama jendela, int jumlahData, Color warna, int idThread, List<Thread> daftarThread, boolean bisaInterupsi) {
        // Melempar parameter ke konstruktor General (Induk)
        super("PRISMA", jendela, jumlahData, warna, idThread, daftarThread, bisaInterupsi);
    }

    // Overloading method setDimensi dengan 3 parameter
    public void setDimensi(double d1, double d2, double tinggi) {
        super.setDimensi(d1, d2); // Gunakan sifat General untuk d1 dan d2
        this.tinggi = tinggi;
        this.volume = luas * tinggi; // dapat variable luas dari parent class
    }

    @Override 
    public double hitungVolume() { 
        return volume; // Luas alas * tinggi
    }

    // Override Sifat General: Saat di dalam thread, cetak Volume bukan Luas
    @Override
    protected void setDimensiAcakDanCetakLog(int i) {
        setDimensi((Math.random() * 50) + 1, (Math.random() * 50) + 1, (Math.random() * 30) + 1);
        jendela.tambahLog(String.format("[%s-%d] Data %d | Vol: %.2f", nama, idThread, i, hitungVolume()), warna);
    }
    
    // TIDAK PERLU MENULIS ULANG METHOD run() !
    // Sifat multithreading otomatis diwariskan dari induk (LayangLayang).
}