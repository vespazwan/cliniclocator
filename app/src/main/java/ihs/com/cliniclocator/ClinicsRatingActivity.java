package ihs.com.cliniclocator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ClinicsRatingActivity extends AppCompatActivity {

    // Comment either or
    //protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://apicliniclocator-ihssb.rhcloud.com/clinicsrating"; // PROD V1
    protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://apicliniclocatorv2-ihssb.rhcloud.com/clinicsrating"; // PROD V2 with rating
    //protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://mywildflyrestv2-emafazillah.rhcloud.com/clinicsrating"; // DEV

    public final static String ID = "ID";
    public final static String FROM = "FROM";
    public static final String parcelableKey = "clinicparcelable";

    private TextInputLayout clinicidlayout, ratinglayout, titlelayout, descriptionlayout, useridlayout, usernamelayout;
    private EditText clinicid, rating, title, description, userid, username;
    private String clinicidstr, ratingstr, titlestr, descriptionstr, useridstr, usernamestr, loggedInUserName;
    private ProgressDialog pDialog;
    private String from;
    private Clinic clinic;
    private static ArrayList<Clinic> clinics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinics_rating);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Rating");

        //start here
        Intent it = getIntent();
        from = it.getExtras().getString(FROM);
        clinic = it.getParcelableExtra(ID);
        clinics = it.getParcelableArrayListExtra(parcelableKey);

        //Clinic Id
        clinicidlayout = (TextInputLayout) findViewById(R.id.input_layout_clinic_id);
        clinicid = (EditText) findViewById(R.id.input_clinic_id);
        clinicid.setText(clinic.getResponse());

        //Rating
        ratinglayout = (TextInputLayout) findViewById(R.id.input_layout_clinic_rate);
        rating = (EditText) findViewById(R.id.input_clinic_rate);

        //Title
        titlelayout = (TextInputLayout) findViewById(R.id.input_layout_clinic_rate_title);
        title = (EditText) findViewById(R.id.input_clinic_rate_title);

        //Description
        descriptionlayout = (TextInputLayout) findViewById(R.id.input_layout_clinic_rate_description);
        description = (EditText) findViewById(R.id.input_clinic_rate_description);

        //User Id
        useridlayout = (TextInputLayout) findViewById(R.id.input_layout_userid);
        userid = (EditText) findViewById(R.id.input_userid);

        loggedInUserName = PrefUtils.getFromPrefs(ClinicsRatingActivity.this, PrefUtils.PREFS_LOGIN_DISPLAY_NAME, "Anonymous");

        //Name
        usernamelayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        username = (EditText) findViewById(R.id.input_name);
        username.setText(loggedInUserName);

        // TODO: 23/5/2016 validation
        clinicid.addTextChangedListener(new MyTextWatcher(clinicid));
        rating.addTextChangedListener(new MyTextWatcher(rating));
        title.addTextChangedListener(new MyTextWatcher(title));
        description.addTextChangedListener(new MyTextWatcher(description));
        userid.addTextChangedListener(new MyTextWatcher(userid));
        username.addTextChangedListener(new MyTextWatcher(username));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.normal_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] args = new String[6];

                args[0] = clinic.getResponse(); //args[0] = clinicidstr;
                args[1] = String.valueOf(1); //args[1] = useridstr;
                args[2] = ratingstr;
                args[3] = titlestr;
                args[4] = descriptionstr;
                args[5] = loggedInUserName; //args[5] = usernamestr;

                new AsyncHttpTask().execute(args);

            }
        });
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.input_clinic_id:
                    // TODO: 23/5/2016 validation
                    break;
                case R.id.input_userid:
                    // TODO: 23/5/2016 validation
                    break;
                case R.id.input_clinic_rate:
                    // TODO: 23/5/2016 validation
                    ratingstr = rating.getText().toString();
                    break;
                case R.id.input_clinic_rate_title:
                    // TODO: 23/5/2016 validation
                    titlestr = title.getText().toString();
                    break;
                case R.id.input_clinic_rate_description:
                    // TODO: 23/5/2016 validation
                    descriptionstr = description.getText().toString();
                    break;
                case R.id.input_name:
                    // TODO: 23/5/2016 validation
                    break;
            }
        }
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ClinicsRatingActivity.this);
            pDialog.setMessage("Creating rating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 1;

            try {

                JSONStringer js = new JSONStringer().object()
                        .key("clinicId").object()
                            .key("id").value(clinic.getResponse())
                            .key("clinicName").value(clinic.getName())
                            .key("clinicType").value(clinic.getJenis())
                            .key("address").value(clinic.getAddress())
                            .key("latitude").value(clinic.getLat())
                            .key("longitude").value(clinic.getLng())
                            .key("tel").value(clinic.getPhoto())
                            .endObject()
                        .key("userId").value(params[1])
                        .key("rate").value(params[2])
                        .key("title").value(params[3])
                        .key("description").value(params[4])
                        .key("signinUsername").value(params[5])
                        .endObject();

                JSONObject jo = new JSONObject(js.toString());
                //Log.e("js:= ", js.toString());

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = (HttpPost) createPostForJSONObject(jo, CLINICLOCATOR_PANEL_API_ENDPOINT);
                HttpResponse response = httpclient.execute(post);

            } catch (Exception e) {
                result = 0;
                Log.e("doInBackground:= ", "Failed to fetch data!");
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            pDialog.dismiss();

            if (result == 1) {

                Intent i = new Intent(ClinicsRatingActivity.this, DetailsActivity.class);
                i.putExtra(DetailsActivity.ID, clinic);
                i.putExtra(DetailsActivity.FROM, "locator");
                i.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();

            } else {
                Log.e("onPostExecute:= ", "Failed to fetch data!");
            }
        }

    }

    public static HttpUriRequest createPostForJSONObject(JSONObject params, String url) {
        HttpPost post = new HttpPost(url);
        post.setEntity(createStringEntity(params));
        return post;
    }

    private static HttpEntity createStringEntity(JSONObject params) {
        StringEntity se = null;
        try {
            se = new StringEntity(params.toString(), "UTF-8");
            se.setContentType("application/json; charset=UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("createStringEntity:= ", "Failed to create StringEntity", e);
        }
        return se;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backtolist = new Intent(ClinicsRatingActivity.this, DetailsActivity.class);
                backtolist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                backtolist.putExtra(DetailsActivity.ID, clinic);
                backtolist.putExtra(DetailsActivity.FROM, "locator");
                backtolist.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(backtolist);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
