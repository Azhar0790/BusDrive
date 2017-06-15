package busdriver.com.vidriver;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import busdriver.com.vidriver.service.GPSTracker;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MapsActivity";
    private static final int MY_PERMISSION_LOCATION = 0;
    private GoogleMap mMap;
    Button wk;
    protected GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    boolean GpsStatus;
    Marker marker;
    int btn = 0;
    double dLat, dLong;
    String resp;
    SharedPreferences pref;
    String category, id, email_id, is_free;
    GPSTracker gps;
    //Ringtone ringtone;
    Timer gpstimer;
    Timer locationtimer;
    Toast toast = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            dLat = mLastLocation.getLatitude();
            dLong = mLastLocation.getLongitude();
            //Does this log?
            Log.d(getClass().getSimpleName(), String.valueOf(dLat) + ", " + String.valueOf(dLong));
            mMap.addMarker(new MarkerOptions().position(
                    new LatLng(dLat, dLong)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
        } else {
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (!InternetConnection.isInternetOn(MapsActivity.this)) {
            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(MapsActivity.this);
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


            pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
            mapFragment.getMapAsync(this);
            getSupportActionBar().show();
            getSupportActionBar().setTitle("VI Driver Map");
            getSupportActionBar().setHomeButtonEnabled(true);


            new AsyncMethod().execute();
            is_free = pref.getString("is_free", "");
            wk = (Button) findViewById(R.id.btnWork);

            gps = new GPSTracker(MapsActivity.this);
            CheckGpsStatus();

            if (GpsStatus == true) {
                service_start();
            } else {
                if (wk != null) {
                    String d = wk.getText().toString();
                    if (d.equalsIgnoreCase("stop work") /*&& btn == 1*/) {
                        btn = 1;
                        {
                            service_end();
                            service_start();
                            Toast.makeText(getApplicationContext(), "Please enable GPS or select 'Stop Work' in Vi Driver", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        service_end();
                    }
                } else {
                    service_end();
                /*    if (ringtone != null)
                        ringtone.stop();*/
                }

            }
            if (locationtimer != null) {
                locationtimer.cancel();
            }
            locationtimer = new Timer();
            locationtimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshlocation();
                            new marker_maps_asynctask().execute();

                        }//public void run() {
                    });
                }
            }, 7000, 7000);
            if (gpstimer != null) {
                gpstimer.cancel();
            }
            gpstimer = new Timer();

            gpstimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            CheckGpsStatus();
                            if (GpsStatus == true) {
                                service_end();
                             /*   if (ringtone != null)
                                    ringtone.stop();*/
                            } else {

                                if (wk != null) {
                                    String d = wk.getText().toString();
                                    if (d.equalsIgnoreCase("stop work") /*&& btn == 1*/) {
                                        btn = 1;
                                        // if (!ringtone.isPlaying())
                                        {
                                            service_end();
                                            service_start();
                                         /*   ringtone.stop();
                                            ringtone.play();*/
                                            Toast.makeText(getApplicationContext(), "Please enable GPS or select 'Stop Work' in Vi Driver", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        service_end();
                                       /* if (ringtone != null)
                                            ringtone.stop();*/
                                    }
                                } else {
                                    service_end();
                               /*     if (ringtone != null)
                                        ringtone.stop();*/
                                }


                               /* if (btn == 1) {
                                    if (!ringtone.isPlaying()) {
                                        ringtone.play();
                                    }
                                    Toast.makeText(getApplicationContext(), "Please enabled your GPS or select 'Stop Working' in Vi Driver", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (ringtone!=null)
                                    ringtone.stop();
                                }*/
                            }

                        }//public void run() {
                    });

                }
            }, 3300, 3300);
//            gpstimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//
//                    MapsActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            CheckGpsStatus();
////                            if(gps.canGetLocation()){
////
////                                ringtone.stop();
////
////                                // \n is for new line
////                            }else {
////                                gps.showSettingsAlert();
////                                ringtone.play();
//////                                showSettingsAlert(1);
////                            }
//                            if(GpsStatus == true)
//                            {
//                                ringtone.stop();
////                                showSettingsAlert(0);
//                            }else {
//                                gps.showSettingsAlert();
//                                ringtone.play();
////                                showSettingsAlert(1);
//                            }
//
//                        }//public void run() {
//                    });
//
//                }
//            }, 4600,4600);
            wk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("FIRST", " -1");
                    if (btn == 0) {

                        Log.d("FIRST", " 0");
                        wk.setBackgroundColor(Color.parseColor("#FF0000"));
                        wk.setText("stop work");
                        btn = 1;
//                    new UpdateLatLngAsynctask().execute();
                        new startWorkOrStop(MapsActivity.this, 1).SendRequestServiAppMethod();
                        marshmallowGPSPremissionCheck();

                    } else {

                        Log.d("FIRST", " 1");
                        wk.setBackgroundColor(Color.parseColor("#ecb30b"));
                        wk.setText("start work");
                        btn = 0;
                        new startWorkOrStop(MapsActivity.this, 0).SendRequestServiAppMethod();
                        stopService(new Intent(MapsActivity.this, AllRequestSErvice.class));
                    }
                }
            });
            category = pref.getString("category", "");
            id = pref.getString("id", "");


            Timer te = new Timer();
            te.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Your code to run in GUI thread here
                            LatLng sydney = new LatLng(18.4229441, -64.6836995);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));

                        }//public void run() {
                    });

                }
            }, 3200);
        }
        System.out.println("is_free :- " + is_free);
        if (is_free.equals("p")) {
            new ActivationPeriod().execute();
        } else {
            System.out.println("is_free :- " + is_free);
        }

        new Email_Deleted_Asynctask().execute();
    }

    public void service_start() {
        Intent startIntent = new Intent(this, GPSRingtonePlayingService.class);
        startService(startIntent);
    }

    public void service_end() {
        Intent stopIntent = new Intent(MapsActivity.this, GPSRingtonePlayingService.class);
        stopService(stopIntent);
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION);
        } else {
            //   gps functions..
            buildGoogleApiClient();
            startService(new Intent(this, AllRequestSErvice.class));
        }

    }


    public void refreshlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            dLat = mLastLocation.getLatitude();
            dLong = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSION_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
            buildGoogleApiClient();
            startService(new Intent(this, AllRequestSErvice.class));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            dLat = mLastLocation.getLatitude();
            dLong = mLastLocation.getLongitude();
            //Does this log?
            Log.d(getClass().getSimpleName(), String.valueOf(dLat) + ", " + String.valueOf(dLong));
            mMap.addMarker(new MarkerOptions().position(
                    new LatLng(dLat, dLong)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLat, dLong), 8));
        } else {
            // Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        dLat = location.getLatitude();
        dLong = location.getLongitude();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close:

               // Toast.makeText(getApplicationContext(), "close", Toast.LENGTH_LONG).show();
                new DatabaseHandlerUser(this).resetTables();
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
                AlarmReceiver.stop_alarm(this);
                AlarmReceiver2.stop_alarm(this);
                break;
            case R.id.change:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.alarm:
                startActivity(new Intent(this, AlarmActivity2.class));
                break;
            case R.id.scheduleday:
                startActivity(new Intent(this, ScheduleActivity.class));
                break;
            case R.id.message:
                startActivity(new Intent(this, MessageActivity.class));
                break;
            case R.id.trialperiod:
                new TrialPeriod().execute();
                break;
            case R.id.driver_feedback:
                startActivity(new Intent(this, CustomerActivity.class));
                break;
            case R.id.admin_message:
                startActivity(new Intent(this, AdminMessageActivity.class));
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                String shareSub = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
        }
        return true;
    }

    private class AsyncMethod extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
            String email = new DatabaseHandlerUser(MapsActivity.this).getEmail();
            String request = "http://webview.bvibus.com/admin/getWork.php?em=" + email;
            resp = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        new AsyncMethod().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },5000);

            System.out.println("resp :- "+resp);
            if (resp.equals("1")) {
                wk.setBackgroundColor(Color.parseColor("#FF0000"));
                wk.setText("stop work");
//                btn = 1;
//                new startWorkOrStop(MapsActivity.this, 1).SendRequestServiAppMethod();
            } else {
                wk.setBackgroundColor(Color.parseColor("#ecb30b"));
                wk.setText("start work");
//                btn = 0;
//                new startWorkOrStop(MapsActivity.this, 0).SendRequestServiAppMethod();
            }
        }

    }

    class marker_maps_asynctask extends AsyncTask<String, String, String> {
        String url = "http://webview.bvibus.com/admin/getCategorybyuser.php?id=" + id;
        String catname, icon;


        @Override
        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsnStr = sh.makeServiceCall(url, ServiceHandler.GET);
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    catname = jsonObject.getString("catname");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMap.clear();
            try {
                if (catname.equals("Bus")) {
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(dLat, dLong)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                } else if (catname.equals("Blue Team")) {
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(dLat, dLong)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_team)));
                } else if ((catname.equals("Green Team"))) {
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(dLat, dLong)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.green_team)));
                } else if ((catname.equals("Taxi"))) {
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(dLat, dLong)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)));
                } else if ((catname.equals(""))) {
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(dLat, dLong)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error :- " + e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Toast.makeText(getApplicationContext(), "Resume", Toast.LENGTH_LONG).show();

        AlarmReceiver.stop_alarm(this);
        AlarmReceiver2.stop_alarm(this);
        if (wk != null) {
            String d = wk.getText().toString();
            if (d.equalsIgnoreCase("stop work") /*&& btn == 1*/) {
                btn = 1;
                // if (!ringtone.isPlaying())
                {

                    service_end();
                    service_start();
                 /*   ringtone.stop();
                    ringtone.play();*/
                    Toast.makeText(getApplicationContext(), "Please enable GPS or select 'Stop Work' in Vi Driver", Toast.LENGTH_SHORT).show();
                }
            } else {
                service_end();
          /*      if (ringtone != null)
                    ringtone.stop();*/
            }
        } else {
            service_end();
        /*    if (ringtone != null)
                ringtone.stop();*/
        }

        if (locationtimer != null) {
            locationtimer.cancel();
        }
        locationtimer = new Timer();
//        locationtimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshlocation();
//                        new marker_maps_asynctask().execute();
//
//                    }//public void run() {
//                });
//            }
//        }, 7000, 7000);


        if (gpstimer != null) {
            gpstimer.cancel();
            gpstimer = new Timer();
            gpstimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            CheckGpsStatus();
                            if (GpsStatus == true) {
                                service_end();
                                /* if (ringtone != null)
                                    ringtone.stop();*/
                            } else {

                                if (wk != null) {
                                    String d = wk.getText().toString();
                                    if (d.equalsIgnoreCase("stop work") /*&& btn == 1*/) {
                                        btn = 1;
                                        // if (!ringtone.isPlaying())
                                        {

                                            service_end();
                                            service_start();
                                         /*   ringtone.stop();
                                            ringtone.play();*/
                                            Toast.makeText(getApplicationContext(), "Please enable GPS or select 'Stop Work' in Vi Driver", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        service_end();/*   if (ringtone != null)
                                            ringtone.stop();*/
                                    }
                                } else {
                                    service_end();/*if (ringtone != null)
                                        ringtone.stop();*/
                                }

                               /* if (btn == 1) {
                                    if (!ringtone.isPlaying()) {
                                        ringtone.play();
                                    }
                                    Toast.makeText(getApplicationContext(), "Please enabled your GPS or select 'Stop Working' in Vi Driver", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (ringtone!=null)
                                    ringtone.stop();
                                }*/
                            }

                        }//public void run() {
                    });
//5 2 00
                }
            }, 3300, 3300);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

       // Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_LONG).show();
       /* if (ringtone!=null)
        ringtone.stop();
        if (gpstimer!=null){
            gpstimer.cancel();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       // Toast.makeText(getApplicationContext(), "destroy", Toast.LENGTH_LONG).show();
        service_end();
        /*     if (ringtone != null)
            ringtone.stop();*/
        if (gpstimer != null) {
            gpstimer.cancel();
        }
        if (locationtimer != null) {
            locationtimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

       // Toast.makeText(getApplicationContext(), "backpress", Toast.LENGTH_LONG).show();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       /* ringtone.stop();*/
        AlarmReceiver.stop_alarm(this);
        AlarmReceiver2.stop_alarm(this);
        startActivity(a);
    }


    class ActivationPeriod extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            String url = "http://webview.bvibus.com/admin/trialperiod.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("mailid", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
                    System.out.println("message :- " + message);
                    if (message.equals("Expired")) {
//                        new DatabaseHandlerUser(MapsActivity.this).resetTables();
//                        finish();
                        startActivity(new Intent(MapsActivity.this, Package_activity.class));
//                        startActivity(new Intent(MapsActivity.this, PaymentStripe.class));
                    } else if (message.equals("Trial Period :2 Days remaining")) {
                        Toast.makeText(getApplicationContext(), "2 days are remaining for expiration. Please activate your account", Toast.LENGTH_LONG).show();
                    } else if (message.equals("Trial Period :1 Days remaining")) {
                        Toast.makeText(getApplicationContext(), "1 day is remaining for expiration. Please activate your account", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class TrialPeriod extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            System.out.println("email_id :- " + email_id);
            String url = "http://webview.bvibus.com/admin/trialperiod.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("mailid", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            android.app.AlertDialog.Builder alertbox;
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
//                    alertbox = new android.app.AlertDialog.Builder(MapsActivity.this);
//                    alertbox.setTitle("Trial Remaining");
//                    alertbox.setMessage(message);
//                    alertbox.setPositiveButton(
//                            getResources().getString(R.string.ok),
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface arg0, int arg1) {
//                                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
//                                }
//                            });
//                    alertbox.show();
                    trial_dialog(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void trial_dialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_trial);
        Button btn_cancel_sub = (Button) dialog.findViewById(R.id.btn_cancel_sub);
        ImageView iv_cancel = (ImageView) dialog.findViewById(R.id.iv_cancel);
        TextView tv_trial_period = (TextView) dialog.findViewById(R.id.tv_trial_period);
        tv_trial_period.setText(message);

        if (message.equals("Expired")) {
            btn_cancel_sub.setVisibility(View.VISIBLE);
        }
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_cancel_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CancelSubscriptionAsynctask().execute();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class CancelSubscriptionAsynctask extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            System.out.println("email_id :- " + email_id);
            String url = "http://webview.bvibus.com/stripe/cancelSubscription.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("user_mail", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), "Subscription cancel successfully", Toast.LENGTH_SHORT).show();
                    new DatabaseHandlerUser(MapsActivity.this).resetTables();
                    finish();
                    startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Email_Deleted_Asynctask extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            System.out.println("email_id :- " + email_id);
            String url = "http://webview.bvibus.com/admin/isExist.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("em", email_id));
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
                    if (message.equals("exists")) {

                    } else {
                        new DatabaseHandlerUser(MapsActivity.this).resetTables();
                        finish();
                        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            new Email_Deleted_Asynctask().execute();
        }
    }

    public void CheckGpsStatus() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showSettingsAlert(int i) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // Setting Icon to Dialog
//        //alertDialog.setIcon(R.drawable.delete);
//
//        // On pressing Settings button
//
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        });
//
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });

        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.setContentView(R.layout.dialog_gps);
        Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
        ImageView iv_cancel = (ImageView) dialog.findViewById(R.id.iv_cancel);

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // Showing Alert Message
        if (i == 0) {
            dialog.dismiss();
        } else {
            dialog.show();
        }

    }
}
