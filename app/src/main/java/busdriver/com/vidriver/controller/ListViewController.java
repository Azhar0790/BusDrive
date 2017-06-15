package busdriver.com.vidriver.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Plan;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import busdriver.com.vidriver.GetUserCallback;
import busdriver.com.vidriver.LoginActivity;
import busdriver.com.vidriver.R;
import busdriver.com.vidriver.ServiceHandler;
import busdriver.com.vidriver.User;
import busdriver.com.vidriver.httpHandler;

/**
 * A controller for the {@link ListView} used to display the results.
 */
public class ListViewController {
    TextView tokenId;
    String price, str_tokenId, emaild;
    SharedPreferences pref;
    RequestQueue requestQueue;
    private SimpleAdapter mAdatper;
    private List<Map<String, String>> mCardTokens = new ArrayList<Map<String, String>>();
    private Context mContext;

    public ListViewController(ListView listView) {
        mContext = listView.getContext();
        pref = mContext.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        mAdatper = new SimpleAdapter(
                mContext,
                mCardTokens,
                R.layout.list_item_layout,
                new String[]{"last4", "tokenId"},
                new int[]{R.id.last4, R.id.tokenId});
        listView.setAdapter(mAdatper);
        price = pref.getString("price", "");
        str_tokenId = pref.getString("tokenId", "");
        emaild = pref.getString("emaild", "");
        //Log.v("TOKEN:-", String.valueOf(tokenId));

    }

    void addToList(Token token) {
        addToList(token.getCard().getLast4(), token.getId());
    }

    void addToList(@NonNull String last4, @NonNull String tokenId) {
        String endingIn = mContext.getString(R.string.endingIn);
        Map<String, String> map = new HashMap<>();
        map.put("last4", endingIn + " " + last4);
        map.put("tokenId", tokenId);
        mCardTokens.add(map);
        mAdatper.notifyDataSetChanged();
        System.out.println("TOK:-" + tokenId);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("tokenId", tokenId);
        editor.commit();


        new insertTokenAsynctask().execute(tokenId);

    }

    class insertTokenAsynctask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String s = params[0];
            System.out.println("s :-" + s);
            String url = "http://webview.bvibus.com/admin/insertToken.php";
            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("em", emaild));
            param.add(new BasicNameValuePair("token", s));
            param.add(new BasicNameValuePair("price", price));

            String jsnStr = sh.makeServiceCall(url, ServiceHandler.GET, param);
            if (jsnStr != null) {
                jsnStr = "Success";
                if (jsnStr.equals("Success")) {

                    try {
                        System.out.println("jsnStr :- " + jsnStr);
                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                        Map<String, Object> chargeParams = new HashMap<String, Object>();
                        chargeParams.put("amount", price);
                        chargeParams.put("currency", "usd");
                        chargeParams.put("source", s); // obtained with Stripe.js
                        chargeParams.put("description", "");

                        Map<String, Object> params2 = new HashMap<String, Object>();
                        params2.put("name", "Basic Plan");
                        params2.put("id", "basic-monthly");
                        params2.put("interval", "month");
                        params2.put("currency", "usd");
                        params2.put("amount", price);


                        Map<String, Object> params3 = new HashMap<String, Object>();
                        params3.put("email", emaild);
                        Charge.create(chargeParams);
                        Plan.create(params2);
                        Customer.create(params3);

                        System.out.println("Charge String :- " + Charge.create(chargeParams));
                        System.out.println("Plan String :- " + Plan.create(params2));
                        System.out.println("Customer String :- " + Customer.create(params3));
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    } catch (InvalidRequestException e) {
                        e.printStackTrace();
                    } catch (APIConnectionException e) {
                        e.printStackTrace();
                    } catch (CardException e) {
                        e.printStackTrace();
                    } catch (APIException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        public static final String SERVER_ADDRESS = "http://webview.bvibus.com/admin/";
        User user;
        GetUserCallback userCallBack;
        String activo = null, email = null;

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
                    String id = jObject.getString("id");

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("id", id);
                    editor.putString("emaild", email);
                    editor.commit();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            if (activo.equals("1")) {
                returnedUser = new User(email, 0, 0, user.username, user.password, "app", "app");
            } else {

            }
            userCallBack.done(returnedUser);
        }
    }
}
