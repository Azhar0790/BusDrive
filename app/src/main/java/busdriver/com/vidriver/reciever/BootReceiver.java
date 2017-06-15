package busdriver.com.vidriver.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import busdriver.com.vidriver.MapsActivity;

/**
 * Created by azhar-sarps on 16-May-17.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MapsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
