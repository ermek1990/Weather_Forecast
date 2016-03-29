package kz.beam.weatherforecast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    String DEBUG_TAG = "kz.beam.weatherforecast", measurement, instState, city;
    private RadioButton rdio_fahr, rdio_cels;
    private EditText edit_street, edit_city;
    private Spinner spinner_states;
    private ProgressBar progressSpin;
    private FragmentDrawer drawerFragment;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        final FloatingActionButton floatingBtn = (FloatingActionButton) findViewById(R.id.floatingBtn);
        floatingBtn.hide();
        progressSpin = (ProgressBar) findViewById(R.id.progressSpinner);
        progressSpin.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(700);
        }
        alertDialog = new AlertDialog.Builder(this);
        spinner_states = (Spinner) findViewById(R.id.spinner_states);
        /** Replaced this to make the dropdown list with Inverse text color
         *  final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, android.R.layout.simple_spinner_item);*/
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_states.setAdapter(adapter);
        spinner_states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    instState = adapter.getItem(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                instState = "";
            }
        });
        rdio_fahr = (RadioButton) findViewById(R.id.radio_fahrenheit);
        rdio_fahr.setChecked(true);
        measurement = "us";
        rdio_cels = (RadioButton) findViewById(R.id.radio_celsius);
        Button btn_srch = (Button) findViewById(R.id.button_search);
        Button btn_clr = (Button) findViewById(R.id.button_clear);
        edit_street = (EditText) findViewById(R.id.edit_street);
        edit_city = (EditText) findViewById(R.id.edit_city);
        edit_street.requestFocus();
        btn_srch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (edit_street.getText().toString().trim().isEmpty()) {
                    alertDialog.setTitle("App Notification")
                            .setMessage("Please enter a street address")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    edit_street.requestFocus();
                                }
                            })
                            .setIcon(R.drawable.ic_action)
                            .show();
                } else if (edit_city.getText().toString().trim().isEmpty()) {
                    alertDialog.setTitle("App Notification")
                            .setMessage("Please enter the city name")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    edit_city.requestFocus();
                                }
                            })
                            .setIcon(R.drawable.ic_action)
                            .show();
                } else if (spinner_states.getSelectedItemPosition() == 0) {
                    alertDialog.setTitle("App Notification")
                            .setMessage("Please select state from the list")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    spinner_states.requestFocusFromTouch();
                                }
                            })
                            .setIcon(R.drawable.ic_action)
                            .show();
                } else {
                    if (checkConnection()) {
                        progressSpin.setVisibility(View.VISIBLE);
                        floatingBtn.hide();
                        city = edit_city.getText().toString().trim();
                        String queryParams = "?street_address=" + encodeParams(edit_street.getText().toString().trim()) + "&city_name=" +
                                encodeParams(edit_city.getText().toString().trim()) + "&state_name=" + encodeParams(spinner_states.getSelectedItem().toString())
                                + "&measurement=" + measurement;
                        new requestJSONTask().execute(getString(R.string.request) + queryParams);
                    } else {
                        alertDialog.setTitle("App Notification")
                                .setMessage("No internet connection. Would you like to go to WIFI settings to connect to a network?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        floatingBtn.setImageResource(R.drawable.ic_wifi_settings);
                                        floatingBtn.show();
                                        floatingBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                })
                                .setIcon(R.drawable.ic_network)
                                .show();
                    }
                }
            }
        });
        btn_clr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                edit_street.setText("");
                edit_city.setText("");
                spinner_states.setSelection(0);
                rdio_fahr.setChecked(true);
                edit_street.requestFocus();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /** Launches About Developer Activity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_dev) {
            Intent newIntent = new Intent(MainActivity.this, AboutDevActivity.class);
            MainActivity.this.startActivity(newIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Changes the state of the $measurement on a RadioButton Click */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radio_fahrenheit:
                if (checked)
                    measurement = "us";
                break;
            case R.id.radio_celsius:
                if (checked)
                    measurement = "si";
                break;
        }
    }

    /** Checks the connection before sending requests to the server */
    public boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /** Encodes Parameters of URI for further sending to server */
    public String encodeParams(String query) {
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private class requestJSONTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return requestUrl(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        /** Does Some Work with Retrieved JSON */
        /** Opens a new Activity and sends json string with some data to the new activity */
        @Override
        protected void onPostExecute(String result) {
            Intent newIntent = new Intent(MainActivity.this, ResultActivity.class);
            Bundle extraBundle = new Bundle();
            extraBundle.putString("jsonObject", result);
            extraBundle.putString("measurement", measurement);
            extraBundle.putString("city", city);
            extraBundle.putString("state", instState);
            newIntent.putExtras(extraBundle);
            progressSpin.setVisibility(View.GONE);
            MainActivity.this.startActivity(newIntent);
        }
    }

    /** Requests from the sever (url_string) */
    private String requestUrl(final String url_string) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = new URL(url_string);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            inputStream = connection.getInputStream();
            return convertStreamToString(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /** Converts InputStream to a String */
    private String convertStreamToString(InputStream inputStreams) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreams));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStreams.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Intent newIntent = null;
        switch (position) {
            case 0:
                newIntent = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(newIntent);
                break;
            case 1:
                newIntent = new Intent(MainActivity.this, CreditsActivity.class);
                MainActivity.this.startActivity(newIntent);
                break;
            case 2:
                newIntent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(newIntent);
                break;
            case 3:
                alertDialog.setTitle("App Notification")
                        .setMessage("This feature has not been implemented yet. Please, check back later.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_action)
                        .show();
                break;
            case 4:
                newIntent = new Intent(MainActivity.this, AboutDevActivity.class);
                MainActivity.this.startActivity(newIntent);
                break;
            default:
                break;
        }
    }
}