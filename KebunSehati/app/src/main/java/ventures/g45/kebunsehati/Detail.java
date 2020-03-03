package ventures.g45.kebunsehati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.NumberPicker;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Detail extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    String defaultUrl, urlGetdetailProduk, token, kategori, dataUrl, idProduk, shortUrl, namaBarang, mUrl;
    String imgBarangUrl, beratBarang, hargaBarang, urlPlacingOrder, no_hp, keterangan_cart, alamat, idAlamat;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat decimalFormat;
    Intent intent;
    TextView namaProduk, hargaProduk, txtNilaiBerat, txtSatuanBerat, txtLabel, txtDelivery;
    TextView txtTitleInfo, txtInfoProduk, txtTitleLainnya, txtTotalCart, inpSearch;
    ImageView imgProduk, icoWA, icoBack, icoCart, imgLabel;
    float factor;
    LinearLayout barisanProdukLainnya;
    Button btnBeli;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    Integer totalCart, qty_cart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        kategori = sharedPreferences.getString("kategori", "1");
        totalCart = sharedPreferences.getInt("totalCart", 0);
        no_hp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        mUrl = ((KebunSehati) getApplication()).getmUrl();
        urlGetdetailProduk = defaultUrl + "getdetailproduk.html";
        urlPlacingOrder = defaultUrl + "placingorder.html";

        decimalFormat = new DecimalFormat("#,###.##");
        factor = getResources().getDisplayMetrics().density;

        intent = getIntent();
        idProduk = intent.getStringExtra("id");

        namaProduk = (TextView) findViewById(R.id.namaProduk);
        namaProduk.setTypeface(openSansSemiBold);
        inpSearch = (TextView) findViewById(R.id.inpSearch);
        inpSearch.setTypeface(openSansRegular);
        hargaProduk = (TextView) findViewById(R.id.hargaProduk);
        hargaProduk.setTypeface(openSansBold);
        txtNilaiBerat = (TextView) findViewById(R.id.txtNilaiBerat);
        txtNilaiBerat.setTypeface(openSansSemiBold);
        txtSatuanBerat = (TextView) findViewById(R.id.txtSatuanBerat);
        txtSatuanBerat.setTypeface(openSansRegular);
        txtLabel = (TextView) findViewById(R.id.txtLabel);
        txtLabel.setTypeface(openSansRegular);
        txtDelivery = (TextView) findViewById(R.id.txtDelivery);
        txtDelivery.setTypeface(openSansRegular);
        imgProduk = (ImageView) findViewById(R.id.imgProduk);
        txtTitleInfo = (TextView) findViewById(R.id.txtTitleInfo);
        txtTitleInfo.setTypeface(openSansSemiBold);
        txtInfoProduk = (TextView) findViewById(R.id.txtInfoProduk);
        txtInfoProduk.setTypeface(openSansRegular);
        barisanProdukLainnya = (LinearLayout) findViewById(R.id.barisanProdukLainnya);
        txtTitleLainnya = (TextView) findViewById(R.id.txtTitleLainnya);
        txtTitleLainnya.setTypeface(openSansSemiBold);
        imgLabel = (ImageView) findViewById(R.id.imgLabel);
        icoWA = (ImageView) findViewById(R.id.icoWA);
        icoWA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=+628995292402";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnBeli = (Button) findViewById(R.id.btnBeli);
        btnBeli.setTypeface(openSansBold);
        btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (no_hp.isEmpty()) {
                    intent = new Intent(Detail.this, SignIn.class);
                    startActivity(intent);
                } else {
                    AddToCartDialog(namaBarang, beratBarang, hargaBarang, imgBarangUrl);
                }
            }
        });
        txtTotalCart = (TextView) findViewById(R.id.txtTotalCart);
        txtTotalCart.setTypeface(openSansLight);
        if (totalCart > 0) {
            txtTotalCart.setText(totalCart.toString());
            txtTotalCart.setVisibility(View.VISIBLE);
        } else {
            txtTotalCart.setVisibility(View.INVISIBLE);
        }

        icoCart = (ImageView) findViewById(R.id.icoCart);
        icoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Detail.this, ShoppingCart.class);
                startActivity(intent);
            }
        });

        inpSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductSearching();
            }
        });

        new GetDetailProduk().execute();
    }

    private class GetDetailProduk extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Detail.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", idProduk));
            params.add(new BasicNameValuePair("noHp", no_hp));
            params.add(new BasicNameValuePair("kategori", kategori));

            JSONObject json = jsonParser.makeHttpRequest(urlGetdetailProduk, "POST", params);

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
                        Picasso.get().load(dataUrl + "uploads/thumbnail/" + result.getString("thumbnail")).into(imgProduk);
                        namaProduk.setText(result.getString("nama_produk"));
                        namaBarang = result.getString("nama_produk");
                        shortUrl = mUrl + "produk/detail/" + result.getString("meta_url") + ".html";
                        imgBarangUrl = dataUrl + "uploads/thumbnail/" + result.getString("thumbnail");
                        beratBarang = decimalFormat.format(result.getInt("qty")) + " " + result.getString("nama_satuan");
                        hargaBarang = "IDR " + decimalFormat.format(result.getInt("harga"));
                        hargaProduk.setText("IDR " + decimalFormat.format(result.getInt("harga")));
                        txtNilaiBerat.setText(decimalFormat.format(result.getInt("qty")));
                        txtSatuanBerat.setText(result.getString("nama_satuan"));
                        if (result.getString("nama_label").equals("Tanpa Label")) {
                            txtLabel.setText("Produk Lokal");
                        } else {
                            txtLabel.setText(result.getString("nama_label"));
                        }
                        if (result.getString("image_label").equals("no-image")) {
                            imgLabel.setImageDrawable(getResources().getDrawable(R.drawable.petani_lokal));
                        } else {
                            Picasso.get().load(dataUrl + "uploads/label/" + result.getString("image_label")).into(imgLabel);
                        }
                        qty_cart = result.getInt("qty_cart");
                        keterangan_cart = result.getString("keterangan_cart");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtInfoProduk.setText(Html.fromHtml(result.getString("detail"), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            txtInfoProduk.setText(Html.fromHtml(result.getString("detail")));
                        }

                        JSONArray popularArray = new JSONArray(result.getString("lainnya"));
                        if (popularArray.length() > 0) {
                            for (int i = 0; i < popularArray.length(); i++) {

                                final JSONObject popular = popularArray.getJSONObject(i);

                                LinearLayout popularLayout = new LinearLayout(Detail.this);
                                popularLayout.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), 0);
                                popularLayout.setOrientation(LinearLayout.VERTICAL);
                                popularLayout.setBackground(getResources().getDrawable(R.drawable.radius_corners));
                                ImageView thumbnail = new ImageView(Detail.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + popular.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(150*factor), (int)(150*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                popularLayout.addView(thumbnail);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(Detail.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(14);
                                nama.setText( popular.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(nama);

                                TextView satuan = new TextView(Detail.this);
                                satuan.setLayoutParams(layoutNama);
                                satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( decimalFormat.format(popular.getInt("qty")) + " " + popular.getString("nama_satuan"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(Detail.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( "IDR " + decimalFormat.format(popular.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(harga);

                                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams1.setMargins(0, 0, (int)(10*factor), 0);

                                popularLayout.setLayoutParams(layoutParams1);

                                popularLayout.setOnClickListener(new DetailProduk(popular.getString("id_produk")));

                                barisanProdukLainnya.addView(popularLayout);
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
            Intent intent = new Intent(Detail.this, Detail.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    public void shareProduk(View view) {
        intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, namaBarang + " dari KebunSehati.id");
        intent.putExtra(Intent.EXTRA_TEXT, shortUrl);

        startActivity(Intent.createChooser(intent, namaBarang));
    }

    public void AddToCartDialog(String namaBarangnya, String beratProduknya, String hargaProduknya, String imgProdukUrlnya) {
        dialog = new AlertDialog.Builder(Detail.this, R.style.AddToCart);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.popup_add_to_cart, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        TextView txtTambah = dialogView.findViewById(R.id.txtTambah);
        txtTambah.setTypeface(openSansSemiBold);
        TextView txtNamaProduk = dialogView.findViewById(R.id.txtNamaProduk);
        txtNamaProduk.setTypeface(openSansSemiBold);
        TextView txtBeratProduk = dialogView.findViewById(R.id.txtBeratProduk);
        txtBeratProduk.setTypeface(openSansRegular);
        TextView txtHargaProduk = dialogView.findViewById(R.id.txtHargaProduk);
        txtHargaProduk.setTypeface(openSansSemiBold);
        TextView txtTitleQty = dialogView.findViewById(R.id.txtTitleQty);
        txtTitleQty.setTypeface(openSansSemiBold);
        ImageView imgProduk = dialogView.findViewById(R.id.imgProduk);
        Picasso.get().load(imgProdukUrlnya).into(imgProduk);
        txtNamaProduk.setText(namaBarangnya);
        txtBeratProduk.setText(beratProduknya);
        txtHargaProduk.setText(hargaProduknya);
        final EditText inpCatatan = dialogView.findViewById(R.id.inpCatatan);
        final NumberPicker jumlah = dialogView.findViewById(R.id.number_picker);
        if (qty_cart > 0) {
            jumlah.setValue(qty_cart);
        }
        if (!keterangan_cart.isEmpty()) {
            inpCatatan.setText(keterangan_cart);
        }

        ImageView icoClose = dialogView.findViewById(R.id.icoClose);
        icoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        Button btnBeliPopup = dialogView.findViewById(R.id.btnBeliPopup);
        btnBeliPopup.setTypeface(openSansSemiBold);
        btnBeliPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer qty = jumlah.getValue();
                final String catatan = inpCatatan.getText().toString();
                //if (qty > 0) {
                    new PlacingOrder().execute(qty.toString(), catatan);
                //}
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private class PlacingOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Detail.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", idProduk));
            params.add(new BasicNameValuePair("noHp", no_hp));
            params.add(new BasicNameValuePair("qty", args[0]));
            params.add(new BasicNameValuePair("kategori", kategori));
            params.add(new BasicNameValuePair("keterangan", args[1]));

            JSONObject json = jsonParser.makeHttpRequest(urlPlacingOrder, "POST", params);

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
                        editor.putInt("totalCart", result.getInt("totalCart"));
                        editor.apply();
                        if (result.getInt("totalCart") > 0) {
                            txtTotalCart.setText(result.getString("totalCart"));
                            txtTotalCart.setVisibility(View.VISIBLE);
                        } else {
                            txtTotalCart.setVisibility(View.INVISIBLE);
                        }
                        intent = new Intent(Detail.this, ShoppingCart.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void ProductSearching(){
        dialog = new AlertDialog.Builder(Detail.this, R.style.Searching);
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
                    intent = new Intent(Detail.this, SearchingResult.class);
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
        intent = new Intent(Detail.this, SearchingResult.class);
        intent.putExtra("kata", tag);
        startActivity(intent);
    }
}
