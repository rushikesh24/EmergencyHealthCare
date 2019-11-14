package health.care;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.Locale;


public class Details extends AppCompatActivity {

    private String post_key = null;
    private String post_query = null;
    private String name;
    private DatabaseReference databaseReference,locationReference;
    private TextView details_name,details_mno,details_address,details_vno,tx,gpsStatus,shareStatus;
    String postmobileno = "";
    private ProgressBar locationProgress;
    private boolean sent;
    private String identifier;
    private ImageView gpsImg,msg;
    private LinearLayout track;


    private static final String SHOWCASE_ID = "sequence example";
    //LOCATION SHARING CODE
    private String[] maps={"Google Maps"};
    private static final int PERMISSION_REQUEST = 1 ;
    //Location Sharing code
    private ToggleButton gpsButton;
    private Button shareButton;
    private LocationManager locManager;
    private Location lastLocation;


    //LOCATION SHARING
    private final LocationListener locListener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            updateLocation(loc);
        }
        public void onProviderEnabled(String provider) {
            updateLocation();
        }
        public void onProviderDisabled(String provider) {
            updateLocation();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private String phone = "";
    private String track_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        //*****************************Permission Request dialog*************************************
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST);
        }
        tx = (TextView) findViewById(R.id.toolbarTitle);
        tx.setText("Details");

        //Location sharing
        //gpsButton = (Button)findViewById(R.id.gpsButton);
        // Button area
        gpsButton = (ToggleButton) findViewById(R.id.gpsButton);
        shareButton = (Button)findViewById(R.id.shareButton);
        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        shareStatus = findViewById(R.id.shareStatus);
        gpsImg = findViewById(R.id.gpsImg);
        msg = findViewById(R.id.msg);
        track =findViewById(R.id.track);

        identifier = getIntent().getExtras().getString("Identifier");
        if(identifier.equals("Mortuary Van  Registrations")){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Mortuary Van  Registrations");
        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Ambulance Registrations");
        }

        details_name = findViewById(R.id.name);
        details_address = findViewById(R.id.address);
        details_mno  = findViewById(R.id.mobileno);
        details_vno = findViewById(R.id.v_no);
        post_key = getIntent().getExtras().getString("Post ID");
        post_query = getIntent().getExtras().getString("Post Parent");
        locationProgress = findViewById(R.id.location_progress);
        gpsStatus = findViewById(R.id.gpsStatus);

        shareStatus.setText("Location Unavailable");
        track.setEnabled(false);
        msg.setImageResource(R.drawable.warning);

        locationReference=FirebaseDatabase.getInstance().getReference().child("Location").child(post_key);

        locationReference.child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue().equals("available")) {
                        shareStatus.setText("Track Vehicle's Location");
                        track.setEnabled(true);
                        msg.setImageResource(R.drawable.ic_location);
                    }else if(dataSnapshot.getValue().equals("unavailable")){
                        shareStatus.setText("Location Unavailable");
                        track.setEnabled(false);
                        msg.setImageResource(R.drawable.warning);
                    }
                }catch(Exception e){

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        locationReference.child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue().equals("null")) {
                    } else {
                        String value = dataSnapshot.getValue().toString();
                        track_value = value;
                    }
                }catch(Exception e){

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //-------------------------------------Show post Detail----------------------------------
        databaseReference.child(post_query).child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String postName = (String)dataSnapshot.child("Name").getValue();
                postmobileno = (String)dataSnapshot.child("Mobileno").getValue();
                phone = postmobileno;
                String poststate = (String)dataSnapshot.child("State").getValue();
                String postcity = (String)dataSnapshot.child("City").getValue();
                String postpincode = (String)dataSnapshot.child("Pin Code").getValue();
                String postvehicleno = (String)dataSnapshot.child("Vehicle No").getValue();

                details_name.setText(postName);
                name = postName;
                details_mno.setText("+91 "+postmobileno);
                details_address.setText(postcity+" , "+poststate+" "+postpincode);
                details_vno.setText(postvehicleno);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //-------------------------------------End of show post Detail----------------------------------
    }

    //Location Sharing
    @Override
    protected void onStop() {
        super.onStop();
        try {
            locManager.removeUpdates(locListener);
        } catch (SecurityException ignored) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRequestingLocation();
        updateLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST &&
                grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRequestingLocation();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // ----------------------------------------------------
    // UI
    // ----------------------------------------------------
    private void updateLocation() {
        // Trigger a UI update without changing the location
        updateLocation(lastLocation);
    }

    private void updateLocation(Location location) {
        boolean locationEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean waitingForLocation = locationEnabled && !validLocation(location);
        boolean haveLocation = locationEnabled && !waitingForLocation;

        if(locationEnabled == false){
            gpsStatus.setText("Your Mobile GPS is Off");
            gpsButton.setChecked(false);
        }
        else if(locationEnabled==true){
            gpsStatus.setText("Your Mobile GPS is On");
            gpsButton.setChecked(true);
        }

        // Update display area
        //gpsButton.setChecked(locationEnabled ? false : true);
        // Update buttons
        if(haveLocation == false){
            locationProgress.setVisibility(ProgressBar.VISIBLE);
            shareButton.setText("Getting Your Location...");
            shareButton.setEnabled(false);
        }else if (haveLocation == true && sent==false){
            locationProgress.setVisibility(ProgressBar.INVISIBLE);
            shareButton.setText("Share Your Location");
            gpsImg.setVisibility(ImageView.VISIBLE);
            locationProgress.setVisibility(ProgressBar.GONE);
            shareButton.setEnabled(true);
        }



        if (haveLocation) {
            String newline = System.getProperty("line.separator");
            lastLocation = location;
        }
    }

    // ----------------------------------------------------
    // DialogInterface Listeners
    // ----------------------------------------------------
    private class onClickShareListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int i) {
            String link = formatLocation(lastLocation, getResources().getStringArray(R.array.link_templates)[i]);
            sendSMS(phone,link);
        }
    }

    public void sendSMS(String phoneNo , String msg ){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
            shareButton.setEnabled(false);
            shareButton.setText("Location Shared");
            gpsImg.setImageResource(R.drawable.verify);
            sent = true;
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    // ----------------------------------------------------
    // Actions
    // ----------------------------------------------------
    public void shareLocation(View view) {
        if (!validLocation(lastLocation)) {
            return;
        }
        new AlertDialog.Builder(this).setTitle("Choose a Link Type")
                .setCancelable(true)
                .setItems(maps, new onClickShareListener())
                .create()
                .show();
    }


    public void openLocationSettings(View view) {
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    // ----------------------------------------------------
    // Helper functions
    // ----------------------------------------------------
    private void startRequestingLocation() {
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
//************************************Request dialog**************
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            return;
        }

        // GPS enabled and have permission - start requesting location updates
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
    }

    private boolean validLocation(Location location) {
        if (location == null) {
            return false;
        }

        // Location must be from less than 30 seconds ago to be considered valid

        return SystemClock.elapsedRealtime() - location.getElapsedRealtimeNanos() < 30e9;
    }

    private String getAccuracy(Location location) {
        float accuracy = location.getAccuracy();
        if (accuracy < 0.01) {
            return "?";
        } else if (accuracy > 99) {
            return "99+";
        } else {
            return String.format(Locale.US, "%2.0fm", accuracy);
        }
    }

    private String getLatitude(Location location) {
        return String.format(Locale.US, "%2.5f", location.getLatitude());
    }

    private String getLongitude(Location location) {
        return String.format(Locale.US, "%3.5f", location.getLongitude());
    }

    private String formatLocation(Location location, String format) {
        return MessageFormat.format(format,
                getLatitude(location), getLongitude(location));
    }

    public void onCallClicked(View view){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+postmobileno));
        //Ignore this error
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    public void onTrackClicked(View view){
        Intent intent = new Intent(Details.this,VehiclesLocation.class);
        intent.putExtra("LOC",track_value);
        intent.putExtra("name",name);
        startActivity(intent);
    }

}
