package busdriver.com.vidriver;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText et_email;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private int time = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        progressDialog = new ProgressDialog(ResetPasswordActivity.this);
        ResetPasswordActivity.this.getSupportActionBar().show();
        ResetPasswordActivity.this.getSupportActionBar().setHomeButtonEnabled(true);
        et_email = (EditText) findViewById(R.id.et_email);
        Button save = (Button) findViewById(R.id.btnsave);
//        GET_EMAIL_ADDRESSES();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_email.getText().toString().equals("")) {
                    new ChangePasswordTask().execute();
                    ResetPasswordActivity.this.finish();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Password is empty!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /**
         * handle home button pressed
         */
        if (id == android.R.id.home) {
            //Start your main activity here
            ResetPasswordActivity.this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ChangePasswordTask extends AsyncTask<Void, Integer, String> {
        String message = null;
        String email = et_email.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                httpHandler handler = new httpHandler();
                DatabaseHandlerUser db = new DatabaseHandlerUser(ResetPasswordActivity.this);
//                String email = db.getEmail();
                String txt = handler.post("http://webview.bvibus.com/admin/resetpasslink.php?username=" + email);

                System.out.println("emai :- " + email);
                if (txt != null) {
                    JSONObject jsonObject = new JSONObject(txt);
                    message = jsonObject.getString("message");
                    System.out.println("message :- " + message);
                }
                Log.e("REALTIMELOCATION", txt);
            } catch (Exception e) {

            }
            return null;
        }


        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);
//            progressDialog.dismiss();
            if (message.equals("Email Sends")) {
                final Toast toast = Toast.makeText(getApplicationContext(), "Password reset token sent to your email address. Please check your email", Toast.LENGTH_LONG);
                toast.show();
                new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        toast.show();
                    }

                    public void onFinish() {
                        toast.cancel();
                    }
                }.start();
            } else {
                Toast.makeText(getApplicationContext(), "Email not send. Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getEmiailID(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

//    public String GET_EMAIL_ADDRESSES() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{android.Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        } else {
//            email = getEmiailID(getApplicationContext());
//            System.out.println("CORREO SELECCIONADO: " + email);
//        }
//        return email;
//    }
}
