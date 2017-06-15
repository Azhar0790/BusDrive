package busdriver.com.vidriver;

/**
 * Created by ajldpc on 13/11/2016.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.io.File;
import java.util.Calendar;


public class AlarmReceiver2 extends WakefulBroadcastReceiver {

        @Override
    public void onReceive(final Context context, Intent intent) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        SharedPreferences pref;
        pref = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);

        if (pref.getBoolean("day" + day, false)) {
            Uri mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (mUri == null) {
                mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            Intent startIntent = new Intent(context, RingtonePlayingService.class);
            startIntent.putExtra("ringtone-uri", mUri);
            context.startService(startIntent);
/*
            Uri mUri = Uri.parse("android.resource://"
                    + context.getPackageName() + "/raw/alarm_tone"  );*/

      /*      Uri mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (mUri == null) {
                mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            LoginActivity.ringtone = RingtoneManager.getRingtone(context, mUri);
            LoginActivity.ringtone.play();*/

            //this will send a notification message
            ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);

/*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ringtone.stop();
                }
            }, 15000);*/
        }
    }
//    @Override
//    public void onReceive(final Context context, Intent intent) {
//
//        //this will sound the alarm tone
//        //this will sound the alarm once, if you wish to
//        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Intent startIntent = new Intent(context, RingtonePlayingService.class);
//        startIntent.putExtra("ringtone-uri", alarmUri);
//        context.startService(startIntent);
//        //this will send a notification message
//        ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
//
//
//    }

    public static void stop_alarm(Context ee) {
        try {
            Intent stopIntent = new Intent(ee, RingtonePlayingService.class);
            ee.stopService(stopIntent);
            //ringtone.stop();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error :- " + e);
        }
    }

}