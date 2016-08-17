package ihs.com.cliniclocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by g41113red on 5/18/2016.
 */
public class ClinicsRating implements Parcelable {

    private String ratingId;
    private String clinicId;
    private String userId;
    private String rate;
    private String title;
    private String description;
    private String signinUsername;

    public ClinicsRating() {
    }

    public ClinicsRating(String ratingId, String clinicId, String userId, String rate, String title, String description, String signinUsername) {
        this.ratingId = ratingId;
        this.clinicId = clinicId;
        this.userId = userId;
        this.rate = rate;
        this.title = title;
        this.description = description;
        this.signinUsername = signinUsername;
    }

    public static final Creator<ClinicsRating> CREATOR = new Creator<ClinicsRating>() {
        @Override
        public ClinicsRating createFromParcel(Parcel in) {
            return new ClinicsRating(in);
        }

        @Override
        public ClinicsRating[] newArray(int size) {
            return new ClinicsRating[size];
        }
    };

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSigninUsername() {
        return signinUsername;
    }

    public void setSigninUsername(String signinUsername) {
        this.signinUsername = signinUsername;
    }

    protected ClinicsRating (Parcel in) {
        ratingId = in.readString();
        clinicId = in.readString();
        userId = in.readString();
        rate = in.readString();
        title = in.readString();
        description = in.readString();
        signinUsername = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ratingId);
        dest.writeString(clinicId);
        dest.writeString(userId);
        dest.writeString(rate);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(signinUsername);
    }
}
