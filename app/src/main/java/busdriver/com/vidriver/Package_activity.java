package busdriver.com.vidriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WIN 10 on 12/18/2016.
 */
public class Package_activity extends AppCompatActivity {
    RadioGroup rg_packages;
    Button btn_apply;
    RadioButton rb_pack;
    SharedPreferences pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_package);
        pref=getSharedPreferences("Pref",MODE_PRIVATE);
        btn_apply = (Button) findViewById(R.id.btn_apply);
        rg_packages = (RadioGroup) findViewById(R.id.rg_packages);

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rg_packages.getCheckedRadioButtonId();
                rb_pack = (RadioButton) findViewById(selectedId);
                Toast.makeText(getApplicationContext(), "You choosed " + rb_pack.getText(), Toast.LENGTH_SHORT).show();
                if (rb_pack.getText().equals("Independent Driver | $49 per month with Free One Month Trial")) {
//                    Intent i = new Intent(getApplicationContext(), PaymentActivity.class);
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putString("price", "$49");
//                    editor.commit();
//                    startActivity(i);
                    new PackageAsynctask("$49").execute();
                } else {
//                    Intent i = new Intent(getApplicationContext(), PaymentActivity.class);
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putString("price", "$39");
//                    editor.commit();
//                    startActivity(i);
                    new PackageAsynctask("$39").execute();
                }
            }
        });
    }


    class PackageAsynctask extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null, email_id=null;

        String rbStr;

        public PackageAsynctask(String rbStr) {
            this.rbStr = rbStr;
        }

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("emaild", "");
            System.out.println("email_id :- " + email_id);
            String url = "http://webview.bvibus.com/admin/plan.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("plan", rbStr));
            param.add(new BasicNameValuePair("mailid", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.POST, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
                    if (message.equals("Updated")) {
                        Intent i=new Intent(getApplicationContext(),PaymentStripe.class);
                        i.putExtra("plan",rbStr);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"Plan selected successfully ",Toast.LENGTH_SHORT).show();
                    }else{
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new DatabaseHandlerUser(Package_activity.this).resetTables();
        finish();
        startActivity(new Intent(Package_activity.this, LoginActivity.class));
    }
}
