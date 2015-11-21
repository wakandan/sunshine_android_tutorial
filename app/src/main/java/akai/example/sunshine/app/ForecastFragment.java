package akai.example.sunshine.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import akai.example.sunshine.data.WeatherContract;
import akai.example.sunshine.interfaces.ILocationChanged;
import akai.example.sunshine.interfaces.IWeatherItemSelected;
import akai.example.sunshine.sync.SunshineSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ILocationChanged {
    public static final int LOADER_ID = 0;
    public static final String LIST_POSITION = "LIST_POSITION";

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    static final int COL_PRESSURE = 9;
    private ListView listView;
    private int mSelectionPosition;
    private boolean mUseTodayLayout;

    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
//        fetchWeather();
    }

    @Override
    public void onLocationChanged(String newLocation) {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.v(getClass().getName(), savedInstanceState + "");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            fetchWeather();
            return true;
        } else if (id == R.id.action_view_location) {
            viewLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewLocation() {
        Intent intent = new Intent(Intent.ACTION_VIEW, getGeoLocation(Utility.getPreferredLocation(getActivity())));
        PackageManager packageManager = getActivity().getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "No Application To Show Location", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getGeoLocation(String postCode) {
        return Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", postCode).build();
    }

    private void fetchWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    private ForecastAdapter mForecastAdapter;

    public ForecastAdapter getmForecastAdapter() {
        return mForecastAdapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        if (savedInstanceState != null) {
            mSelectionPosition = savedInstanceState.getInt(LIST_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listView.setAdapter(mForecastAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_POSITION)) {
            mSelectionPosition = savedInstanceState.getInt(LIST_POSITION);
        }
        Log.v(getClass().getName(), savedInstanceState + "");
        Log.v(getClass().getName(), "Restored list position: " + mSelectionPosition);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                mSelectionPosition = position;
                Log.v(getClass().getName(), "Updated position: " + mSelectionPosition);
                if (cursor != null) {
                    IWeatherItemSelected callback = (IWeatherItemSelected) getActivity();
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Uri weatherLocationWithDateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting,
                            cursor.getLong(COL_WEATHER_DATE));
                    callback.onItemSelected(weatherLocationWithDateUri);
                }
            }
        });
        return rootView;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(Utility.getPreferredLocation(getActivity()), System.currentTimeMillis()),
                FORECAST_COLUMNS, null, null, WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        Log.v(getClass().getName(), "Current list position: " + mSelectionPosition);
        if (mSelectionPosition != ListView.INVALID_POSITION) {
            listView.smoothScrollToPosition(mSelectionPosition);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectionPosition != ListView.INVALID_POSITION) {
            outState.putInt(LIST_POSITION, mSelectionPosition);
        }
        Log.v(getClass().getName(), "Stored list position: " + mSelectionPosition);
        super.onSaveInstanceState(outState);
    }

    public void setUseTodayLayout(boolean b) {
        mUseTodayLayout = b;
        if (mForecastAdapter != null) {
            mForecastAdapter.setmUseTodayLayout(mUseTodayLayout);
        }
    }
}
