package model;

import view.JendelaUtama;
import java.awt.Color;
import java.util.List;

public abstract class BangunGeometri implements Kalkulasi, Runnable {
    protected String nama;
    protected JendelaUtama jendela;
    protected int jumlahData;
    protected Color warna;
    protected int idThread;
    protected List<Thread> daftarThread;

    public BangunGeometri() {}

    public BangunGeometri(String nama, JendelaUtama jendela, int jumlahData, Color warna, int idThread, List<Thread> daftarThread) {
        this.nama = nama;
        this.jendela = jendela;
        this.jumlahData = jumlahData;
        this.warna = warna;
        this.idThread = idThread;
        this.daftarThread = daftarThread;
    }

    public String getNama() {
        return nama;
    }

    @Override
    public abstract double hitungLuas();

    @Override
    public abstract double hitungVolume();

    @Override
    public abstract void run();
}