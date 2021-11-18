package com.rentalapp.sisteminformasilogistikobat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ObatModel implements Parcelable {
    private String obatId, name, pack;

    public ObatModel() {
    }

    public ObatModel(String obatId, String name, String pack) {
        this.obatId = obatId;
        this.name = name;
        this.pack = pack;
    }

    protected ObatModel(Parcel in) {
        obatId = in.readString();
        name = in.readString();
        pack = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(obatId);
        dest.writeString(name);
        dest.writeString(pack);
    }
}
