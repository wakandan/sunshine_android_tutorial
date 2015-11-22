package akai.example.sunshine.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import akai.example.sunshine.interfaces.IWeatherItemSelected;

public class DetailActivity extends ActionBarActivity implements IWeatherItemSelected {

    private final String LOG_TAG = getClass().getName();
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.weather_detail_container, new DetailFragment()).commit();
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, new DetailFragment()).commit();
    }


}
