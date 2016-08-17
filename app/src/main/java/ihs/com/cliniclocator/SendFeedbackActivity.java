package ihs.com.cliniclocator;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SendFeedbackActivity extends AppCompatActivity {

    private static final String parcelableKey = "clinicparcelable";
    private static ArrayList<Clinic> clinics;
    private static ArrayList<HashMap<String, String>> clinicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_send_feedback);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Send Feedback");

        // code here
        initFormFeedback();

    }

    public void initFormFeedback(){
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Light.ttf");

        TextView textViewTo = (TextView) findViewById(R.id.textViewTo);
        textViewTo.setTypeface(tf);

        final TextView textViewToEmail = (TextView) findViewById(R.id.textViewToEmail);
        textViewToEmail.setTypeface(tf);

        TextView textViewSubject = (TextView) findViewById(R.id.textViewSubject);
        textViewSubject.setTypeface(tf);

        final EditText editTextSubject = (EditText) findViewById(R.id.editTextSubject);
        editTextSubject.setTypeface(tf);

        TextView textViewMessage = (TextView) findViewById(R.id.textViewMessage);
        textViewMessage.setTypeface(tf);

        final EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        editTextMessage.setTypeface(tf);

        Button buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setTypeface(tf);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = textViewToEmail.getText().toString();
                String subject = "Feedback for ClinicLocator - " + editTextSubject.getText().toString();
                String message = editTextMessage.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setTypeface(tf);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
}
