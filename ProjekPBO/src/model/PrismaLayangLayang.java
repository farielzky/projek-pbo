package model;

public class PrismaLayangLayang extends BangunGeometri {
    private LayangLayang alas;
    private double tinggi;

    public PrismaLayangLayang(double d1, double d2, double tinggi) {
        super("Prisma Layang-Layang");
        this.alas = new LayangLayang(d1, d2);
        this.tinggi = tinggi;
    }

    @Override
    public double hitungLuas() {
        return 2 * alas.hitungLuas();
    }

    @Override
    public double hitungVolume() {
        return alas.hitungLuas() * tinggi;
    }
}