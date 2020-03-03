package ventures.g45.kebunsehati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    TextView inpSearch, txtTitlePromo, txtPaket, txtKategori;
    TextView txtYourPoint, txtPoint, txtBestSeller, txtKatalogProduk, txtTotalCart;
    Intent intent;
    String noHp, kode, defaultUrl, urlGetHomeData, token, kategori, dataUrl;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout barisanKategori, barisanBestSeller, blockKatalogProduk, barisanPromo, barisanPaket;
    float factor;
    DecimalFormat decimalFormat;
    BottomNavigationView bottomNavigationView;
    RelativeLayout blockNotif, blockPointnya, blockFreeOngkir;
    ImageView icoPoint;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    final String[] sAlamat = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        barisanKategori = (LinearLayout) findViewById(R.id.barisanKategori);
        barisanBestSeller = (LinearLayout) findViewById(R.id.barisanBestSeller);
        barisanPromo = (LinearLayout) findViewById(R.id.barisanPromo);
        barisanPaket = (LinearLayout) findViewById(R.id.barisanPaket);
        factor = getResources().getDisplayMetrics().density;

        decimalFormat = new DecimalFormat("#,###.##");

        inpSearch = (TextView) findViewById(R.id.inpSearch);
        inpSearch.setTypeface(openSansLight);
        txtYourPoint = (TextView) findViewById(R.id.txtYourPoint);
        txtYourPoint.setTypeface(openSansSemiBold);
        txtPoint = (TextView) findViewById(R.id.txtPoint);
        txtPoint.setTypeface(openSansRegular);
        txtBestSeller = (TextView) findViewById(R.id.txtBestSeller);
        txtBestSeller.setTypeface(openSansSemiBold);
        txtKatalogProduk = (TextView) findViewById(R.id.txtKatalogProduk);
        txtKatalogProduk.setTypeface(openSansSemiBold);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        txtTotalCart = (TextView) findViewById(R.id.txtTotalCart);
        txtTotalCart.setTypeface(openSansLight);
        txtTitlePromo = (TextView) findViewById(R.id.txtTitlePromo);
        txtTitlePromo.setTypeface(openSansSemiBold);
        txtPaket = (TextView) findViewById(R.id.txtPaket);
        txtPaket.setTypeface(openSansSemiBold);
        txtKategori = (TextView) findViewById(R.id.txtKategori);
        txtKategori.setTypeface(openSansSemiBold);

        blockKatalogProduk = (LinearLayout) findViewById(R.id.blockKatalogProduk);
        blockNotif = (RelativeLayout) findViewById(R.id.blockNotif);
        blockNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, ShoppingCart.class);
                startActivity(intent);
            }
        });

        blockPointnya = (RelativeLayout) findViewById(R.id.blockPointnya);
        blockPointnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, Point.class);
                startActivity(intent);
            }
        });

        blockFreeOngkir = (RelativeLayout) findViewById(R.id.blockFreeOngkir);

        if (noHp.isEmpty()) {
            blockPointnya.setVisibility(View.INVISIBLE);
            blockFreeOngkir.setVisibility(View.VISIBLE);
        } else {
            blockPointnya.setVisibility(View.VISIBLE);
            blockFreeOngkir.setVisibility(View.INVISIBLE);
        }

        blockFreeOngkir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
            }
        });

        inpSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductSearching();
            }
        });

        new GetHomeData().execute();
    }

    private class GetHomeData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
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

            JSONObject json = jsonParser.makeHttpRequest(urlGetHomeData, "POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server. Silahkan cek koneksi anda dan coba lagi.", Toast.LENGTH_LONG).show();
                    } else {
                        editor.putInt("totalCart", result.getInt("totalCart"));
                        editor.apply();
                        if (result.getInt("totalCart") > 0) {
                            txtTotalCart.setText(result.getString("totalCart"));
                            txtTotalCart.setVisibility(View.VISIBLE);
                        } else {
                            txtTotalCart.setVisibility(View.INVISIBLE);
                        }

                        JSONArray promoArray = new JSONArray(result.getString("promo"));
                        if (promoArray.length() > 0) {
                            for (int i = 0; i < promoArray.length(); i++) {

                                final JSONObject promo = promoArray.getJSONObject(i);

                                LinearLayout promoLayout = new LinearLayout(MainActivity.this);
                                promoLayout.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), 0);
                                promoLayout.setOrientation(LinearLayout.VERTICAL);
                                promoLayout.setBackground(getResources().getDrawable(R.drawable.radius_corners));
                                ImageView thumbnail = new ImageView(MainActivity.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + promo.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(130*factor), (int)(130*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                promoLayout.addView(thumbnail);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(MainActivity.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(14);
                                nama.setText( promo.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                promoLayout.addView(nama);

                                TextView satuan = new TextView(MainActivity.this);
                                satuan.setLayoutParams(layoutNama);
                                satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( decimalFormat.format(promo.getInt("qty")) + " " + promo.getString("nama_satuan"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                promoLayout.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(MainActivity.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( "IDR " + decimalFormat.format(promo.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                promoLayout.addView(harga);

                                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams1.setMargins(0, 0, (int)(10*factor), 0);

                                promoLayout.setLayoutParams(layoutParams1);

                                promoLayout.setOnClickListener(new DetailProduk(promo.getString("id_produk"), "promo"));

                                barisanPromo.addView(promoLayout);
                            }
                        }

                        JSONArray paketArray = new JSONArray(result.getString("paket"));
                        if (paketArray.length() > 0) {
                            for (int i = 0; i < paketArray.length(); i++) {

                                final JSONObject paket = paketArray.getJSONObject(i);

                                LinearLayout paketLayout = new LinearLayout(MainActivity.this);
                                paketLayout.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), 0);
                                paketLayout.setOrientation(LinearLayout.VERTICAL);
                                paketLayout.setBackground(getResources().getDrawable(R.drawable.radius_corners));
                                ImageView thumbnail = new ImageView(MainActivity.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + paket.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(130*factor), (int)(130*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                paketLayout.addView(thumbnail);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(MainActivity.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(14);
                                nama.setText( paket.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                paketLayout.addView(nama);

                                TextView satuan = new TextView(MainActivity.this);
                                satuan.setLayoutParams(layoutNama);
                                satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( decimalFormat.format(paket.getInt("qty")) + " " + paket.getString("nama_satuan"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                paketLayout.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(MainActivity.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( "IDR " + decimalFormat.format(paket.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                paketLayout.addView(harga);

                                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams1.setMargins(0, 0, (int)(10*factor), 0);

                                paketLayout.setLayoutParams(layoutParams1);

                                paketLayout.setOnClickListener(new DetailProduk(paket.getString("id_produk"), "paket"));

                                barisanPaket.addView(paketLayout);
                            }
                        }

                        JSONArray arrayKategori = new JSONArray(result.getString("kategori"));
                        if (arrayKategori.length() > 0) {
                            for (int i = 0; i < arrayKategori.length(); i++) {

                                final JSONObject kategori = arrayKategori.getJSONObject(i);

                                LinearLayout frameLayout = new LinearLayout(MainActivity.this);
                                frameLayout.setPadding(0, 0, (int)(20*factor), 0);
                                frameLayout.setOrientation(LinearLayout.VERTICAL);

                                ImageView thumbnail = new ImageView(MainActivity.this);
                                Picasso.get().load(dataUrl + "uploads/kategori/" + kategori.getString( "thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(80*factor), (int)(80*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                frameLayout.addView(thumbnail);

                                RelativeLayout.LayoutParams layoutNama = new RelativeLayout.LayoutParams((int)(80*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(MainActivity.this);
                                layoutNama.addRule(RelativeLayout.CENTER_IN_PARENT);
                                nama.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                nama.setGravity(Gravity.CENTER_VERTICAL);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(10*factor), 0, (int)(10*factor), 0);
                                nama.setTypeface(openSansLight);
                                nama.setTextSize(14);
                                nama.setText( kategori.getString("nama_kategori"));
                                frameLayout.addView(nama);

                                frameLayout.setOnClickListener(new Kategori(kategori.getString("id_katproduk")));

                                barisanKategori.addView(frameLayout);
                            }
                        }

                        JSONArray popularArray = new JSONArray(result.getString("best"));
                        if (popularArray.length() > 0) {
                            for (int i = 0; i < popularArray.length(); i++) {

                                final JSONObject popular = popularArray.getJSONObject(i);

                                LinearLayout popularLayout = new LinearLayout(MainActivity.this);
                                popularLayout.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), 0);
                                popularLayout.setOrientation(LinearLayout.VERTICAL);
                                popularLayout.setBackground(getResources().getDrawable(R.drawable.radius_corners));
                                ImageView thumbnail = new ImageView(MainActivity.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + popular.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(130*factor), (int)(130*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                popularLayout.addView(thumbnail);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(MainActivity.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(14);
                                nama.setText( popular.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(nama);

                                TextView satuan = new TextView(MainActivity.this);
                                satuan.setLayoutParams(layoutNama);
                                satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( decimalFormat.format(popular.getInt("qty")) + " " + popular.getString("nama_satuan"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(satuan);

                                /*ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(MainActivity.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( "IDR " + decimalFormat.format(popular.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(harga);*/

                                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams1.setMargins(0, 0, (int)(10*factor), 0);

                                popularLayout.setLayoutParams(layoutParams1);

                                popularLayout.setOnClickListener(new DetailProduk(popular.getString("id_produk"), "laku"));

                                barisanBestSeller.addView(popularLayout);
                            }
                        }

                        JSONArray katalogArray = new JSONArray(result.getString("produk"));
                        int baris = (int) Math.ceil(katalogArray.length()/2);
                        int k = 0;
                        if (katalogArray.length() > 0) {
                            for (int i = 0; i < baris; i++) {
                                LinearLayout linearLayoutProduk = new LinearLayout(MainActivity.this);
                                linearLayoutProduk.setWeightSum(2);

                                for (int j = 0; j < katalogArray.length(); j++) {

                                    final JSONObject barang = katalogArray.getJSONObject(k);

                                    LinearLayout linearLayoutKiri = new LinearLayout(MainActivity.this);
                                    linearLayoutKiri.setOrientation(LinearLayout.VERTICAL);
                                    linearLayoutKiri.setId(View.generateViewId());
                                    linearLayoutKiri.setBackground(getResources().getDrawable(R.drawable.radius_corners));

                                    LinearLayout.LayoutParams layoutParamsKiri = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                                    if (j == 0) {
                                        layoutParamsKiri.setMargins((int) (20 * factor), 0, (int) (10 * factor), (int) (20 * factor));
                                    } else {
                                        layoutParamsKiri.setMargins((int) (10 * factor), 0, (int) (20 * factor), (int) (20 * factor));
                                    }
                                    linearLayoutKiri.setLayoutParams(layoutParamsKiri);

                                    ImageView thumbnail = new ImageView(MainActivity.this);
                                    Picasso.get().load(dataUrl + "uploads/thumbnail/" + barang.getString("thumbnail")).into(thumbnail);
                                    thumbnail.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), (int)(1*factor));
                                    thumbnail.setAdjustViewBounds(true);

                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                                    thumbnail.setLayoutParams(layoutParams);

                                    linearLayoutKiri.addView(thumbnail);

                                    ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                    TextView nama = new TextView(MainActivity.this);
                                    nama.setLayoutParams(layoutNama);
                                    nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                    nama.setTypeface(openSansSemiBold);
                                    nama.setTextSize(14);
                                    nama.setText( barang.getString("nama_produk"));
                                    nama.setMaxLines(1);
                                    nama.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayoutKiri.addView(nama);

                                    ViewGroup.LayoutParams layoutSatuan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                    TextView satuan = new TextView(MainActivity.this);
                                    satuan.setLayoutParams(layoutSatuan);
                                    satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                    satuan.setTypeface(openSansSemiBold);
                                    satuan.setTextSize(14);
                                    satuan.setText( decimalFormat.format(barang.getInt("qty")) + " " + barang.getString("nama_satuan"));
                                    satuan.setMaxLines(1);
                                    satuan.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayoutKiri.addView(satuan);

                                    ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                    TextView harga = new TextView(MainActivity.this);
                                    harga.setLayoutParams(layoutHarga);
                                    harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                    harga.setTypeface(openSansSemiBold);
                                    harga.setTextSize(14);
                                    harga.setText( "IDR " + decimalFormat.format(barang.getInt("harga")));
                                    harga.setMaxLines(1);
                                    harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                    harga.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayoutKiri.addView(harga);

                                    linearLayoutKiri.setOnClickListener(new DetailProduk(barang.getString("id_produk"), "katalog"));

                                    linearLayoutProduk.addView(linearLayoutKiri);
                                    if ((j%2) == 1) {
                                        break;
                                    }
                                    k++;
                                }
                                k++;

                                blockKatalogProduk.addView(linearLayoutProduk);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server. Silahkan cek koneksi anda dan coba lagi.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public class DetailProduk implements View.OnClickListener {

        String id, kat;
        public DetailProduk(String id, String kat) {
            this.id = id;
            this.kat = kat;
        }

        @Override
        public void onClick(View v) {
            if (kat.equals("promo")) {
                if (noHp.isEmpty()) {
                    intent = new Intent(MainActivity.this, SignIn.class);
                } else {
                    intent = new Intent(MainActivity.this, Detail.class);
                    intent.putExtra("id", id);
                }
            } else {
                intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("id", id);
            }
            startActivity(intent);
        }
    }

    public class Kategori implements View.OnClickListener {

        String id;
        public Kategori(String id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Category.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home:
                Intent a= new Intent(MainActivity.this, MainActivity.class);
                startActivity(a);
                break;
            case R.id.order:
                if (noHp.isEmpty()) {
                    intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                } else {
                    Intent b = new Intent(MainActivity.this, RiwayatOrder.class);
                    b.putExtra("no_telp", noHp);
                    startActivity(b);
                }
                break;
            case R.id.inbox:
                if (noHp.isEmpty()) {
                    intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                } else {
                    Intent c = new Intent(MainActivity.this, Inbox.class);
                    c.putExtra("no_telp", noHp);
                    startActivity(c);
                }
                break;
            case R.id.akunmember:
                if (noHp.isEmpty()) {
                    intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                } else {
                    Intent d = new Intent(MainActivity.this, UserAccount.class);
                    d.putExtra("no_telp", noHp);
                    startActivity(d);
                }
                break;
        }
        return true;
    }

    public void ProductSearching(){
        dialog = new AlertDialog.Builder(MainActivity.this, R.style.Searching);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.popup_searching, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        TextView txtTitleSearching = dialogView.findViewById(R.id.txtTitleSearching);
        txtTitleSearching.setTypeface(openSansBold);
        TextView txtTitleTranding = dialogView.findViewById(R.id.txtTitleTranding);
        txtTitleTranding.setTypeface(openSansSemiBold);
        final EditText inpSearching = dialogView.findViewById(R.id.inpSearching);
        inpSearching.setTypeface(openSansRegular);
        ImageView icoBack = dialogView.findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        inpSearching.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    intent = new Intent(MainActivity.this, SearchingResult.class);
                    intent.putExtra("kata", inpSearching.getText().toString());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        alertDialog.show();
    }

    public void Mencari(View view){
        String tag = view.getTag().toString();
        intent = new Intent(MainActivity.this, SearchingResult.class);
        intent.putExtra("kata", tag);
        startActivity(intent);
    }
}
