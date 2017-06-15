package busdriver.com.vidriver;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    private static final int ADMIN_INTENT = 15;
    Button ing, reg;
    TextView tv_reset;
    DatabaseHandlerUser databaseHandlerUser;
    DevicePolicyManager mDevicePolicyManager;
    ImageView adBanner;
    private Button shareButton;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlarmReceiver.stop_alarm(this);
        AlarmReceiver2.stop_alarm(this);

        setContentView(R.layout.activity_login);

        if (!InternetConnection.isInternetOn(LoginActivity.this)) {
            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(LoginActivity.this);
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
            final AdsLoader Ads = new AdsLoader(LoginActivity.this, 5);
            Ads.LoadAd();
            //--------------------------------------------
            pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
            ing = (Button) findViewById(R.id.btnIngresar);
            reg = (Button) findViewById(R.id.btnRegistro);
            tv_reset = (TextView) findViewById(R.id.tv_reset);
            final EditText Username = (EditText) findViewById(R.id.editText);
            final EditText Password = (EditText) findViewById(R.id.editText2);
            ing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!InternetConnection.isInternetOn(LoginActivity.this)) {
                        android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(LoginActivity.this);
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
                        alertbox.setCancelable(false);
                        alertbox.show();
                    } else {

                        String username = Username.getText().toString();
                        String password = Password.getText().toString();
                        if (username.length() > 0 && password.length() > 0) {
                            Log.i("DATOS", "" + username + " / " + password);
                            User user = new User(username, password);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("email_id", username);
                            editor.commit();
                            authenticate(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "fields empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity_.class));
                }
            });
            tv_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                }
            });
            databaseHandlerUser = new DatabaseHandlerUser(LoginActivity.this);

            if (databaseHandlerUser.EstaLogueado()) {

               // Toast.makeText(getApplicationContext(), "login Maps open", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                finish();
            }
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    adBanner = (ImageView) findViewById(R.id.adBanner);
                    Ads.LoadBanner(adBanner);
                }
            }, 8000);
            shareButton = (Button) findViewById(R.id.share_btn);
            shareButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    String shareBody = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                    String shareSub = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));


                }
            });
        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(LoginActivity.this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                    Log.i("ERROR", String.valueOf(returnedUser));
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Username and password do not match. Please try again");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        databaseHandlerUser.addUser(returnedUser);

//        startActivity(new Intent(LoginActivity.this, AlarmActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

       // Toast.makeText(getApplicationContext(), "login back", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

       // Toast.makeText(getApplicationContext(), "Login destroy", Toast.LENGTH_LONG).show();
        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

}
