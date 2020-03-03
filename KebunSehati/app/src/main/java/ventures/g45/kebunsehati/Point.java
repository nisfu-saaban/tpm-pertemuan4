package ventures.g45.kebunsehati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Point extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    Intent intent;
    String noHp, kode, defaultUrl, urlGetHomeData, token, kategori, dataUrl;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView txtTitle, txtYourPoint, txtPoint, txtTitleRiwayatTransaksi, noTransaksi;
    ImageView icoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        kategori = sharedPreferences.getString("kategori", "1");
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetHomeData = defaultUrl + "gethomedata.html";

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTypeface(openSansSemiBold);
        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtYourPoint = (TextView) findViewById(R.id.txtYourPoint);
        txtYourPoint.setTypeface(openSansSemiBold);
        txtPoint = (TextView) findViewById(R.id.txtPoint);
        txtPoint.setTypeface(openSansRegular);
        txtTitleRiwayatTransaksi = (TextView) findViewById(R.id.txtTitleRiwayatTransaksi);
        txtTitleRiwayatTransaksi.setTypeface(openSansSemiBold);
        noTransaksi = (TextView) findViewById(R.id.noTransaksi);
        noTransaksi.setTypeface(openSansLight);
    }
}
