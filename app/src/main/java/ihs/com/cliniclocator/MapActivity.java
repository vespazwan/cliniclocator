package ihs.com.cliniclocator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final static String FROM = "FROM";
    private static final String parcelableKey = "clinicparcelable";
    private static ArrayList<Clinic> clinics;

    private GoogleMap mMap;
    private double lat, lng;
    public String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Map View");

        //map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // draw multiple masrker
        mMap.clear();

        //get parcelable
        Intent it = getIntent();
        from = it.getExtras().getString(FROM);
        clinics = it.getParcelableArrayListExtra(parcelableKey);
        if (clinics.size() > 0) {
            for (int idx = 0; idx < clinics.size(); idx++) {
                Clinic clinic = clinics.get(idx);
                HashMap<String, String> clinicMap = new HashMap<String, String>();
                clinicMap.put("name", clinic.getName());
                clinicMap.put("jarak", clinic.getJarak());
                clinicMap.put("lat", clinic.getLat());
                clinicMap.put("lng", clinic.getLng());
                clinicMap.put("address", clinic.getAddress());
                clinicMap.put("photo", clinic.getPhoto());
                clinicMap.put("response", clinic.getResponse());
                clinicMap.put("mylat", clinic.getMylat());
                clinicMap.put("mylng", clinic.getMylng());
                clinicMap.put("jenis", clinic.getJenis());

                if (idx == 0) {
                    lat = Double.parseDouble(clinic.getMylat());
                    lng = Double.parseDouble(clinic.getMylng());
                }

                double cliniclat = Double.parseDouble(clinic.getLat());
                double cliniclng = Double.parseDouble(clinic.getLng());
                //Person coordinate
                double personLat = Double.parseDouble(clinic.getMylat());
                double personLng = Double.parseDouble(clinic.getMylng());
                double radius = 5000.0;
                LatLng cliniccoord = new LatLng(cliniclat, cliniclng);
                LatLng personcoord = new LatLng(personLat, personLng);

                if ("PUBLIC".equalsIgnoreCase(clinic.getJenis())) {

                    mMap.addMarker(new MarkerOptions()
                            .position(cliniccoord)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_gov))
                            .title(clinic.getName())
//                            .snippet(clinic.getAddress())
                    );

                } else {

                    mMap.addMarker(new MarkerOptions()
                            .position(cliniccoord)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_private_red))
                            .title(clinic.getName())
//                            .snippet(clinic.getAddress())
                    );

                }

                mMap.addMarker(new MarkerOptions()
                        .position(personcoord)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_me))
                        .title("Me!"));

//                mMap.addCircle(new CircleOptions()
//                        .center(personcoord)
//                        .radius(radius)
//                        .strokeColor(Color.BLUE)
//                        .strokeWidth(5)
//                        .fillColor(Color.argb(1, 50, 0, 255))
//                        .zIndex(55));


            }
        }

        // current coordinate
        LatLng curcoord = new LatLng(lat, lng);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(curcoord));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curcoord, 15));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MAPS_RECEIVE)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }


//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            // Use default InfoWindow frame
//            @Override
//            public View getInfoWindow(Marker args) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker args) {
//                // Getting view from the layout file info_window_layout
//                //FrameLayout v = (FrameLayout) findViewById(R.id.info_window_layout);
//
//                // Getting the position from the marker
//                LatLng clickMarkerLatLng = args.getPosition();
//
//                TextView clinicName = (TextView) findViewById(R.id.map_clinic_name);
//                clinicName.setText(args.getTitle());
//
//                TextView clinicAddress = (TextView) findViewById(R.id.map_clinic_address);
//                clinicAddress.setText(args.getSnippet());
//
//                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                    public void onInfoWindowClick(Marker marker){
//
//                    }
//                });
//                // Returning the view containing InfoWindow contents
//                return null;
//            }
//
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent backtolist = null;
                if (from.trim().length() > 0) {
                    backtolist = new Intent(MapActivity.this, LocatorActivity.class);
                } else {
                    backtolist = new Intent(MapActivity.this, NavigationDrawerActivity.class);
                }
                backtolist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                backtolist.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(backtolist);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
