package ventures.g45.kebunsehati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class DetailOrder extends AppCompatActivity {

    ImageView icoBack;
    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    TextView txtTitle, txtStatusOrder, txtIdOrder, txtTanggal, txtTitleAlamat, txtAlamat;
    String defaultUrl, urlGetDetailOrder, no_hp, idOrder, token, dataUrl, urlUbahStatus, statusOrder;
    String metodePembayaran, tujuanPembayaran;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat decimalFormat;
    Intent intent;
    float factor;
    LinearLayout barisanCart;
    Button btnTerima;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    Integer total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        no_hp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        urlGetDetailOrder = defaultUrl + "getdetailorder.html";
        urlUbahStatus = defaultUrl + "ubahstatus.html";

        decimalFormat = new DecimalFormat("#,###.##");
        factor = getResources().getDisplayMetrics().density;

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTypeface(openSansSemiBold);
        txtStatusOrder = (TextView) findViewById(R.id.txtStatusOrder);
        txtStatusOrder.setTypeface(openSansRegular);
        txtIdOrder = (TextView) findViewById(R.id.txtIdOrder);
        txtIdOrder.setTypeface(openSansLight);
        txtTanggal = (TextView) findViewById(R.id.txtTanggal);
        txtTanggal.setTypeface(openSansLight);
        txtTitleAlamat = (TextView) findViewById(R.id.txtTitleAlamat);
        txtTitleAlamat.setTypeface(openSansRegular);
        txtAlamat = (TextView) findViewById(R.id.txtAlamat);
        txtAlamat.setTypeface(openSansLight);
        btnTerima = (Button) findViewById(R.id.btnTerima);
        btnTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((statusOrder.equals("Sedang Disiapkan")) || (statusOrder.equals("Sedang Dikirim"))) {
                    AlertDialog.Builder alertDialogBuilder;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        alertDialogBuilder = new AlertDialog.Builder(DetailOrder.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        alertDialogBuilder = new AlertDialog.Builder(DetailOrder.this);
                        alertDialogBuilder
                                .setMessage("Apakah anda sudah menerima pesanan anda?")
                                .setCancelable(true)
                                .setPositiveButton("SUDAH",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                new UbahStatus().execute();
                                            }
                                        })
                                .setNegativeButton("BELUM",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                } else {
                    InfoPembayaran();
                }
            }
        });

        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        intent = getIntent();
        idOrder = intent.getStringExtra("id_order");

        barisanCart = (LinearLayout) findViewById(R.id.barisanCart);

        new GetDetailProduk().execute();
    }

    private class GetDetailProduk extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(DetailOrder.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_order", idOrder));

            JSONObject json = jsonParser.makeHttpRequest(urlGetDetailOrder, "POST", params);

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
                        txtStatusOrder.setText(result.getString("status"));
                        txtTanggal.setText(result.getString("waktu_order"));
                        txtAlamat.setText(result.getString("alamat"));
                        txtIdOrder.setText(idOrder);
                        statusOrder = result.getString("status");
                        metodePembayaran = result.getString("metode");
                        tujuanPembayaran = result.getString("tujuan");
                        total = result.getInt("total");
                        if ((statusOrder.equals("Menunggu Pembayaran"))) {
                            btnTerima.setText("Lihat info pembayaran");
                            btnTerima.setVisibility(View.VISIBLE);
                        } else {
                            if ((statusOrder.equals("Sedang Disiapkan")) || (statusOrder.equals("Sedang Dikirim"))) {
                                btnTerima.setText("Sudah menerima pesanan anda?");
                                btnTerima.setVisibility(View.VISIBLE);
                            } else {
                                btnTerima.setVisibility(View.INVISIBLE);
                            }
                        }

                        JSONArray cartArray = new JSONArray(result.getString("cart"));
                        if (cartArray.length() > 0) {
                            for (int i = 0; i < cartArray.length(); i++) {

                                final JSONObject cart = cartArray.getJSONObject(i);

                                LinearLayout linearLayoutCart = new LinearLayout(DetailOrder.this);
                                linearLayoutCart.setWeightSum(2);
                                linearLayoutCart.setBackgroundColor(getResources().getColor(android.R.color.white));

                                LinearLayout linearLayoutKiri = new LinearLayout(DetailOrder.this);

                                LinearLayout.LayoutParams layoutParamsKiri = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f);
                                linearLayoutKiri.setLayoutParams(layoutParamsKiri);

                                ImageView thumbnail = new ImageView(DetailOrder.this);
                                Picasso.get().load(dataUrl + "uploads/thumbnail/" + cart.getString("thumbnail")).into(thumbnail);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(100*factor), (int)(100*factor));
                                thumbnail.setLayoutParams(layoutParams);

                                linearLayoutKiri.addView(thumbnail);

                                linearLayoutCart.addView(linearLayoutKiri);

                                LinearLayout linearLayoutKanan = new LinearLayout(DetailOrder.this);
                                linearLayoutKanan.setOrientation(LinearLayout.VERTICAL);

                                LinearLayout.LayoutParams layoutParamsKanan = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f);
                                linearLayoutKanan.setLayoutParams(layoutParamsKanan);

                                ViewGroup.LayoutParams layoutNama = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView nama = new TextView(DetailOrder.this);
                                nama.setLayoutParams(layoutNama);
                                nama.setPadding((int)(5*factor), (int)(5*factor), (int)(5*factor), (int)(5*factor));
                                nama.setTypeface(openSansSemiBold);
                                nama.setTextSize(16);
                                nama.setText( cart.getString("nama_produk"));
                                nama.setMaxLines(1);
                                nama.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(nama);

                                ViewGroup.LayoutParams layoutSatuan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView satuan = new TextView(DetailOrder.this);
                                satuan.setLayoutParams(layoutSatuan);
                                satuan.setPadding((int)(5*factor), 0, (int)(5*factor), (int)(5*factor));
                                satuan.setTypeface(openSansSemiBold);
                                satuan.setTextSize(14);
                                satuan.setText( cart.getString("ukuran"));
                                satuan.setMaxLines(1);
                                satuan.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(satuan);

                                ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView harga = new TextView(DetailOrder.this);
                                harga.setLayoutParams(layoutHarga);
                                harga.setPadding((int)(5*factor), 0, (int)(5*factor), (int)(10*factor));
                                harga.setTypeface(openSansSemiBold);
                                harga.setTextSize(14);
                                harga.setText( decimalFormat.format(cart.getInt("qty")) + " x " + "IDR " + decimalFormat.format(cart.getInt("harga")));
                                harga.setMaxLines(1);
                                harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                harga.setEllipsize(TextUtils.TruncateAt.END);
                                linearLayoutKanan.addView(harga);

                                ViewGroup.LayoutParams layoutKeterangan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                TextView keterangan = new TextView(DetailOrder.this);
                                keterangan.setLayoutParams(layoutKeterangan);
                                keterangan.setPadding((int)(5*factor), 0, (int)(5*factor), (int)(10*factor));
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

                                //linearLayoutCart.setOnClickListener(new EditCart(cart.getString("id_produk"), cart.getString("nama_produk"), cart.getString("ukuran"), decimalFormat.format(cart.getInt("harga")), dataUrl + "uploads/thumbnail/" + cart.getString("thumbnail"), cart.getString("keterangan"), cart.getInt("qty")));

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

    private class UbahStatus extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(DetailOrder.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("id_order", idOrder));
            params.add(new BasicNameValuePair("status", "telah diterima"));

            JSONObject json = jsonParser.makeHttpRequest(urlUbahStatus, "POST", params);

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
                        intent = new Intent(DetailOrder.this, RiwayatOrder.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    public void InfoPembayaran(){
        dialog = new AlertDialog.Builder(DetailOrder.this, R.style.Searching);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.popup_info_pembayaran, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        TextView txtTitleInfoPembayaran = dialogView.findViewById(R.id.txtTitleInfoPembayaran);
        txtTitleInfoPembayaran.setTypeface(openSansBold);
        TextView txtMetode = dialogView.findViewById(R.id.txtMetode);
        txtMetode.setTypeface(openSansRegular);
        txtMetode.setText(metodePembayaran);
        TextView txtTujuan = dialogView.findViewById(R.id.txtTujuan);
        txtTujuan.setTypeface(openSansRegular);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtTujuan.setText(Html.fromHtml(tujuanPembayaran, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtTujuan.setText(Html.fromHtml(tujuanPembayaran));
        }
        TextView txtTotal = dialogView.findViewById(R.id.txtTotal);
        txtTotal.setTypeface(openSansSemiBold);
        txtTotal.setText("Total Tagihan : IDR " + decimalFormat.format(total));
        ImageView icoClose = dialogView.findViewById(R.id.icoClose);
        icoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        Button btnKonfirmasi = dialogView.findViewById(R.id.btnKonfirmasi);
        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=+628995292402";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        alertDialog.show();
    }
}
