package ventures.g45.kebunsehati;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Inbox extends AppCompatActivity {

    ImageView icoBack;
    Typeface openSansLight, openSansSemiBold, openSansRegular, openSansBold;
    TextView txtTitle, txtNoInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        openSansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTypeface(openSansSemiBold);
        txtNoInbox = (TextView) findViewById(R.id.txtNoInbox);
        txtNoInbox.setTypeface(openSansSemiBold);

        icoBack = (ImageView) findViewById(R.id.icoBack);
        icoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
