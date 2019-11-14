package health.care;

/**
 * Created by Sandeep on 2/22/2018.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText name,email,password,state,city,pincode,vehicleno,mobilenumber;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference,locationReference;
    private ProgressDialog registerDialog;
    private RadioGroup vehicle_type;
    private RadioButton rb;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_van_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tx = (TextView) findViewById(R.id.toolbarTitle);
        tx.setText("Provide Your Service");


        //REFERENCES
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        state = (EditText) findViewById(R.id.state);
        city = (EditText) findViewById(R.id.city);
        pincode = (EditText) findViewById(R.id.pincode);
        vehicleno = (EditText) findViewById(R.id.vehicleno);
        mobilenumber = (EditText) findViewById(R.id.mobileno);

        vehicle_type = (RadioGroup) findViewById(R.id.vehicle_type);
        vehicle_type.clearCheck();

        vehicle_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(rb.getText().toString());
                }

            }
        });


        //---------------------------------All instances-------------------
        auth = FirebaseAuth.getInstance();

        locationReference = FirebaseDatabase.getInstance().getReference().child("Location");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");


        registerDialog=new ProgressDialog(this);
        registerDialog.setMessage("Registering..");
        //---------------------------------End of all instances-------------------



    }//---------------------End Of onCreate Method--------------------------------



    //----------------------------------------Register Button-----------------------------------
    public void registerButtonClicked(View view) {
        final String emailstr = email.getText().toString().trim();
        final String passstr = password.getText().toString().trim();

        final String namevalue = name.getText().toString().trim();
        final String statevalue = state.getText().toString().trim();
        final String cityvalue = city.getText().toString().trim();
        final String pincodevalue = pincode.getText().toString();
        final String vehiclenovalue = vehicleno.getText().toString();
        final String mobilenovalue = mobilenumber.getText().toString();

        if (!TextUtils.isEmpty(namevalue) && !TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(passstr)) {
            registerDialog.show();
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = auth.getCurrentUser().getUid();
                        DatabaseReference currunt_user_db = databaseReference.child(cityvalue).child(user_id);
                        DatabaseReference db_location = locationReference.child(user_id);
                        db_location.child("Location").setValue("null");
                        db_location.child("Status").setValue("unavailable");
                        DatabaseReference user_city = userReference.child(user_id);
                        user_city.child("City").setValue(cityvalue);
                        user_city.child("Name").setValue(namevalue);
                        user_city.child("Type").setValue(rb.getText().toString());
                        currunt_user_db.child("Email").setValue(emailstr);
                        currunt_user_db.child("Image").setValue("Default");
                        currunt_user_db.child("Name").setValue(namevalue);
                        currunt_user_db.child("State").setValue(statevalue);
                        currunt_user_db.child("City").setValue(cityvalue);
                        currunt_user_db.child("Pin Code").setValue(pincodevalue);
                        currunt_user_db.child("Vehicle No").setValue(vehiclenovalue);
                        currunt_user_db.child("Mobileno").setValue(mobilenovalue);
                        startActivity(new Intent(Register.this, RegistrationSuccessful.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("Activity Name","Vehicle"));
                        registerDialog.dismiss();
                        finish();
                    }
                    registerDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_LONG).show();
        }
    }
    //----------------------------------------End of register Button-----------------------------------
}

