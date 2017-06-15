package busdriver.com.vidriver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

/**
 * Created by azhar-sarps on 30-May-17.
 */

public class AlertDemoActivity extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        /** Creating a alert dialog builder */
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /** Setting title for the alert dialog */
        builder.setTitle("Bus Driver");

        /** Setting the content for the alert dialog */
        builder.setMessage("Please click ok to stop alarm ");

        /** Defining an OK button event listener */

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** Exit application on click OK */
                        service_end();
                getActivity().finish();
            }
        });

        /** Creating the alert dialog window */
        return builder.create();
    }

    public void service_end() {
        Intent stopIntent = new Intent(getActivity(), GPSRingtonePlayingService.class);
        getActivity().stopService(stopIntent);
    }
    /** The application should be exit, if the user presses the back button */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
