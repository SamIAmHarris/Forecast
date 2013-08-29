package com.udev.locationproject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private class WeatherListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
//            Log.i(TAG, location.getLatitude() + ", " + location.getLongitude());
//            Forecast forecast = new Forecast(location.getLatitude(), location.getLongitude());
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
    }

    private static final String TAG = "MainActivity";

    /* API variables */
    private final String API_KEY = "5e07f9dc4b8932b18f19cea015e5512c";

    /* MainActivity variables */
    protected Location location;
    private static long retrieveForecastDefaultDelay = 5000;
    private static long updateViewsDefaultDelay = 1000;

    /* loading layout variables */
    private boolean contentLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        WeatherListener weatherListener = new WeatherListener();

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, weatherListener);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, weatherListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        retrieveForecastData();
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
                if (location != null) {
                    updateViews(
                            new Forecast(                                       // instantiate a forecast.io forecast
                                    MainActivity.this.location.getLatitude(),   // the Location.getLatitude()
                                    MainActivity.this.location.getLongitude(),  // the Location.getLongitude()
                                    API_KEY                                     // your unique forecast.io api_key
                            )
                    );
                    contentLoaded = true;
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

                    // hide the loading progress indicator
                    ProgressBar progressBar = (ProgressBar)findViewById(R.id.loading_spinner);
                    progressBar.setVisibility(View.GONE);

                    TextView loadingMessage = (TextView)findViewById(R.id.loading_message);
                    loadingMessage.setVisibility(View.GONE);

                    try {
                        ListView listView = (ListView) findViewById(R.id.list_view);

                        JSONObject currentForecast = forecast.getData().getJSONObject("currently");
                        Long time = currentForecast.getLong("time");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        Date formattedTime = new Date();
<<<<<<< HEAD

                        try {
                            formattedTime = format.parse(time.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String[] conditions = new String[]{
                                "Date: " + formattedTime.toString(),
                                stringValueForKey(currentForecast, "summary", "Summary"),
                                stringValueForKey(currentForecast, "precipType", "Precipitation Type"),
                                stringValueForKey(currentForecast, "temperature", "Temperature"),
                                stringValueForKey(currentForecast, "apparentTemperature", "Feels like"),
                                stringValueForKey(currentForecast, "dewPoint", "Dew Point"),
                                stringValueForKey(currentForecast, "windSpeed", "Wind Speed"),
                                stringValueForKey(currentForecast, "windBearing", "Wind Bearing"),
                                stringValueForKey(currentForecast, "cloudCover", "Cloud Cover"),
                                stringValueForKey(currentForecast, "humidity", "Humidity"),
                                stringValueForKey(currentForecast, "pressure", "Pressure"),
                                stringValueForKey(currentForecast, "visibility", "Visibility"),
                                stringValueForKey(currentForecast, "ozone", "Ozone")
=======
                        
                        formattedTime = format.parse(time.toString());

                        String[] conditions = new String[]{
                                formattedTime.toString(),
                                currentForecast.getString("summary"),
                                //currentForecast.getString("precipType"),
                                currentForecast.getString("temperature"),
                                currentForecast.getString("apparentTemperature"),
                                currentForecast.getString("dewPoint"),
                                currentForecast.getString("windSpeed"),
                                currentForecast.getString("windBearing"),
                                currentForecast.getString("cloudCover"),
                                currentForecast.getString("humidity"),
                                currentForecast.getString("pressure"),
                                currentForecast.getString("visibility"),
                                currentForecast.getString("ozone")
>>>>>>> 28bcf505b1f01bbba4c8617523dccf9d1c03bc61
                        };

                        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.weather_info, conditions);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch(ParseException e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
}
