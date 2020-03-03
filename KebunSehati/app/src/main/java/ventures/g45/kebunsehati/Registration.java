package ventures.g45.kebunsehati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    TextView txtNama, txtEmail, txtPIN;
    EditText inpNama, inpEmail, inpPIN;
    Button btnLanjutkan;
    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    String sNama, sEmail, sPIN, sNoHP, defaultUrl, urlSignUp, token;
    Intent intent;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        openSansSemiBold = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-Bold.ttf");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlSignUp = defaultUrl + "signup.html";

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");

        txtNama = (TextView) findViewById(R.id.txtNama);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPIN = (TextView) findViewById(R.id.txtPIN);
        txtNama.setTypeface(openSansSemiBold);
        txtEmail.setTypeface(openSansSemiBold);
        txtPIN.setTypeface(openSansSemiBold);

        inpNama = (EditText) findViewById(R.id.inpNama);
        inpEmail = (EditText) findViewById(R.id.inpEmail);
        inpPIN = (EditText) findViewById(R.id.inpPIN);
        inpNama.setTypeface(openSansRegular);
        inpEmail.setTypeface(openSansRegular);
        inpPIN.setTypeface(openSansRegular);

        intent = getIntent();
        sNoHP = intent.getStringExtra("noHp");

        btnLanjutkan = (Button) findViewById(R.id.btnLanjutkan);
        btnLanjutkan.setTypeface(openSansRegular);

        btnLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNama = inpNama.getText().toString();
                sEmail = inpEmail.getText().toString();
                sPIN = inpPIN.getText().toString();
                if ((!sNama.isEmpty()) && (!sEmail.isEmpty()) && (!sPIN.isEmpty())) {
                    if (isEmailValid(sEmail)) {
                        new SignUp().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email yang anda isikan tidak valid!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nama, Email dan PIN tidak boleh kosong!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private class SignUp extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(Registration.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("phone", sNoHP));
            params.add(new BasicNameValuePair("nama", sNama));
            params.add(new BasicNameValuePair("email", sEmail));
            params.add(new BasicNameValuePair("pin", sPIN));
            params.add(new BasicNameValuePair("token", token));

            JSONObject json = jsonParser.makeHttpRequest(urlSignUp, "POST", params);

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
                        intent = new Intent(Registration.this, MainActivity.class);
                        editor.putString("nama", sNama);
                        editor.putString("noHp", sNoHP);
                        editor.putString("kategori", "1");
                        editor.apply();
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
