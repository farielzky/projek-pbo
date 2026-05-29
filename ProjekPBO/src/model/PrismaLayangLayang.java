package model;

import view.JendelaUtama;
import java.awt.Color;
import java.util.List;

// SPECIALIZATION dari LayangLayang
public class PrismaLayangLayang extends LayangLayang {
    private double tinggi; 
    private double volume;
    private double luasPermukaan; // Atribut baru

    public PrismaLayangLayang(JendelaUtama jendela, int jumlahData, Color warna, int idThread, List<Thread> daftarThread, boolean bisaInterupsi) {
        super("PRISMA", jendela, jumlahData, warna, idThread, daftarThread, bisaInterupsi);
    }

    // Overloading method setDimensi dengan 3 parameter
    public void setDimensi(double d1, double d2, double tinggi) {
        super.setDimensi(d1, d2); 
        this.tinggi = tinggi;
        this.volume = luas * tinggi; 
        
        // Rumus Luas Permukaan Prisma
        this.luasPermukaan = (2 * luas) + (keliling * tinggi);
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