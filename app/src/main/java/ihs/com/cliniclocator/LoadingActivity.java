package ihs.com.cliniclocator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoadingActivity extends Activity implements LocationListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private static final float MIN_DISTANCE = 1000;
    private static final long MIN_TIME = 400;
    private static final String parcelableKey = "clinicparcelable";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private LocationManager locationManager;
    private Location location;

    private double lat, lng;
    private String responseMessage;
    private ArrayList<HashMap<String, String>> clinicList = new ArrayList<HashMap<String, String>>();
    private ArrayList<Clinic> clinics = new ArrayList<Clinic>();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    //private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            /*
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            */
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.splash_tag);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        // code here
        //tfLoading = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
        Typeface tfLoading = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Light.ttf");
        TextView tvLoading = (TextView) findViewById(R.id.splash_tag);
        tvLoading.setTypeface(tfLoading);

        // check internet connection
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

        Log.d("Internet Present?", isInternetPresent+"");

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (isInternetPresent) {

            //getlatlng();
            toggleBestUpdates();


        } else {

            Intent intent = new Intent(LoadingActivity.this, FullscreenCheckConnectionActivity.class);
            intent.putExtra("errmsg", "errconnection");
            startActivity(intent);
            finish();

        }
        // code here

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        /*
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        */
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;

        }
    }

    //SPACE
    private boolean checkLocation() {
        if(!isLocationEnabled()){
            showAlert();
        }
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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

                        toggleBestUpdates();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void toggleBestUpdates() {

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
                    //Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
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
                args[0] = "";

                new LoadClinicList().execute(args);
            } else {
                requestPermission();
            }
        }
    }

//    private Location getLastKnownLocation() {
//        checkPermission();
//        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
//        List<String> providers = locationManager.getProviders(true);
//        Location bestLocation = null;
//        for (String provider : providers) {
//            Location l = locationManager.getLastKnownLocation(provider);
//            if (l == null) {
//                Log.d("LOCATION", "null");
//                toggleBestUpdates();
//                continue;
//            }
//            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                // Found best last known location: %s", l);
//                bestLocation = l;
//                Log.d("BEST LOCATION", bestLocation.getProvider());
//            }
//        }
//        return bestLocation;
//    }

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            lng = location.getLongitude();
            lat = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("LAT+LONG",lat+" | "+lng);
                    //Toast.makeText(LoadingActivity.this, "Best Provider update. lang:"+lng+" | lat:"+lat, Toast.LENGTH_SHORT).show();
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

    //SPACE

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Snackbar.make(mContentView, "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                    //getlatlng();
                    toggleBestUpdates();

                } else {

                    Snackbar.make(mContentView, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();

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
                    clinicList = arrClinicList.ArrClinicListLoad(lat, lng, "null");

                    responseMessage = "";

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
                Intent intent = new Intent(LoadingActivity.this, FullscreenCheckConnectionActivity.class);
                intent.putExtra("errmsg", responseMessage);
                startActivity(intent);
                finish();

            } else {

                //Intent i = new Intent(LoadingActivity.this, LocatorActivity.class);
                Intent i = new Intent(LoadingActivity.this, NavigationDrawerActivity.class);
                //parcelable
                if(clinicList != null){
                    if(clinicList.size() > 0){

                        for(int idx = 0; idx < clinicList.size(); idx++){

                            HashMap<String, String> clinicMap = clinicList.get(idx);
                            Clinic clinic = new Clinic();

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
                }

                //parcelable
                i.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(i);
                finish();

            }
        }
    }

}
