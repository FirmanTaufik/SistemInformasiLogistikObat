package com.rentalapp.sisteminformasilogistikobat.Model;

public class LaporanObatModel {
    private String obatId, name;
    private int jmlMasuk, jmlKeluar,stockExp, sisaStock;

    public LaporanObatModel(String obatId, String name, int jmlMasuk, int jmlKeluar, int stockExp, int sisaStock) {
        this.obatId = obatId;
        this.name = name;
        this.jmlMasuk = jmlMasuk;
        this.jmlKeluar = jmlKeluar;
        this.stockExp = stockExp;
        this.sisaStock = sisaStock;
    }

    public String getObatId() {
        return obatId;
    }

    public void setObatId(String obatId) {
        this.obatId = obatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJmlMasuk() {
        return jmlMasuk;
    }

    public void setJmlMasuk(int jmlMasuk) {
        this.jmlMasuk = jmlMasuk;
    }

    public int getJmlKeluar() {
        return jmlKeluar;
    }

    public void setJmlKeluar(int jmlKeluar) {
        this.jmlKeluar = jmlKeluar;
    }

    public int getStockExp() {
        return stockExp;
    }

    public void setStockExp(int stockExp) {
        this.stockExp = stockExp;
    }

    public int getSisaStock() {
        return sisaStock;
    }

    public void setSisaStock(int sisaStock) {
        this.sisaStock = sisaStock;
    }
}
