package com.rentalapp.sisteminformasilogistikobat.Adapter;

public class JabatanModel {
    int jabatanId;
    String jabatan;

    public JabatanModel(int jabatanId, String jabatan) {
        this.jabatanId = jabatanId;
        this.jabatan = jabatan;
    }

    public int getJabatanId() {
        return jabatanId;
    }

    public void setJabatanId(int jabatanId) {
        this.jabatanId = jabatanId;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }
}
