package ventures.g45.kebunsehati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ventures.g45.kebunsehati.Adapter.ARiwayatOrder;
import ventures.g45.kebunsehati.model.MRiwayatOrder;

public class RiwayatOrder extends AppCompatActivity {

    Intent intent;
    String noHp, idOrder ,token, no_hp, defaultUrl, urlGetTransaksi;
    float factor;
    ImageView icoBack;
    RelativeLayout blockRingkasan, blockNoTransaksi;
    Integer id_order;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private RecyclerView rvView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MRiwayatOrder> riwayatOrder;
    //private JsonArrayRequest request;
    private RequestQueue requestQueue;
    ARiwayatOrder adapter;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    TextView idorderan, waktuorder, status, total;
    DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_order);

        getSupportActionBar().hide();

        intent = getIntent();
        noHp = intent.getStringExtra("no_hp");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        id_order = sharedPreferences.getInt("idOrder", 0);
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlGetTransaksi = defaultUrl + "getTransaksi.html";
        decimalFormat = new DecimalFormat("#,###.##");

        idorderan = (TextView)findViewById(R.id.idOrder);
        waktuorder = (TextView)findViewById(R.id.waktuOrder);
        status = (TextView) findViewById(R.id.status);
        total = (TextView) findViewById(R.id.totalOrder);

        riwayatOrder= new ArrayList<>() ;
        rvView = findViewById(R.id.rvRingkasan);

        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        blockRingkasan = (RelativeLayout) findViewById(R.id.blockRingkasan);
        blockNoTransaksi = (RelativeLayout) findViewById(R.id.blockNoTransaksi);


        new GetTransaksi().execute();

    }

    private class GetTransaksi extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(RiwayatOrder.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));


            JSONObject json = jsonParser.makeHttpRequest(urlGetTransaksi, "POST", params);

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
                        JSONArray arrayKategori = new JSONArray(result.getString("data"));
                        if (arrayKategori.length() > 0) {
                            blockNoTransaksi.setVisibility(View.INVISIBLE);
                            for (int i = 0; i < arrayKategori.length(); i++) {

                                final JSONObject kategori = arrayKategori.getJSONObject(i);

                                MRiwayatOrder riwayat = new MRiwayatOrder();
                                riwayat.setIdOrder(kategori.getString("id_order"));
                                riwayat.setWaktuOrder(kategori.getString("waktu_order"));
                                riwayat.setTotalOrder(kategori.getInt("total"));
                                riwayat.setStatus(kategori.getString("status"));

                                riwayatOrder.add(riwayat);
                            }
                        } else {
                            blockNoTransaksi.setVisibility(View.VISIBLE);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            setuprecyclerview(riwayatOrder);
        }

    }

    private void setuprecyclerview(ArrayList<MRiwayatOrder> riwayatOrder) {
        ARiwayatOrder myadapter = new ARiwayatOrder(riwayatOrder, this);
        rvView.setLayoutManager(new LinearLayoutManager(this));
        rvView.setAdapter(myadapter);
    }
}
