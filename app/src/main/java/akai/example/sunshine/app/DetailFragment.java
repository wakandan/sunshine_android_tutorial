package akai.example.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import akai.example.sunshine.data.WeatherContract.WeatherEntry;
import akai.example.sunshine.interfaces.ILocationChanged;

/**
 * Created by akai on 4/10/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ILocationChanged {
    public static final String DETAIL_URI = "DETAIL_URI";
    private final String HASH_TAG = "#sunshineApp";

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_PRESSURE = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_WIND_DIRECTION = 7;
    private static final int COL_WEATHER_CONDITION_ID = 8;

    private TextView textView;
    private ShareActionProvider shareActionProvider;
    private String mForecast;
    private TextView dateTextView;
    private TextView maxTempTextView;
    private TextView minTempTextView;
    private TextView forecastTextView;
    private TextView pressureTextView;
    private TextView windTextView;
    private TextView longDateTextView;
    private ImageView iconView;

    private Uri mUri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        long dateFromUri = WeatherEntry.getDateFromUri(mUri);
        String friendlyDate = Utility.formatDate(dateFromUri);
        getActivity().setTitle(friendlyDate);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DetailFragment.DETAIL_URI);
            Log.v(getClass().getName(), "Received uri " + mUri);
        } else {
            mUri = getActivity().getIntent().getData();
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        dateTextView = (TextView) rootView.findViewById(R.id.list_item_date_textview);
        longDateTextView = (TextView) rootView.findViewById(R.id.list_item_long_date_textview);
        maxTempTextView = (TextView) rootView.findViewById(R.id.list_item_high_textview);
        minTempTextView = (TextView) rootView.findViewById(R.id.list_item_low_textview);
        forecastTextView = (TextView) rootView.findViewById(R.id.list_item_forecast_textview);
        pressureTextView = (TextView) rootView.findViewById(R.id.detail_pressure);
        windTextView = (TextView) rootView.findViewById(R.id.detail_wind);
        iconView = (ImageView) rootView.findViewById(R.id.forecast_item_img);
        //in tablet layout there's no loader initialized
        if (getLoaderManager().getLoader(DETAIL_LOADER) != null) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mForecast != null) {
            shareActionProvider.setShareIntent(getShareForeCastIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent getShareForeCastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + " " + HASH_TAG);
        return shareIntent;
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(getClass().getName(), "Fetching with uri: " + mUri);
        if (mUri != null) {
            return new CursorLoader(getActivity(),
                    mUri, FORECAST_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.v(getClass().getName(), "In onLoadFinished");
        if (data != null && data.moveToFirst()) {
            long date = data.getLong(COL_WEATHER_DATE);
            int weatherConditionId = data.getInt(COL_WEATHER_CONDITION_ID);
            Log.v(getClass().getName(), "Weather condition id: " + weatherConditionId);
            String dateText = Utility.getFriendlyDayString(getActivity(), date);
            String description = data.getString(COL_WEATHER_DESC);
            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherConditionId));
            maxTempTextView.setText(high);
            dateTextView.setText(dateText);
            forecastTextView.setText(description);
            minTempTextView.setText(low);
            longDateTextView.setText(Utility.getFormattedMonthDay(getActivity(), date));
            pressureTextView.setText(String.format("Pressure: %.2f", data.getDouble(COL_WEATHER_PRESSURE)));
            windTextView.setText(Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_WIND_DIRECTION)));
            // We still need this for the share intent
            mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);
            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(getShareForeCastIntent());
            }
        } else {
            Log.v(getClass().getName(), "No data");
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    @Override
    public void onLocationChanged(String newLocation) {
        Uri uri = mUri;
        if (uri != null) {
            long date = WeatherEntry.getDateFromUri(uri);
            Uri updateUri = WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = uri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
