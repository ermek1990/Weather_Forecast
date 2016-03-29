package kz.beam.weatherforecast;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HoursFragment extends Fragment {
    String measurement;
    String timezone;
    JSONArray hourArr = null;
    JSONObject currentObj;
    View view;

    public HoursFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hours, container, false);
        try {
            DetailsActivity activity = (DetailsActivity) getActivity();
            measurement = activity.getMeasurement();
            String jsonString = activity.getJSONStringFromActivity();
            JSONObject jsonObject = new JSONObject(jsonString);
            hourArr = jsonObject.getJSONObject("hourly").optJSONArray("data");
            timezone = jsonObject.getString("timezone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (hourArr != null) {
            final TableLayout tableLayout = (TableLayout) view.findViewById(R.id.hours_table);
            int i = 0; // Some counter to keep track of the index position
            while(i < hourArr.length()){
                TableRow row= new TableRow(this.getContext());
                TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(rowParams);
                row.setPadding(50, 50, 50, 50);
                ImageView img = new ImageView(this.getContext());
                TextView time = new TextView(this.getContext());
                TextView temp = new TextView(this.getContext());
                TextView summary = new TextView(this.getContext());
                currentObj = null;
                try {
                    currentObj = hourArr.getJSONObject(i);
                    row.addView(img);
                    time.setText(convertToUnit("time"));
                    temp.setText(convertToUnit("temperature"));
                    summary.setText(currentObj.getString("summary"));
                    if (Build.VERSION.SDK_INT < 23) {
                        time.setTextAppearance(this.getContext(), android.R.style.TextAppearance_Large);
                        temp.setTextAppearance(this.getContext(), android.R.style.TextAppearance_Large);
                        summary.setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small);
                    } else {
                        time.setTextAppearance(android.R.style.TextAppearance_Large);
                        temp.setTextAppearance(android.R.style.TextAppearance_Large);
                        summary.setTextAppearance(android.R.style.TextAppearance_Small);
                    }
                    time.setTypeface(null, Typeface.BOLD);
                    time.setPadding(130, 0, 70, 0);
                    time.setTypeface(null, Typeface.NORMAL);
                    summary.setPadding(130, 0, 70, 0);
                    temp.setTypeface(null, Typeface.BOLD);
                    temp.setPadding(10, 30, 10, 30);
                    switch (currentObj.getString("icon")) {
                        case "clear-day":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            img.setImageResource(R.drawable.clear_day);
                            break;
                        case "clear-night":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardClearNight));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            img.setImageResource(R.drawable.clear_night);
                            break;
                        case "cloudy":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardCloudy));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            img.setImageResource(R.drawable.cloudy);
                            break;
                        case "fog":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardFog));
                            time.setTextColor(getResources().getColor(R.color.colorText));
                            temp.setTextColor(getResources().getColor(R.color.colorText));
                            summary.setTextColor(getResources().getColor(R.color.colorText));
                            img.setImageResource(R.drawable.fog);
                            break;
                        case "partly-cloudy-day":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardPartlyDay));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            img.setImageResource(R.drawable.partly_cloudy_day);
                            break;
                        case "partly-cloudy-night":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardPartlyNight));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            img.setImageResource(R.drawable.partly_cloudy_night);
                            break;
                        case "rain":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardRain));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            img.setImageResource(R.drawable.rain);
                            break;
                        case "sleet":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardSleet));
                            img.setImageResource(R.drawable.sleet);
                            time.setTextColor(getResources().getColor(R.color.colorText));
                            temp.setTextColor(getResources().getColor(R.color.colorText));
                            summary.setTextColor(getResources().getColor(R.color.colorText));
                            break;
                        case "snow":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardSnow));
                            time.setTextColor(getResources().getColor(R.color.colorText));
                            temp.setTextColor(getResources().getColor(R.color.colorText));
                            summary.setTextColor(getResources().getColor(R.color.colorText));
                            img.setImageResource(R.drawable.snow);
                            break;
                        case "wind":
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardWind));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            img.setImageResource(R.drawable.wind);
                            break;
                        default:
                            row.setBackgroundColor(getResources().getColor(R.color.colorCardClearDay));
                            time.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            temp.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            summary.setTextColor(getResources().getColor(R.color.colorTextInverse));
                            break;
                    }
                    LinearLayout timeLayout = new LinearLayout(this.getContext());
                    timeLayout.setOrientation(LinearLayout.VERTICAL);
                    timeLayout.addView(time);
                    timeLayout.addView(summary);
                    row.addView(timeLayout);
                    row.addView(temp);
                    img.getLayoutParams().height = 150;
                    img.getLayoutParams().width = 150;
                    if (i < 23) {
                        tableLayout.addView(row, i);
                    } else if (i == 23) {
                        tableLayout.addView(row, i);
                        final TableRow btnRow= new TableRow(this.getContext());
                        btnRow.setLayoutParams(rowParams);
                        btnRow.setPadding(50, 20, 50, 20);
                        ImageView fakeImage = new ImageView(this.getContext());
                        btnRow.addView(fakeImage);
                        fakeImage.getLayoutParams().height = 150;
                        fakeImage.getLayoutParams().width = 150;
                        final Button expandr = new Button(this.getContext());
                        expandr.setBackgroundColor(getResources().getColor(R.color.colorSearch));
                        expandr.setText(getResources().getString(R.string.load));
                        expandr.setTextColor(getResources().getColor(R.color.colorTextInverse));
                        expandr.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                int k = 0;
                                while (k < tableLayout.getChildCount()) {
                                    if (k > 23){
                                        TableRow tableRow = (TableRow) tableLayout.getChildAt(k);
                                        tableRow.setVisibility(View.VISIBLE);
                                    }
                                    k++;
                                }
                                btnRow.setVisibility(View.GONE);
                            }
                        });
                        btnRow.addView(expandr);
                        tableLayout.addView(btnRow, i + 1);
                    } else if (i > 23) {
                        row.setVisibility(View.GONE);
                        tableLayout.addView(row, i + 1);
                    }
                    i++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            /** TODO Implement return to Main Activity */
        }
        return view;
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
                    SimpleDateFormat format = new SimpleDateFormat("ha (EEE)", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone(timezone));
                    return format.format(date);
                } else return "N.A.";
            default:
                return "N.A.";
        }
    }
}