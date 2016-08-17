package ihs.com.cliniclocator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("About Us");

        // code here
        ImageView ivLogo = (ImageView) findViewById(R.id.imgloadingscreenabout);
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://h2care.com.my/cliniclocator"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        TextView tvLoading = (TextView) findViewById(R.id.fullscreen_content_about);

        TextView tvLoadingv = (TextView) findViewById(R.id.fullscreen_content_version);

        TextView tvVisit = (TextView) findViewById(R.id.visit_web);
        tvVisit.setMovementMethod(LinkMovementMethod.getInstance());

        Button btnPromote = (Button) findViewById(R.id.btn_promote);
        btnPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        Button btnRate = (Button) findViewById(R.id.btn_rate);
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMarket();
            }
        });

        String copytext = "Copyright \u00a9 2016 Integrated Healthcare Solutions Sdn. Bhd. All rights reserved";
        TextView tvCopyright = (TextView) findViewById(R.id.fullscreen_content_copyright);
        tvCopyright.setText(copytext);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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
}
