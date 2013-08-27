/* instantiating a Forecast object retrieves data automatically */

/*
 * notes:
 * - USE YOUR OWN API KEY!!!
 * - Be sure that your GPS is enabled!!!
 * - You need these permissions in your AndroidManifest.xml
 *   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *   <uses-permission android:name="android.permission.INTERNET" />
 *   
 * - Calling Forecast's constructor will use your API key. Do not call
 *   the constructor in LocationListener.onLocationChanged or you will
 *   exhaust the Forecast.io 1000 call per day limit.
 */

Forecast forecast = new Forecast(             /* instantiate a Forecast.io forecast */
  MainActivity.this.location.getLatitude(),   /* the Location.getLatitude() */
  MainActivity.this.location.getLongitude(),  /* the Location.getLongitude() */
  API_KEY                                     /* your unique forecast.io api_key */
)

/* use the data */

if(forecast.getStatus() != HttpStatus.SC_OK) {
  /* the data is still being retrieved or there was an error */
}

JSONObject currentForecast = forecast.getData().getJSONObject("currently");
currentForecast.getString("summary");
