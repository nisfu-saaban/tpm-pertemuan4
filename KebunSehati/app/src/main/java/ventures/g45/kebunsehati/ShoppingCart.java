package ventures.g45.kebunsehati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ShoppingCart extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    Intent intent;
    String noHp, defaultUrl, urlGetShoppingBarang, token, kategori, dataUrl, urlGetShoppingCart, urlPlacingOrder, urlHapusOrder;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat decimalFormat;
    TextView txtTitle, txtEmpty, txtYukBelanja, txtPalingLaku, txtOrganik, txtTotal, txtNilaiTotal;
    float factor;
    LinearLayout barisanPalingLaku, barisanOrganik, barisanCart;
    ImageView icoBack;
    Button btnBeli;
    RelativeLayout blockVoucher, blockCart, blockKosong, blockButton;
    Integer totalCart, totalBelanja;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        kategori = sharedPreferences.getString("kategori", "1");
        noHp = sharedPreferences.getString("noHp", "");
        totalCart = sharedPreferences.getInt("totalCart", 0);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetShoppingBarang = defaultUrl + "getshoppingbarang.html";
        urlGetShoppingCart = defaultUrl + "getshoppingcart.html";
        urlPlacingOrder = defaultUrl + "placingorder.html";
        urlHapusOrder = defaultUrl + "hapusorder.html";

        factor = getResources().getDisplayMetrics().density;

        decimalFormat = new DecimalFormat("#,###.##");

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTypeface(openSansSemiBold);
        txtEmpty = (TextView) findViewById(R.id.txtEmpty);
        txtEmpty.setTypeface(openSansRegular);
        txtYukBelanja = (TextView) findViewById(R.id.txtYukBelanja);
        txtYukBelanja.setTypeface(openSansSemiBold);
        barisanPalingLaku = (LinearLayout) findViewById(R.id.barisanPalingLaku);
        txtPalingLaku = (TextView) findViewById(R.id.txtPalingLaku);
        txtPalingLaku.setTypeface(openSansSemiBold);
        txtOrganik = (TextView) findViewById(R.id.txtOrganik);
        txtOrganik.setTypeface(openSansSemiBold);
        barisanOrganik = (LinearLayout) findViewById(R.id.barisanOrganik);
        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtTotal.setTypeface(openSansSemiBold);
        txtNilaiTotal = (TextView) findViewById(R.id.txtNilaiTotal);
        txtNilaiTotal.setTypeface(openSansSemiBold);
        btnBeli = (Button) findViewById(R.id.btnBeli);
        btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalBelanja < 20000) {
                    AlertDialog.Builder alertDialogBuilder;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        alertDialogBuilder = new AlertDialog.Builder(ShoppingCart.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        alertDialogBuilder = new AlertDialog.Builder(ShoppingCart.this);
                    }
                    alertDialogBuilder
                            .setMessage("Minimum order adalah sebesar IDR 20.000")
                            .setCancelable(true)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                } else {
                    intent = new Intent(ShoppingCart.this, Shipping.class);
                    startActivity(intent);
                }
            }
        });

        blockVoucher = (RelativeLayout) findViewById(R.id.blockVoucher);
        blockVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ShoppingCart.this, VoucherList.class);
                startActivity(intent);
            }
        });

        barisanCart = (LinearLayout) findViewById(R.id.barisanCart);
        blockCart = (RelativeLayout) findViewById(R.id.blockCart);
        blockKosong = (RelativeLayout) findViewById(R.id.blockKosong);
        blockButton = (RelativeLayout) findViewById(R.id.blockButton);

        if (totalCart > 0) {
            blockCart.setVisibility(View.VISIBLE);
            blockKosong.setVisibility(View.INVISIBLE);
            blockButton.setVisibility(View.VISIBLE);
            new GetShoppingCart().execute();
        } else {
            blockCart.setVisibility(View.INVISIBLE);
            blockKosong.setVisibility(View.VISIBLE);
            blockButton.setVisibility(View.INVISIBLE);
            new GetShoppingBarang().execute();
        }
    }

    private class GetShoppingBarang extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(ShoppingCart.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("kategori", kategori));
            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject json = jsonParser.makeHttpRequest(urlGetShoppingBarang, "POST", params);

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
                        JSONArray popularArray = new JSONArray(result.getString("best"));
                        if (popularArray.length() > 0) {
                            for (int i = 0; i < popularArray.length(); i++) {

                                final JSONObject popular = popularArray.getJSONObject(i);

                                LinearLayout popularLayout = new LinearLayout(ShoppingCart.this);
                                popularLayout.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), 0);
                                popularLayout.setOrientation(LinearLayout.VERTICAL);
                                popularLayout.setBackground(getResources().getDrawable(R.drawable.radius_corners));
                                ImageView thumbnail = new ImageView(ShoppingCart.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + popular.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(130*factor), (int)(130*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                popularLayout.addView(thumbnail);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(ShoppingCart.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(14);
                                nama.setText( popular.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(nama);

                                TextView satuan = new TextView(ShoppingCart.this);
                                satuan.setLayoutParams(layoutNama);
                                satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( decimalFormat.format(popular.getInt("qty")) + " " + popular.getString("nama_satuan"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(ShoppingCart.this);
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

                                barisanPalingLaku.addView(popularLayout);
                            }
                        }

                        JSONArray organikArray = new JSONArray(result.getString("organik"));
                        if (organikArray.length() > 0) {
                            for (int i = 0; i < organikArray.length(); i++) {

                                final JSONObject organik = organikArray.getJSONObject(i);

                                LinearLayout popularLayout = new LinearLayout(ShoppingCart.this);
                                popularLayout.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), 0);
                                popularLayout.setOrientation(LinearLayout.VERTICAL);
                                popularLayout.setBackground(getResources().getDrawable(R.drawable.radius_corners));
                                ImageView thumbnail = new ImageView(ShoppingCart.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + organik.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(130*factor), (int)(130*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                popularLayout.addView(thumbnail);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(ShoppingCart.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(14);
                                nama.setText( organik.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(nama);

                                TextView satuan = new TextView(ShoppingCart.this);
                                satuan.setLayoutParams(layoutNama);
                                satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( decimalFormat.format(organik.getInt("qty")) + " " + organik.getString("nama_satuan"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams((int)(130*factor), ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(ShoppingCart.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( "IDR " + decimalFormat.format(organik.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                popularLayout.addView(harga);

                                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams1.setMargins(0, 0, (int)(10*factor), 0);

                                popularLayout.setLayoutParams(layoutParams1);

                                popularLayout.setOnClickListener(new DetailProduk(organik.getString("id_produk")));

                                barisanOrganik.addView(popularLayout);
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
            Intent intent = new Intent(ShoppingCart.this, Detail.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    private class GetShoppingCart extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(ShoppingCart.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("kategori", kategori));
            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject json = jsonParser.makeHttpRequest(urlGetShoppingCart, "POST", params);

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
                        txtNilaiTotal.setText("IDR " + decimalFormat.format(result.getInt("total")));
                        totalBelanja = result.getInt("total");
                        btnBeli.setText("Beli (" + decimalFormat.format(result.getInt("items")) + ")");
                        JSONArray cartArray = new JSONArray(result.getString("cart"));
                        if (cartArray.length() > 0) {
                            for (int i = 0; i < cartArray.length(); i++) {

                                final JSONObject cart = cartArray.getJSONObject(i);

                                LinearLayout linearLayoutCart = new LinearLayout(ShoppingCart.this);
                                linearLayoutCart.setWeightSum(2);
                                linearLayoutCart.setBackgroundColor(getResources().getColor(android.R.color.white));

                                LinearLayout linearLayoutKiri = new LinearLayout(ShoppingCart.this);

                                LinearLayout.LayoutParams layoutParamsKiri = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f);
                                linearLayoutKiri.setLayoutParams(layoutParamsKiri);

                                ImageView thumbnail = new ImageView(ShoppingCart.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + cart.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(130*factor), (int)(130*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                linearLayoutKiri.addView(thumbnail);

                                linearLayoutCart.addView(linearLayoutKiri);

                                LinearLayout linearLayoutKanan = new LinearLayout(ShoppingCart.this);
                                linearLayoutKanan.setOrientation(LinearLayout.VERTICAL);

                                LinearLayout.LayoutParams layoutParamsKanan = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f);
                                linearLayoutKanan.setLayoutParams(layoutParamsKanan);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(ShoppingCart.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(5*factor), (int)(5*factor), (int)(5*factor), (int)(5*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(16);
                                nama.setText( cart.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(nama);

                                ViewGroup.LayoutParams layoutSatuan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView satuan = new TextView(ShoppingCart.this);
                                satuan.setLayoutParams(layoutSatuan);
                                satuan.setPadding((int)(5*factor), 0, (int)(5*factor), (int)(5*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( cart.getString("ukuran"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(ShoppingCart.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( decimalFormat.format(cart.getInt("qty")) + " x " + "IDR " + decimalFormat.format(cart.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(harga);

                                ViewGroup.LayoutParams layoutKeterangan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView keterangan = new TextView(ShoppingCart.this);
                                keterangan.setLayoutParams(layoutKeterangan);
                                keterangan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                keterangan.setTypeface(openSansLight);
                                keterangan.setTextSize(13);
                                keterangan.setText(cart.getString("keterangan"));
                                keterangan.setMaxLines(2);
                                keterangan.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(keterangan);

                                linearLayoutCart.addView(linearLayoutKanan);

                                LinearLayout.LayoutParams layoutParamsCart = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParamsCart.setMargins(0, 0, 0, (int)(10*factor));

                                linearLayoutCart.setLayoutParams(layoutParamsCart);

                                linearLayoutCart.setOnClickListener(new EditCart(cart.getString("id_produk"), cart.getString("nama_produk"), cart.getString("ukuran"), decimalFormat.format(cart.getInt("harga")), dataUrl + "uploads/thumbnail/" + cart.getString("thumbnail"), cart.getString("keterangan"), cart.getInt("qty")));

                                barisanCart.addView(linearLayoutCart);
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

    public class EditCart implements View.OnClickListener {

        String idProduk, namaBarangnya, beratProduknya, hargaProduknya, imgProdukUrlnya, keterangan_cart;
        Integer qty_cart;
        public EditCart(String id_produk, String nama_produk, String ukuran, String harga, String thumbnail, String keterangan, Integer qty) {
            this.idProduk = id_produk;
            this.namaBarangnya = nama_produk;
            this.beratProduknya = ukuran;
            this.hargaProduknya = harga;
            this.imgProdukUrlnya = thumbnail;
            this.keterangan_cart= keterangan;
            this.qty_cart = qty;
        }

        @Override
        public void onClick(View v) {
            EditCartDialog(namaBarangnya, beratProduknya, hargaProduknya, imgProdukUrlnya, qty_cart, keterangan_cart, idProduk);
        }
    }

    public void EditCartDialog(String namaBarangnya, String beratProduknya, String hargaProduknya, String imgProdukUrlnya, Integer qty_cart, String keterangan_cart, final String idProduk_cart) {
        dialog = new AlertDialog.Builder(ShoppingCart.this, R.style.AddToCart);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.popup_edit_cart, null);
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
                new PlacingOrder().execute(qty.toString(), catatan, idProduk_cart);
                alertDialog.dismiss();
            }
        });

        Button btnHapusPopup = dialogView.findViewById(R.id.btnHapusPopup);
        btnHapusPopup.setTypeface(openSansSemiBold);
        btnHapusPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RemoveOrder().execute(idProduk_cart);
            }
        });

        alertDialog.show();
    }

    private class PlacingOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(ShoppingCart.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", args[2]));
            params.add(new BasicNameValuePair("noHp", noHp));
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
                        finish();
                        startActivity(getIntent());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class RemoveOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(ShoppingCart.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject json = jsonParser.makeHttpRequest(urlHapusOrder, "POST", params);

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
                        finish();
                        startActivity(getIntent());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
