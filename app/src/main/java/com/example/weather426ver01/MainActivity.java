package com.example.weather426ver01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Locale;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    TextView cityName;
    TextView currTemp;
    TextView currWeather;

    TextView currWeatherClimacon;
    TextView highTemp;

    TextView lowTemp;

    TextView dayOne;

    TextView dayOneClimacon;

    TextView dayTwo;

    TextView dayTwoClimacon;

    TextView dayThree;

    TextView dayThreeClimacon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.textView14);
        currTemp = (TextView) findViewById(R.id.textView3);
        currWeather = (TextView) findViewById(R.id.textView4);
        currWeatherClimacon = (TextView) findViewById(R.id.textView9);

        highTemp = (TextView) findViewById(R.id.textView6);
        lowTemp = (TextView) findViewById(R.id.textView7);

        dayOne = (TextView) findViewById(R.id.textView10);
        dayOneClimacon = (TextView) findViewById(R.id.textView13);
        dayTwo = (TextView) findViewById(R.id.textView11);
        dayTwoClimacon = (TextView) findViewById(R.id.textView16);
        dayThree = (TextView) findViewById(R.id.textView12);
        dayThreeClimacon = (TextView) findViewById(R.id.textView15);

        fetchWeatherData();
    }

    public void fetchWeatherData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Ypsilanti&appid=ac5088d78ea1228b6096540fc432c460");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String response = readStream(in);
                    JSONObject jsonResponse = new JSONObject(response);

                    double temp = jsonResponse.getJSONObject("main").getDouble("temp");
                    String weather = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("main");

                    String cityNameStr = jsonResponse.getString("name");
                    double tempMax = jsonResponse.getJSONObject("main").getDouble("temp_max");
                    double tempMin = jsonResponse.getJSONObject("main").getDouble("temp_min");



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double tempFahrenheit = (temp - 273.15) * 9/5 + 32;
                            long roundedTempFahrenheit = (int) Math.round(tempFahrenheit);
                            currTemp.setText(String.format(Locale.getDefault(), "%d°F", roundedTempFahrenheit));

                            double tempMaxFahrenheit = (tempMax - 273.15) * 9/5 + 32;
                            double tempMinFahrenheit = (tempMin - 273.15) * 9/5 + 32;

                            int roundedMaxTempFahrenheit = (int) Math.round(tempMaxFahrenheit);
                            int roundedMinTempFahrenheit = (int) Math.round(tempMinFahrenheit);

                            highTemp.setText(String.format(Locale.getDefault(), "H %d°", roundedMaxTempFahrenheit));
                            lowTemp.setText(String.format(Locale.getDefault(), "L %d°", roundedMinTempFahrenheit));

                            currWeather.setText(weather);


                            char weatherIconChar = mapWeatherConditionToIcon(weather);
                            currWeatherClimacon.setText(String.valueOf(weatherIconChar));


                            cityName.setText(cityNameStr);
                        }
                    });
                    fetchForecastData();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    private String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String nextLine = "";
        while ((nextLine = reader.readLine()) != null) {
            sb.append(nextLine);
        }
        return sb.toString();
    }

    private char mapWeatherConditionToIcon(String weatherCondition) {
        switch (weatherCondition) {
            case "Clouds":
                return '!';
            case "Clear":
                return 'I';
            case "Rain":
                return '$';
            case "Drizzle":
                return '-';
            case "Thunderstorm":
                return 'F';
            case "Snow":
                return '9';
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Ash":
                return '<';
            case "Squall":
            case "Tornado":
                return 'X';

            case "Cloudy Night":
                return '#';
            case "Cloudy Day":
                return '"';
            case "Rainy Night":
                return '&';
            case "Rainy Day":
                return '%';
            case "Snowy Night":
                return ';';
            case "Snowy Day":
                return ':';
            case "Sleet Night":
                return '2';
            case "Sleet Day":
                return '1';
            case "Hail Night":
            case "Hail Day":
                return '3';
            case "Flurries Night":
                return '8';
            case "Flurries Day":
                return '7';
            case "Sunny":
                return 'I';
            case "Moon":
                return 'N';
            case "Foggy Night":
            case "Foggy Day":
                return '<';
            case "Windy":
                return 'B';
            case "Hot":
                return ']';
            case "Cold":
                return '[';
            default:
                return 'I';
        }
    }

    public void fetchForecastData() {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            try {

                URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?q=Ypsilanti&appid=ac5088d78ea1228b6096540fc432c460&units=imperial");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = readStream(in);
                JSONObject jsonResponse = new JSONObject(response);


                JSONArray list = jsonResponse.getJSONArray("list");

                for (int i = 8; i < list.length(); i += 8) {
                    JSONObject forecast = list.getJSONObject(i);
                    long timestamp = forecast.getLong("dt");
                    String mainWeather = forecast.getJSONArray("weather").getJSONObject(0).getString("main");

                    String dayName = getDayNameFromTimestamp(timestamp);
                    char weatherIcon = mapWeatherConditionToIcon(mainWeather);

                    // Adjust dayIndex calculation since we're starting from tomorrow
                    int dayIndex = (i / 8);
                    runOnUiThread(() -> {
                        switch (dayIndex) {
                            case 1: // Now corresponds to tomorrow
                                dayOne.setText(dayName);
                                dayOneClimacon.setText(String.valueOf(weatherIcon));
                                break;
                            case 2:
                                dayTwo.setText(dayName);
                                dayTwoClimacon.setText(String.valueOf(weatherIcon));
                                break;
                            case 3:
                                dayThree.setText(dayName);
                                dayThreeClimacon.setText(String.valueOf(weatherIcon));
                                break;
                        }
                    });

                    if (dayIndex == 3) break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }).start();
    }

    private String getDayNameFromTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000);
        return new SimpleDateFormat("EEE", Locale.getDefault()).format(cal.getTime());
    }

}

