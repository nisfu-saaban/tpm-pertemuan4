package ventures.g45.kebunsehati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SearchingResult extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    TextView txtTitleNoResult, txtTitleSpesial;
    ImageView icoBack;
    LinearLayout barisanSpesial;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat decimalFormat;
    float factor;
    String kategori, noHp, defaultUrl, urlSearching, dataUrl, kataKunci;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_result);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        kategori = sharedPreferences.getString("kategori", "1");
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlSearching = defaultUrl + "searchingproduct.html";

        factor = getResources().getDisplayMetrics().density;
        decimalFormat = new DecimalFormat("#,###.##");

        intent = getIntent();
        kataKunci = intent.getStringExtra("kata");

        txtTitleNoResult = (TextView) findViewById(R.id.txtTitleNoResult);
        txtTitleNoResult.setTypeface(openSansSemiBold);
        txtTitleSpesial = (TextView) findViewById(R.id.txtTitleSpesial);
        txtTitleSpesial.setTypeface(openSansSemiBold);
        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        barisanSpesial = (LinearLayout) findViewById(R.id.barisanSpesial);

        new Pencarian().execute(kataKunci);
    }

    private class Pencarian extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SearchingResult.this);
            pDialog.setMessage("Mengambil data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("kategori", kategori));
            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("kata", args[0]));

            JSONObject json = jsonParser.makeHttpRequest(urlSearching, "POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    } else {
                        if (result.getInt("cari") == 1) {
                            txtTitleNoResult.setVisibility(View.GONE);
                            txtTitleSpesial.setVisibility(View.GONE);
                        } else {
                            txtTitleNoResult.setVisibility(View.VISIBLE);
                            txtTitleSpesial.setVisibility(View.VISIBLE);
                        }
                        JSONArray cartArray = new JSONArray(result.getString("spesial"));
                        if (cartArray.length() > 0) {
                            for (int i = 0; i < cartArray.length(); i++) {

                                final JSONObject cart = cartArray.getJSONObject(i);

                                LinearLayout linearLayoutCart = new LinearLayout(SearchingResult.this);
                                linearLayoutCart.setWeightSum(2);
                                linearLayoutCart.setBackgroundColor(getResources().getColor(android.R.color.white));

                                LinearLayout linearLayoutKiri = new LinearLayout(SearchingResult.this);

                                LinearLayout.LayoutParams layoutParamsKiri = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f);
                                linearLayoutKiri.setLayoutParams(layoutParamsKiri);

                                ImageView thumbnail = new ImageView(SearchingResult.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + cart.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(90*factor), (int)(90*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                linearLayoutKiri.addView(thumbnail);

                                linearLayoutCart.addView(linearLayoutKiri);

                                LinearLayout linearLayoutKanan = new LinearLayout(SearchingResult.this);
                                linearLayoutKanan.setOrientation(LinearLayout.VERTICAL);

                                LinearLayout.LayoutParams layoutParamsKanan = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f);
                                linearLayoutKanan.setLayoutParams(layoutParamsKanan);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(SearchingResult.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(5*factor), (int)(5*factor), (int)(5*factor), (int)(5*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(16);
                                nama.setText( cart.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(nama);

                                ViewGroup.LayoutParams layoutSatuan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView satuan = new TextView(SearchingResult.this);
                                satuan.setLayoutParams(layoutSatuan);
                                satuan.setPadding((int)(5*factor), 0, (int)(5*factor), (int)(5*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( decimalFormat.format(cart.getInt("qty")) + " " + cart.getString("nama_satuan"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(SearchingResult.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( "IDR " + decimalFormat.format(cart.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(harga);

                                linearLayoutCart.addView(linearLayoutKanan);

                                LinearLayout.LayoutParams layoutParamsCart = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParamsCart.setMargins(0, 0, 0, (int)(20*factor));

                                linearLayoutCart.setLayoutParams(layoutParamsCart);

                                linearLayoutCart.setOnClickListener(new DetailProduk(cart.getString("id_produk")));

                                barisanSpesial.addView(linearLayoutCart);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public class DetailProduk implements View.OnClickListener {

        String id;
        public DetailProduk(String id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            intent = new Intent(SearchingResult.this, Detail.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }
}
