package ihs.com.cliniclocator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro2 {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(first_fragment);
//        addSlide(second_fragment);
//        addSlide(third_fragment);
//        addSlide(fourth_fragment);
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_first));
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_second));
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_third));
        addSlide(IntroFragment.newInstance(R.layout.intro_fragment_fourth));


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
//        addSlide(AppIntroFragment.newInstance("Welcome and thank you for downloading ClinicLocator",
//                "This app provides the fastest way to find clinics in Malaysia on your smartphone (Version IOS will be available soon)",
//                R.drawable.intro1, Color.parseColor("#1abc9c")));
//        addSlide(AppIntroFragment.newInstance("Your health distress app to find list of clinics nearest to your location",
//                "And map view of private (red) and government clinics (blue)",
//                R.drawable.intro2, Color.parseColor("#2980b9")));
//        addSlide(AppIntroFragment.newInstance("Search preferences base on names, street and towns near your location",
//                "Call the clinic before you arrive. Share details of clinic location and direction with your family or friends",
//                R.drawable.intro3, Color.parseColor("#e74c3c")));
//        addSlide(AppIntroFragment.newInstance("You are all set. Enjoy.",
//                "Get Started",
//                R.drawable.intro4, Color.parseColor("#00a19a")));

        // OPTIONAL METHODS

        // SHOW or HIDE the statusbar
        showStatusBar(false);

        // Edit the color of the nav bar on Lollipop+ devices
//        setNavBarColor(Color.parseColor("#3F51B5"));

        // Turn vibration on and set intensity
        // NOTE: you will need to ask VIBRATE permission in Manifest if you haven't already
        //setVibrate(true);
        //setVibrateIntensity(30);

        // Animations -- use only one of the below. Using both could cause errors.
        setFadeAnimation(); // OR
//        setZoomAnimation(); // OR
//        setFlowAnimation(); // OR
//        setSlideOverAnimation(); // OR
//        setDepthAnimation(); // OR
//        setCustomTransformer(yourCustomTransformer);

        // Permissions -- takes a permission and slide number
//        askForPermissions(new String[]{Manifest.permission.CAMERA}, 3);
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }
}