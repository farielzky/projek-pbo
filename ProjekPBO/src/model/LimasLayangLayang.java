package model;

import view.JendelaUtama;
import java.awt.Color;
import java.util.List;

// SPECIALIZATION dari LayangLayang
public class LimasLayangLayang extends LayangLayang {
    private double tinggi;
    private double volume;
    private double luasPermukaan; // Atribut baru

    public LimasLayangLayang(JendelaUtama jendela, int jumlahData, Color warna, int idThread, List<Thread> daftarThread, boolean bisaInterupsi) {
        super("LIMAS", jendela, jumlahData, warna, idThread, daftarThread, bisaInterupsi);
    }

    public void setDimensi(double d1, double d2, double tinggi) {
        super.setDimensi(d1, d2); 
        this.tinggi = tinggi;
        this.volume = (1.0 / 3.0) * luas * tinggi;
        
        // --- Perhitungan Luas Permukaan Limas ---
        // 1. Mencari panjang 1 sisi alas (karena keliling = 4 * sisi)
        double sisi = keliling / 4.0;
        
        // 2. Mencari inradius (jari-jari lingkaran dalam alas)
        double inradius = (sisi == 0) ? 0 : luas / (2.0 * sisi);
        
        // 3. Mencari Apotema (tinggi segitiga tegak selimut) memakai Pythagoras
        double apotema = Math.sqrt(Math.pow(tinggi, 2) + Math.pow(inradius, 2));
        
        // 4. Luas selimut (4 x luas segitiga tegak)
        double luasSelimut = 4 * (0.5 * sisi * apotema);
        
        // 5. Total Luas Permukaan
        this.luasPermukaan = luas + luasSelimut;
    }

    @Override 
    public double hitungVolume() { 
        return volume; 
    }

    // Getter baru untuk Luas Permukaan
    public double hitungLuasPermukaan() {
        return luasPermukaan;
    }

    @Override
    protected void setDimensiAcakDanCetakLog(int i) {
        setDimensi((Math.random() * 50) + 1, (Math.random() * 50) + 1, (Math.random() * 30) + 1);

        // Format 8 Kolom untuk 3D (Pastikan ada variabel luasPermukaan dan volume)
        String row = String.format("| %-12s | %-9d | %-6.2f | %-6.2f | %-8.2f | %-8s | %-8.2f | %-8.2f |", 
            nama + "-" + idThread, i, d1, d2, luas, "-", luasPermukaan, volume);

        jendela.tambahLog(row, warna);
    }
}