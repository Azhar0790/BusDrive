package busdriver.com.vidriver;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    String password = "",email;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ChangePasswordActivity.this.getSupportActionBar().show();
        ChangePasswordActivity.this.getSupportActionBar().setHomeButtonEnabled(true);
        final EditText pass = (EditText) findViewById(R.id.pass);
        final EditText repass = (EditText) findViewById(R.id.repass);
        Button save = (Button) findViewById(R.id.btnsave);
        GET_EMAIL_ADDRESSES();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pass.getText().toString().equals("")) {
                    if (pass.getText().toString().equals(repass.getText().toString())) {
                        password = pass.getText().toString();
                        new ChangePasswordTask().execute();
                        Toast.makeText(ChangePasswordActivity.this, "¡Changed successfully!", Toast.LENGTH_SHORT).show();
                        ChangePasswordActivity.this.finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Password is empty!!!", Toast.LENGTH_SHORT).show();
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
            ChangePasswordActivity.this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class ChangePasswordTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                httpHandler handler = new httpHandler();
                DatabaseHandlerUser db = new DatabaseHandlerUser(ChangePasswordActivity.this);
//                String email = db.getEmail();
                String txt = handler.post("http://webview.bvibus.com/admin/ChangePass.php?em=" + email + "&pass="+password +"&type="+"d");
                System.out.println("emai :- "+email);
                Log.e("REALTIMELOCATION", txt);
            }catch (Exception e){

            }
            return true;
        }



        @Override
        protected void onPostExecute(Boolean result) {

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

    public String GET_EMAIL_ADDRESSES() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            email = getEmiailID(getApplicationContext());
            System.out.println("CORREO SELECCIONADO: " + email);
        }
        return email;
    }
}
