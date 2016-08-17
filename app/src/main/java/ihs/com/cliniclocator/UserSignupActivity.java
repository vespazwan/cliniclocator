package ihs.com.cliniclocator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

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

public class UserSignupActivity extends AppCompatActivity {

    // Comment either or
    //protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://apicliniclocator-ihssb.rhcloud.com/"; // PROD V1
    protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://apicliniclocatorv2-ihssb.rhcloud.com/"; // PROD V2 with rating
    //protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://mywildflyrestv2-emafazillah.rhcloud.com/"; // DEV

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] args = new String[4];

                /*
                    args[0] = getText username;
                    args[1] = getText password;
                    args[2] = getText displayName;
                    args[3] = getText isGoPro;

                */

                new AsyncHttpTask().execute(args);

            }
        });
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(UserSignupActivity.this);
            pDialog.setMessage("Creating user...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 1;
            JSONStringer js;
            JSONObject jo;

            try {

                String url = CLINICLOCATOR_PANEL_API_ENDPOINT + "usersignup";

                js = new JSONStringer().object().key("username").value(params[0])
                        .key("password").value(params[1])
                        .key("displayName").value(params[2])
                        .key("isGoPro").value(params[3])
                        .endObject();

                jo = new JSONObject(js.toString());

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = (HttpPost) createPostForJSONObject(jo, url);
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

                //TODO: Send notification
                Intent i = new Intent(UserSignupActivity.this, DetailsActivity.class);
                startActivity(i);

            } else {
                Log.e("onPostExecute:= ", "Failed to fetch data!");
            }
        }

    }

    public static HttpUriRequest createPostForJSONObject(JSONObject params, String url) {
        HttpPost post = new HttpPost(url);
        post.setEntity(createStringEntity(params));
        Log.e("post:= ", Long.toString(post.getEntity().getContentLength()));
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

}
