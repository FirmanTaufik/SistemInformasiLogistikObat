package com.rentalapp.sisteminformasilogistikobat.Model;

public class SumberDanaModel {
    private int sumberId;
    private String  name;

    public SumberDanaModel() {
    }

    public SumberDanaModel(int sumberId, String name) {
        this.sumberId = sumberId;
        this.name = name;
    }

    public int getSumberId() {
        return sumberId;
    }

    public void setSumberId(int sumberId) {
        this.sumberId = sumberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
