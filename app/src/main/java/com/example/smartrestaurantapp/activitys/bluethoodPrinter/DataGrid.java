package com.example.smartrestaurantapp.activitys.bluethoodPrinter;

import android.os.Parcel;
import android.os.Parcelable;

public  class DataGrid  implements Parcelable {


    String name;
    String price;
    int id;
    String quantity;
    Double total;
    Double total1;
    boolean box;


    DataGrid(String _describe, String _price, int _id, String _quantity, Double _total, Double _total1 ,  boolean _box) {
        name = _describe;
        price = _price;
        id=_id;
        quantity=_quantity;
        total=_total;
        total1=_total1;
        box = _box;
    }

    protected DataGrid(Parcel in) {
        name = in.readString();
        price = in.readString();
        id = in.readInt();
        quantity = in.readString();
        total = in.readByte() == 0x00 ? null : in.readDouble();
        total1 = in.readByte() == 0x00 ? null : in.readDouble();
        box = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeInt(id);
        dest.writeString(quantity);
        if (total == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(total);
        }
        if (total1 == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(total1);
        }
        dest.writeByte((byte) (box ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DataGrid> CREATOR = new Parcelable.Creator<DataGrid>() {
        @Override
        public DataGrid createFromParcel(Parcel in) {
            return new DataGrid(in);
        }

        @Override
        public DataGrid[] newArray(int size) {
            return new DataGrid[size];
        }
    };
}
