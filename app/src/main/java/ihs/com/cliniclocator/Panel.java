package ihs.com.cliniclocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EMA on 11/5/2016.
 */
public class Panel implements Parcelable {

    private String panelId;
    private String clinicId;
    private String panelName;

    public Panel() {}

    public Panel(String panelId, String clinicId, String panelName){
        this.panelId = panelId;
        this.clinicId = clinicId;
        this.panelName = panelName;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    protected Panel(Parcel in) {
        panelId = in.readString();
        clinicId = in.readString();
        panelName = in.readString();
    }

    public static final Creator<Panel> CREATOR = new Creator<Panel>() {
        @Override
        public Panel createFromParcel(Parcel in) {
            return new Panel(in);
        }

        @Override
        public Panel[] newArray(int size) {
            return new Panel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(panelId);
        dest.writeString(clinicId);
        dest.writeString(panelName);
    }
}
