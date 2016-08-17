package ihs.com.cliniclocator;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by i.Fikri on 4/29/2016.
 */
public class LoadClinicList {

    private static final float MIN_DISTANCE = 1000;
    private static final long MIN_TIME = 400;
    private static final String parcelableKey = "clinicparcelable";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Context contexts;
    private LocationManager locationManager;
    private Location location;
    private double lat,lng;

    private String responseMessage;
    private HashMap<String, String> clinicMap;
    private Clinic clinic;
    private ArrayList<HashMap<String, String>> clinicList = new ArrayList<HashMap<String, String>>();
    public ArrayList<Clinic> clinics = new ArrayList<Clinic>();

    LoadClinicList(Context context){
        contexts = context;
        locationManager = (LocationManager)contexts.getSystemService(Context.LOCATION_SERVICE);

        toggleBestUpdates();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(contexts, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;

        }
    }

    public void toggleBestUpdates() {
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

            new LoadsClinicList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
        }
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            lng = location.getLongitude();
            lat = location.getLatitude();
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

    public class LoadsClinicList extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try{

                String kywd = params[0].trim();

                if(lat > 0 && lng > 0){

                    ObjectList arrClinicList = new ObjectList();
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
                Intent intent = new Intent(contexts, FullscreenCheckConnectionActivity.class);
                intent.putExtra("errmsg", responseMessage);

            } else {

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
                }
                //Toast.makeText(contexts, "clinics size: "+clinics.size()+" context name: "+contexts.getPackageName(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public ArrayList<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(ArrayList<Clinic> clinics) {
        this.clinics = clinics;
    }
}
