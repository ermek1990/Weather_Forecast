package kz.beam.weatherforecast;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DaysFragment extends Fragment {
    static String DEBUG_TAG = "kz.beam.weatherforecast";
    String measurement;
    String timezone;
    JSONObject currentObj;
    JSONArray dailyArr = null;
    Integer i;
    View view;
    TextView fcastText1, lowTemp1, highTemp1, fcastText2, lowTemp2, highTemp2, fcastText3, lowTemp3, highTemp3,
            fcastText4, lowTemp4, highTemp4, fcastText5, lowTemp5, highTemp5, fcastText6, lowTemp6, highTemp6,
            fcastText7, lowTemp7, highTemp7, fcastDay1, fcastDay2, fcastDay3, fcastDay4, fcastDay5, fcastDay6, fcastDay7;
    ImageView imgCast1, imgCast2, imgCast3, imgCast4, imgCast5, imgCast6, imgCast7;
    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6, cardView7;

    public DaysFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_days, container, false);
        try {
            DetailsActivity activity = (DetailsActivity) getActivity();
            measurement = activity.getMeasurement();
            String jsonString = activity.getJSONStringFromActivity();
            JSONObject jsonObject = new JSONObject(jsonString);
            timezone = jsonObject.getString("timezone");
            dailyArr = jsonObject.getJSONObject("daily").optJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (dailyArr != null) {
            initiateTextViews();
            for (i = 1; i <= 7; i++) {
                currentObj = null;
                try {
                    currentObj = dailyArr.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (i) {
                    case 1:
                        insertValues(cardView1, fcastText1, fcastDay1, lowTemp1, highTemp1, imgCast1);
                        break;
                    case 2:
                        insertValues(cardView2, fcastText2, fcastDay2, lowTemp2, highTemp2, imgCast2);
                        break;
                    case 3:
                        insertValues(cardView3, fcastText3, fcastDay3, lowTemp3, highTemp3, imgCast3);
                        break;
                    case 4:
                        insertValues(cardView4, fcastText4, fcastDay4, lowTemp4, highTemp4, imgCast4);
                        break;
                    case 5:
                        insertValues(cardView5, fcastText5, fcastDay5, lowTemp5, highTemp5, imgCast5);
                        break;
                    case 6:
                        insertValues(cardView6, fcastText6, fcastDay6, lowTemp6, highTemp6, imgCast6);
                        break;
                    case 7:
                        insertValues(cardView7, fcastText7, fcastDay7, lowTemp7, highTemp7, imgCast7);
                        break;
                    default:
                        break;
                }
            }
        } else {
            /** TODO Implement return to Main Activity */
        }
        return view;
    }

    private void insertValues(CardView cardView, TextView fcastText, TextView fcastDay, TextView lowTemp, TextView highTemp, ImageView img) {
        try {
            String oneString = currentObj.getString("summary");
            /** Takes care of long summaries */
            if (oneString.length() >= 45) {
                if (Build.VERSION.SDK_INT < 23) {
                    fcastText.setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small);
                } else {
                    fcastText.setTextAppearance(android.R.style.TextAppearance_Small);
                }
            }
            fcastText.setText(oneString);
            switch (currentObj.getString("icon")) {
                case "clear-day":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                    img.setImageResource(R.drawable.clear_day);
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
                case "clear-night":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearNight));
                    img.setImageResource(R.drawable.clear_night);
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
                case "cloudy":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardCloudy));
                    img.setImageResource(R.drawable.cloudy);
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
                case "fog":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardFog));
                    img.setImageResource(R.drawable.fog);
                    changeFontColor(fcastText, fcastDay, lowTemp, highTemp);
                    break;
                case "partly-cloudy-day":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardPartlyDay));
                    img.setImageResource(R.drawable.partly_cloudy_day);
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
                case "partly-cloudy-night":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardPartlyNight));
                    img.setImageResource(R.drawable.partly_cloudy_night);
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
                case "rain":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardRain));
                    img.setImageResource(R.drawable.rain);
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
                case "sleet":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardSleet));
                    img.setImageResource(R.drawable.sleet);
                    changeFontColor(fcastText, fcastDay, lowTemp, highTemp);
                    break;
                case "snow":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardSnow));
                    img.setImageResource(R.drawable.snow);
                    changeFontColor(fcastText, fcastDay, lowTemp, highTemp);
                    break;
                case "wind":
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardWind));
                    img.setImageResource(R.drawable.wind);
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
                default:
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                    if (oneString.length() >= 45) {
                        fcastText.setTextColor(getResources().getColor(R.color.colorTextInverse));
                    }
                    break;
            }
            fcastDay.setText(convertToUnit("time"));
            lowTemp.setText(convertToUnit("temperatureMin"));
            highTemp.setText(convertToUnit("temperatureMax"));
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
            case "time":
                Long timestampRise = currentObj.getLong("time");
                if (timestampRise != 0) {
                    long time = timestampRise * (long) 1000;
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone(timezone));
                    return format.format(date);
                } else return "N.A.";
            case "temperatureMin":
                Double tempMin = currentObj.getDouble("temperatureMin");
                if (!tempMin.isNaN()) {
                    if (measurement.equals("us"))
                        return tempMin.intValue() + "\u2109";
                    else if (measurement.equals("si"))
                        return tempMin.intValue() + "\u2103";
                } else
                    return "N.A.";
            case "temperatureMax":
                Double tempMax = currentObj.getDouble("temperatureMax");
                if (!tempMax.isNaN()) {
                    if (measurement.equals("us"))
                        return tempMax.intValue() + "\u2109";
                    else if (measurement.equals("si"))
                        return tempMax.intValue() + "\u2103";
                } else
                    return "N.A.";
            default:
                return "N.A.";
        }
    }

    private void initiateTextViews() {
        cardView1 = (CardView) view.findViewById(R.id.cardView1);
        cardView2 = (CardView) view.findViewById(R.id.cardView2);
        cardView3 = (CardView) view.findViewById(R.id.cardView3);
        cardView4 = (CardView) view.findViewById(R.id.cardView4);
        cardView5 = (CardView) view.findViewById(R.id.cardView5);
        cardView6 = (CardView) view.findViewById(R.id.cardView6);
        cardView7 = (CardView) view.findViewById(R.id.cardView7);
        fcastText1 = (TextView) view.findViewById(R.id.fcast_text1);
        fcastDay1 = (TextView) view.findViewById(R.id.fcast_day1);
        lowTemp1 = (TextView) view.findViewById(R.id.low_temp1);
        highTemp1 = (TextView) view.findViewById(R.id.high_temp1);
        fcastText2 = (TextView) view.findViewById(R.id.fcast_text2);
        fcastDay2 = (TextView) view.findViewById(R.id.fcast_day2);
        lowTemp2 = (TextView) view.findViewById(R.id.low_temp2);
        highTemp2 = (TextView) view.findViewById(R.id.high_temp2);
        fcastText3 = (TextView) view.findViewById(R.id.fcast_text3);
        fcastDay3 = (TextView) view.findViewById(R.id.fcast_day3);
        lowTemp3 = (TextView) view.findViewById(R.id.low_temp3);
        highTemp3 = (TextView) view.findViewById(R.id.high_temp3);
        fcastText4 = (TextView) view.findViewById(R.id.fcast_text4);
        fcastDay4 = (TextView) view.findViewById(R.id.fcast_day4);
        lowTemp4 = (TextView) view.findViewById(R.id.low_temp4);
        highTemp4 = (TextView) view.findViewById(R.id.high_temp4);
        fcastText5 = (TextView) view.findViewById(R.id.fcast_text5);
        fcastDay5 = (TextView) view.findViewById(R.id.fcast_day5);
        lowTemp5 = (TextView) view.findViewById(R.id.low_temp5);
        highTemp5 = (TextView) view.findViewById(R.id.high_temp5);
        fcastText6 = (TextView) view.findViewById(R.id.fcast_text6);
        fcastDay6 = (TextView) view.findViewById(R.id.fcast_day6);
        lowTemp6 = (TextView) view.findViewById(R.id.low_temp6);
        highTemp6 = (TextView) view.findViewById(R.id.high_temp6);
        fcastText7 = (TextView) view.findViewById(R.id.fcast_text7);
        fcastDay7 = (TextView) view.findViewById(R.id.fcast_day7);
        lowTemp7 = (TextView) view.findViewById(R.id.low_temp7);
        highTemp7 = (TextView) view.findViewById(R.id.high_temp7);
        imgCast1 = (ImageView) view.findViewById(R.id.image_weather1);
        imgCast2 = (ImageView) view.findViewById(R.id.image_weather2);
        imgCast3 = (ImageView) view.findViewById(R.id.image_weather3);
        imgCast4 = (ImageView) view.findViewById(R.id.image_weather4);
        imgCast5 = (ImageView) view.findViewById(R.id.image_weather5);
        imgCast6 = (ImageView) view.findViewById(R.id.image_weather6);
        imgCast7 = (ImageView) view.findViewById(R.id.image_weather7);
    }

    private void changeFontColor(TextView textView1, TextView textView2, TextView textView3, TextView textView4) {
        textView1.setTextColor(getResources().getColor(R.color.colorText));
        textView2.setTextColor(getResources().getColor(R.color.colorText));
        textView3.setTextColor(getResources().getColor(R.color.colorText));
        textView4.setTextColor(getResources().getColor(R.color.colorText));
    }
}
