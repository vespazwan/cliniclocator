package ihs.com.cliniclocator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by EMA on 21/3/2016.
 */
public class ConnectionDetector {

    private Context mContext;

    public ConnectionDetector(Context context) {
        this.mContext = context;
    }
    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    Log.d("ConnectionDetector", "RETURN TRUE@1 :"+networkInfo.getTypeName());
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
//                            LogUtils.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                            System.out.println("NETWORKNAME: " + anInfo.getTypeName());
                            Log.d("ConnectionDetector", "RETURN TRUE@2");
                            return true;
                        }
                    }
                }
            }
        }
        Toast.makeText(mContext,"Please connect to the internet.",Toast.LENGTH_SHORT).show();
        Log.d("ConnectionDetector", "RETURN FALSE");
        return false;
    }
}
