package ventures.g45.kebunsehati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

public class Category extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    String noHp, defaultUrl, urlGetProdukByKategori, token, kategori, dataUrl, idKategori;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    float factor;
    DecimalFormat decimalFormat;
    LinearLayout blockKatalogProduk;
    Intent intent;
    TextView inpSearch;
    ImageView icoBack;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

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
        urlGetProdukByKategori = defaultUrl + "getpodukbykategori.html";
        factor = getResources().getDisplayMetrics().density;

        decimalFormat = new DecimalFormat("#,###.##");

        blockKatalogProduk = (LinearLayout) findViewById(R.id.blockKatalogProduk);

        intent = getIntent();
        idKategori = intent.getStringExtra("id");

        inpSearch = (TextView) findViewById(R.id.inpSearch);
        inpSearch.setTypeface(openSansSemiBold);

        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        inpSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductSearching();
            }
        });

        new GetProdukByKategori().execute();
    }

    private class GetProdukByKategori extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Category.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("kategori", kategori));
            params.add(new BasicNameValuePair("idKategori", idKategori));
            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject json = jsonParser.makeHttpRequest(urlGetProdukByKategori, "POST", params);

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
                        inpSearch.setText(result.getString("kategori"));
                        JSONArray katalogArray = new JSONArray(result.getString("produk"));
                        int baris = (int) Math.ceil(katalogArray.length()/2);
                        int k = 0;
                        if (katalogArray.length() > 0) {
                            for (int i = 0; i < baris; i++) {
                                LinearLayout linearLayoutProduk = new LinearLayout(Category.this);
                                linearLayoutProduk.setWeightSum(2);

                                for (int j = 0; j < katalogArray.length(); j++) {

                                    final JSONObject barang = katalogArray.getJSONObject(k);

                                    LinearLayout linearLayoutKiri = new LinearLayout(Category.this);
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

                                    ImageView thumbnail = new ImageView(Category.this);
                                    Picasso.get().load(dataUrl + "uploads/thumbnail/" + barang.getString("thumbnail")).into(thumbnail);
                                    thumbnail.setPadding((int)(1*factor), (int)(1*factor), (int)(1*factor), (int)(1*factor));
                                    thumbnail.setAdjustViewBounds(true);
                                    thumbnail.setId(View.generateViewId());

                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                                    thumbnail.setLayoutParams(layoutParams);

                                    linearLayoutKiri.addView(thumbnail);

                                    /*RelativeLayout.LayoutParams layoutLabel = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutLabel.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                                    TextView label = new TextView(Category.this);
                                    label.setLayoutParams(layoutLabel);
                                    label.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                    label.setTypeface(openSansSemiBold);
                                    label.setTextSize(14);
                                    label.setText( barang.getString("nama_label"));
                                    label.setMaxLines(1);
                                    label.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayoutKiri.addView(label);*/

                                    RelativeLayout.LayoutParams layoutNama = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutNama.addRule(RelativeLayout.BELOW, thumbnail.getId());
                                    TextView nama = new TextView(Category.this);
                                    nama.setLayoutParams(layoutNama);
                                    nama.setPadding((int)(10*factor), (int)(10*factor), (int)(10*factor), (int)(10*factor));
                                    nama.setTypeface(openSansSemiBold);
                                    nama.setTextSize(14);
                                    nama.setText( barang.getString("nama_produk"));
                                    nama.setMaxLines(1);
                                    nama.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayoutKiri.addView(nama);

                                    ViewGroup.LayoutParams layoutSatuan = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                    TextView satuan = new TextView(Category.this);
                                    satuan.setLayoutParams(layoutSatuan);
                                    satuan.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                    satuan.setTypeface(openSansSemiBold);
                                    satuan.setTextSize(14);
                                    satuan.setText( barang.getString("qty") + " " + barang.getString("nama_satuan"));
                                    satuan.setMaxLines(1);
                                    satuan.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayoutKiri.addView(satuan);

                                    ViewGroup.LayoutParams layoutHarga = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                    TextView harga = new TextView(Category.this);
                                    harga.setLayoutParams(layoutHarga);
                                    harga.setPadding((int)(10*factor), 0, (int)(10*factor), (int)(10*factor));
                                    harga.setTypeface(openSansSemiBold);
                                    harga.setTextSize(14);
                                    harga.setText( "IDR " + decimalFormat.format(barang.getInt("harga")));
                                    harga.setMaxLines(1);
                                    harga.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                    harga.setEllipsize(TextUtils.TruncateAt.END);
                                    linearLayoutKiri.addView(harga);

                                    linearLayoutKiri.setOnClickListener(new DetailProduk(barang.getString("id_produk")));

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
            Intent intent = new Intent(Category.this, Detail.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    public void ProductSearching(){
        dialog = new AlertDialog.Builder(Category.this, R.style.Searching);
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
                    intent = new Intent(Category.this, SearchingResult.class);
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
        intent = new Intent(Category.this, SearchingResult.class);
        intent.putExtra("kata", tag);
        startActivity(intent);
    }
}
