package ventures.g45.kebunsehati;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class KSFirebaseMessagingService extends FirebaseMessagingService {

    SharedPreferences sharedpreferences;
    private static final String TAG = KSFirebaseMessagingService.class.getSimpleName();
    String token;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
        sharedpreferences = getSharedPreferences("kebunsehati", 0);
        token = sharedpreferences.getString("token", "");
        if (token.isEmpty()) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("token", s);
            editor.apply();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        /*if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(NotifConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
            Log.e(TAG, "Disini: " + message);
        }*/
    }

    private void handleDataMessage(JSONObject json) {
        Log.d(TAG, "Di dalam handleDataMessage");
        /*try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            String tipe = data.getString("tipe");

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                if (tipe.equals("dapatdriver")) {
                    String hasil = data.getString("hasil");
                    Intent resultIntent = new Intent(getApplicationContext(), GetDriver.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    resultIntent.putExtra("result", hasil);
                    startActivity(resultIntent);
                } else {
                    if (tipe.equals("ontheway")) {
                        String hasil = data.getString("hasil");
                        Intent resultIntent = new Intent(getApplicationContext(), OnTheWay.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        resultIntent.putExtra("result", hasil);
                        startActivity(resultIntent);
                    } else {
                        if (tipe.equals("sampaitujuan")) {
                            String hasil = data.getString("hasil");
                            Intent resultIntent = new Intent(getApplicationContext(), Sampai.class);
                            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            resultIntent.putExtra("result", hasil);
                            startActivity(resultIntent);
                        }
                    }
                }
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent;
                if (tipe.equals("dapatdriver")) {
                    resultIntent = new Intent(getApplicationContext(), GetDriver.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //resultIntent.putExtra("result", hasil);
                } else {
                    if (tipe.equals("ontheway")) {
                        resultIntent = new Intent(getApplicationContext(), OnTheWay.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //resultIntent.putExtra("result", hasil);
                    } else {
                        //if (tipe.equals("sampaitujuan")) {
                        resultIntent = new Intent(getApplicationContext(), Sampai.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }*/
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        /*notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);*/
    }
}
