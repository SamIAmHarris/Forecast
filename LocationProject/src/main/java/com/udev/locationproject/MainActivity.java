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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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

    /* MainActivity variables */
    protected Location location;
    private static long updateForecastDefaultDelay = 5000;
    private static long updateDisplayDefaultDelay = 1000;

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

        updateForecast();
    }

    /**
     * waits for location data to be received at some specified interval
     */
    private void updateForecast(long interval) {
        final Handler h = new Handler();
        final Location location = this.location;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (location != null) {
                    updateDisplay(new Forecast(MainActivity.this.location.getLatitude(), MainActivity.this.location.getLongitude()));
                    h.removeCallbacks(this);
                } else {
                    updateForecast();
                }
            }
        }, interval); /* todo:simulate a slow network */
    }

    private void updateForecast() {
        updateForecast(updateForecastDefaultDelay);
    }

    private void updateDisplay(final Forecast forecast, long interval) {
        final Handler h = new Handler();
        final Forecast f = forecast;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (f.data != null) {
                    h.removeCallbacks(this);
                    TextView textView = (TextView) findViewById(R.id.text_view);
                    textView.setText("");

                    try {
                        ListView listView = (ListView)findViewById(R.id.list_view);

                        JSONObject currentForecast = forecast.data.getJSONObject("currently");
                        String time = new Date(Long.parseLong(currentForecast.getString("time"))).toString();
                        String[] conditions = new String[]{
                                time,
                                currentForecast.getString("summary"),
                                currentForecast.getString("precipType"),
                                currentForecast.getString("temperature"),
                                currentForecast.getString("apparentTemperature"),
                                currentForecast.getString("dewPoint"),
                                currentForecast.getString("windSpeed"),
                                currentForecast.getString("windSpeed"),
                                currentForecast.getString("windBearing"),
                                currentForecast.getString("windBearing"),
                                currentForecast.getString("cloudCover"),
                                currentForecast.getString("humidity"),
                                currentForecast.getString("pressure"),
                                currentForecast.getString("visibility"),
                                currentForecast.getString("ozone")
                        };

                        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.weather_info, conditions);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    updateDisplay(f);
                }
            }
        }, interval);
    }

    private void updateDisplay(final Forecast forecast) {
        updateDisplay(forecast, updateDisplayDefaultDelay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
