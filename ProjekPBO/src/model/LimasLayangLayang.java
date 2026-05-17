package model;

public class LimasLayangLayang extends BangunGeometri {
    private LayangLayang alas;
    private double tinggi;

    public LimasLayangLayang(double d1, double d2, double tinggi) {
        super("Limas Layang-Layang");
        this.alas = new LayangLayang(d1, d2);
        this.tinggi = tinggi;
    }

    @Override
    public double hitungLuas() {
        return alas.hitungLuas();
    }

    @Override
    public double hitungVolume() {
        return (1.0/3.0) * alas.hitungLuas() * tinggi;
    }
}