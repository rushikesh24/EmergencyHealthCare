package health.care;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FloatingActionButton profile;
    private LinearLayout provideservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() == null){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.BloodBankLayout), "Please Connect to Internet", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
        profile = findViewById(R.id.profilefabbutton);
        profile.setEnabled(false);
        provideservice  = findViewById(R.id.provideservice);



        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    profile.setVisibility(FloatingActionButton.VISIBLE);
                    profile.setEnabled(true);
                    profile.setImageResource(R.drawable.fabuser);
                    provideservice.setVisibility(LinearLayout.GONE);
                }
            }
        };

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
            if (isFirstRun)
            {
                    TapTargetView.showFor(this,                 // `this` is an Activity
                            TapTarget.forView(findViewById(R.id.profilefabbutton), "Go to  your Profile", "View your profile details here and manage your location sharing status eaisly \nyou must have to log in first as service provider to use this feature")
                                    // All options below are optional
                                    .outerCircleColor(R.color.red)      // Specify a color for the outer circle
                                    .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                    .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                    .titleTextSize(22)                  // Specify the size (in sp) of the title text
                                    .titleTextColor(R.color.white)      // Specify the color of the title text
                                    .descriptionTextSize(13)            // Specify the size (in sp) of the description text
                                    .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                    .textColor(R.color.white)            // Specify a color for both the title and description text
                                    .textTypeface(Typeface.createFromAsset(getAssets(),  "fonts/Montserrat-Bold.otf"))// Specify a typeface for the text
                                    .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                    .drawShadow(true)                   // Whether to draw a drop shadow or not
                                    .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)                   // Whether to tint the target view's color
                                    .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                    //.icon(Drawable)                     // Specify a custom drawable to draw as the target
                                    .targetRadius(40),                  // Specify the target radius (in dp)
                            new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                @Override
                                public void onTargetClick(TapTargetView view) {
                                    //onProfileFabClicked();
                                }
                            });
                    // Code to run once
                    SharedPreferences.Editor editor = wmbPreference.edit();
                    editor.putBoolean("FIRSTRUN", false);
                    editor.commit();
            }
    }

    public void onFindMortuaryVanClicked(View view) throws Exception{
        startActivity(new Intent(this,VanList.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }
    public void onRegisterVanClicked(View view){
        startActivity(new Intent(this,SelectRegistration.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void onAmbulanceSearchClicked(View view){
        startActivity(new Intent(this,Ambulance.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void onBloodBankClicked(View view){
        startActivity(new Intent(this,BloodBankList.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    public void onProfileFabClicked(View view){
        startActivity(new Intent(Home.this,ProvidersProfile.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    public void onProfileFabClicked(){
        startActivity(new Intent(Home.this,ProvidersProfile.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onBackPressed(){
        finish();
    }

}
