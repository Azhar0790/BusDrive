package busdriver.com.vidriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
 * Created by Sarps on 12/13/2016.
 */
public class MessageActivity extends AppCompatActivity {
    EditText et_message;
    Button btn_send;
    SharedPreferences pref;
    String email_id;
    ProgressDialog p_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        init();
        if (!InternetConnection.isInternetOn(MessageActivity.this)) {
            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(MessageActivity.this);
            alertbox.setTitle(getResources().getString(R.string.app_name));
            alertbox.setMessage(getResources().getString(R.string.internet));
            alertbox.setPositiveButton(
                    getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }
                    });
            alertbox.show();
        } else {
            pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(et_message.getText().toString().length()!=0) {
                        new messageAsynctask().execute();
                    }else {
                        Toast.makeText(getApplicationContext(),"Please enter your message",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void init() {
        btn_send = (Button) findViewById(R.id.btn_send);
        et_message = (EditText) findViewById(R.id.et_message);
        p_dialog = new ProgressDialog(MessageActivity.this);
    }

    class messageAsynctask extends AsyncTask<String, String, String> {
        String msg = et_message.getText().toString();
        String jsnStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p_dialog.setMessage("loading..");
            p_dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            System.out.println("email_id :- " + email_id);
            String url = "http://webview.bvibus.com/admin/insertMessage.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("subject", "Driver's Message"));
            param.add(new BasicNameValuePair("msg", msg));
            param.add(new BasicNameValuePair("em", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.POST, param);
            if (jsnStr != null) {
                jsnStr = "success";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            p_dialog.dismiss();
            if (jsnStr.equals("success")) {
                Toast.makeText(getApplicationContext(), "Your message was sent successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Message error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
