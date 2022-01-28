package com.rentalapp.sisteminformasilogistikobat.Model;

public class LaporanMasukModel {
    String supplierId, obatId;
    int jmlObat;

    public LaporanMasukModel() {
    }

    public LaporanMasukModel(String supplierId, String obatId, int jmlObat) {
        this.supplierId = supplierId;
        this.obatId = obatId;
        this.jmlObat = jmlObat;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getObatId() {
        return obatId;
    }

    public void setObatId(String obatId) {
        this.obatId = obatId;
    }

    public int getJmlObat() {
        return jmlObat;
    }

    public void setJmlObat(int jmlObat) {
        this.jmlObat = jmlObat;
    }
}
