package com.rentalapp.sisteminformasilogistikobat.Model;

public class KeluarModel {
    private long tglKeluar;
    private String keluarId=null;
    private int faskesId;

    public KeluarModel() {
    }

    public KeluarModel(long tglKeluar, String keluarId, int faskesId) {
        this.tglKeluar = tglKeluar;
        this.keluarId = keluarId;
        this.faskesId = faskesId;
    }

    public long getTglKeluar() {
        return tglKeluar;
    }

    public void setTglKeluar(long tglKeluar) {
        this.tglKeluar = tglKeluar;
    }

    public String getKeluarId() {
        return keluarId;
    }

    public void setKeluarId(String keluarId) {
        this.keluarId = keluarId;
    }

    public int getFaskesId() {
        return faskesId;
    }

    public void setFaskesId(int faskesId) {
        this.faskesId = faskesId;
    }
}
