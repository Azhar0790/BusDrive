package busdriver.com.vidriver;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHandlerUser extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 16;
    // Database Name
    private static final String DATABASE_NAME = "android_api";
    // Login table name
    private static final String TABLE_LOGIN = "login";
    // Login Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_EDAD = "edad";
    private static final String KEY_UID = "uid";
    private static final String KEY_TIPOVEH = "tipoveh";
    private static final String KEY_CONF_NOTIF = "conf_notif";
    private static final String KEY_ACTUALIZO = "actualizo";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_NUMERO = "numero";
    private static final String TABLE_RESPUESTAS = "respuestas";
    private static final String KEY_ID = "id";
    private static final String KEY_PETICION = "id_peticion";
    private static final String TABLE_PET = "peticiones";

    String name, username, password;
    int edad,tipoveh;
    public DatabaseHandlerUser(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EMAIL + " TEXT"+ ")";
        String TABLE_RESPUESTA = "CREATE TABLE " + TABLE_RESPUESTAS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PETICION + " TEXT" + ")";
        String TABLE_PETICIONES = "CREATE TABLE " + TABLE_PET + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PETICION + " TEXT" +")";
        db.execSQL(TABLE_PETICIONES);
        db.execSQL(TABLE_RESPUESTA);
        db.execSQL(CREATE_LOGIN_TABLE);

    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        // Create tables again
        onCreate(db);
    }
    /**
     * Storing user details in database
     * */
    public void addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.username); // Email
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("tipoveh", String.valueOf(cursor.getString(4)));
            user.put("created_at", cursor.getString(5));
        }

        cursor.close();
        db.close();
        // return user
        return user;
    }
    public Boolean EstaLogueado(){
        Boolean EstaLogueado = false;
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            EstaLogueado = true;
        }

        cursor.close();
        db.close();
        // return user
        return EstaLogueado;
    }
  /*  public void setAct(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACTUALIZO, 1); // Name
        db.update(TABLE_LOGIN,values,null,null);

    }*/
    public int getTipoveh(){

        String selectQuery = "SELECT "+KEY_TIPOVEH+" FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        int tipoveh = 0;
        if(cursor.getCount() > 0) {
           tipoveh = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        // return user
        return tipoveh;
    }
    /*public int getActualizo(){

        String selectQuery = "SELECT "+KEY_ACTUALIZO+" FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        int actualizo = 0;
        if(cursor.getCount() > 0) {
            actualizo = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        // return user
        return tipoveh;
    }*/
    public String getName(){

        String selectQuery = "SELECT name FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String name = "";
        if(cursor.getCount() > 0) {
            name = cursor.getString(0);
        }

        cursor.close();
        db.close();
        // return user
        return name;
    }
    public String getNumero(){

        String selectQuery = "SELECT numero FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String numero = "";
        if(cursor.getCount() > 0) {
            numero = cursor.getString(0);
        }

        cursor.close();
        db.close();
        // return user
        return numero;
    }

    public String getImage(){

        String selectQuery = "SELECT image FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String image = "";
        if(cursor.getCount() > 0) {
            image = cursor.getString(0);
        }

        cursor.close();
        db.close();
        // return user
        return image;
    }

    public String getEmail(){

        String selectQuery = "SELECT email FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String name = "";
        if(cursor.getCount() > 0) {
            name = cursor.getString(0);
        }

        cursor.close();
        db.close();
        // return user
        return name;
    }

    public boolean existColunm(String x){
        String selectQuery = "SELECT "+x+" FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String name = "";
        if(cursor.getCount() > 0) {
            return true;
        }

        cursor.close();
        db.close();
        // return user
        return false;
    }
    public void setTutorialTrue() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(KEY_ACTUALIZO, 1);
            db.update(TABLE_LOGIN, values, null, null);
            db.close(); // Closing database connection
        }catch(Exception e){
            Log.e("UPDATE DATABASE ERROR", e.getMessage(), e);
        }
    }
    public int getTutorialValue() {
        int act = 0;
            String selectQuery = "SELECT " + KEY_ACTUALIZO + " FROM " + TABLE_LOGIN;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

        // Move to first row
        cursor.moveToFirst();

        if(cursor.getCount() > 0) {
           act = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        // return user
            return act;

    }


    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }
    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }
    public void addId_pet(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PETICION, id); // id_peticion
        db.insert(TABLE_PET, null, values);
        db.close();
        Log.i("BASE DE DATOS PETICIONES", "SE GUARDÓ ID DE LA PETICION");
    }

    public Boolean isNewRequest_pet(String id){
        Boolean nuevo = false;
        try {
            String selectQuery = "SELECT * FROM " + TABLE_PET + " WHERE " + KEY_PETICION + "=" + id;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                nuevo = true;
            }
            cursor.close();
            db.close();
        }catch(Exception e){
            Log.e("DATABASE ERROR", e.getMessage(),e.getCause());
        }
        return nuevo;
    }

    public void addId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PETICION, id); // id_peticion
        db.insert(TABLE_RESPUESTAS, null, values);
        db.close();
        Log.i("BASE DE DATOS PETICIONES", "SE GUARDÓ ID DE LA PETICION");
    }

    public Boolean isNewRequest(String id) {
        Boolean nuevo = false;
        try {
            String selectQuery = "SELECT * FROM " + TABLE_RESPUESTAS + " WHERE " + KEY_PETICION + "=" + id;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                nuevo = true;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e("DATABASE ERROR", e.getMessage(), e.getCause());
        }
        return nuevo;
    }

}