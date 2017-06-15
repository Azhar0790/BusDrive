package busdriver.com.vidriver;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ajldpc on 28/10/2016.
 */
public class AdsLoader {
    Context context;
    int Time = 0;
    String urlimagen;
    String url = "http://webview.bvibus.com/admin/getAds.php?t=1";
    ImageView imageView;
    public AdsLoader(Context context, int Time){
     this.context = context;
     this.Time = Time;
    }

    public void LoadAd(){
        Timer t = new Timer();
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                context.startActivity(new Intent(context,AdsActivity.class));
            }
        };
        t.schedule(tarea,Time);
    }

    public void LoadBanner(ImageView banner){
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new LoadBannerAsync().execute();
            }
        }, 0, 15000);
        imageView = banner;
    }

    private class LoadBannerAsync extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "";


        @Override
        protected Void doInBackground(Void... arg0) {


            HttpHandleer sh = new HttpHandleer();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    urlimagen = jsonObj.getString("imagen");

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (urlimagen != null) {
                try {
                    Picasso.with(context).load(urlimagen).into(imageView);
                } catch (Exception e) {
                //    Toast.makeText(context, "Error 404: Image not load", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.e("No cargó el anuncio", "Ocurrió un error al cargar el anuncio URL de imagen NULO!");

            }
        }
    }
}
