Info on instantiating a Forecast object retrieves data automatically

notes:

 * USE YOUR OWN API KEY!!!
 * Be sure that your GPS is enabled!!!
 * You need these permissions in your AndroidManifest.xml under the manifest tag
  * uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"
  * uses-permission android:name="android.permission.INTERNET"
 *   
 * Calling Forecast's constructor will use your API key. Do not call the constructor in LocationListener.onLocationChanged or you will exhaust the Forecast.io 1000 call per day limit.

1. instantiate a Forecast.io forecast
2. arg1: the Location.getLatitude()
3. arg2: the Location.getLongitude()
4. arg3: use your unique forecast.io api_key

Forecast forecast = new Forecast(

  MainActivity.this.location.getLatitude(),
  
  MainActivity.this.location.getLongitude(),
  
  API_KEY
  
)

// use the data

if(forecast.getStatus() == HttpStatus.SC_OK) {

  JSONObject currentForecast = forecast.getData().getJSONObject("currently");
  Log.i("Forecast.io", currentForecast.getString("summary"));
  
} else {

  // the data is still being retrieved or there was an error
  
}

JSONObject currentForecast = forecast.getData().getJSONObject("currently");

currentForecast.getString("summary");
