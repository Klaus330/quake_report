/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 1;
    public static final String URL_LINK = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    EarthquakeAdapter adapter;
    ListView earthquakeListView;
    TextView emptyView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        initializeViews();

        setUpListView();

        getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID,null,this);
    }

    private void setUpListView() {
        adapter = new EarthquakeAdapter(
                EarthquakeActivity.this, android.R.layout.simple_list_item_1, new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri parsedUri = Uri.parse("https://www.claudiupopa.ro");
                Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = findViewById(R.id.list);
        emptyView = findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(emptyView);
        progressBar = findViewById(R.id.progress_bar);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new EarthquakeLoader(this, buildUrl().toString());
    }

    private Uri.Builder buildUrl() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(URL_LINK);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("format", "geojson");
        builder.appendQueryParameter("limit", "10");
        builder.appendQueryParameter("minmag", minMagnitude);
        builder.appendQueryParameter("orderby", orderBy);
        return builder;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
        progressBar.setVisibility(View.GONE);


        if(!isInternetAvailable())
            emptyView.setText("No internet connection!");
        else
            emptyView.setText(R.string.no_earthquakes);

        if(earthquakes != null && ! earthquakes.isEmpty())
        {
            adapter.clear();
            adapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i(LOG_TAG, " Loader Reseted");
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
