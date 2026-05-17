package model;

public abstract class BangunGeometri implements Kalkulasi {
    private String nama;

    public BangunGeometri(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    @Override
    public abstract double hitungLuas();

    @Override
    public abstract double hitungVolume();
}