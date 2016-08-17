package ihs.com.cliniclocator;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;
import java.util.HashMap;

public class LocatorResultActivity extends AppCompatActivity implements LocationListener {

    private static final float MIN_DISTANCE = 1000;
    private static final long MIN_TIME = 400;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private LocationManager locationManager;
    private Location location;

    private double lat, lng;
    private String responseMessage, gotoactivity;
    private ArrayList<HashMap<String, String>> clinicList = new ArrayList<HashMap<String, String>>();
    private ArrayList<Clinic> clinics = new ArrayList<Clinic>();
    private HashMap<String, String> clinicMap;
    private Clinic clinic;
    private String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator_result);
//        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        handleIntent(getIntent());

        //progress
        CircularProgressView progressView = (CircularProgressView) findViewById(R.id.progress_view);
        progressView.startAnimation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        String query = "";

        // check internet connection
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false
        if(isInternetPresent) {
            //System.out.println("masuk LocatorResultActivity > isInternetPresent");

            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

                query = intent.getStringExtra(SearchManager.QUERY);

            }

            toggleBestUpdates(query);

        } else {

            Intent i = new Intent(LocatorResultActivity.this, FullscreenCheckConnectionActivity.class);
            intent.putExtra("errmsg", "errconnection");
            startActivity(i);
            finish();

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        boolean isEnabled = false;
        if(locationManager != null){
            isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d("locationManager", "NOT NULL: "+isEnabled);
        }
        return isEnabled;
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void toggleBestUpdates(String query) {
        if(!checkLocation())
            return;
        else {
            if(checkPermission()){
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                String provider = locationManager.getBestProvider(criteria, true);
                if(provider != null) {
                    locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, locationListenerBest);
//                    Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
                    location = locationManager.getLastKnownLocation(provider);
                }

                if(location != null){
                    lng = location.getLongitude();
                    lat = location.getLatitude();
                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListenerBest);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null){
                        lng = location.getLongitude();
                        lat = location.getLatitude();

                        Log.d("LAT/LONG @else", lat+"|"+lng);
                    }
                }

                String[] args = new String[1];
                args[0] = query;

                new LoadClinicList().execute(args);
            } else {
                requestPermission();
            }
        }
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            lng = location.getLongitude();
            lat = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(LocatorResultActivity.this, "Best Provider update. lang:"+lng+" | lat:"+lat, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Snackbar.make(mContentView, "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                    //getlatlng("");
                    toggleBestUpdates("");

                } else {

                    View view = (View) findViewById(R.id.locatorresult);
                    Snackbar.make(view, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

    public class LoadClinicList extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try{

                String kywd = params[0].trim();

                if(lat > 0 && lng > 0){

                    ObjectList arrClinicList = new ObjectList();
                    //clinicList = arrClinicList.ArrClinicListNew(lat, lng, kywd);
                    String encodekywd = arrClinicList.encodeURIComponent(kywd);
                    //clinicList = arrClinicList.ArrClinicListNew(lat, lng, encodekywd);
                    clinicList = arrClinicList.ArrClinicListLoad(lat, lng, encodekywd);

                    /*
                    if(clinicList.size() > 1){
                        Collections.sort(clinicList, new MapComparator("jarak"));
                    }
                    */

                    responseMessage = "";
                    gotoactivity = kywd;

                }else{
                    responseMessage = "Could not locate device.";
                }

            }catch(Exception ex){
                responseMessage = "Could not fetch data from server";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (responseMessage.trim().length() > 0) {

                Intent intent = new Intent(LocatorResultActivity.this, FullscreenCheckConnectionActivity.class);
                intent.putExtra("errmsg", responseMessage);
                startActivity(intent);
                finish();

            } else {

                Intent i = null;

                if(gotoactivity.trim().length() > 0) {
                    Log.d("intent","LocatorActivity");
                    i = new Intent(LocatorResultActivity.this, LocatorActivity.class);
                } else {
                    Log.d("intent","NavigationDrawerActivity");
                    i = new Intent(LocatorResultActivity.this, NavigationDrawerActivity.class);
                }

                //parcelable
                if(clinicList != null){
                    if(clinicList.size() > 0){

                        for(int idx = 0; idx < clinicList.size(); idx++){

                            clinicMap = clinicList.get(idx);
                            clinic = new Clinic();

                            clinic.setName(clinicMap.get("name"));
                            clinic.setJarak(clinicMap.get("jarak"));
                            clinic.setLat(clinicMap.get("lat"));
                            clinic.setLng(clinicMap.get("lng"));
                            clinic.setAddress(clinicMap.get("address"));
                            clinic.setPhoto(clinicMap.get("photo"));
                            clinic.setResponse(clinicMap.get("response"));
                            clinic.setMylat(clinicMap.get("mylat"));
                            clinic.setMylng(clinicMap.get("mylng"));
                            clinic.setJenis(clinicMap.get("jenis"));

                            clinics.add(clinic);
                        }

                    }
                } else {
                    Log.d("clinicList","null");
                }

                //parcelable
                i.putParcelableArrayListExtra("clinicparcelable", clinics);
                startActivity(i);
                finish();

            }
        }
    }
}
