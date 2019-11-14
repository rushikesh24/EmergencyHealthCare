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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
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


public class BloodBankList extends AppCompatActivity {

    private String[] maps={"Google Maps"};
    private static final int PERMISSION_REQUEST = 1 ;
    private RecyclerView socialList;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference databaseReference;
    private String query = "",link="";
    private EditText queryText;
    private ImageView call,name,number;
    private ProgressBar p_bar;
    private TextView results,address;



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
        setContentView(R.layout.activity_blood_bank_list);

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
        }


        queryText = findViewById(R.id.search);
        queryText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onSearchClicked();
                    return true;
                }
                return false;
            }
        });

        socialList = (RecyclerView) findViewById(R.id.datalist);
        socialList.setItemAnimator(new SlideInLeftAnimator());
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        socialList.setItemAnimator(animator);
        socialList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BloodBankList.this);
        socialList.setLayoutManager(mLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blood Banks");
        p_bar = findViewById(R.id.v_search_proggressbar);
        name = findViewById(R.id.namedisplay);
        number= findViewById(R.id.mobilenumber);
        results = findViewById(R.id.results);
        address = findViewById(R.id.address);


    }


    //---------------------------Firebase data loading-----------------------------
    public void loadData(){
        p_bar.setVisibility(ProgressBar.VISIBLE);
        FirebaseRecyclerAdapter<GetSet,ContentHolder> FBRA = new FirebaseRecyclerAdapter<GetSet,ContentHolder>(
                GetSet.class,
                R.layout.blood_bank_card,
                ContentHolder.class,
                databaseReference.child(query)

        ) {
            @Override
            protected void populateViewHolder(ContentHolder viewHolder, GetSet model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setMobileno(model.getMobileno());
                viewHolder.setAddress(model.getAddress());
                results.setText("Total "+socialList.getAdapter().getItemCount()+" results for "+query);
                View v  = findViewById(R.id.separator);
                v.setVisibility(View.VISIBLE);
                p_bar.setVisibility(ProgressBar.GONE);
                final String post_key=getRef(position).getKey().toString();

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

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    String d_link;
                    @Override
                    public void onClick(View view) {
                        databaseReference.child(query).child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                d_link = dataSnapshot.child("Stock Link").getValue().toString();
                                Log.d("d_link",d_link);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent i = new Intent(BloodBankList.this,BloodBankStock.class);
                        i.putExtra("Link",d_link);
                        startActivity(i);
                    }
                });

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

    private void onSearchClicked() {
        query = queryText.getText().toString().trim();
        if(!TextUtils.isEmpty(query)) {
            loadData();
        }
        else
            queryText.setError("Enter City");
    }

}

