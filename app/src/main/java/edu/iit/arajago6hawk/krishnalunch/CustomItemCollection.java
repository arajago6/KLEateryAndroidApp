package edu.iit.arajago6hawk.krishnalunch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rasuishere on 2/12/16.
 */
public class CustomItemCollection implements Parcelable{
    private String name = "";
    private String type = "";
    private String make = "";
    private String briefDes = "";
    private String nature = "";
    private double price = 0.0;
    Integer selected = 0;
    Integer quantity = 0;

    public CustomItemCollection(){}

    public CustomItemCollection(Parcel source){
        this.name = source.readString();
        this.type = source.readString();
        this.make = source.readString();
        this.briefDes = source.readString();
        this.nature = source.readString();
        this.price = source.readDouble();
        this.selected = source.readInt();
        this.quantity = source.readInt();
    }

    public static final Parcelable.Creator<CustomItemCollection> CREATOR = new Parcelable.Creator<CustomItemCollection>() {
        public CustomItemCollection createFromParcel(Parcel source) {
            return new CustomItemCollection(source);
        }

        public CustomItemCollection[] newArray(int size) {
            return new CustomItemCollection[size];
        }
    };

    @Override
    public int describeContents() {
        // hashCode() of this class
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(make);
        dest.writeString(briefDes);
        dest.writeString(nature);
        dest.writeDouble(price);
        dest.writeInt(selected);
        dest.writeInt(quantity);

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMake() {
        return make;
    }

    public void setBriefDes(String briefDes) {
        this.briefDes = briefDes;
    }

    public String getBriefDes() {
        return briefDes;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getNature() {
        return nature;
    }

    public int isSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}