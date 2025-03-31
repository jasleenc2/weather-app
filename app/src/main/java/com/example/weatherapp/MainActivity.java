package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.run_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipCode = ((EditText)findViewById(R.id.zip_et)).getText().toString();
                if(zipCode != null && zipCode.length() == 5){
                    //valid imput
                    callApiToGetLatLon(zipCode);
                }
                else{
                    //invalid or incomplete imput
                    Toast.makeText(MainActivity.this, "enter a valid zipcode", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callApiToGetLatLon(String zipCode) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.openweathermap.org/geo/1.0/zip?zip="+zipCode+",US&appid=85ebddb24b1d3d95991815eaf82b3199";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject obj = new JSONObject(response);
                            Double lat = obj.getDouble("lat");
                            Double lon = obj.getDouble("lon");
                            String name = obj.getString("name");
                            callApiToGetWeather(lat, lon, name);
                            TextView textView = findViewById(R.id.id_latlon);
                            //textView.setText(" " + response);
                            textView.setText("lat = " + lat + ", lon = " + lon + " city/town: " + name);
                            //Toast.makeText(MainActivity.this, "lat = " + lat + ", lon = " + lon, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "error, enter valid zipcode " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error, enter valid zipcode" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void callApiToGetWeather(Double lat, Double lon, String name) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&appid=85ebddb24b1d3d95991815eaf82b3199";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("hourly");

                            ImageView firstIV = findViewById(R.id.first_hour_iv);
                            TextView firstDescTV = findViewById(R.id.first_hour_desc_tv);
                            TextView firstTempTv = findViewById(R.id.first_hour_temp_tv);
                            String icon1 = getImageUrl(array.getJSONObject(0));
                            JSONObject firstWeather = getWeatherObject(array.getJSONObject(0));
                            new ImageLoadTask(icon1, firstIV).execute();
                            firstDescTV.setText(firstWeather.getString("description"));
                            firstTempTv.setText(getString(R.string.temp) + " : " + array.getJSONObject(0).getString("temp"));

                            ImageView secondIV = findViewById(R.id.second_hour_iv);
                            TextView secondDescTV = findViewById(R.id.second_hour_desc_tv);
                            TextView secondTempTv = findViewById(R.id.second_hour_temp_tv);
                            JSONObject second = array.getJSONObject(1);
                            String icon2 = getImageUrl(second);
                            JSONObject secondWeather = getWeatherObject(second);
                            new ImageLoadTask(icon2, secondIV).execute();
                            secondDescTV.setText(secondWeather.getString("description"));
                            secondTempTv.setText(getString(R.string.temp) + " : " + second.getString("temp"));

                            ImageView thirdIV = findViewById(R.id.third_hour_iv);
                            TextView thirdDescTV = findViewById(R.id.third_hour_desc_tv);
                            TextView thirdTempTv = findViewById(R.id.third_hour_temp_tv);
                            JSONObject third = array.getJSONObject(2);
                            String icon3 = getImageUrl(third);
                            JSONObject thirdWeather = getWeatherObject(third);
                            new ImageLoadTask(icon3, thirdIV).execute();
                            thirdDescTV.setText(thirdWeather.getString("description"));
                            thirdTempTv.setText(getString(R.string.temp) + " : " + third.getString("temp"));

                            ImageView fourthIV = findViewById(R.id.fourth_hour_iv);
                            TextView fourthDescTV = findViewById(R.id.fourth_hour_desc_tv);
                            TextView fourthTempTv = findViewById(R.id.fourth_hour_temp_tv);
                            JSONObject fourth = array.getJSONObject(3);
                            String icon4 = getImageUrl(fourth);
                            JSONObject fourthWeather = getWeatherObject(fourth);
                            new ImageLoadTask(icon4, fourthIV).execute();
                            fourthDescTV.setText(fourthWeather.getString("description"));
                            fourthTempTv.setText(getString(R.string.temp) + " : " + fourth.getString("temp"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "error, enter valid zipcode " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error, enter valid zipcode " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private String getImageUrl(JSONObject firstHour) {
        String icon = "";
        if(firstHour != null){
            try {
                JSONObject object = getWeatherObject(firstHour);
                if(object != null){
                    icon = "https://openweathermap.org/img/wn/" + object.getString("icon") + ".png";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return icon;
    }

    private JSONObject getWeatherObject(JSONObject firstHour) throws JSONException {
        JSONArray array = firstHour.getJSONArray("weather");
        JSONObject object = array.getJSONObject(0);
        return object;
    }
}