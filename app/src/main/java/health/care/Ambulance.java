package health.care;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class Ambulance extends AppCompatActivity {

    private String[] maps={"Google Maps"};
    private static final int PERMISSION_REQUEST = 1 ;
    private RecyclerView socialList;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference databaseReference;
    private String query = "";
    private EditText queryText;
    private ImageView call,name,number;
    private ProgressBar p_bar;
    private TextView results;



    /*private ImageView shareButton;
    private LocationManager locManager;
    private Location lastLocation;

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
    private String phone = "8208033855";*/




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance);

        //locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() == null){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.datalist), "Please Connect to Internet", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

        //*****************************Permission Request dialog*************************************
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
         checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
         requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST);
         return;
         }


        queryText = findViewById(R.id.search);

        socialList = (RecyclerView) findViewById(R.id.datalist);
        socialList.setItemAnimator(new SlideInLeftAnimator());
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        socialList.setItemAnimator(animator);
        socialList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(Ambulance.this);
        socialList.setLayoutManager(mLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ambulance Registrations");
        p_bar = findViewById(R.id.v_search_proggressbar);
        name = findViewById(R.id.namedisplay);
        number= findViewById(R.id.mobilenumber);
        results = findViewById(R.id.results);


    }


    //---------------------------Firebase data loading-----------------------------
    public void loadData(){
        p_bar.setVisibility(ProgressBar.VISIBLE);
        FirebaseRecyclerAdapter<GetSet,ContentHolder> FBRA = new FirebaseRecyclerAdapter<GetSet,ContentHolder>(
                GetSet.class,
                R.layout.card,
                ContentHolder.class,
                databaseReference.child(query)

        ) {
            @Override
            protected void populateViewHolder(ContentHolder viewHolder, GetSet model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setMobileno(model.getMobileno());
                results.setText("Total "+socialList.getAdapter().getItemCount()+" results for "+query);
                View v  = findViewById(R.id.separator);
                v.setVisibility(View.VISIBLE);
                p_bar.setVisibility(ProgressBar.GONE);
                final String post_key=getRef(position).getKey().toString();


                //ON CARD VIEW CLICKED
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent pd = new Intent(Ambulance.this,Details.class);
                        pd.putExtra("Post ID",post_key);
                        pd.putExtra("Post Parent",query);
                        pd.putExtra("Identifier","Ambulance Registrations");
                        startActivity(pd);
                    }
                });

                viewHolder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child(query).child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String number = dataSnapshot.child("Mobileno").getValue().toString();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+number));
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

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                

                /*viewHolder.sharelocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child(query).child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                phone = dataSnapshot.child("Mobileno").getValue().toString();
                                if (!validLocation(lastLocation)) {
                                    return;
                                }

                                new AlertDialog.Builder(VanList.this).setTitle("Choose Link Type")
                                        .setCancelable(true)
                                        .setItems(maps, new onClickShareListener())
                                        .create()
                                        .show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });*/
            }
        };
        socialList.setAdapter(FBRA);
    }
    //---------------------------------End of data loading-------------------------------

    public void onSearchClicked(View view){
        query = queryText.getText().toString().trim();
        if(!TextUtils.isEmpty(query)) {
            loadData();
        }
        else
            queryText.setError("Enter City");
    }





















   /*
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

        // Update buttons
        shareButton.setEnabled(haveLocation);

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
*/
}

