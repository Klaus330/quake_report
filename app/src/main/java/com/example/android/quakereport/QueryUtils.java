package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

public final class QueryUtils {

    /** Sample JSON response for a USGS query */
    private static ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    public static String getJsonResponse(URL url) throws IOException {
        return new QueryUtils().makeHttpConnection(url);
    }

    public static URL createURL(String stringUrl) {
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e)
        {
            Log.e("URL", e.getMessage());
            return null;
        }
        return url;
    }


    public String makeHttpConnection(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;

    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return an {@link Earthquake} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    public static ArrayList<Earthquake> extractFeatureFromJson(String earthquakesJSON) {
        Log.i(LOG_TAG, "Loading Data fetched");
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakesJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakesJSON);
            JSONArray featureArray = baseJsonResponse.getJSONArray("features");

            // If there are results in the features array
            if (featureArray.length() > 0) {

                for(int i = 0; i < featureArray.length(); i++)
                {
                    // Extract out the first feature (which is an earthquake)
                    JSONObject firstFeature = featureArray.getJSONObject(i);
                    JSONObject properties = firstFeature.getJSONObject("properties");

                    // Extract out the title, time, and tsunami values
                    Double mag = properties.getDouble("mag");
                    String place = properties.getString("place");
                    Date time = new Date(properties.getInt("time"));

                    // Create a new {@link Event} object
                    earthquakes.add(new Earthquake(mag, place, time));
                }
                return  earthquakes;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }


}
