package com.rentalapp.sisteminformasilogistikobat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MasukModel implements Parcelable {
    private long tglMasuk;
    private String masukId, supplierId;
    private int sumberId;

    public MasukModel() {
    }

    public MasukModel(long tglMasuk, String masukId, String supplierId, int sumberId ) {
        this.tglMasuk = tglMasuk;
        this.masukId = masukId;
        this.supplierId = supplierId;
        this.sumberId = sumberId;
    }

    protected MasukModel(Parcel in) {
        tglMasuk = in.readLong();
        masukId = in.readString();
        supplierId = in.readString();
        sumberId = in.readInt();
    }

    public static final Creator<MasukModel> CREATOR = new Creator<MasukModel>() {
        @Override
        public MasukModel createFromParcel(Parcel in) {
            return new MasukModel(in);
        }

        @Override
        public MasukModel[] newArray(int size) {
            return new MasukModel[size];
        }
    };

    public long getTglMasuk() {
        return tglMasuk;
    }

    public void setTglMasuk(long tglMasuk) {
        this.tglMasuk = tglMasuk;
    }

    public String getMasukId() {
        return masukId;
    }

    public void setMasukId(String masukId) {
        this.masukId = masukId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public int getSumberId() {
        return sumberId;
    }

    public void setSumberId(int sumberId) {
        this.sumberId = sumberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(tglMasuk);
        dest.writeString(masukId);
        dest.writeString(supplierId);
        dest.writeInt(sumberId);
    }
}
