package busdriver.com.vidriver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class InternetConnection {

	public static final boolean isInternetOn(Context ctx) {

		boolean isInternetOn = false;
		boolean mobileDataEnabled = false;
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		if (wifi.isAvailable() && wifi.isConnected()) {

			isInternetOn = true;
		}
		// gets the current TelephonyManager
		else if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {

			// the phone has a sim card
			if (mobile != null) {

				 // Assume disabled
				ConnectivityManager cm = (ConnectivityManager) ctx
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				try {
					Class<?> cmClass = Class.forName(cm.getClass().getName());
					Method method = cmClass
							.getDeclaredMethod("getMobileDataEnabled");
					method.setAccessible(true);
					mobileDataEnabled = (Boolean) method.invoke(cm);
					if (!mobileDataEnabled) {
						isInternetOn = false;
					} else if (mobileDataEnabled) {
						isInternetOn = true;
					}
				} catch (Exception e) {
					Log.e("Error", e + "");
				}
				return isInternetOn;

			} else {
				isInternetOn = false;
			}
		} else {
			// no sim card available
			isInternetOn = false;
		}

		return isInternetOn;
	}
}