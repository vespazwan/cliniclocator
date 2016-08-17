package ihs.com.cliniclocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EMA on 9/3/2016.
 */
public class Clinic implements Parcelable {

    private String name;
    private String jarak;
    private String lat;
    private String lng;
    private String address;
    private String photo;
    private String response;
    private String mylat;
    private String mylng;
    private String jenis;

    public Clinic() {}

    public Clinic(String name, String jarak, String lat, String lng, String address, String photo, String response, String mylat, String mylng, String jenis) {
        this.name = name;
        this.jarak = jarak;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.photo = photo;
        this.response = response;
        this.mylat = mylat;
        this.mylng = mylng;
        this.jenis = jenis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMylat() { return mylat; }

    public void setMylat(String mylat) { this.mylat = mylat; }

    public String getMylng() { return mylng; }

    public void setMylng(String mylng) { this.mylng = mylng; }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    protected Clinic(Parcel in) {
        name = in.readString();
        jarak = in.readString();
        lat = in.readString();
        lng = in.readString();
        address = in.readString();
        photo = in.readString();
        response = in.readString();
        mylat = in.readString();
        mylng = in.readString();
        jenis = in.readString();
    }

    public static final Creator<Clinic> CREATOR = new Creator<Clinic>() {
        @Override
        public Clinic createFromParcel(Parcel in) {
            return new Clinic(in);
        }

        @Override
        public Clinic[] newArray(int size) {
            return new Clinic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(jarak);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(address);
        dest.writeString(photo);
        dest.writeString(response);
        dest.writeString(mylat);
        dest.writeString(mylng);
        dest.writeString(jenis);
    }
}
