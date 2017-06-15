package busdriver.com.vidriver;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by richardalexander on 03/03/16.
 */
public class BackgroundTaskRealTimeLocation {

    Context context;
    Location latLng;

    public BackgroundTaskRealTimeLocation(Context context, Location latLng) {
        this.context = context;
        this.latLng = latLng;
    }

    public void RealTimeLocatioSubmit() {
        TaskRealTimeLocationSubmit task = new TaskRealTimeLocationSubmit();
        task.execute();
    }

    private class TaskRealTimeLocationSubmit extends AsyncTask<Void, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                httpHandler handler = new httpHandler();
                DatabaseHandlerUser db = new DatabaseHandlerUser(context);
                String email = db.getEmail();
                //YYYY-MM-DD HH:MM:SS
                //last_update_ time
                DateFormat df = new android.text.format.DateFormat();
                Date date = new Date();
                String d = df.format("yyyy-MM-dd hh:mm:ss", date) + "";
                d = d.replaceAll(" ", "%20");

                String txt = handler.post("http://webview.bvibus.com/admin/IAmOnline.php?em=" + email + "&lat=" + latLng.getLatitude() + "&lng=" + latLng.getLongitude());
//                String txt = handler.post("http://webview.bvibus.com/admin/IAmOnline.php?em=" + email + "&lat=" + latLng.getLatitude() + "&lng=" + latLng.getLongitude() + "&last_update_time=" + d);

                JSONObject jsonObject = new JSONObject(txt);
                String lat = jsonObject.getString("lat");
                String lng = jsonObject.getString("long");

                System.out.println("jsonObject :- " + jsonObject.toString());
                System.out.println("lng :- " + lng);
                System.out.println("lng :- " + lng);

                Log.e("REALTIMELOCATION", txt);
            } catch (Exception e) {

            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                Log.i("Location real time upda", "Good");
        }

    }

}
