package ventures.g45.kebunsehati;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Shipping extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    TextView txtTitle, txtTitleAlamat, txtNamaPenerima, txtAddress, txtTitleJadwal, txtTitlePembayaran;
    TextView txtTitleRekap, txtTitleTotalBelanja, txtNilaiTotalBelanja, txtAlamat;
    TextView txtTitleTotalOngkir, txtNilaiTotalOngkir, txtTitleTotalTagihan, txtNilaiTotalTagihan;
    TextView txtTanggalPengiriman, txtTanggal;
    ImageView icoBack;
    Button btnEditAlamat, btnBeli, btnEditTanggal;
    RadioButton jadwalPagi, jadwalSore, bayarCOD;
    String defaultUrl, urlGetDataShipping, no_hp, token, alamat, idAlamat, urlSimpanOrder, tglKirim, ketPembayaran;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat decimalFormat;
    Intent intent;
    float factor;
    RadioGroup blockPilihanJadwal, blockPilihanPembayaran;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    String myFormat = "dd MMM yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        no_hp = sharedPreferences.getString("noHp", "");
        alamat = sharedPreferences.getString("alamat", "");
        idAlamat = sharedPreferences.getString("id_alamat", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlGetDataShipping = defaultUrl + "getdatashipping.html";
        urlSimpanOrder = defaultUrl + "simpanorder.html";

        decimalFormat = new DecimalFormat("#,###.##");
        factor = getResources().getDisplayMetrics().density;

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTypeface(openSansSemiBold);
        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtAlamat = (TextView) findViewById(R.id.txtAlamat);
        txtAlamat.setTypeface(openSansSemiBold);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtAddress.setTypeface(openSansRegular);
        btnEditAlamat = (Button) findViewById(R.id.btnEditAlamat);
        btnEditAlamat.setTypeface(openSansSemiBold);
        txtTitleJadwal = (TextView) findViewById(R.id.txtTitleJadwal);
        txtTitleJadwal.setTypeface(openSansSemiBold);
        txtTitlePembayaran = (TextView) findViewById(R.id.txtTitlePembayaran);
        txtTitlePembayaran.setTypeface(openSansSemiBold);
        txtTitleRekap = (TextView) findViewById(R.id.txtTitleRekap);
        txtTitleRekap.setTypeface(openSansSemiBold);
        txtTitleTotalBelanja = (TextView) findViewById(R.id.txtTitleTotalBelanja);
        txtTitleTotalBelanja.setTypeface(openSansSemiBold);
        txtNilaiTotalBelanja = (TextView) findViewById(R.id.txtNilaiTotalBelanja);
        txtNilaiTotalBelanja.setTypeface(openSansBold);
        btnBeli = (Button) findViewById(R.id.btnBeli);
        btnBeli.setTypeface(openSansSemiBold);
        txtTitleTotalOngkir = (TextView) findViewById(R.id.txtTitleTotalOngkir);
        txtTitleTotalOngkir.setTypeface(openSansSemiBold);
        txtNilaiTotalOngkir = (TextView) findViewById(R.id.txtNilaiTotalOngkir);
        txtNilaiTotalOngkir.setTypeface(openSansBold);
        txtTitleTotalTagihan = (TextView) findViewById(R.id.txtTitleTotalTagihan);
        txtTitleTotalTagihan.setTypeface(openSansSemiBold);
        txtNilaiTotalTagihan = (TextView) findViewById(R.id.txtNilaiTotalTagihan);
        txtNilaiTotalTagihan.setTypeface(openSansBold);
        blockPilihanJadwal = (RadioGroup) findViewById(R.id.blockPilihanJadwal);
        blockPilihanPembayaran = (RadioGroup) findViewById(R.id.blockPilihanPembayaran);
        txtTanggalPengiriman = (TextView) findViewById(R.id.txtTanggalPengiriman);
        txtTanggalPengiriman.setTypeface(openSansSemiBold);
        txtTanggal = (TextView) findViewById(R.id.txtTanggal);
        txtTanggal.setTypeface(openSansRegular);

        btnEditAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Shipping.this, ChangeAddress.class);
                startActivity(intent);
            }
        });

        btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idJadwal = blockPilihanJadwal.getCheckedRadioButtonId();
                int idPembayaran = blockPilihanPembayaran.getCheckedRadioButtonId();
                RadioButton radioPembayaran = (RadioButton) findViewById(idJadwal);
                ketPembayaran = radioPembayaran.getText().toString();
                new SimpanOrder().execute(String.valueOf(idJadwal), String.valueOf(idPembayaran), ketPembayaran);

            }
        });

        myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.DATE, 1);
        tglKirim = sdf.format(myCalendar.getTime());
        txtTanggal.setText(sdf.format(myCalendar.getTime()));

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        btnEditTanggal = (Button) findViewById(R.id.btnEditTanggal);
        btnEditTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Shipping.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if (idAlamat.isEmpty()) {
            intent = new Intent(Shipping.this, ChangeAddress.class);
            startActivity(intent);
        } else {
            txtAddress.setText(alamat);
            new GetDataShipping().execute();
        }
    }

    private void updateLabel() {
        tglKirim = sdf.format(myCalendar.getTime());
        txtTanggal.setText(sdf.format(myCalendar.getTime()));
    }

    private class GetDataShipping extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Shipping.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", no_hp));

            JSONObject json = jsonParser.makeHttpRequest(urlGetDataShipping, "POST", params);

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
                        txtNilaiTotalTagihan.setText(decimalFormat.format(result.getInt("total")));
                        txtNilaiTotalBelanja.setText(decimalFormat.format(result.getInt("total")));
                        JSONArray pembayaranArray = new JSONArray(result.getString("pembayaran"));
                        if (pembayaranArray.length() > 0) {
                            for (int i = 0; i < pembayaranArray.length(); i++) {

                                final JSONObject pembayaran = pembayaranArray.getJSONObject(i);

                                RadioButton pembayarannya = new RadioButton(Shipping.this);
                                if (pembayaran.getString("tujuan").isEmpty()) {
                                    pembayarannya.setText(pembayaran.getString("metode"));
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        pembayarannya.setText(Html.fromHtml(pembayaran.getString("metode") + "<br>" + pembayaran.getString("tujuan"), Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        pembayarannya.setText(Html.fromHtml(pembayaran.getString("metode") + "<br>" + pembayaran.getString("tujuan")));
                                    }
                                }
                                if (i == 0) {
                                    pembayarannya.setChecked(true);
                                }
                                pembayarannya.setTypeface(openSansRegular);
                                pembayarannya.setTextSize(14);
                                pembayarannya.setId(pembayaran.getInt("id"));

                                blockPilihanPembayaran.addView(pembayarannya);
                            }
                        }

                        JSONArray jadwalArray = new JSONArray(result.getString("jadwal"));
                        if (jadwalArray.length() > 0) {
                            for (int i = 0; i < jadwalArray.length(); i++) {

                                final JSONObject jadwal = jadwalArray.getJSONObject(i);

                                RadioButton jadwalnya = new RadioButton(Shipping.this);
                                if (jadwal.getString("keterangan").isEmpty()) {
                                    jadwalnya.setText(jadwal.getString("jadwal"));
                                } else {
                                    jadwalnya.setText(jadwal.getString("jadwal") + "\n" + jadwal.getString("keterangan"));
                                }
                                if (i == 0) {
                                    jadwalnya.setChecked(true);
                                }
                                jadwalnya.setTypeface(openSansRegular);
                                jadwalnya.setTextSize(14);
                                jadwalnya.setId(jadwal.getInt("id"));

                                blockPilihanJadwal.addView(jadwalnya);
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

    private class SimpanOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Shipping.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", no_hp));
            params.add(new BasicNameValuePair("id_jadwal", args[0]));
            params.add(new BasicNameValuePair("id_pembayaran", args[1]));
            params.add(new BasicNameValuePair("id_alamat", idAlamat));
            params.add(new BasicNameValuePair("tglKirim", tglKirim));

            JSONObject json = jsonParser.makeHttpRequest(urlSimpanOrder, "POST", params);

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
                        editor.putInt("totalCart", 0);
                        editor.apply();
                        intent = new Intent(Shipping.this, PlacingOrder.class);
                        intent.putExtra("ketPembayaran", ketPembayaran);
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
}
