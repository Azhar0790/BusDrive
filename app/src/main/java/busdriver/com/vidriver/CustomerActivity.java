package busdriver.com.vidriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarps on 2/8/2017.
 */
public class CustomerActivity extends AppCompatActivity {
    Button btn_snd_feedback;
    EditText et_feeback;
    ProgressDialog pDialog;
    SharedPreferences pref;
    String email_id,admin_msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cust_feed_activity);
        init();
        pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        email_id = pref.getString("email_id", "");
        try {
            Bundle bundle=getIntent().getExtras();
            admin_msg= bundle.getString("admin_msg");
            if(admin_msg!=null){
                getSupportActionBar().setTitle("Reply To Admin");
                btn_snd_feedback.setText("Reply");
                et_feeback.setHint("Reply admin...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error :- "+e);
        }
        btn_snd_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_feeback.getText().toString().length()!=0) {
                    new SendFeedbackAsynctask().execute();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter some text",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void init() {
        btn_snd_feedback = (Button) findViewById(R.id.btn_snd_feedback);
        et_feeback = (EditText) findViewById(R.id.et_feedback);
    }

    class SendFeedbackAsynctask extends AsyncTask<String, String, String> {
        String msg = et_feeback.getText().toString();
        String url = "http://webview.bvibus.com/admin/insertFeedback.php";
        String jsnStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CustomerActivity.this);
            pDialog.setMessage("please wait while loading..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("em", email_id));
            param.add(new BasicNameValuePair("msg", msg));

            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.POST, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (jsnStr != null) {
                if (jsnStr.equals("Success")) {
                    Toast.makeText(getApplicationContext(), "Feedback send successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    finish();
                }
            }

        }
    }
}
