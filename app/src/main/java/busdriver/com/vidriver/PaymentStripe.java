package busdriver.com.vidriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarps on 1/16/2017.
 */
public class PaymentStripe extends AppCompatActivity {
    EditText number, cvc;
    Spinner expMonth, expYear, currency;
    Button save;
    SharedPreferences pref;
    String email;
    String month, year, currency_type;
    ProgressDialog dialog;
    String plan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        pref = getSharedPreferences("Pref", MODE_PRIVATE);
        email = pref.getString("email_id", "");
        number = (EditText) findViewById(R.id.number);
        expMonth = (Spinner) findViewById(R.id.expMonth);
        expYear = (Spinner) findViewById(R.id.expYear);
        cvc = (EditText) findViewById(R.id.cvc);
        currency = (Spinner) findViewById(R.id.currency);
        save = (Button) findViewById(R.id.save);
        Bundle bundle=getIntent().getExtras();
        plan=bundle.getString("plan");
        expMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = parent.getSelectedItem().toString();
                System.out.println("month :- " + month);
                expYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        year = parent.getSelectedItem().toString();
                        System.out.println("year :- " + year);
//                        currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                System.out.println("testing :- " );
//                                currency_type = parent.getSelectedItem().toString();
//                                System.out.println("currency_type :- " + currency_type);
                        System.out.println("testing :- " );
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        System.out.println("data :- ");
                                        if (number.getText().toString().length() < 15) {
                                            System.out.println("Please Enter Valid Card Number");
                                            number.setError("Please enter the valid card number");
                                        } else if (cvc.getText().toString().length() < 3) {
                                            System.out.println("Please Enter Valid Card Number");
                                            number.setError("Please enter valid the cvc number");
                                        } else {
                                            new stripeAsynctask().execute();
                                        }
                                    }
                                });

//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    class stripeAsynctask extends AsyncTask<String, String, String> {

        String url = "http://webview.bvibus.com/stripe/createCustomer.php";
        String message, jsnStr = null;
        String card_number = number.getText().toString();

        String cvc_number = cvc.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(PaymentStripe.this);
            dialog.setMessage("Uploading the data");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("cust_mail", email));
            param.add(new BasicNameValuePair("plan_name", plan));
            param.add(new BasicNameValuePair("card_number", card_number));
            param.add(new BasicNameValuePair("month", month));
            param.add(new BasicNameValuePair("year", year));
            param.add(new BasicNameValuePair("cvc_number", cvc_number));

            System.out.println("email :-"+email);
            System.out.println("card_number :-"+card_number);
            System.out.println("month :-"+month);
            System.out.println("cvc_number :-"+cvc_number);

            jsnStr = sh.makeServiceCall(url, ServiceHandler.POST, param);
            if (jsnStr != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
                    System.out.println("message :-"+message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                if (message.equals("Insert Successfully"))
                {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), AlarmActivity2.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
