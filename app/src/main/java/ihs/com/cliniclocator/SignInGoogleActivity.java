package ihs.com.cliniclocator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class SignInGoogleActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    //Signin button
    private SignInButton signInButton;

    private Button skipBtn;
//    private Button signOutBtn;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    //TextViews
    private TextView textViewName;
    private NetworkImageView profilePhoto;
    private TextView textViewWelcome;

    //Image Loader
    private ImageLoader imageLoader;

    private SharedPreferences prefs;
    private boolean isLogin = true;
    private ProgressBar spinner;

    private ImageView bgImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_google);

        //Initializing Views

        textViewWelcome = (TextView) findViewById(R.id.tvWelcome);

        textViewName = (TextView) findViewById(R.id.textViewName);

        profilePhoto = (CircularNetworkImageView) findViewById(R.id.profileImage);

        skipBtn = (Button) findViewById(R.id.btnSkipLogin);

//        signOutBtn = (Button) findViewById(R.id.btnSignOut);
//        signOutBtn.setVisibility(View.INVISIBLE);

        bgImage = (ImageView) findViewById(R.id.signin_img);
        Picasso.with(SignInGoogleActivity.this).load(R.drawable.sign_in_bg_kl).fit().centerCrop().into(bgImage);

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing signinbutton
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.INVISIBLE);

        //Setting onclick listener to signing button
        signInButton.setOnClickListener(this);
        skipBtn.setOnClickListener(this);
//        signOutBtn.setOnClickListener(this);

        boolean Islogin = PrefUtils.getFromPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_IS_LOGIN, false);
        if(!Islogin){
            //hide text
            textViewWelcome.setVisibility(View.INVISIBLE);
            textViewName.setVisibility(View.INVISIBLE);
            profilePhoto.setVisibility(View.INVISIBLE);
//            signOutBtn.setVisibility(View.INVISIBLE);
        } else {
            initLoggedInView();
            //show text
            textViewWelcome.setVisibility(View.VISIBLE);
            textViewName.setVisibility(View.VISIBLE);
            profilePhoto.setVisibility(View.VISIBLE);
            skipBtn.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
        }

    }

    private void initLoggedInView(){
        String photoURL = PrefUtils.getFromPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_LOGIN_PHOTO_URL, null);
        String displayName = PrefUtils.getFromPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_LOGIN_DISPLAY_NAME, null);

        //Initializing image loader
        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                .getImageLoader();

        imageLoader.get(photoURL,
                ImageLoader.getImageListener(profilePhoto,
                        R.mipmap.ic_launcher_three,
                        R.mipmap.ic_launcher_three));

        profilePhoto.setImageUrl(photoURL, imageLoader);
        textViewName.setText(displayName);
        textViewWelcome.setText("You are already logged in as ");
    }


    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            Log.d("login-result",result.getStatus().toString());
            handleSignInResult(result);
        }
    }


    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            spinner.setVisibility(View.INVISIBLE);
            skipBtn.setVisibility(View.INVISIBLE);
            //show text
            textViewWelcome.setVisibility(View.VISIBLE);
            textViewName.setVisibility(View.VISIBLE);
            profilePhoto.setVisibility(View.VISIBLE);

            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email
            textViewName.setText(acct.getDisplayName());

            //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                    .getImageLoader();

            imageLoader.get(acct.getPhotoUrl().toString(),
                    ImageLoader.getImageListener(profilePhoto,
                            R.mipmap.ic_launcher_three,
                            R.mipmap.ic_launcher_three));

            //Loading image
            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);

            //hide signInButton
            signInButton.setVisibility(View.INVISIBLE);

            //store logged info to SharedPreferences
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_IS_LOGIN, true);
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_LOGIN_DISPLAY_NAME, acct.getDisplayName());
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_LOGIN_EMAIL, acct.getEmail());
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_LOGIN_ID, acct.getId());
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_LOGIN_ID_TOKEN, acct.getIdToken());
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_LOGIN_PHOTO_URL, acct.getPhotoUrl().toString());

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    back();
                }
            }, 3000);

//            prefs = PreferenceManager.getDefaultSharedPreferences(this);
//            prefs.edit().putBoolean("isLogin", isLogin).commit();

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed. Please retry", Toast.LENGTH_LONG).show();
            spinner.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    public void back(){
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == signInButton) {
            //Calling signin
            spinner.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            signIn();
        } else if (v == skipBtn) {
            back();
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_IS_LOGIN, true);
        } else if (v == signInButton) {
            PrefUtils.saveToPrefs(SignInGoogleActivity.this, PrefUtils.PREFS_IS_LOGIN, true);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}