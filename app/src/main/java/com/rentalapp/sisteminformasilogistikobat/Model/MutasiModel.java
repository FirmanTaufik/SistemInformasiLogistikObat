package com.rentalapp.sisteminformasilogistikobat.Model;

public class MutasiModel {

    private String obatId, name, pack;
    private int stockAwal, masuk,
            pIGD, pKBS,pGNG, pKTK, pRSUD, pLain,
            jumlah, sisaStock;
    private String ket;

    public MutasiModel() {
    }

    public MutasiModel(String obatId, String name, String pack, int stockAwal, int masuk, int pIGD, int pKBS, int pGNG, int pKTK, int pRSUD, int pLain, int jumlah, int sisaStock, String ket) {
        this.obatId = obatId;
        this.name = name;
        this.pack = pack;
        this.stockAwal = stockAwal;
        this.masuk = masuk;
        this.pIGD = pIGD;
        this.pKBS = pKBS;
        this.pGNG = pGNG;
        this.pKTK = pKTK;
        this.pRSUD = pRSUD;
        this.pLain = pLain;
        this.jumlah = jumlah;
        this.sisaStock = sisaStock;
        this.ket = ket;
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

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public int getStockAwal() {
        return stockAwal;
    }

    public void setStockAwal(int stockAwal) {
        this.stockAwal = stockAwal;
    }

    public int getMasuk() {
        return masuk;
    }

    public void setMasuk(int masuk) {
        this.masuk = masuk;
    }

    public int getpIGD() {
        return pIGD;
    }

    public void setpIGD(int pIGD) {
        this.pIGD = pIGD;
    }

    public int getpKBS() {
        return pKBS;
    }

    public void setpKBS(int pKBS) {
        this.pKBS = pKBS;
    }

    public int getpGNG() {
        return pGNG;
    }

    public void setpGNG(int pGNG) {
        this.pGNG = pGNG;
    }

    public int getpKTK() {
        return pKTK;
    }

    public void setpKTK(int pKTK) {
        this.pKTK = pKTK;
    }

    public int getpRSUD() {
        return pRSUD;
    }

    public void setpRSUD(int pRSUD) {
        this.pRSUD = pRSUD;
    }

    public int getpLain() {
        return pLain;
    }

    public void setpLain(int pLain) {
        this.pLain = pLain;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getSisaStock() {
        return sisaStock;
    }

    public void setSisaStock(int sisaStock) {
        this.sisaStock = sisaStock;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }
}
