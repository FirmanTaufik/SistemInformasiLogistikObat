package com.rentalapp.sisteminformasilogistikobat.Model;

import java.util.ArrayList;

public class MasukModel {
    private long tglMasuk;
    private String masukId, supplierId;
    private int sumberId;

    public MasukModel() {
    }

    public MasukModel(long tglMasuk, String masukId, String supplierId, int sumberId ) {
        this.tglMasuk = tglMasuk;
        this.masukId = masukId;
        this.supplierId = supplierId;
        this.sumberId = sumberId;
    }

    public long getTglMasuk() {
        return tglMasuk;
    }

    public void setTglMasuk(long tglMasuk) {
        this.tglMasuk = tglMasuk;
    }

    public String getMasukId() {
        return masukId;
    }

    public void setMasukId(String masukId) {
        this.masukId = masukId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public int getSumberId() {
        return sumberId;
    }

    public void setSumberId(int sumberId) {
        this.sumberId = sumberId;
    }
}
