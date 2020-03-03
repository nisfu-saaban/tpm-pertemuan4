package ventures.g45.kebunsehati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OneTimePassword extends AppCompatActivity {

    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    TextView txtIsikan, txtPhoneNumber, txtTidakTerima;
    EditText inpKodeOTP;
    Intent intent;
    String phoneNumber, status, token, defaultUrl, urlSignIn;
    Button btnPin;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    private FirebaseAuth mAuth;
    private String verificationId;
    RelativeLayout blockTidakTerima;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time_password);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        sharedpreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedpreferences.edit();
        token = sharedpreferences.getString("token", "");

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlSignIn = defaultUrl + "signinpin.html";

        txtIsikan = (TextView) findViewById(R.id.txtIsikan);
        txtIsikan.setTypeface(openSansRegular);
        txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
        txtPhoneNumber.setTypeface(openSansSemiBold);
        inpKodeOTP = (EditText) findViewById(R.id.inpKodeOTP);
        inpKodeOTP.setTypeface(openSansSemiBold);
        btnPin = (Button) findViewById(R.id.btnPin);
        btnPin.setTypeface(openSansLight);
        txtTidakTerima = (TextView) findViewById(R.id.txtTidakTerima);
        txtTidakTerima.setTypeface(openSansLight);
        blockTidakTerima = (RelativeLayout) findViewById(R.id.blockTidakTerima);

        intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        status = intent.getStringExtra("status");
        //txtPhoneNumber.setText(status + " " + phoneNumber);

        if (status.equals("regis")) {
            blockTidakTerima.setVisibility(View.INVISIBLE);
        }

        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new AlertDialog.Builder(OneTimePassword.this, R.style.DialogPutih);
                inflater = getLayoutInflater();
                dialogView = inflater.inflate(R.layout.popup_input_pin, null);
                dialog.setView(dialogView);
                final AlertDialog alertDialog = dialog.create();
                Button btnPin = (Button) dialogView.findViewById(R.id.btnPin);
                final EditText inpPin = (EditText) dialogView.findViewById(R.id.inpPin);
                btnPin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Login().execute(inpPin.getText().toString());
                    }
                });

                alertDialog.show();
            }
        });

        //inpKodeOTP.addTextChangedListener(hitungPanjang);

        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode(phoneNumber);

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                //resend.setText("Request a new code in : " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                //resend.setText("Resend code!");
                if (status.equals("login")) {
                    blockTidakTerima.setVisibility(View.VISIBLE);
                    btnPin.setEnabled(true);
                    btnPin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    intent = new Intent(OneTimePassword.this, Registration.class);
                    intent.putExtra("noHp", phoneNumber);
                    startActivity(intent);
                }
            }

        }.start();
    }


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (status.equals("login")) {
                                editor.putString("noHp", phoneNumber);
                                editor.apply();
                                intent = new Intent(OneTimePassword.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            } else {
                                intent = new Intent(OneTimePassword.this, Registration.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("noHp", phoneNumber);
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(OneTimePassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                inpKodeOTP.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OneTimePassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    /*public void NextAktivasi() {
        String code = inpKodeOTP.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6) {
            inpKodeOTP.setError("Enter code...");
            inpKodeOTP.requestFocus();
            return;
        }
        verifyCode(code);
    }

    public final TextWatcher hitungPanjang = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 6) {
                NextAktivasi();
            }
        }
    };*/



    private class Login extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(OneTimePassword.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("phone", phoneNumber));
            params.add(new BasicNameValuePair("pin", args[0]));
            params.add(new BasicNameValuePair("token", token));

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
                        if (result.getInt("error") == 2) {
                            Toast.makeText(getApplicationContext(), result.getString("pesan"), Toast.LENGTH_LONG).show();
                        } else {
                            intent = new Intent(OneTimePassword.this, MainActivity.class);
                            editor.putString("nama", result.getString("nama"));
                            editor.putString("email", result.getString("email"));
                            editor.putString("kategori", result.getString("id_kategori"));
                            editor.putString("noHp", phoneNumber);
                            editor.apply();
                            startActivity(intent);
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
}
