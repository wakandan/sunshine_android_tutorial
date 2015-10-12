package akai.example.sunshine.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by akai on 6/10/15.
 */
public class WeatherContract {
    public static final String CONTENT_AUTHORITY = "akai.example.sunshine.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_LOCATION_SETTING = "location_settings";
        public static final String COLUMN_COORD_LONG = "coord_long";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_CITY_NAME = "city_name";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = StringUtils.join(new String[]{ContentResolver.CURSOR_DIR_BASE_TYPE, CONTENT_AUTHORITY, PATH_LOCATION}, "/");
        public static final String CONTENT_ITEM_TYPE = StringUtils.join(new String[]{ContentResolver.CURSOR_ITEM_BASE_TYPE, CONTENT_AUTHORITY, PATH_LOCATION}, "/");

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE = StringUtils.join(new String[]{ContentResolver.CURSOR_DIR_BASE_TYPE, CONTENT_AUTHORITY, PATH_WEATHER}, "/");
        public static final String CONTENT_ITEM_TYPE = StringUtils.join(new String[]{ContentResolver.CURSOR_ITEM_BASE_TYPE, CONTENT_AUTHORITY, PATH_WEATHER}, "/");

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationQuery, long testDate) {
            return CONTENT_URI.buildUpon().appendPath(locationQuery).appendPath(Long.toString(normalizeDate(testDate))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            return StringUtils.isEmpty(dateString) ? 0 : Long.parseLong(dateString);
        }

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min_temp";
        public static final String COLUMN_MAX_TEMP = "max_temp";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";
    }
}
