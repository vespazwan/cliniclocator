package ihs.com.cliniclocator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class ViewRatingActivity extends AppCompatActivity {

    public final static String ID = "ID";
    public final static String FROM = "FROM";
    private static final String parcelableKey = "clinicparcelable";

    public String from;
    public Clinic clinic;
    private static ArrayList<Clinic> clinics;

    private ArrayList<HashMap<String, String>> ratingList = new ArrayList<HashMap<String, String>>();

    private String responseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rating);

        //start here
        Intent it = getIntent();
        from = it.getExtras().getString(FROM);
        clinic = it.getParcelableExtra(ID);
        clinics = it.getParcelableArrayListExtra(parcelableKey);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Rating Details");
        
        //rating list
        new SetRatingAsync().execute();

    }

    private class SetRatingAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try{

                ObjectList objRatingList = new ObjectList();
                ratingList = objRatingList.ArrClinicRatingListLoad(Long.parseLong(clinic.getResponse()));
                responseMessage = "";
                
            } catch (Exception e) {

                responseMessage = "Error View Rating";
                e.printStackTrace();

            }
            return null;
        }

        protected void onPostExecute(String str) {

            if (responseMessage.trim().length() > 0) {

                Intent intent = new Intent(ViewRatingActivity.this, FullscreenCheckConnectionActivity.class);
                intent.putExtra("errmsg", responseMessage);
                startActivity(intent);
                finish();

            } else {

                RecyclerView rv = (RecyclerView) findViewById(R.id.rv); // layout reference
                TextView emptyView = (TextView) findViewById(R.id.emptyView);

                if(ratingList != null && ratingList.size()>0){

                    rv.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    LinearLayoutManager llm = new LinearLayoutManager(ViewRatingActivity.this);
                    rv.setLayoutManager(llm);
                    rv.setHasFixedSize(true); // to improve performance

                    //animation
                    DataManagerRating adapter = new DataManagerRating(ratingList); // the data manager is assigner to the RV
                    AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
                    alphaAdapter.setFirstOnly(false);
                    alphaAdapter.setDuration(1000);
                    alphaAdapter.setInterpolator(new OvershootInterpolator(.5f));
                    rv.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

                } else {

                    rv.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);

                }

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backtolist = new Intent(ViewRatingActivity.this, DetailsActivity.class);
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
