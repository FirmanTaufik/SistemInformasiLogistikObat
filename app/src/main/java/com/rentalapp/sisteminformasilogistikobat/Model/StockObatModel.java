package com.rentalapp.sisteminformasilogistikobat.Model;

public class StockObatModel  {
    private String obatId, name, pack;
    private int sisaStock, stockExp;

    public StockObatModel() {
    }

    public StockObatModel(String obatId, String name, String pack, int sisaStock, int stockExp) {
        this.obatId = obatId;
        this.name = name;
        this.pack = pack;
        this.sisaStock = sisaStock;
        this.stockExp = stockExp;
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

    public int getSisaStock() {
        return sisaStock;
    }

    public void setSisaStock(int sisaStock) {
        this.sisaStock = sisaStock;
    }

    public int getStockExp() {
        return stockExp;
    }

    public void setStockExp(int stockExp) {
        this.stockExp = stockExp;
    }

//    @Override
//    public int compareTo(StockObatModel o) {
//        return Integer.compare(this.stockExp, o.stockExp);
//    }
}
