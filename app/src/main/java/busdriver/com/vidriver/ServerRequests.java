package busdriver.com.vidriver;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.util.ArrayList;


public class ServerRequests {
    ProgressDialog progressDialog;
    RadioGroup rg_packages;
    private RadioButton rb_pack;
    Dialog dialog_;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://webview.bvibus.com/admin/";
    SharedPreferences pref;
    Context context;
    User user;
    String activo = null, email = null;

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Connecting...");
        pref = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        this.context = context;
    }

    public void storeUserDataInBackground(User user,
                                          GetUserCallback userCallBack) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallBack).execute();
    }

    public void fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
        progressDialog.show();
        new fetchUserDataAsyncTask(user, userCallBack).execute();
    }

    /**
     * parameter sent to task upon execution progress published during
     * background computation result of the background computation
     */

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name", user.name));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS
                    + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private HttpParams getHttpRequestParams() {
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            return httpRequestParams;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            userCallBack.done(null);
        }

    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallBack;
        String activo = null, email = null, is_free = null;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {


            User returnedUser = null;

            try {

                httpHandler httphandler = new httpHandler();
                String responseText = httphandler.post2(SERVER_ADDRESS + "fetchdatauseruser.php?username=" + user.username + "&pass=" + user.password, user.username, user.password);
                Log.i("Resultado consulta", responseText);
                JSONObject jObject = new JSONObject(responseText);
                if (jObject.length() != 0) {
                    email = jObject.getString("email");
                    activo = jObject.getString("activo");
                    is_free = jObject.getString("is_free");
                    String id = jObject.getString("id");

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("id", id);
                    editor.putString("emaild", email);
                    editor.putString("is_free", is_free);
                    editor.commit();

                    System.out.println("activo :- " + activo);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            try {
                if (activo.equals("1")) {
                    returnedUser = new User(email, 0, 0, user.username, user.password, "app", "app");
                    //                context.startActivity(new Intent(context,PaymentStripe.class));
                    //                context.startActivity(new Intent(context,Package_activity.class));
                    //                new TrialPeriod().execute();
                    context.startActivity(new Intent(context, MapsActivity.class));
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("e :- " + e);
            }
            userCallBack.done(returnedUser);
        }
    }


}