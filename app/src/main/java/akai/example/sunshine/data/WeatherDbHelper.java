package akai.example.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by akai on 6/10/15.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_LOCATION_TABLE = String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT UNIQUE NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "UNIQUE (%s) ON CONFLICT REPLACE);",
                WeatherContract.LocationEntry.TABLE_NAME,
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME

        );
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        String SQL_CREATE_WEATHER_TABLE = String.format("CREATE TABLE %s " +
                        "( %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s TEXT NOT NULL DEFAULT 'Sunny'," +
                        "FOREIGN KEY (%s) REFERENCES %s(%s), " +
                        "UNIQUE (%s, %s) ON CONFLICT REPLACE) ;",
                WeatherContract.WeatherEntry.TABLE_NAME,
                WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
                WeatherContract.LocationEntry.TABLE_NAME,
                WeatherContract.LocationEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY
        );
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
