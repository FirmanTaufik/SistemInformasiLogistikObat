package com.rentalapp.sisteminformasilogistikobat.Model;

public class KaryawanModel {
    String karyawanId, nama;
    int jabatan;
    long  nip;

    public KaryawanModel() {
    }

    public KaryawanModel(String karyawanId, String nama, int jabatan, long nip) {
        this.karyawanId = karyawanId;
        this.nama = nama;
        this.jabatan = jabatan;
        this.nip = nip;
    }

    public String getKaryawanId() {
        return karyawanId;
    }

    public void setKaryawanId(String karyawanId) {
        this.karyawanId = karyawanId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getJabatan() {
        return jabatan;
    }

    public void setJabatan(int jabatan) {
        this.jabatan = jabatan;
    }

    public long getNip() {
        return nip;
    }

    public void setNip(long nip) {
        this.nip = nip;
    }
}
