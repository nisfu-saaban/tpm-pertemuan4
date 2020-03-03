package ventures.g45.kebunsehati;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends Activity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String no_hp, token, session, mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();
        no_hp = sharedPreferences.getString("noHp", "");
        token = sharedPreferences.getString("token", "");
        session = sharedPreferences.getString("session", "");

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    /*if (no_hp.isEmpty()) {
                        Intent intent = new Intent(SplashScreen.this, SignIn.class);
                        startActivity(intent);
                    } else {
                        //editor.putString("id_alamat", "");
                        //editor.apply();*/
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    //}
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
