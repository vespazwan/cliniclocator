package ihs.com.cliniclocator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
//import android.support.design.widget.FloatingActionButton;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DetailsActivity extends AppCompatActivity {

    public final static String ID = "ID";
    public final static String FROM = "FROM";
    private static final String parcelableKey = "clinicparcelable";
    protected static final String STATIC_MAP_API_ENDPOINT = "https://maps.googleapis.com/maps/api/staticmap?center=";
    protected static final String STATIC_STREET_VIEW_API_ENDPOINT = "https://maps.googleapis.com/maps/api/streetview?size=600x300&location=";
    protected static final String STATIC_MAP_API_KEY = "AIzaSyCih-wez3UjdDtqIt7zOAMQUGSrXbGduUY";
    //protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://apicliniclocator-ihssb.rhcloud.com/clinicspanels/panel/";

    public String from;
    public Clinic clinic;
    private static ArrayList<Clinic> clinics;

    private TextView name, address, response, rating;
    private Toolbar toolbar;
    private FloatingActionButton fabdirection, fabcall, fabshare, fabrating;
    private Button btnreadmore;

    private ArrayList<HashMap<String, String>> panelList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> panelMap;
    private LinearLayout panelLayout;

    private ArrayList<HashMap<String, String>> ratingList = new ArrayList<HashMap<String, String>>();
    private String responseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //start here
        Intent it = getIntent();
        from = it.getExtras().getString(FROM);
        clinic = it.getParcelableExtra(ID);
        clinics = it.getParcelableArrayListExtra(parcelableKey);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Details");

        this.setClinicDetails();
        this.setClinicImages();
        this.setClinicActions();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new SetPanleAsync().execute();

    }

    public void setClinicImages(){
        //map
        AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                String mapurl = "";

                if ("PUBLIC".equalsIgnoreCase(clinic.getJenis())) {
                    mapurl = STATIC_MAP_API_ENDPOINT + clinic.getLat() + "," + clinic.getLng() + "&markers=color:blue%7Clabel:G%7C" + clinic.getLat() + "," + clinic.getLng() + "&zoom=16&size=1080x617&key=" + STATIC_MAP_API_KEY;
                } else {
                    mapurl = STATIC_MAP_API_ENDPOINT + clinic.getLat() + "," + clinic.getLng() + "&markers=color:red%7Clabel:P%7C" + clinic.getLat() + "," + clinic.getLng() + "&zoom=16&size=1080x617&key=" + STATIC_MAP_API_KEY;
                }

                HttpGet request = new HttpGet(mapurl);

                InputStream in = null;
                try {
                    in = httpclient.execute(request).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {
                    final ImageView iv = (ImageView) findViewById(R.id.map);
                    iv.setImageBitmap(bmp);

                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            double navLat = Double.parseDouble(clinic.getLat());
                            double navLong = Double.parseDouble(clinic.getLng());

                            Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("http://maps.google.com/maps?saddr=" + clinic.getMylat() + "," + clinic.getMylng() + "&daddr=" + navLat + "," + navLong));
                            startActivity(navigation);
                        }
                    });
                }
            }
        };
        setImageFromUrl.execute();

        //gambar1
        AsyncTask<Void, Void, Bitmap> setImageFromUrl1 = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                String gambarurl1 = STATIC_STREET_VIEW_API_ENDPOINT + clinic.getLat() + "," + clinic.getLng() + "&heading=0&pitch=0&key=" + STATIC_MAP_API_KEY;
                HttpGet request1 = new HttpGet(gambarurl1);

                InputStream in = null;
                try {
                    in = httpclient.execute(request1).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {
                    final ImageView iv = (ImageView) findViewById(R.id.gambar1);
                    iv.setImageBitmap(bmp);
                }
            }
        };
        setImageFromUrl1.execute();

        //gambar2
        AsyncTask<Void, Void, Bitmap> setImageFromUrl2 = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                String gambarurl2 = STATIC_STREET_VIEW_API_ENDPOINT + clinic.getLat() + "," + clinic.getLng() + "&heading=90&pitch=0&key=" + STATIC_MAP_API_KEY;
                HttpGet request2 = new HttpGet(gambarurl2);

                InputStream in = null;
                try {
                    in = httpclient.execute(request2).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {
                    final ImageView iv = (ImageView) findViewById(R.id.gambar2);
                    iv.setImageBitmap(bmp);
                }
            }
        };
        setImageFromUrl2.execute();

        //gambar3
        AsyncTask<Void, Void, Bitmap> setImageFromUrl3 = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                String gambarurl3 = STATIC_STREET_VIEW_API_ENDPOINT + clinic.getLat() + "," + clinic.getLng() + "&heading=180&pitch=0&key=" + STATIC_MAP_API_KEY;
                HttpGet request3 = new HttpGet(gambarurl3);

                InputStream in = null;
                try {
                    in = httpclient.execute(request3).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {
                    final ImageView iv = (ImageView) findViewById(R.id.gambar3);
                    iv.setImageBitmap(bmp);
                }
            }
        };
        setImageFromUrl3.execute();

        //gambar4
        AsyncTask<Void, Void, Bitmap> setImageFromUrl4 = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                String gambarurl4 = STATIC_STREET_VIEW_API_ENDPOINT + clinic.getLat() + "," + clinic.getLng() + "&heading=270&pitch=0&key=" + STATIC_MAP_API_KEY;
                HttpGet request4 = new HttpGet(gambarurl4);

                InputStream in = null;
                try {
                    in = httpclient.execute(request4).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {
                    final ImageView iv = (ImageView) findViewById(R.id.gambar4);
                    iv.setImageBitmap(bmp);
                }
            }
        };
        setImageFromUrl4.execute();
    }

    public void setClinicDetails(){
        //name
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Light.ttf");

        TextView tvname = (TextView) findViewById(R.id.DETAILS_name);
        tvname.setTypeface(tf);
        name = (TextView) findViewById(R.id.DETAILS_name);
        name.setText(clinic.getName());

        //address
        TextView tvaddresslabel = (TextView) findViewById(R.id.DETAILS_address_label);
        tvaddresslabel.setTypeface(tf);

        TextView tvaddress = (TextView) findViewById(R.id.DETAILS_address);
        tvaddress.setTypeface(tf);
        address = (TextView) findViewById(R.id.DETAILS_address);
        address.setText(clinic.getAddress());

        //phone
        //photo = (TextView) findViewById(R.id.DETAILS_phone);
        //photo.setText(clinic.getPhoto());

        //panel
        TextView tvpanellabel = (TextView) findViewById(R.id.DETAILS_panel_label);
        tvpanellabel.setTypeface(tf);

//        TextView tvpanel = (TextView) findViewById(R.id.DETAILS_panel);
//        tvpanel.setTypeface(tf);

        panelLayout = (LinearLayout) findViewById(R.id.panelLayout);
        new SetPanleAsync().execute();

        //response = (TextView) findViewById(R.id.DETAILS_panel);
        //response.setText(clinic.getResponse());

        //rating
        TextView tvratinglabel = (TextView) findViewById(R.id.DETAILS_rating_label);
        tvratinglabel.setTypeface(tf);
        new SetRatingAsync().execute();

        /* TODO
        HorizontalBarChart barChart = (HorizontalBarChart) findViewById(R.id.chart_rating);

        //chart entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(4f, 0));
        entries.add(new BarEntry(8f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(12f, 3));
        entries.add(new BarEntry(18f, 4));

        BarDataSet dataset = new BarDataSet(entries, "Rating");

        //chart labels
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("5");
        labels.add("4");
        labels.add("3");
        labels.add("2");
        labels.add("1");

        //set data
        BarData data = new BarData(labels, dataset);
        barChart.setData(data);

        // set the description & axis
        barChart.setDescription("");
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getXAxis().setDrawLabels(false);
        barChart.getLegend().setEnabled(false);
        */

    }

    public void setClinicActions(){
        fabdirection = (FloatingActionButton) findViewById(R.id.action_c);
        fabdirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double navLat = Double.parseDouble(clinic.getLat());
                double navLong = Double.parseDouble(clinic.getLng());

                Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
                        .parse("http://maps.google.com/maps?saddr=" + clinic.getMylat() + "," + clinic.getMylng() + "&daddr=" + navLat + "," + navLong));
                startActivity(navigation);
            }
        });

        fabcall = (FloatingActionButton) findViewById(R.id.action_a);
        fabcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makecall = new Intent(Intent.ACTION_DIAL);
                makecall.setData(Uri.parse("tel:" + clinic.getPhoto()));
                startActivity(makecall);
            }
        });

        fabshare = (FloatingActionButton) findViewById(R.id.action_b);
        fabshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double navLat = Double.parseDouble(clinic.getLat());
                double navLong = Double.parseDouble(clinic.getLng());
                String texturi = "http://maps.google.com/maps?saddr=" + clinic.getMylat() + "," + clinic.getMylng() + "&daddr=" + navLat + "," + navLong;
                String cliniclocatormsg = "ClinicLocator wants to share location of " + clinic.getName() + " as below;\n";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, cliniclocatormsg + texturi);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });

        fabrating = (FloatingActionButton) findViewById(R.id.action_d);
        fabrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentrating = new Intent(DetailsActivity.this, ClinicsRatingActivity.class);
                intentrating.putExtra(DetailsActivity.ID, clinic);
                intentrating.putExtra(DetailsActivity.FROM, "locator");
                intentrating.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(intentrating);
                finish();
            }
        });

//        CheckBox favCb = (CheckBox) findViewById(R.id.fav_cb);
//        favCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    setFavourite();
//                }
//            }
//        });

        //rating
        btnreadmore = (Button) findViewById(R.id.btnreadmore);
        btnreadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentdisplayrating = new Intent(DetailsActivity.this, ViewRatingActivity.class);
                intentdisplayrating.putExtra(DetailsActivity.ID, clinic);
                intentdisplayrating.putExtra(DetailsActivity.FROM, "locator");
                intentdisplayrating.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(intentdisplayrating);
                finish();
            }
        });
    }

    private class SetPanleAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try{
                ObjectList objPanelList = new ObjectList();
                panelList = objPanelList.StrPanelListLoad(Long.parseLong(clinic.getResponse()));
                System.out.println("panelList.size(1): "+panelList.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String str) {
            System.out.println("panelList.size(): "+panelList.size());
            String panelName = "";
            if(panelList.size() > 0){
                for(int idx = 0; idx < panelList.size(); idx++){
                    panelMap = panelList.get(idx);
                    panelName = panelMap.get("panelName");

                    TextView panelTV = new TextView(getBaseContext());
                    panelTV.setTextColor(Color.WHITE);
                    panelTV.setText(panelName);

                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(0, 0, 7, 0); // llp.setMargins(left, top, right, bottom);
                    panelTV.setLayoutParams(llp);

                    if(panelName.equalsIgnoreCase("pmcare")){
                        panelTV.setBackgroundResource(R.drawable.panel_pmcare_bg);
                    }else if(panelName.equalsIgnoreCase("red alert")){
                        panelTV.setBackgroundResource(R.drawable.panel_redalert_bg);
                    }else{
                        //donothing
                    }

                    panelLayout.addView(panelTV);
                }
            }
        }

    }

    private class SetRatingAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try{

                ObjectList objRatingList = new ObjectList();
                ratingList = objRatingList.ArrClinicRatingListLoad(Long.parseLong(clinic.getResponse()));
                responseMessage = "";

            } catch (Exception e) {

                responseMessage = "Error Getting Rating";
                e.printStackTrace();

            }
            return null;
        }

        protected void onPostExecute(String str) {

            if (responseMessage.trim().length() > 0) {

                Intent intent = new Intent(DetailsActivity.this, FullscreenCheckConnectionActivity.class);
                intent.putExtra("errmsg", responseMessage);
                startActivity(intent);
                finish();

            } else {

                rating = (TextView) findViewById(R.id.rating_circle);

                try{

                    if(ratingList.size()>0){

                        int averageRating = 0;
                        double ar = 0;
                        for(int i = 0; i < ratingList.size(); i++){
                            averageRating += Integer.parseInt(ratingList.get(i).get("rate"));
                        }
                        ar = averageRating / ratingList.size();
                        DecimalFormat df = new DecimalFormat(".##");
                        rating.setText(df.format(ar));

                    } else {
                        rating.setText("0");
                    }


                } catch (Exception ex) {
                    rating.setText("0");
                }

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backtolist = null;

                if (from.trim().length() > 0) {
                    backtolist = new Intent(DetailsActivity.this, LocatorActivity.class);
                } else {
                    backtolist = new Intent(DetailsActivity.this, NavigationDrawerActivity.class);
                }
                backtolist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                backtolist.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(backtolist);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFavourite(){
        Set<String> myStringSet = new HashSet<String>() {{
            add(clinic.getName());
        }};
        SharedPreferences settings = getSharedPreferences("FavouritesApp", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet("myFavourites", myStringSet);
        editor.commit();
        readFavourite();
    }

    public void readFavourite(){
        Set<String> myStringSet = new HashSet<String>();
        String[] array;
        // Read the favourites
        SharedPreferences settings = getBaseContext().getSharedPreferences("FavouritesApp", 0);
        Set<String> favourites = settings.getStringSet("myFavourites",myStringSet);

        array = favourites.toArray(new String[favourites.size()]);
        if(array.length > 0)
            Log.d("favourites",array[0].toString());
    }
}
