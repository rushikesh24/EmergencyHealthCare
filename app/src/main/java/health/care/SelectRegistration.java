package health.care;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SelectRegistration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_registration);
        TextView tx = (TextView) findViewById(R.id.toolbarTitle);
        tx.setText("Provide Your Service");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth=FirebaseAuth.getInstance();
    }

    public void onRegisterMortuaryVanClicked(View view){
        startActivity(new Intent(SelectRegistration.this,Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void onRegisterBloodBankClicked(View view){
        startActivity(new Intent(SelectRegistration.this,RegisterBloodBank.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

}
