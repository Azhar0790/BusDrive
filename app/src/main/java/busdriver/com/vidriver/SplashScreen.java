package busdriver.com.vidriver;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.gms.location.LocationListener;


public class SplashScreen extends AppCompatActivity implements LocationListener {
    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 6000;
    //DatabaseHandlerUser databaseHandlerUser = new DatabaseHandlerUser(SplashScreen.this);
  //  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "Pantalla de carga";
    AlertDialog.Builder builderd;
    AlertDialog alertd;
    Boolean DialogShown = false;
    Boolean ss = false;
    public final String[] PERMISSION_ALL = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS
    };
    public final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ACRA.init(SplashScreen.this.getApplication());

        // Set portrait orientation
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
       // requestWindowFeature(Window.FEATURE_NO_TITLE);

       // LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.splash);
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(SplashScreen.this, PERMISSION_ALL, PERMISSION_REQUEST_CODE);
        }
       // ImageView image = (ImageView) findViewById(R.id.imageView6);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (!InternetConnection.isInternetOn(SplashScreen.this)) {
                    android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(SplashScreen.this);
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
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }

        }, SPLASH_SCREEN_DELAY);




    }
        // Simulate a long loading process on application startup.




    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showDialogGps();
                }else{
                    hideDialogGps();
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiverInternet = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            final android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable() || mobile.isAvailable()) {
                // Do something
                Log.d("Network Available ", "Flag No 1");
            }else{
                showDialogInternet();
            }
        }
    };



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(
                "android.location.PROVIDERS_CHANGED"));
        registerReceiver(broadcastReceiverInternet, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));
        registerReceiver(broadcastReceiverInternet, new IntentFilter(
                "android.net.wifi.WIFI_STATE_CHANGED"));
        if(ss){
          /*  Intent mainIntent = new Intent(SplashScreen.this, SplashScreen.class);
            startActivity(mainIntent);*/
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        ss = true;
        unregisterReceiver(broadcastReceiver); // Register receiver
        unregisterReceiver(broadcastReceiverInternet);
    }

    public void showDialogGps() {
        // Do what you need to do
        DialogShown = false;
        builderd = new AlertDialog.Builder(SplashScreen.this);
        builderd.setMessage("Es necesario tener activo el GPS, por favor activa el GPS")
                .setCancelable(false)
                .setTitle("GPS inactivo")
                .setNegativeButton("Activar GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        turnGpsOn(SplashScreen.this);
                    }
                })
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });
        alertd = builderd.create();
        if(DialogShown)
            alertd.show();

    }

    public void hideDialogGps() {
        try{
            while(DialogShown){
                alertd.hide();
                alertd.dismiss();
            }

        }catch(Exception e){

        }
    }



    public void showDialogInternet() {
        // Do what you need to do
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        builder.setMessage("Es necesario tener acceso a internet, por favor activa tu red movil o conectate a una red Wi-Fi")
                .setCancelable(false)
                .setTitle("Sin conexión a internet")
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void turnGpsOn (Context context) {
        String beforeEnable = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String newSet = String.format ("%s,%s",
                beforeEnable,
                LocationManager.GPS_PROVIDER);
        try {
            Settings.Secure.putString (context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
                    newSet);
        } catch(Exception e) {}
    }

}
