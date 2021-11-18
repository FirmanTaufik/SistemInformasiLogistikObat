package com.rentalapp.sisteminformasilogistikobat.Model;

public class ListModel {
    private String listId =null;
    private String obatId;
    private long  tglExp;
    private int  jumlah;
    private int sumberId;

    public ListModel() {
    }

    public ListModel(String listId, String obatId, long tglExp, int jumlah, int sumberId) {
        this.listId = listId;
        this.obatId = obatId;
        this.tglExp = tglExp;
        this.jumlah = jumlah;
        this.sumberId = sumberId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getObatId() {
        return obatId;
    }

    public void setObatId(String obatId) {
        this.obatId = obatId;
    }

    public long getTglExp() {
        return tglExp;
    }

    public void setTglExp(long tglExp) {
        this.tglExp = tglExp;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getSumberId() {
        return sumberId;
    }

    public void setSumberId(int sumberId) {
        this.sumberId = sumberId;
    }
}
