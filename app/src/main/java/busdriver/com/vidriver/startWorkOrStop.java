package busdriver.com.vidriver;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by ajldpc on 27/08/2016.
 */
public class startWorkOrStop {
    Context context;
    int action = 0;

    public startWorkOrStop(Context context, int action){
        this.context = context;
        this.action = action;
    }

    public void SendRequestServiAppMethod(){
        AsyncMethod task = new AsyncMethod();
        task.execute();
    }

    private class AsyncMethod extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
            String email = new DatabaseHandlerUser(context).getEmail();
            String txt = handler.post("http://webview.bvibus.com/admin/startworkorstop.php?em="+email+"&work="+action);
            Log.e("registro de user", txt);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }

    }
}
