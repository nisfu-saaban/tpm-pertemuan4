package ventures.g45.kebunsehati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlacingOrder extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    Intent intent;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String token, noHp, ketPembayaran;
    Button btnLanjutkan;
    TextView txtSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placing_order);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        noHp = sharedPreferences.getString("noHp", "");

        intent = getIntent();
        ketPembayaran = intent.getStringExtra("ketPembayaran");

        txtSuccess = (TextView) findViewById(R.id.txtSuccess);
        txtSuccess.setTypeface(openSansRegular);

        btnLanjutkan = (Button) findViewById(R.id.btnLanjutkan);
        btnLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(PlacingOrder.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
