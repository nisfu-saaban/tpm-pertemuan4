package ventures.g45.kebunsehati;

import android.app.Application;

/*import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;*/

public class KebunSehati extends Application {

    public static final String TAG = KebunSehati.class.getSimpleName();

    /*private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;*/
    private static KebunSehati mInstance;

    private String url, urlData, mUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        setUrl();
        setUrlData();
        setmUrl();
    }

    public static synchronized KebunSehati getInstance() {
        return mInstance;
    }

    /*public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }*/

    public String getUrl() {
        return url;
    }

    public void setUrl() {
        this.url = "http://kebunsehati.com/api/";
    }

    public void setUrlData() {
        this.urlData = "http://data.kebunsehati.com/";
    }

    public String getUrlData() {
        return urlData;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl() {
        this.mUrl = "https://www.kebunsehati.id/";
    }
}
