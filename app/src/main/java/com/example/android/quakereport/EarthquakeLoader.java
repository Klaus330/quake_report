package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

public class EarthquakeLoader extends AsyncTaskLoader {
    ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
    String url;

    public EarthquakeLoader(Context context, String urlLink) {
        super(context);
        this.url = urlLink;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, " On start loading called");
        forceLoad();
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        Log.i(LOG_TAG, " Loading in background called");

        if(this.url == null)
            return null;

        URL url = QueryUtils.createURL(this.url);

        String jsonResponse = "";
        try{
            jsonResponse = QueryUtils.getJsonResponse(url);

        }catch (IOException e)
        {
            Log.e("HTTP", e.getMessage());
        }

        earthquakes = QueryUtils.extractFeatureFromJson(jsonResponse);

        return earthquakes;
    }
}
