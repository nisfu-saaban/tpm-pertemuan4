package ventures.g45.kebunsehati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserAccount extends AppCompatActivity {

    Typeface openSansRegular;
    JSONParser jsonParser = new JSONParser();
    //private JsonArrayRequest request ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String defaultUrl, dataUrl, urlGetdetailMember, token, Url, noHp, mUrl, nama, email, urlSimpanBiodata;
    TextView inpNohp,  inpAlamat;
    EditText inpNama, inpEmail;
    ImageView inpAvatar, icoBack, EditAlamat;
    private ProgressDialog pDialog;
    private JSONObject result;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;
    private Uri filepath;
    Intent intent;
    Button btnEditBiodata, btnLogOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        getSupportActionBar().hide();

        intent = getIntent();
        noHp = intent.getStringExtra("no_telp");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");
        noHp = sharedPreferences.getString("noHp", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        dataUrl = ((KebunSehati) getApplication()).getUrlData();
        mUrl = ((KebunSehati) getApplication()).getmUrl();
        urlGetdetailMember = defaultUrl + "getDataMember.html";
        urlSimpanBiodata = defaultUrl + "simpanbiodata.html";

        nama = "";
        email = "";

        inpNama = (EditText) findViewById(R.id.inpNama);
        inpNama.setTypeface(openSansRegular);
        inpNohp = (TextView) findViewById(R.id.inpNohp);
        inpNohp.setTypeface(openSansRegular);
        inpEmail = (EditText) findViewById(R.id.inpEmail);
        inpEmail.setTypeface(openSansRegular);
        /*inpAlamat = (TextView) findViewById(R.id.inpAlamat);
        inpAlamat.setTypeface(openSansRegular);*/
        inpAvatar = (ImageView) findViewById(R.id.inpAvatar);
        //EditAlamat = (ImageView) findViewById(R.id.btnEditAlamat);
        btnEditBiodata = (Button) findViewById(R.id.btnEditBiodata);
        btnEditBiodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inpNama.getText().toString().isEmpty()) {
                    inpNama.setError("Silahkan isi nama anda");
                } else {
                    nama = inpNama.getText().toString();
                }
                if (inpEmail.getText().toString().isEmpty()) {
                    inpEmail.setError("Silahkan isi email anda");
                } else {
                    email = inpEmail.getText().toString();
                }

                if ((!nama.isEmpty()) && (!email.isEmpty())) {
                    new SimpanBiodata().execute();
                }
            }
        });

        inpNama.setBackground(null);
        inpEmail.setBackground(null);

        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /*EditAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAccount.this, ChangeAddress.class);
                startActivity(intent);
            }
        });*/

        inpAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove("noHp");
                editor.commit();
                intent = new Intent(UserAccount.this, MainActivity.class);
                startActivity(intent);
            }
        });

        new GetDetailMember().execute();

    }

    private class GetDetailMember extends AsyncTask<Void, Void, JSONObject> {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(UserAccount.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));

            JSONObject json = jsonParser.makeHttpRequest(urlGetdetailMember, "POST", params);

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

                        inpNama.setText(result.getString("nama"));
                        inpEmail.setText(result.getString("email"));
                        inpNohp.setText(result.getString("noHp"));

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getImage() {
        CharSequence[] menu = {"Kamera", "Galeri"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Upload Foto")
                .setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //Mengambil gambar dari Kemara ponsel
                                Intent imageIntentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(imageIntentCamera, REQUEST_CODE_CAMERA);
                                break;

                            case 1:
                                //Mengambil gambar dari galeri
                                Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(imageIntentGallery, REQUEST_CODE_GALLERY);
                                break;
                        }
                    }
                });
        dialog.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    inpAvatar.setVisibility(View.VISIBLE);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    inpAvatar.setImageBitmap(bitmap);
                    filepath = data.getData();
                }
                break;

            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    inpAvatar.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    inpAvatar.setImageURI(uri);
                    filepath = data.getData();
                }
                break;
        }
    }

    private class SimpanBiodata extends AsyncTask<Void, Void, JSONObject> {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(UserAccount.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("noHp", noHp));
            params.add(new BasicNameValuePair("nama", nama));
            params.add(new BasicNameValuePair("email", email));

            JSONObject json = jsonParser.makeHttpRequest(urlSimpanBiodata, "POST", params);

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
                        finish();
                        startActivity(getIntent());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
