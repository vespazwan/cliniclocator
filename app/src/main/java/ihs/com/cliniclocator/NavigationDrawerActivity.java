package ihs.com.cliniclocator;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private boolean Islogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        //get parcelable
        Intent it = getIntent();
        ArrayList<Clinic> clinics = it.getParcelableArrayListExtra("clinicparcelable");

        // Set the home as default
        //getSupportActionBar().setTitle("Home");
        Bundle args = new Bundle();
        args.putParcelableArrayList("clinicparcelable", clinics);

        Fragment fragment = new HomeFragment();
        fragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
        initIntro(false);

        Islogin = PrefUtils.getFromPrefs(NavigationDrawerActivity.this, PrefUtils.PREFS_IS_LOGIN, false);
        Log.d("isLogin", Islogin+"");
        if(!Islogin){
            initLogin(true);
        }
    }

    public void initIntro(final Boolean start) {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart;
                //boolean isFirstStart = true;
                if(start){
                    isFirstStart = true;
                } else {
                    isFirstStart = getPrefs.getBoolean("firstStart", true);
                }

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(NavigationDrawerActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }

    public void initLogin(final Boolean login) {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart;
                //boolean isFirstStart = true;
                if(login){
                    isFirstStart = true;
                } else {
                    isFirstStart = getPrefs.getBoolean("firstStart", true);
                }

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(NavigationDrawerActivity.this, SignInGoogleActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        Fragment whichFragment = getVisibleFragment();//getVisible method return current visible fragment
        String shareVisible=whichFragment.getClass().toString();
        if(shareVisible.equals(AboutFragment.class.toString()) ||shareVisible.equals(SendFeedbackFragment.class.toString())){
            MenuItem itemMap=menu.findItem(R.id.action_map);
            itemMap.setVisible(false);

            MenuItem itemSearch=menu.findItem(R.id.action_search);
            itemSearch.setVisible(false);
        }

        MenuItem itemSignIn = menu.findItem(R.id.sign_in);
        MenuItem itemAccount = menu.findItem(R.id.account);

        if(!Islogin){
            itemSignIn.setVisible(true);
            itemAccount.setVisible(false);
        } else {
            itemSignIn.setVisible(false);
            itemAccount.setVisible(true);
        }

        // If not "Home" then hide
        /*
        String title = getSupportActionBar().getTitle().toString();
        if ("Home".equalsIgnoreCase(title)) {
            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_map).setVisible(true);
        } else {
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_map).setVisible(false);
        }
        */

        return true;
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = NavigationDrawerActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent it1 = getIntent();
                ArrayList<Clinic> clinics1 = it1.getParcelableArrayListExtra("clinicparcelable");

                Bundle args = new Bundle();
                args.putParcelableArrayList("clinicparcelable", clinics1);
                initCreateFragment(new SendFeedbackFragment(), getString(R.string.app_name));
                return true;
            case R.id.action_map:
                Intent it = getIntent();
                ArrayList<Clinic> clinics = it.getParcelableArrayListExtra("clinicparcelable");
                Intent gotomap = new Intent(NavigationDrawerActivity.this, MapActivity.class);
                gotomap.putExtra(MapActivity.FROM, "");
                gotomap.putParcelableArrayListExtra("clinicparcelable", clinics);
                startActivity(gotomap);
                return true;
            case R.id.nav_info:
                Intent gotoAbout = new Intent(NavigationDrawerActivity.this, AboutActivity.class);
                startActivity(gotoAbout);
                return true;
            case R.id.nav_send:
                Intent gotoFeedback = new Intent(NavigationDrawerActivity.this, SendFeedbackActivity.class);
                startActivity(gotoFeedback);
                return true;
            /*case R.id.nav_promote:
                try
                { Intent promoteIntent = new Intent(Intent.ACTION_SEND);
                    promoteIntent.setType("text/plain");
                    promoteIntent.putExtra(Intent.EXTRA_SUBJECT, "ClinicLocator");
                    String sAux = "Let me recommend you this application\n\n";
                    sAux = sAux + "http://h2care.com.my/cliniclocator";
                    promoteIntent.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(promoteIntent, "choose one"));
                }
                catch(Exception e)
                { //e.toString();
                }
                return true;
            case R.id.nav_rate:
                launchMarket();
                return true;*/
            case R.id.app_intro:
                initIntro(true);
                return true;
            case R.id.sign_in:
                initLogin(true);
                return true;
            case R.id.account:
                initLogin(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    public void initCreateFragment(Fragment fragment, String title){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack("about")
                .commit();


        getSupportActionBar().setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_home) {

            Intent it = getIntent();
            ArrayList<Clinic> clinics = it.getParcelableArrayListExtra("clinicparcelable");

            Bundle args = new Bundle();
            args.putParcelableArrayList("clinicparcelable", clinics);

            fragment = new HomeFragment();
            fragment.setArguments(args);
            //title = "Home";

        // } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_info) {

            fragment = new AboutFragment();
            title = "About Us";

        //} else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

            fragment = new SendFeedbackFragment();
            title = "Send Feedback";

        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        getSupportActionBar().setTitle(title);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
