package ventures.g45.kebunsehati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignIn extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    TextView txtEnterPhoneNumber, txtWelcome;
    EditText inpKodeNegara, inpNoTelp;
    Button btnLanjutkan;
    Intent intent;
    String noHp, kode, defaultUrl, urlSignIn, token;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        openSansSemiBold = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(),  "fonts/OpenSans-Bold.ttf");

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlSignIn = defaultUrl + "signin.html";

        txtEnterPhoneNumber = (TextView) findViewById(R.id.txtEnterPhoneNumber);
        txtEnterPhoneNumber.setTypeface(openSansRegular);
        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        txtWelcome.setTypeface(openSansBold);
        inpKodeNegara = (EditText) findViewById(R.id.inpKodeNegara);
        inpKodeNegara.setTypeface(openSansSemiBold);
        inpNoTelp = (EditText) findViewById(R.id.inpNoTelp);
        inpNoTelp.setTypeface(openSansSemiBold);
        btnLanjutkan = (Button) findViewById(R.id.btnLanjutkan);
        btnLanjutkan.setTypeface(openSansLight);

        btnLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kode = inpKodeNegara.getText().toString();
                noHp = inpNoTelp.getText().toString();
                if ((!kode.isEmpty()) && (!noHp.isEmpty())) {
                    new Login().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Kode Negara dan No. Handphone tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (token.isEmpty()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w("", "getInstanceId failed", task.getException());
                        return;
                    }
                    token = task.getResult().getToken();
                    editor.putString("token", token);
                    editor.apply();
                }
            });
        }
    }

    private class Login extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("phone", noHp));
            params.add(new BasicNameValuePair("kode", kode));

            JSONObject json = jsonParser.makeHttpRequest(urlSignIn, "POST", params);

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
                        intent = new Intent(SignIn.this, OneTimePassword.class);
                        if (result.getString("nama").isEmpty()) {
                            intent.putExtra("phoneNumber", kode + noHp);
                            intent.putExtra("status", "regis");
                        } else {
                            intent.putExtra("phoneNumber", kode + noHp);
                            intent.putExtra("status", "login");
                        }
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
