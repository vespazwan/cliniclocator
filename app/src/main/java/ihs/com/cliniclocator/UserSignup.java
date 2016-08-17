package ihs.com.cliniclocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by g41113red on 5/18/2016.
 */
public class UserSignup implements Parcelable{

    private String userid;
    private String username;
    private String password;
    private String displayName;
    private String isGoPro;

    public UserSignup() {
    }

    public UserSignup(String userid, String username, String password, String displayName, String isGoPro) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.isGoPro = isGoPro;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIsGoPro() {
        return isGoPro;
    }

    public void setIsGoPro(String isGoPro) {
        this.isGoPro = isGoPro;
    }

    protected UserSignup(Parcel in) {
        userid = in.readString();
        username = in.readString();
        password = in.readString();
        displayName = in.readString();
        isGoPro = in.readString();
    }

    public static final Creator<UserSignup> CREATOR = new Creator<UserSignup>() {
        @Override
        public UserSignup createFromParcel(Parcel in) {
            return new UserSignup(in);
        }

        @Override
        public UserSignup[] newArray(int size) {
            return new UserSignup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(displayName);
        dest.writeString(isGoPro);
    }
}
