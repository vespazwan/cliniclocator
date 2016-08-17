package ihs.com.cliniclocator;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class LocatorActivity extends AppCompatActivity {

    private static final String parcelableKey = "clinicparcelable";
    private static ArrayList<Clinic> clinics;
    private static ArrayList<HashMap<String, String>> clinicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        //get parcelable
        Intent it = getIntent();
        clinics = it.getParcelableArrayListExtra(parcelableKey);
        clinicList = new ArrayList<HashMap<String, String>>();
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
                clinicList.add(clinicMap);
            }
        }

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search Result");

        //start here
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv); // layout reference
        TextView emptyView = (TextView) findViewById(R.id.emptyView);

        if(clinicList != null && clinicList.size()>0){
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            rv.setHasFixedSize(true); // to improve performance

            //animation
            DataManager adapter = new DataManager(clinicList); // the data manager is assigner to the RV
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            alphaAdapter.setFirstOnly(false);
            alphaAdapter.setDuration(1000);
            alphaAdapter.setInterpolator(new OvershootInterpolator(.5f));
            rv.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

            //rv.setAdapter(new DataManager(clinicList));
            rv.addOnItemTouchListener( // and the click is handled
                    new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            Clinic clinic = clinics.get(position);

                            Intent intent = new Intent(LocatorActivity.this, DetailsActivity.class);
                            intent.putExtra(DetailsActivity.ID, clinic);
                            intent.putExtra(DetailsActivity.FROM, "locator");
                            intent.putParcelableArrayListExtra(parcelableKey, clinics);

                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    // the context of the activity
                                    LocatorActivity.this,

                                    // For each shared element, add to this method a new Pair item,
                                    // which contains the reference of the view we are transitioning *from*,
                                    // and the value of the transitionName attribute
                                    new Pair<View, String>(view.findViewById(R.id.CONTACT_circle_jarak),
                                            getString(R.string.transition_name_circle)),
                                    new Pair<View, String>(view.findViewById(R.id.CONTACT_name),
                                            getString(R.string.transition_name_name)),
                                    new Pair<View, String>(view.findViewById(R.id.CONTACT_phone),
                                            getString(R.string.transition_name_phone))
                            );
                            ActivityCompat.startActivity(LocatorActivity.this, intent, options.toBundle());
                        }
                    }));

            //rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        } else {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }


        //end here
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);

        /*
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            /*
            case R.id.action_settings:
                Intent gotoabout = new Intent(LocatorActivity.this, AboutActivity.class);
                gotoabout.putParcelableArrayListExtra(parcelableKey, clinics);
                startActivity(gotoabout);
                return true;
                */
            case R.id.action_map:
                Intent it = getIntent();
                ArrayList<Clinic> clinics = it.getParcelableArrayListExtra("clinicparcelable");
                Intent gotomap = new Intent(LocatorActivity.this, MapActivity.class);
                gotomap.putExtra(MapActivity.FROM, "locator");
                gotomap.putParcelableArrayListExtra("clinicparcelable", clinics);
                startActivity(gotomap);
                return true;
            case android.R.id.home:
                Intent backtolist = new Intent(LocatorActivity.this, LocatorResultActivity.class);
                backtolist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backtolist);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
