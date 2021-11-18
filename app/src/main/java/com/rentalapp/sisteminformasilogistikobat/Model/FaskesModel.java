package com.rentalapp.sisteminformasilogistikobat.Model;

public class FaskesModel {
    int faskesId;
    String faskesName;

    public FaskesModel() {
    }

    public FaskesModel(int faskesId, String faskesName) {
        this.faskesId = faskesId;
        this.faskesName = faskesName;
    }

    public int getFaskesId() {
        return faskesId;
    }

    public void setFaskesId(int faskesId) {
        this.faskesId = faskesId;
    }

    public String getFaskesName() {
        return faskesName;
    }

    public void setFaskesName(String faskesName) {
        this.faskesName = faskesName;
    }
}
