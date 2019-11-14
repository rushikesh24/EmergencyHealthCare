package health.care;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RegistrationSuccessful extends AppCompatActivity {


    private ImageView rsb_icon;
    private TextView rsb_text;
    private LinearLayout rsb_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_successful);

        rsb_icon = (ImageView) findViewById(R.id.rs_image);
        rsb_text = (TextView) findViewById(R.id.rs_text);
        rsb_button = (LinearLayout) findViewById(R.id.rsb_button);

        String identify = getIntent().getExtras().getString("Activity Name");

        if(identify.equals("Blood Bank")){
            rsb_icon.setImageResource(R.drawable.backhome);
            rsb_text.setText("Continue to Home");
        }else if(identify.equals("Vehicle")){
            rsb_icon.setImageResource(R.drawable.fabuser);
            rsb_text.setText("Continue to Profile");
        }

        rsb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String help = rsb_text.getText().toString();
                if(help.equals("Continue to Home")){
                    startActivity(new Intent(RegistrationSuccessful.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else if(help.equals("Continue to Profile")){
                    startActivity(new Intent(RegistrationSuccessful.this,ProvidersProfile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });

    }
}
