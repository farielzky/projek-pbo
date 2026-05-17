package model;

public class LayangLayang extends BangunGeometri {
    private double d1, d2;

    public LayangLayang(double d1, double d2) {
        super("Layang-Layang");
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public double hitungLuas() {
        return 0.5 * d1 * d2;
    }

    @Override
    public double hitungVolume() {
        return 0;
    }
}