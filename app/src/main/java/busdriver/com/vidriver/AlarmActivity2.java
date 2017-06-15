package busdriver.com.vidriver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmActivity2 extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmActivity2 inst;
    private TextView alarmTextView, textView9, tv_prev_starttime;
    Button btn_next;
    ToggleButton alarmToggle;
    SharedPreferences pref;
    String alarmtime;

    public static AlarmActivity2 instance() {
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
        setContentView(R.layout.activity_alarm2);
        pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        textView9 = (TextView) findViewById(R.id.textView9);
        tv_prev_starttime = (TextView) findViewById(R.id.tv_prev_starttime);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        btn_next = (Button) findViewById(R.id.btn_next);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String first = "Schedule time to";
        String second = "work day";
        String next = "<font color='#EE0000'>begin</font>";
        textView9.setText(Html.fromHtml(first + " " + next + " " + second));

        try {
            if (pref.getString("alarmtime", "").isEmpty()) {
                tv_prev_starttime.setText("");
            } else {
                tv_prev_starttime.setText("Prev start alarm time :: " + pref.getString("alarmtime", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlarmActivity2.this, AlarmActivity.class));
                finish();
            }
        });
        alarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {
                    Intent intent = new Intent(AlarmActivity2.this, AlarmReceiver2.class);
                    PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity2.this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(sender);

                    Intent i = new Intent("in.wptrafficanalyzer.servicealarmdemo.demoactivity2");
                    PendingIntent operation = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager2 = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);


                    Log.d("MyActivity", "Alarm On");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());


                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                    alarmtime = sdf.format(calendar.getTime());
                    System.out.println(" alarmtime :- " + alarmtime);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("alarmtime", alarmtime);
                    editor.commit();
                    Intent myIntent = new Intent(AlarmActivity2.this, AlarmReceiver2.class);
                    // Intent myIntent =  new Intent("android.intent.action.ALARM_RECEIVER");

                    //pendingIntent = PendingIntent.getBroadcast(AlarmActivity2.this,)

                    pendingIntent = PendingIntent.getBroadcast(AlarmActivity2.this, 10, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                   /* alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
*/


//                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                            AlarmManager.INTERVAL_DAY, pendingIntent);

//                    86400000
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                    alarmManager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, operation);
                } else {
                    alarmManager.cancel(pendingIntent);
                    Log.d("MyActivity", "Alarm Off");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
