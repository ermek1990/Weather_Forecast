package kz.beam.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hamweather.aeris.communication.AerisEngine;

public class AerisMapActivity extends AppCompatActivity {

    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeris_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public String getLatitude() {
        Intent intent = getIntent();
        Bundle extraBundle = intent.getExtras();
        latitude = extraBundle.getString("latitude");
        return latitude;
    }

    public String getLongitude() {
        Intent intent = getIntent();
        Bundle extraBundle = intent.getExtras();
        longitude = extraBundle.getString("longitude");
        return longitude;
    }

    public void initMap(){
        AerisEngine.initWithKeys(this.getString(R.string.aeris_client_id), this.getString(R.string.aeris_client_secret), this);
    }
}
