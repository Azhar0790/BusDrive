package busdriver.com.vidriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import busdriver.com.vidriver.adapter.Message_adapter;
import busdriver.com.vidriver.module.Message_item;


/**
 * Created by Sarps on 2/8/2017.
 */
public class AdminMessageActivity extends AppCompatActivity {

    ListView lv_msg;
    ProgressDialog pDialog;
    SharedPreferences pref;
    String email_id;
    Message_adapter adapter;
    Message_item message_item;
    ArrayList<Message_item> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list_activity);
        init();
        list = new ArrayList<>();
        pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        email_id = pref.getString("email_id", "");
        new MessageListAsynctask().execute();
    }

    public void init() {
        lv_msg = (ListView) findViewById(R.id.lv_messages);
    }

    class MessageListAsynctask extends AsyncTask<String, String, String> {
        String url = "http://webview.bvibus.com/admin/list_admin_msg.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AdminMessageActivity.this);
            pDialog.setMessage("loading...");
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("em", email_id));

            String jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.POST, param);

            if (jsnStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsnStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String a_msg = jsonObject.getString("a_msg");
                        String dat = jsonObject.getString("d_date");
                        message_item = new Message_item(a_msg, dat);
                        list.add(message_item);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            adapter = new Message_adapter(getApplicationContext(), list);
            lv_msg.setAdapter(adapter);
        }
    }
}
