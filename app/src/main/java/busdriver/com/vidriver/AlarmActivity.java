package busdriver.com.vidriver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmActivity inst;
    private TextView alarmTextView, textView9, tv_prev_endttime;
    Button btn_accept;
    ToggleButton alarmToggle;
    SharedPreferences pref;
    String alarmtime;

    public static AlarmActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        textView9 = (TextView) findViewById(R.id.textView9);
        tv_prev_endttime = (TextView) findViewById(R.id.tv_prev_endttime);
        btn_accept = (Button) findViewById(R.id.btn_accept);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        String first = "Schedule time to";
        String second = "work day";
        String next = "<font color='#EE0000'>end</font>";
        textView9.setText(Html.fromHtml(first + " " + next + " " + second));

        try {
            if (pref.getString("endalarmtime", "").isEmpty()) {
                tv_prev_endttime.setText("");
            } else {
                tv_prev_endttime.setText("Prev end alarm time :: " + pref.getString("endalarmtime", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        alarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {

                    Intent intent = new Intent(AlarmActivity.this, AlarmReceiver2.class);
                    PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this, 20, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(sender);

                    /*Dialog for alarm*/
                    Intent i = new Intent("in.wptrafficanalyzer.servicealarmdemo.demoactivity");
                    PendingIntent operation = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager2 = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());



                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                    alarmtime = sdf.format(calendar.getTime());

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("endalarmtime", alarmtime);
                    editor.commit();
                    Intent myIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);

                    pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 20, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    86400000
                    //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY , pendingIntent);
                    alarmManager2.setInexactRepeating(AlarmManager.RTC_WAKEUP  , calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY , operation);
//                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                            AlarmManager.INTERVAL_DAY, pendingIntent);

                } else {
                    alarmManager.cancel(pendingIntent);
                    Log.d("MyActivity", "Alarm Off");
                }
            }
        });
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlarmActivity.this, MapsActivity.class));
                finish();
            }
        });
    }
}