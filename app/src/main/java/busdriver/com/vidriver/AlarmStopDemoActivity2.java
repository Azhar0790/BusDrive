package busdriver.com.vidriver;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by azhar-sarps on 30-May-17.
 */

public class AlarmStopDemoActivity2 extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */
        AlertDemoActivity alert = new AlertDemoActivity();

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
        alert.show(getFragmentManager(), "AlertDemo");
    }
}
