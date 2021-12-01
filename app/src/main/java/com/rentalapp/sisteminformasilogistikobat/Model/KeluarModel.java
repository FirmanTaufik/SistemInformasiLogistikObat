package com.rentalapp.sisteminformasilogistikobat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class KeluarModel implements Parcelable {
    private long tglKeluar;
    private String keluarId=null;
    private int faskesId;

    public KeluarModel() {
    }

    public KeluarModel(long tglKeluar, String keluarId, int faskesId) {
        this.tglKeluar = tglKeluar;
        this.keluarId = keluarId;
        this.faskesId = faskesId;
    }

    protected KeluarModel(Parcel in) {
        tglKeluar = in.readLong();
        keluarId = in.readString();
        faskesId = in.readInt();
    }

    public static final Creator<KeluarModel> CREATOR = new Creator<KeluarModel>() {
        @Override
        public KeluarModel createFromParcel(Parcel in) {
            return new KeluarModel(in);
        }

        @Override
        public KeluarModel[] newArray(int size) {
            return new KeluarModel[size];
        }
    };

    public long getTglKeluar() {
        return tglKeluar;
    }

    public void setTglKeluar(long tglKeluar) {
        this.tglKeluar = tglKeluar;
    }

    public String getKeluarId() {
        return keluarId;
    }

    public void setKeluarId(String keluarId) {
        this.keluarId = keluarId;
    }

    public int getFaskesId() {
        return faskesId;
    }

    public void setFaskesId(int faskesId) {
        this.faskesId = faskesId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(tglKeluar);
        dest.writeString(keluarId);
        dest.writeInt(faskesId);
    }
}
