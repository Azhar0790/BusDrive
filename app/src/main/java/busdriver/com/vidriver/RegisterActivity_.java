package busdriver.com.vidriver;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import busdriver.com.vidriver.service.MultipartEntity;

/**
 * Created by Sarps on 2/2/2017.
 */
public class RegisterActivity_ extends AppCompatActivity {
    EditText name, pass, ws;
    ImageView iv_img;
    Spinner spinner, spinnerh, best, spinnerCat;
    TextView tv_date;
    RadioGroup rg_packages;
    RadioButton rb_pack;
    String resp, ba1, resp3, email, schedule = "", resp2, respcat;
    List<String> cities = new ArrayList<String>();
    List<String> bestlist = new ArrayList<String>();
    CheckBox Sun, Mon, Tue, Wed, Thu, Fri, Sat;
    List<String> hear = new ArrayList<String>();
    List<String> cat = new ArrayList<String>();
    String picturePath;
    Bitmap photo;
    SharedPreferences pref;
    static final int DATE_DIALOG_ID = 0;
    private int mYear, mMonth, mDay;
    String outputDateStr, outputDateStr2;
    public static String URL = "http://webview.bvibus.com/admin/uploadLicense.php";
    SimpleDateFormat sdf;
    private GoogleApiClient client;
    private static final String TAG = RegisterActivity_.class.getSimpleName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int REQUEST_CAMERA = 1;
    ProgressDialog pdialog;
    List country_code;
    Spinner sp_c_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);

        rg_packages = (RadioGroup) findViewById(R.id.rg_packages);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerh = (Spinner) findViewById(R.id.spinnerh);
        spinnerCat = (Spinner) findViewById(R.id.spinnerCategory);
        tv_date = (TextView) findViewById(R.id.tv_date);
        best = (Spinner) findViewById(R.id.best);
        name = (EditText) findViewById(R.id.name);
        pass = (EditText) findViewById(R.id.pass);
        ws = (EditText) findViewById(R.id.ws);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        Sun = (CheckBox) findViewById(R.id.sun);
        Mon = (CheckBox) findViewById(R.id.mon);
        Tue = (CheckBox) findViewById(R.id.tue);
        Wed = (CheckBox) findViewById(R.id.wed);
        Thu = (CheckBox) findViewById(R.id.thu);
        Fri = (CheckBox) findViewById(R.id.fri);
        Sat = (CheckBox) findViewById(R.id.sat);
        country_code = new ArrayList<Integer>();
        for (int i = 1; i <= 1000; i++) {
            country_code.add(Integer.toString(i));
        }
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, country_code);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_c_code = (Spinner) findViewById(R.id.sp_c_code);
        sp_c_code.setAdapter(spinnerArrayAdapter);
        new AsyncMethod().execute();
        new AsyncMethodHear().execute();
        new AsyncMethodCat().execute();
        new AsyncMethodBest().execute();
        GET_EMAIL_ADDRESSES();

        cat.add("Loading...");
        bestlist.add("Loading...");
        hear.add("Loading...");
        cities.add("Loading...");
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                (RegisterActivity_.this, android.R.layout.simple_spinner_item, cities);

        cityAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(cityAdapter);
        ArrayAdapter<String> hearAdapter = new ArrayAdapter<String>
                (RegisterActivity_.this, android.R.layout.simple_spinner_item, hear);

        hearAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerh.setAdapter(hearAdapter);

        ArrayAdapter<String> bestAdapter = new ArrayAdapter<String>
                (RegisterActivity_.this, android.R.layout.simple_spinner_item, bestlist);

        bestAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        best.setAdapter(bestAdapter);

        ArrayAdapter<String> CatAdapter = new ArrayAdapter<String>
                (RegisterActivity_.this, android.R.layout.simple_spinner_item, cat);

        CatAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerCat.setAdapter(CatAdapter);
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        //String dateFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat("MM/dd/yyyy");
        String d = sdf.format(c.getTime());
        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null, date2 = null;
        try {
            date = sdf.parse(d);
            date2 = sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        outputDateStr = outputFormat.format(date);
        outputDateStr2 = sdf.format(date2);
        System.out.println("outputDateStr :- " + outputDateStr);
        System.out.println("outputDateStr2 :-" + outputDateStr2);
        tv_date.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(DATE_DIALOG_ID);

            }
        });
        RegisterActivity_.this.getSupportActionBar().show();
        RegisterActivity_.this.getSupportActionBar().setTitle("Vi Driver");
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rg_packages.getCheckedRadioButtonId();
                if (name.getText().toString().length() == 0) {
                    //Validation for Invalid Email Address
                    Toast.makeText(getApplicationContext(), "Name cannot be Blank", Toast.LENGTH_LONG).show();
                    name.setError("Name cannot be Blank");
                    return;
                } else if (pass.getText().toString().length() < 6) {
                    //Validation for Website Address
                    Toast.makeText(getApplicationContext(), "Password cannot be less than 6 digits", Toast.LENGTH_LONG).show();
                    pass.setError("Password cannot be less than 6 digits");
                    return;
                } else if (ws.getText().toString().length() < 6 || ws.getText().toString().length() > 12) {
                    Toast.makeText(getApplicationContext(), "Add valid phone number with more than 6 digit", Toast.LENGTH_LONG).show();
                    ws.setError("Add your phone number");
                    return;
                } else if (!Sun.isChecked() & !Mon.isChecked() & !Tue.isChecked() & !Wed.isChecked() & !Thu.isChecked() & !Fri.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please check atleast anyone checkbox", Toast.LENGTH_LONG).show();
                    return;
                } else if (rg_packages.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please choose one of the package", Toast.LENGTH_SHORT).show();
                } else if (tv_date.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Date cannot be Blank", Toast.LENGTH_LONG).show();
                    tv_date.setError("Date cannot be Blank");
                    return;
                } else {
                    rb_pack = (RadioButton) findViewById(selectedId);
                    Toast.makeText(getApplicationContext(), "You choosed " + rb_pack.getText(), Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dg = new AlertDialog.Builder(RegisterActivity_.this);
                    dg.setMessage("Your username is: " + email)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder dg = new AlertDialog.Builder(RegisterActivity_.this);
                                    dg.setMessage("Please take a photo of your Taxi Driver license. Please make sure that expiration date is visible")
                                            .setNeutralButton("CAMERA", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    takePhoto();
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();


                                }
                            })
                            .setCancelable(false)
                            .show();
                    //Toast.makeText(getApplicationContext(), "Validated Succesfully", Toast.LENGTH_LONG).show();
                }
//                takePhoto();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        }

        return null;

    }


    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String d_license = "" + new StringBuilder().append(mDay).append("-").append(mMonth + 1).append("-").append(mYear);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = sdf.parse(d_license);
                date2 = sdf.parse(outputDateStr);
                if (date1.after(date2)) {
                    tv_date.setText(new StringBuilder().append(mDay).append("-").append(mMonth + 1).append("-").append(mYear));
                    System.out.println("date :- " + new StringBuilder().append(mDay).append("-").append(mMonth + 1).append("-").append(mYear));
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter the correct date", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    };

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://busdriver.com.vidriver/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://busdriver.com.vidriver/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private void takePhoto() {
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onActivityResult: " + this);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {


            AlertDialog.Builder dg = new AlertDialog.Builder(RegisterActivity_.this);
            dg.setMessage("Your username is: " + email)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder dg = new AlertDialog.Builder(RegisterActivity_.this);
                            dg.setMessage("You will receive a Text or an E-Mail once your application is approved.")
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                          /*  Calendar calendar = Calendar.getInstance();
                                            int day = calendar.get(Calendar.DAY_OF_WEEK);

                                            switch (day) {
                                                case Calendar.SUNDAY:
                                                    // Current day is Sunday

                                                case Calendar.MONDAY:
                                                    // Current day is Monday

                                                case Calendar.TUESDAY:
                                                    // etc.
                                            }*/
                                            SharedPreferences.Editor editor2 = pref.edit();
                                            editor2.putBoolean("day" + Calendar.SUNDAY, Sun.isChecked());
                                            editor2.putBoolean("day" + Calendar.MONDAY, Mon.isChecked());
                                            editor2.putBoolean("day" + Calendar.TUESDAY, Tue.isChecked());
                                            editor2.putBoolean("day" + Calendar.WEDNESDAY, Wed.isChecked());
                                            editor2.putBoolean("day" + Calendar.THURSDAY, Thu.isChecked());
                                            editor2.putBoolean("day" + Calendar.FRIDAY, Fri.isChecked());
                                            editor2.putBoolean("day" + Calendar.SATURDAY, Sat.isChecked());
                                            editor2.commit();

                                            if (Sun.isChecked()) {
                                                if (!schedule.equals("")) {
                                                    schedule = schedule + ",";
                                                }
                                                schedule = schedule + "Sunday";
                                            }
                                            if (Mon.isChecked()) {
                                                if (!schedule.equals("")) {
                                                    schedule = schedule + ",";
                                                }
                                                schedule = schedule + "Monday";
                                            }
                                            if (Tue.isChecked()) {
                                                if (!schedule.equals("")) {
                                                    schedule = schedule + ",";
                                                }
                                                schedule = schedule + "Tuesday";
                                            }
                                            if (Wed.isChecked()) {
                                                if (!schedule.equals("")) {
                                                    schedule = schedule + ",";
                                                }
                                                schedule = schedule + "Wednesday";
                                            }
                                            if (Thu.isChecked()) {
                                                if (!schedule.equals("")) {
                                                    schedule = schedule + ",";
                                                }
                                                schedule = schedule + "Thursday";
                                            }
                                            if (Fri.isChecked()) {
                                                if (!schedule.equals("")) {
                                                    schedule = schedule + ",";
                                                }
                                                schedule = schedule + "Friday";
                                            }
                                            if (Sat.isChecked()) {
                                                if (!schedule.equals("")) {
                                                    schedule = schedule + ",";
                                                }
                                                schedule = schedule + "Saturday";
                                            }

                                            if (email != null && pass.getText().toString() != null && name.getText().toString() != null && ws.getText().toString() != null && tv_date.getText().toString() != null) {

                                                if (rb_pack.getText().equals("$49 per Month, Buses and Taxis")) {
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putString("price", "$49");
                                                    editor.commit();

                                                    pdialog = new ProgressDialog(RegisterActivity_.this);
                                                    pdialog.setMessage("Uploading the data");
                                                    pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                    pdialog.setCanceledOnTouchOutside(false);
                                                    pdialog.show();
                                                    String c_phone = sp_c_code.getSelectedItem().toString() + ws.getText().toString();
                                                    setPic(email, pass.getText().toString(), c_phone, name.getText().toString(), spinner.getSelectedItem().toString(), schedule, spinnerh.getSelectedItem().toString(), best.getSelectedItem().toString(), spinnerCat.getSelectedItem().toString(), tv_date.getText().toString(), outputDateStr2, "$49");
                                                    System.out.println("schedule :- " + schedule);
                                                    editor.putString("category", spinnerCat.getSelectedItem().toString());
                                                    editor.commit();
                                                } else {
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putString("price", "$39");
                                                    editor.commit();
                                                    pdialog = new ProgressDialog(RegisterActivity_.this);
                                                    pdialog.setMessage("Uploading the data");
                                                    pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                    pdialog.setCanceledOnTouchOutside(false);
                                                    pdialog.show();
                                                    String c_phone = sp_c_code.getSelectedItem().toString() + ws.getText().toString();
                                                    setPic(email, pass.getText().toString(), c_phone, name.getText().toString(), spinner.getSelectedItem().toString(), schedule, spinnerh.getSelectedItem().toString(), best.getSelectedItem().toString(), spinnerCat.getSelectedItem().toString(), tv_date.getText().toString(), outputDateStr2, "$39");

                                                    editor.putString("category", spinnerCat.getSelectedItem().toString());
                                                    editor.commit();

                                                }


                                                System.out.println("Camera clicked :- ");
                                                System.out.println("d1 :- " + tv_date.getText().toString());
                                                System.out.println("d2 :- " + outputDateStr2);

                                            } else {
                                                Toast.makeText(RegisterActivity_.this, "Register: [FAIL] Complete all fields...", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    })
                                    .setCancelable(false)
                                    .show();


                        }
                    })
                    .setCancelable(false)
                    .show();


        } else {
            Toast.makeText(RegisterActivity_.this, "Error taking the photo... Please retry.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPhoto(Bitmap bitmap, String email, String pass, String ws, String name, String sp, String schedule, String sph, String bst, String spc, String date, String date2, String plan) throws Exception {

        new PostDataAsynctask(email, pass, ws, name, sp, schedule, sph, bst, spc, date, date2, plan, bitmap).execute();

    }


    class PostDataAsynctask extends AsyncTask<String, String, String> {
        String responseString = null;
        String id = null, error = null;

        String email, pass, ws, name, sp, schedule, sph, bst, spc, date, date2, plan;
        Bitmap bm;

        public PostDataAsynctask(String email, String pass, String ws, String name, String sp, String schedule, String sph, String bst, String spc, String date, String date2, String plan, Bitmap bm) {
            this.email = email;
            this.pass = pass;
            this.ws = ws;
            this.name = name;
            this.sp = sp;
            this.sph = sph;
            this.bst = bst;
            this.spc = spc;
            this.date = date;
            this.date2 = date2;
            this.plan = plan;
            this.bm = bm;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String url = "http://webview.bvibus.com/admin/insertnewdatadriver.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();


            param.add(new BasicNameValuePair("em", email));
            param.add(new BasicNameValuePair("pass", pass));
            param.add(new BasicNameValuePair("name", name));
            param.add(new BasicNameValuePair("ho", schedule));
            param.add(new BasicNameValuePair("ro", sp));
            param.add(new BasicNameValuePair("ws", ws));
            param.add(new BasicNameValuePair("be", bst));
            param.add(new BasicNameValuePair("hear", sph));
            param.add(new BasicNameValuePair("cat", spc));
            param.add(new BasicNameValuePair("license", date));
            param.add(new BasicNameValuePair("current_date", date2));
            param.add(new BasicNameValuePair("plan", plan));


            System.out.println("email :- " + email);
            System.out.println("pass :- " + pass);
            System.out.println("name :-" + name);
            System.out.println("schedule :-" + schedule);
            System.out.println("sp :-" + sp);
            System.out.println("ws :-" + ws);
            System.out.println("bst :-" + bst);
            System.out.println("sph :-" + sph);
            System.out.println("spc :-" + spc);
            System.out.println("date :-" + date);
            System.out.println("date2 :-" + date2);

            responseString = serviceHandler.makeServiceCall(url, ServiceHandler.POST, param);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("responseString 2 :-" + responseString);
            if (responseString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    error = jsonObject.getString("error");
                    id = jsonObject.getString("id");
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    new UploadTask(id, bm).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            pdialog.dismiss();
        }
    }


    private class UploadTask extends AsyncTask<String, String, Bitmap> {

        String responseString = null;
        String id;
        Bitmap bm;

        public UploadTask(String id, Bitmap bm) {

            this.id = id;
            this.bm = bm;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream
            System.out.println("in :- " + in);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpPost httppost = new HttpPost("http://webview.bvibus.com/admin/uploadlicenseimg.php"); // server

                MultipartEntity entity = new MultipartEntity();
//				entity.addPart("myFile",
//						System.currentTimeMillis() + ".jpg", in);


                entity.addPart("id", id);
                entity.addPart("in", System.currentTimeMillis() + ".jpg", in);
                httppost.setEntity(entity);

                Log.i(TAG, "request " + httppost.getRequestLine());
                HttpResponse response = null;

                try {
                    response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();
                    responseString = EntityUtils.toString(r_entity);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    System.out.println("responseString  :-" + responseString);


                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (response != null)
                        Log.i(TAG, "response " + response.getStatusLine().toString());
                } finally {

                }
            } finally {

            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pdialog.dismiss();
            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume: " + this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    String mCurrentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, "photo path = " + mCurrentPhotoPath);
        return image;
    }

    private void setPic(String email, String pass, String ws, String name, String sp, String schedule, String sph, String bst, String spc, String date, String date2, String plan) {

//        (RegisterActivity.this, email, pass.getText().toString(), ws.getText().toString(), name.getText().toString(), spinner.getSelectedItem().toString(), schedule, spinnerh.getSelectedItem().toString(), filStr, best.getSelectedItem().toString(), spinnerCat.getSelectedItem().toString(), tv_date.getText().toString(), outputDateStr2)


        int targetW = iv_img.getWidth();
        int targetH = iv_img.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);


        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);


        Matrix mtx = new Matrix();
        mtx.postRotate(0);
        // Rotating Bitmap
        Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        if (rotatedBMP != bitmap)
            bitmap.recycle();

//	    mImageView.setImageBitmap(rotatedBMP);

        try {
            sendPhoto(rotatedBMP, email, pass, ws, name, sp, schedule, sph, bst, spc, date, date2, plan);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            email = getEmiailID(getApplicationContext());
            System.out.println("CORREO SELECCIONADO: " + email);
        }
        return email;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GET_EMAIL_ADDRESSES();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private class AsyncMethod extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
            String request = "http://webview.bvibus.com/admin/getRoute.php";
            resp = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            cities.clear();
            System.out.println(resp);
            String delimiter = ",";
            String strArray[] = resp.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    cities.add(strArray[i]);
                }
            }
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                    (RegisterActivity_.this, android.R.layout.simple_spinner_item, cities);

            cityAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(cityAdapter);
        }

    }

    private class AsyncMethodCat extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
            String request = "http://webview.bvibus.com/admin/getCategories.php";
            respcat = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            cat.clear();
            System.out.println(respcat);
            String delimiter = ",";
            String strArray[] = respcat.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    cat.remove(strArray[1]);
                    cat.add(strArray[i]);
                }
            }

            ArrayAdapter<String> catAdapter = new ArrayAdapter<String>
                    (RegisterActivity_.this, android.R.layout.simple_spinner_item, cat);

            catAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnerCat.setAdapter(catAdapter);
        }

    }

    private class AsyncMethodHear extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
            String request = "http://webview.bvibus.com/admin/getHear.php";
            resp2 = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hear.clear();
            System.out.println(resp2);
            String delimiter = ",";
            String strArray[] = resp2.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    hear.add(strArray[i]);
                }
            }
            ArrayAdapter<String> hearAdapter = new ArrayAdapter<String>
                    (RegisterActivity_.this, android.R.layout.simple_spinner_item, hear);

            hearAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnerh.setAdapter(hearAdapter);
        }
    }

    private class AsyncMethodBest extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
            String request = "http://webview.bvibus.com/admin/getBest.php";
            resp3 = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            bestlist.clear();
            System.out.println(resp3);
            String delimiter = ",";
            String strArray[] = resp3.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    bestlist.add(strArray[i]);
                }
            }
            ArrayAdapter<String> bestAdapter = new ArrayAdapter<String>
                    (RegisterActivity_.this, android.R.layout.simple_spinner_item, bestlist);

            bestAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            best.setAdapter(bestAdapter);
        }
    }
}