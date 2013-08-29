package com.udev.locationproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity
        implements FragmentManager.OnBackStackChangedListener, LocationListener {

    public static class WeatherFragment extends Fragment {
        public WeatherFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_card_front, container, false);
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
//            Log.i(TAG, location.getLatitude() + ", " + location.getLongitude());

            /*  How to burn all of your Forecast.io calls in a few minutes...
             *  DON'T EVEN THINK ABOUT DOING THIS!!!
             *  Forecast forecast = new Forecast(location.getLatitude(), location.getLongitude());
             */
        Log.i(TAG, loc.getLatitude() + "," + loc.getLongitude());
        location = loc;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onBackStackChanged() {

    }

    private static final String TAG = "MainActivity";

    /* API variables */
    private final String API_KEY = "5e07f9dc4b8932b18f19cea015e5512c";

    /* MainActivity variables */
    protected Location location;
    private static long retrieveForecastDefaultDelay = 5000;
    private static long retrieveForecastTimeout = 20000;/* todo:timeout if it's taking too long to retrieve the Forecast data */
    private static long updateViewsDefaultDelay = 1000;

    /* card action variables */
    private boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, weatherListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button refreshButton = (Button)findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveForecastData();
                hideUi();
                showLoadingUi();
            }
        });

        hideUi();
        retrieveForecastData();

        if (savedInstanceState == null) {
            // If there is no saved instance state, add a fragment representing the
            // front of the card to this activity. If there is saved instance state,
            // this fragment will have already been added to the activity.
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }


        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);

        // Add either a "photo" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_flip, Menu.NONE,
                mShowingBack
                        ? R.string.action_weather
                        : R.string.action_info);
        item.setIcon(mShowingBack
                ? R.drawable.ic_collections_cloud
                : R.drawable.ic_action_info);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                break;
            case R.id.action_flip:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * waits for location data to be received at some specified interval
     */
    private void retrieveForecastData(long interval) {
        final Handler h = new Handler();
        final Location location = this.location;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideUi();
                showLoadingUi();
                if (location != null) {
                    updateViews(
                            new Forecast(                     // instantiate a forecast.io forecast
                                    location.getLatitude(),   // the Location.getLatitude()
                                    location.getLongitude(),  // the Location.getLongitude()
                                    API_KEY                   // your unique forecast.io api_key
                            )
                    );
                    h.removeCallbacks(this);
                } else {
                    retrieveForecastData();
                }
            }
        }, interval); /* todo:simulate a slow network */
    }

    private void retrieveForecastData() {
        retrieveForecastData(retrieveForecastDefaultDelay);
    }

    private void updateViews(final Forecast forecast, long interval) {
        final Handler h = new Handler();
        final Forecast f = forecast;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (f.getStatus() == HttpStatus.SC_OK) {
                    h.removeCallbacks(this);

                    try {
                        JSONObject currentForecast = forecast.getData().getJSONObject("currently");
                        parseJsonData(currentForecast);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    updateViews(f);
                }
            }
        }, interval);
    }

    private void updateViews(final Forecast forecast) {
        updateViews(forecast, updateViewsDefaultDelay);
    }

    private String stringValueForKey(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return "None";
    }

    private String stringValueForKey(JSONObject obj, String key, String label) {

        return label + ": " + stringValueForKey(obj, key);
    }

    public void hideUi() {
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setVisibility(View.GONE);

        Button refreshButton = (Button)findViewById(R.id.refresh_button);
        refreshButton.setVisibility(View.GONE);
    }

    public void showUi() {
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setVisibility(View.VISIBLE);

        Button refreshButton = (Button)findViewById(R.id.refresh_button);
        refreshButton.setVisibility(View.VISIBLE);
    }

    public void showLoadingUi() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.VISIBLE);

        TextView loadingMessage = (TextView) findViewById(R.id.loading_message);
        loadingMessage.setVisibility(View.VISIBLE);
    }

    public void hideLoadingUi() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);

        TextView loadingMessage = (TextView) findViewById(R.id.loading_message);
        loadingMessage.setVisibility(View.GONE);
    }

    private void parseJsonData(JSONObject data) {
        ListView listView = (ListView) findViewById(R.id.list_view);

        Long time = new Long(0);

        try {
            time = data.getLong("time");
        } catch(JSONException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date formattedTime = new Date();

        try {
            formattedTime = format.parse(time.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] conditions = new String[]{
                "Date: " + formattedTime.toString(),
                stringValueForKey(data, "summary", "Summary"),
                stringValueForKey(data, "precipType", "Precipitation"),
                stringValueForKey(data, "temperature", "Temperature"),
                stringValueForKey(data, "apparentTemperature", "Feels like"),
                stringValueForKey(data, "dewPoint", "Dew Point"),
                stringValueForKey(data, "windSpeed", "Wind Speed"),
                stringValueForKey(data, "windBearing", "Wind Bearing"),
                stringValueForKey(data, "cloudCover", "Cloud Cover"),
                stringValueForKey(data, "humidity", "Humidity"),
                stringValueForKey(data, "pressure", "Pressure"),
                stringValueForKey(data, "visibility", "Visibility"),
                stringValueForKey(data, "ozone", "Ozone")
        };

        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.weather_info, conditions);
        listView.setAdapter(adapter);

        showUi();
        hideLoadingUi();
    }
}
