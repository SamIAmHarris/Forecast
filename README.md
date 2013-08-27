/* instantiating a Forecast object retrieves data automatically */

/*
 * note: Be sure that your GPS is enabled!!!
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
