package kz.beam.weatherforecast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@SuppressWarnings("deprecation")
public class ResultActivity extends AppCompatActivity {

    static String DEBUG_TAG = "kz.beam.weatherforecast";
    static private Map<String, String> stateMap = null;
    String city = null, state = null, measurement = null;
    private String fbIcon = null, fbDesc = null;
    private String latitude, longitude;
    JSONObject jsonObject = null, currentObj = null;
    JSONArray dailyArr = null;
    ProgressBar progressSpin;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    TextView fcastText, currentTemp, lowTemp, highTemp, precipitation,
            rain, wind, dewpoint, humidity, visibility, sunrise, sunset;

    private ImageView ic_precip, ic_rain_prob, ic_wind, ic_dewpoint, ic_humid, ic_visib, ic_sunrise, ic_sunset;

    /**
     * There is some work for states list
     */
    private static void populateStates() {
        if (stateMap == null) {
            stateMap = new HashMap<>();
            stateMap.put("Alabama", "AL");
            stateMap.put("Alaska", "AK");
            stateMap.put("Alberta", "AB");
            stateMap.put("American Samoa", "AS");
            stateMap.put("Arizona", "AZ");
            stateMap.put("Arkansas", "AR");
            stateMap.put("Armed Forces (AE)", "AE");
            stateMap.put("Armed Forces Americas", "AA");
            stateMap.put("Armed Forces Pacific", "AP");
            stateMap.put("British Columbia", "BC");
            stateMap.put("California", "CA");
            stateMap.put("Colorado", "CO");
            stateMap.put("Connecticut", "CT");
            stateMap.put("Delaware", "DE");
            stateMap.put("District Of Columbia", "DC");
            stateMap.put("Florida", "FL");
            stateMap.put("Georgia", "GA");
            stateMap.put("Guam", "GU");
            stateMap.put("Hawaii", "HI");
            stateMap.put("Idaho", "ID");
            stateMap.put("Illinois", "IL");
            stateMap.put("Indiana", "IN");
            stateMap.put("Iowa", "IA");
            stateMap.put("Kansas", "KS");
            stateMap.put("Kentucky", "KY");
            stateMap.put("Louisiana", "LA");
            stateMap.put("Maine", "ME");
            stateMap.put("Manitoba", "MB");
            stateMap.put("Maryland", "MD");
            stateMap.put("Massachusetts", "MA");
            stateMap.put("Michigan", "MI");
            stateMap.put("Minnesota", "MN");
            stateMap.put("Mississippi", "MS");
            stateMap.put("Missouri", "MO");
            stateMap.put("Montana", "MT");
            stateMap.put("Nebraska", "NE");
            stateMap.put("Nevada", "NV");
            stateMap.put("New Brunswick", "NB");
            stateMap.put("New Hampshire", "NH");
            stateMap.put("New Jersey", "NJ");
            stateMap.put("New Mexico", "NM");
            stateMap.put("New York", "NY");
            stateMap.put("Newfoundland", "NF");
            stateMap.put("North Carolina", "NC");
            stateMap.put("North Dakota", "ND");
            stateMap.put("Northwest Territories", "NT");
            stateMap.put("Nova Scotia", "NS");
            stateMap.put("Nunavut", "NU");
            stateMap.put("Ohio", "OH");
            stateMap.put("Oklahoma", "OK");
            stateMap.put("Ontario", "ON");
            stateMap.put("Oregon", "OR");
            stateMap.put("Pennsylvania", "PA");
            stateMap.put("Prince Edward Island", "PE");
            stateMap.put("Puerto Rico", "PR");
            stateMap.put("Quebec", "PQ");
            stateMap.put("Rhode Island", "RI");
            stateMap.put("Saskatchewan", "SK");
            stateMap.put("South Carolina", "SC");
            stateMap.put("South Dakota", "SD");
            stateMap.put("Tennessee", "TN");
            stateMap.put("Texas", "TX");
            stateMap.put("Utah", "UT");
            stateMap.put("Vermont", "VT");
            stateMap.put("Virgin Islands", "VI");
            stateMap.put("Virginia", "VA");
            stateMap.put("Washington", "WA");
            stateMap.put("West Virginia", "WV");
            stateMap.put("Wisconsin", "WI");
            stateMap.put("Wyoming", "WY");
            stateMap.put("Yukon Territory", "YT");
        }
    }

    /**
     * TODO Implement this after finishing
     */
    public static void expandAnimation(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapseAnimation(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fcastText = (TextView) findViewById(R.id.fcast_text);
        currentTemp = (TextView) findViewById(R.id.fcast_temp);
        lowTemp = (TextView) findViewById(R.id.low_temp);
        highTemp = (TextView) findViewById(R.id.high_temp);
        precipitation = (TextView) findViewById(R.id.precip_value);
        rain = (TextView) findViewById(R.id.rain_prob_value);
        wind = (TextView) findViewById(R.id.wind_value);
        dewpoint = (TextView) findViewById(R.id.dewpoint_value);
        humidity = (TextView) findViewById(R.id.humid_value);
        visibility = (TextView) findViewById(R.id.visib_value);
        sunrise = (TextView) findViewById(R.id.sunrise_value);
        sunset = (TextView) findViewById(R.id.sunset_value);
        ic_precip = (ImageView) findViewById(R.id.precip);
        ic_rain_prob = (ImageView) findViewById(R.id.rain_prob);
        ic_wind = (ImageView) findViewById(R.id.wind);
        ic_dewpoint = (ImageView) findViewById(R.id.dewpoint);
        ic_humid = (ImageView) findViewById(R.id.humid);
        ic_visib = (ImageView) findViewById(R.id.visib);
        ic_sunrise = (ImageView) findViewById(R.id.sunrise);
        ic_sunset = (ImageView) findViewById(R.id.sunset);
        progressSpin = (ProgressBar) findViewById(R.id.progressSpinner);
        Intent intent = getIntent();
        Bundle extraBundle = intent.getExtras();
        measurement = extraBundle.getString("measurement");
        city = extraBundle.getString("city");
        String stateName = extraBundle.getString("state");
        populateStates();
        for (Map.Entry<String, String> entry : stateMap.entrySet()) {
            if (entry.getKey().equals(stateName)) {
                state = entry.getValue();
            }
        }
        if (!extraBundle.getString("jsonObject").isEmpty()) {
            try {
                jsonObject = new JSONObject(extraBundle.getString("jsonObject"));
                latitude = jsonObject.getString("latitude");
                longitude = jsonObject.getString("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            insertValues();
        } else {
            /** TODO Implement return to Main Activity */
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResults) {
                Log.e(DEBUG_TAG,"Facebook Login success");
            }

            @Override
            public void onCancel() {
                Log.e(DEBUG_TAG,"Facebook Login canceled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(DEBUG_TAG, "Facebook Login failed");
            }
        });
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(), "Sharing was Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Sharing was Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(DEBUG_TAG, "Share: " + exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    /**
     * Launches Activities from the Action Bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_details:
                Intent newIntent = new Intent(ResultActivity.this, DetailsActivity.class);
                Bundle extraBundle = new Bundle();
                extraBundle.putString("jsonObject", jsonObject.toString());
                extraBundle.putString("measurement", measurement);
                extraBundle.putString("city", city);
                extraBundle.putString("state", state);
                newIntent.putExtras(extraBundle);
                ResultActivity.this.startActivity(newIntent);
                return true;
            case R.id.menu_map:
                Intent mapIntent = new Intent(ResultActivity.this, AerisMapActivity.class);
                Bundle exBundle = new Bundle();
                try {
                    exBundle.putString("latitude", jsonObject.getString("latitude"));
                    exBundle.putString("longitude", jsonObject.getString("longitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mapIntent.putExtras(exBundle);
                ResultActivity.this.startActivity(mapIntent);
                return true;
            case R.id.menu_facebook:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Current weather in " + city + ", " + state)
                            .setImageUrl(Uri.parse("http://cs-server.usc.edu:20414/hw8/forecast-images/" + fbIcon))
                            .setContentDescription(fbDesc)
                            .setContentUrl(Uri.parse("http://forecast.io"))
                            .build();
                    shareDialog.show(linkContent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void changeFontColor() {
        fcastText.setTextColor(getResources().getColor(R.color.colorText));
        currentTemp.setTextColor(getResources().getColor(R.color.colorText));
        lowTemp.setTextColor(getResources().getColor(R.color.colorText));
        highTemp.setTextColor(getResources().getColor(R.color.colorText));
        precipitation.setTextColor(getResources().getColor(R.color.colorText));
        rain.setTextColor(getResources().getColor(R.color.colorText));
        wind.setTextColor(getResources().getColor(R.color.colorText));
        dewpoint.setTextColor(getResources().getColor(R.color.colorText));
        humidity.setTextColor(getResources().getColor(R.color.colorText));
        visibility.setTextColor(getResources().getColor(R.color.colorText));
        sunrise.setTextColor(getResources().getColor(R.color.colorText));
        sunset.setTextColor(getResources().getColor(R.color.colorText));
    }

    public void changeIconColor() {
        ic_precip.setImageResource(R.drawable.ic_precip_dark);
        ic_rain_prob.setImageResource(R.drawable.ic_rain_dark);
        ic_wind.setImageResource(R.drawable.ic_wind_dark);
        ic_dewpoint.setImageResource(R.drawable.ic_dewpoint_dark);
        ic_humid.setImageResource(R.drawable.ic_humidity_dark);
        ic_visib.setImageResource(R.drawable.ic_visibility_dark);
        ic_sunrise.setImageResource(R.drawable.ic_sunrise_dark);
        ic_sunset.setImageResource(R.drawable.ic_sunset_dark);
    }

    /** Experimental Animation Implementation */

    private void insertValues() {
        try {
            currentObj = jsonObject.getJSONObject("currently");
            dailyArr = jsonObject.getJSONObject("daily").optJSONArray("data");
            final CardView cardView = (CardView) findViewById(R.id.cardView);
            final CardView cardDetails = (CardView) findViewById(R.id.cardViewDetails);
            cardView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (cardDetails.getVisibility() == View.VISIBLE)
                        cardDetails.setVisibility(View.INVISIBLE);
                    else if (cardDetails.getVisibility() == View.INVISIBLE)
                        cardDetails.setVisibility(View.VISIBLE);
                }
            });
            cardDetails.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (cardDetails.getVisibility() == View.VISIBLE)
                        cardDetails.setVisibility(View.INVISIBLE);
                    else if (cardDetails.getVisibility() == View.INVISIBLE)
                        cardDetails.setVisibility(View.VISIBLE);
                }
            });
            ImageView img = (ImageView) findViewById(R.id.image_weather);
            switch (currentObj.getString("icon")) {
                case "clear-day":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                    img.setImageResource(R.drawable.clear_day);
                    break;
                case "clear-night":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearNight));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearNight));
                    img.setImageResource(R.drawable.clear_night);
                    break;
                case "cloudy":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardCloudy));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardCloudy));
                    img.setImageResource(R.drawable.cloudy);
                    break;
                case "fog":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardFog));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardFog));
                    img.setImageResource(R.drawable.fog);
                    changeFontColor();
                    changeIconColor();
                    break;
                case "partly-cloudy-day":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardPartlyDay));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardPartlyDay));
                    img.setImageResource(R.drawable.partly_cloudy_day);
                    break;
                case "partly-cloudy-night":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardPartlyNight));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardPartlyNight));
                    img.setImageResource(R.drawable.partly_cloudy_night);
                    break;
                case "rain":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardRain));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardRain));
                    img.setImageResource(R.drawable.rain);
                    break;
                case "sleet":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardSleet));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardSleet));
                    img.setImageResource(R.drawable.sleet);
                    changeFontColor();
                    changeIconColor();
                    break;
                case "snow":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardSnow));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardSnow));
                    img.setImageResource(R.drawable.snow);
                    changeFontColor();
                    changeIconColor();
                    break;
                case "wind":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardWind));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardWind));
                    img.setImageResource(R.drawable.wind);
                    break;
                default:
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                    cardDetails.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                    break;
            }
            fbIcon = currentObj.getString("icon") + ".png";
            String oneString = currentObj.getString("summary") + "\nin " + city + ", " + state;
            fcastText.setText(oneString);
            currentTemp.setText(convertToUnit("temperature"));
            fbDesc = currentObj.getString("summary") + ", " + convertToUnit("temperature");
            lowTemp.setText(convertToUnit("temperatureMin"));
            highTemp.setText(convertToUnit("temperatureMax"));
            precipitation.setText(convertToUnit("precipIntensity"));
            rain.setText(convertToUnit("precipProbability"));
            wind.setText(convertToUnit("windSpeed"));
            dewpoint.setText(convertToUnit("dewPoint"));
            humidity.setText(convertToUnit("humidity"));
            visibility.setText(convertToUnit("visibility"));
            sunrise.setText(convertToUnit("sunriseTime"));
            sunset.setText(convertToUnit("sunsetTime"));
        } catch (Throwable t) {
            Log.e(DEBUG_TAG, t.toString());
        }
    }

    public String convertToUnit(String variable) throws JSONException {
        switch (variable) {
            case "temperature":
                Double temp = currentObj.getDouble("temperature");
                if (!temp.isNaN()) {
                    if (measurement.equals("us"))
                        return temp.intValue() + "\u2109";
                    else if (measurement.equals("si"))
                        return temp.intValue() + "\u2103";
                } else
                    return "N.A.";
            case "precipIntensity":
                Double intensity = currentObj.getDouble("precipIntensity");
                if (!intensity.isNaN()) {
                    if (intensity < 0.002) {
                        return "None";
                    } else if (intensity <= 0.002 || intensity < 0.017) {
                        return "Very light";
                    } else if (intensity <= 0.017 || intensity < 0.1) {
                        return "Light";
                    } else if (intensity <= 0.1 || intensity < 0.4) {
                        return "Moderate";
                    } else if (intensity >= 0.4) {
                        return "Heavy";
                    }
                } else return "N.A.";
            case "precipProbability":
                Double probability = currentObj.getDouble("precipProbability");
                if (!probability.isNaN()) {
                    Double percent = probability * 100;
                    return percent.intValue() + "%";
                } else return "N.A.";
            case "windSpeed":
                Double speed = currentObj.getDouble("windSpeed");
                if (!speed.isNaN()) {
                    DecimalFormat twoDForm = new DecimalFormat("0.00");
                    if (measurement.equals("us"))
                        return twoDForm.format(speed) + "mph";
                    else if (measurement.equals("si"))
                        return twoDForm.format(speed) + "m/s";
                } else return "N.A.";
            case "dewPoint":
                Double dewPoint = currentObj.getDouble("dewPoint");
                if (!dewPoint.isNaN()) {
                    DecimalFormat twoDForm = new DecimalFormat("0.00");
                    if (measurement.equals("us"))
                        return twoDForm.format(dewPoint) + "\u2109";
                    else if (measurement.equals("si"))
                        return twoDForm.format(dewPoint) + "\u2103";
                } else return "N.A.";
            case "humidity":
                Double humid = currentObj.getDouble("humidity");
                if (!humid.isNaN()) {
                    Double percent = humid * 100;
                    return percent.intValue() + "%";
                } else return "N.A.";
            case "visibility":
                Double visible = currentObj.getDouble("visibility");
                if (!visible.isNaN()) {
                    DecimalFormat twoDForm = new DecimalFormat("#.00");
                    if (measurement.equals("us"))
                        return twoDForm.format(visible) + "mi";
                    else if (measurement.equals("si"))
                        return twoDForm.format(visible) + "km";
                } else return "N.A.";
            case "temperatureMin":
                Double tempMin = dailyArr.getJSONObject(0).getDouble("temperatureMin");
                if (!tempMin.isNaN()) {
                    if (measurement.equals("us"))
                        return tempMin.intValue() + "\u2109";
                    else if (measurement.equals("si"))
                        return tempMin.intValue() + "\u2103";
                } else
                    return "N.A.";
            case "temperatureMax":
                Double tempMax = dailyArr.getJSONObject(0).getDouble("temperatureMax");
                if (!tempMax.isNaN()) {
                    if (measurement.equals("us"))
                        return tempMax.intValue() + "\u2109";
                    else if (measurement.equals("si"))
                        return tempMax.intValue() + "\u2103";
                } else
                    return "N.A.";
            case "sunriseTime":
                Long timestampRise = dailyArr.getJSONObject(0).getLong("sunriseTime");
                if (timestampRise != 0) {
                    long time = timestampRise * (long) 1000;
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone(jsonObject.getString("timezone")));
                    return format.format(date);
                } else return "N.A.";
            case "sunsetTime":
                Long timestampSet = dailyArr.getJSONObject(0).getLong("sunsetTime");
                if (timestampSet != 0) {
                    long time = timestampSet * (long) 1000;
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone(jsonObject.getString("timezone")));
                    return format.format(date);
                } else return "N.A.";
            default:
                return "N.A.";
        }
    }
}