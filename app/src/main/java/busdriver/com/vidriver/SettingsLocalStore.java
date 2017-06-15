package busdriver.com.vidriver;



import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by tundealao on 29/03/15.
 */
public class SettingsLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;
    Context context;
    public SettingsLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
        this.context = context;
    }

    public void CreateSettingsData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("notificaciones", true);
        userLocalDatabaseEditor.commit();
    }

    public void setNotificacionesBool(boolean bol) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("notificaciones", bol);
        userLocalDatabaseEditor.commit();
        String res;
        if(bol == true){res = "activado";}else{res = "desactivado";}
        Toast.makeText(context, "Se han "+res+" las notificaciones", Toast.LENGTH_SHORT).show();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public Boolean getNotificacionesValue() {
        boolean result = userLocalDatabase.getBoolean("notificaciones", true);
        return result;
    }

}