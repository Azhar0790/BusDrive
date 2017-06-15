package busdriver.com.vidriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Calendar;

/**
 * Created by mu on 4/23/2017.
 */

public class ScheduleActivity extends AppCompatActivity {

    CheckBox Sun, Mon, Tue, Wed, Thu, Fri, Sat;
    Button save;
    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        save = (Button) findViewById(R.id.btnsave);
        Sun = (CheckBox) findViewById(R.id.sun);
        Mon = (CheckBox) findViewById(R.id.mon);
        Tue = (CheckBox) findViewById(R.id.tue);
        Wed = (CheckBox) findViewById(R.id.wed);
        Thu = (CheckBox) findViewById(R.id.thu);
        Fri = (CheckBox) findViewById(R.id.fri);
        Sat = (CheckBox) findViewById(R.id.sat);

        Sun.setChecked(pref.getBoolean("day" + Calendar.SUNDAY, false));
        Mon.setChecked(pref.getBoolean("day" + Calendar.MONDAY, false));
        Tue.setChecked(pref.getBoolean("day" + Calendar.TUESDAY, false));
        Wed.setChecked(pref.getBoolean("day" + Calendar.WEDNESDAY, false));
        Thu.setChecked(pref.getBoolean("day" + Calendar.THURSDAY, false));
        Fri.setChecked(pref.getBoolean("day" + Calendar.FRIDAY, false));
        Sat.setChecked(pref.getBoolean("day" + Calendar.SATURDAY, false));



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

    }

    private void save() {
        SharedPreferences.Editor editor2 = pref.edit();
        editor2.putBoolean("day" + Calendar.SUNDAY, Sun.isChecked());
        editor2.putBoolean("day" + Calendar.MONDAY, Mon.isChecked());
        editor2.putBoolean("day" + Calendar.TUESDAY, Tue.isChecked());
        editor2.putBoolean("day" + Calendar.WEDNESDAY, Wed.isChecked());
        editor2.putBoolean("day" + Calendar.THURSDAY, Thu.isChecked());
        editor2.putBoolean("day" + Calendar.FRIDAY, Fri.isChecked());
        editor2.putBoolean("day" + Calendar.SATURDAY, Sat.isChecked());
        editor2.commit();

        this.finish();
    }
}
