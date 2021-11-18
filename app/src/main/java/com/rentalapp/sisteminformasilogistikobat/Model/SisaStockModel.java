package com.rentalapp.sisteminformasilogistikobat.Model;

public class SisaStockModel {
    private int masuk=0;
    private int keluar= 0;

    public SisaStockModel() {
    }

    public SisaStockModel(int masuk, int keluar) {
        this.masuk = masuk;
        this.keluar = keluar;
    }

    public int getMasuk() {
        return masuk;
    }

    public void setMasuk(int masuk) {
        this.masuk = masuk;
    }

    public int getKeluar() {
        return keluar;
    }

    public void setKeluar(int keluar) {
        this.keluar = keluar;
    }
}
