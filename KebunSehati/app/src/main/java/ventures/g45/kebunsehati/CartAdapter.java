package ventures.g45.kebunsehati;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.travijuu.numberpicker.library.NumberPicker;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends ArrayAdapter<CartInfo> {

    Typeface openSansLight, openSansSemiBold, openSansRegular;
    private Activity activity;
    DecimalFormat decimalFormat;

    public CartAdapter(Activity activity, int resource, int textViewResourceId, List<CartInfo> todoes) {
        super(activity, resource, textViewResourceId, todoes);
        this.activity = activity;
        openSansSemiBold = Typeface.createFromAsset(activity.getAssets(),  "fonts/OpenSans-SemiBold.ttf");
        openSansLight = Typeface.createFromAsset(activity.getAssets(),  "fonts/OpenSans-Light.ttf");
        openSansRegular = Typeface.createFromAsset(activity.getAssets(),  "fonts/OpenSans-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CartAdapter.ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_cart, parent, false);
            // get all UI view
            holder = new CartAdapter.ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        }  else {
            // if holder created, get tag from view
            holder = (CartAdapter.ViewHolder) convertView.getTag();
        }

        CartInfo cartInfo = getItem(position);

        holder.txtNamaProduk.setText(cartInfo.getNama());
        holder.txtNamaProduk.setTypeface(openSansRegular);

        return convertView;
    }

    private static class ViewHolder {
        private  ImageView imgProduk;
        private TextView txtNamaProduk, txtBeratProduk, txtHargaProduk;
        private NumberPicker number_picker;

        public ViewHolder(View v) {
            imgProduk = v.findViewById(R.id.imgProduk);
            txtNamaProduk = v.findViewById(R.id.txtNamaProduk);
            txtBeratProduk = v.findViewById(R.id.txtBeratProduk);
            txtHargaProduk = v.findViewById(R.id.txtHargaProduk);
            number_picker = v.findViewById(R.id.number_picker);
        }
    }
}
