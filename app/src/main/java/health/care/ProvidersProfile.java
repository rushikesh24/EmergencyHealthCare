package health.care;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import GPS.GPSTracker;

public class ProvidersProfile extends AppCompatActivity implements LocationListener{

    private static final int PERMISSION_REQUEST = 1 ;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView details_name,details_mno,details_address,details_vno,tx,gpsStatus;
    private DatabaseReference databaseReference , locationReference;
    private DatabaseReference userReference;
    FirebaseUser user;
    String UID;
    ToggleButton LocationShare;
    private LocationManager locationManager;
    private String message;

    private ProgressDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }

        loginDialog =new ProgressDialog(this);
        loginDialog.setMessage("Loading...");
        loginDialog.show();

        mAuth=FirebaseAuth.getInstance();
        TextView tx = (TextView) findViewById(R.id.toolbarTitle);
        tx.setText("Profile");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //-------------------All Instances---------------------------------------------
        details_name = findViewById(R.id.name);
        details_address = findViewById(R.id.address);
        details_mno  = findViewById(R.id.mobileno);
        details_vno = findViewById(R.id.v_no);
        gpsStatus = findViewById(R.id.gpsStatus);

        user = mAuth.getCurrentUser();
        UID = mAuth.getCurrentUser().getUid();

        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Mortuary Van  Registrations");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);

        // initiate a Switch
        LocationShare = (ToggleButton) findViewById(R.id.shareLocation);
        LocationShare.setChecked(false);
        locationReference = FirebaseDatabase.getInstance().getReference().child("Location").child(UID);
        locationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Location").getValue().toString().equals("null")){
                }
                else{
                    LocationShare.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String users_city = (String) dataSnapshot.child("City").getValue();
                final String users_type = (String) dataSnapshot.child("Type").getValue();
                databaseReference = FirebaseDatabase.getInstance().getReference().child(users_type);

                try {//-------------------------------------Show post Detail----------------------------------
                    databaseReference.child(users_city).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String postName = (String) dataSnapshot.child("Name").getValue();
                            String postmobileno = (String) dataSnapshot.child("Mobileno").getValue();
                            String poststate = (String) dataSnapshot.child("State").getValue();
                            String postcity = (String) dataSnapshot.child("City").getValue();
                            String postpincode = (String) dataSnapshot.child("Pin Code").getValue();
                            String postvehicleno = (String) dataSnapshot.child("Vehicle No").getValue();

                            details_name.setText(postName);
                            details_mno.setText("+91 " + postmobileno);
                            details_address.setText(postcity + " , " + poststate + " " + postpincode);
                            details_vno.setText(postvehicleno);
                            loginDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e){

                }
                //-------------------------------------End of show post Detail----------------------------------

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(ProvidersProfile.this, Login.class));
                    finish();
                }
            }
        };*/

    }
    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);

    }
    //---------------------------------Menu item----------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profilemenu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(this,Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();

           /* case R.id.deleteacc:
                try{
                    userReference.child(UID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String users_city = (String) dataSnapshot.child("City").getValue();
                            databaseReference.child(users_city).child(UID).removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    userReference.child(UID).removeValue();
                    startActivity(new Intent(this,Home.class));
                    user.delete();
                    mAuth.signOut();
                    finish();
                }catch (Exception e){

                }*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //---------------------------------End of Menu item----------------------------------------------------z


    @Override
    public void onLocationChanged(Location location) {
        String msg = "" + location.getLatitude() + " , " + location.getLongitude();
        message = msg;
        updateLocation(msg);
        LocationShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LocationShare.isChecked()==false){
                    locationReference.child("Location").setValue("null");
                    locationReference.child("Status").setValue("unavailable");
                }
                else{
                    locationReference.child("Location").setValue(message);
                    locationReference.child("Status").setValue("available");
                }
            }
        });
    }

    private void updateLocation(String msg) {
        if(LocationShare.isChecked()==false){
            locationReference.child("Location").setValue("null");
        }
        else{
            locationReference.child("Location").setValue(msg);
        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "GPS is turned off!! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "GPS is turned on!! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}

