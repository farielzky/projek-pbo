package model;

import view.JendelaUtama;
import java.awt.Color;
import java.util.List;

// SPECIALIZATION dari LayangLayang
public class LimasLayangLayang extends LayangLayang {
    private double tinggi; // Atribut tambahan hasil spesialisasi
    private double volume;

    public LimasLayangLayang(JendelaUtama jendela, int jumlahData, Color warna, int idThread, List<Thread> daftarThread, boolean bisaInterupsi) {
        // Melempar parameter ke konstruktor General (Induk)
        super("LIMAS", jendela, jumlahData, warna, idThread, daftarThread, bisaInterupsi);
    }

    public void setDimensi(double d1, double d2, double tinggi) {
        super.setDimensi(d1, d2); // Gunakan sifat General
        this.tinggi = tinggi;
        this.volume = (1.0 / 3.0) * luas * tinggi;
    }

    @Override 
    public double hitungVolume() { 
        return volume; // 1/3 * Luas alas * tinggi
    }

    // Override Sifat General: Saat di dalam thread, cetak Volume
    @Override
    protected void setDimensiAcakDanCetakLog(int i) {
        setDimensi((Math.random() * 50) + 1, (Math.random() * 50) + 1, (Math.random() * 30) + 1);
    
        // Format baris untuk data 3D Limas
        String row = String.format("| %-12s | %-9d | %-6.2f | %-6.2f | %-8.2f | %-8s | %-8.2f |", 
            nama + "-" + idThread, i, d1, d2, luas, "-", hitungVolume());

        jendela.tambahLog(row, warna);
    }
}