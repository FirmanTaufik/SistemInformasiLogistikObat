package com.rentalapp.sisteminformasilogistikobat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class SupplierModel implements Parcelable {
    private String supplierId, name, phone, address;

    public SupplierModel() {
    }

    public SupplierModel(String supplierId, String name, String phone, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    protected SupplierModel(Parcel in) {
        supplierId = in.readString();
        name = in.readString();
        phone = in.readString();
        address = in.readString();
    }

    public static final Creator<SupplierModel> CREATOR = new Creator<SupplierModel>() {
        @Override
        public SupplierModel createFromParcel(Parcel in) {
            return new SupplierModel(in);
        }

        @Override
        public SupplierModel[] newArray(int size) {
            return new SupplierModel[size];
        }
    };

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(supplierId);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(address);
    }
}
