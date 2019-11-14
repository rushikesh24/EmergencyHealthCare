package health.care;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterBloodBank extends AppCompatActivity {


    private EditText name, email, password, state, city, pincode, vehicleno, mobilenumber, detailAddress;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference, locationReference;
    private ProgressDialog registerDialog;
    private RadioButton mortuary_van, ambulance;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_blood_bank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tx = (TextView) findViewById(R.id.toolbarTitle);
        tx.setText("Blood Bank Service");


        //REFERENCES
        email = findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);
        state = (EditText) findViewById(R.id.state);
        city = (EditText) findViewById(R.id.city);
        pincode = (EditText) findViewById(R.id.pincode);
        mobilenumber = (EditText) findViewById(R.id.mobileno);
        detailAddress = (EditText) findViewById(R.id.detailAddress);

        //---------------------------------All instances-------------------
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blood Banks");


        registerDialog = new ProgressDialog(this);
        registerDialog.setMessage("Registering..");
        //---------------------------------End of all instances-------------------


    }//---------------------End Of onCreate Method--------------------------------


    //----------------------------------------Register Button-----------------------------------
    public void registerButtonClicked(View view) {
        final String emailstr = email.getText().toString().trim();

        final String namevalue = name.getText().toString().trim();
        final String statevalue = state.getText().toString().trim();
        final String cityvalue = city.getText().toString().trim();
        final String pincodevalue = pincode.getText().toString().trim();
        final String detailAddressValue = detailAddress.getText().toString().trim();
        final String mobilenovalue = mobilenumber.getText().toString();

        if (!TextUtils.isEmpty(namevalue) && !TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(cityvalue)) {
            registerDialog.show();
            DatabaseReference currunt_user_db = databaseReference.child(cityvalue).push();
            currunt_user_db.child("Email").setValue(emailstr);
            currunt_user_db.child("Image").setValue("Default");
            currunt_user_db.child("Name").setValue(namevalue);
            currunt_user_db.child("State").setValue(statevalue);
            currunt_user_db.child("City").setValue(cityvalue);
            currunt_user_db.child("Pin Code").setValue(pincodevalue);
            currunt_user_db.child("Mobileno").setValue(mobilenovalue);
            currunt_user_db.child("Address").setValue(detailAddressValue);

            startActivity(new Intent(RegisterBloodBank.this, RegistrationSuccessful.class).putExtra("Activity Name","Blood Bank"));
            registerDialog.dismiss();
            finish();
        } else {
            Toast.makeText(RegisterBloodBank.this, "Please fill all fields", Toast.LENGTH_LONG).show();
        }
    }
}