package com.rentalapp.sisteminformasilogistikobat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ObatModel implements Parcelable {
    private String obatId, name, pack,noBatch;

    public ObatModel() {
    }

    public ObatModel(String obatId, String name, String pack, String noBatch) {
        this.obatId = obatId;
        this.name = name;
        this.pack = pack;
        this.noBatch = noBatch;
    }

    protected ObatModel(Parcel in) {
        obatId = in.readString();
        name = in.readString();
        pack = in.readString();
        noBatch = in.readString();
    }

    public static final Creator<ObatModel> CREATOR = new Creator<ObatModel>() {
        @Override
        public ObatModel createFromParcel(Parcel in) {
            return new ObatModel(in);
        }

        @Override
        public ObatModel[] newArray(int size) {
            return new ObatModel[size];
        }
    };

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

    public String getNoBatch() {
        return noBatch;
    }

    public void setNoBatch(String noBatch) {
        this.noBatch = noBatch;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(obatId);
        parcel.writeString(name);
        parcel.writeString(pack);
        parcel.writeString(noBatch);
    }
}
