package health.care;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class BloodBankStock extends AppCompatActivity {

    WebView stock_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank_stock);

        stock_view = (WebView) findViewById(R.id.blood_bank_stock);

        String d_link =  getIntent().getExtras().getString("Link");

        stock_view.loadUrl(d_link);

    }
}
