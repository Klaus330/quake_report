package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    private ArrayList<Earthquake> earthquakes;
    private TextView magnitude;
    private TextView primaryLocation;
    private TextView locationOffset;
    private TextView date;

    public EarthquakeAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Earthquake> list) {
        super(context, 0, list);
        this.earthquakes = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View listItemView, @NonNull ViewGroup parent) {

        if(listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);

        Earthquake currentEarthquake = earthquakes.get(position);

        magnitude = listItemView.findViewById(R.id.magnitude);
        primaryLocation = listItemView.findViewById(R.id.primar_location);
        date = listItemView.findViewById(R.id.date);
        locationOffset = listItemView.findViewById(R.id.location_offset);

        setMagnitude(currentEarthquake);

        parseStringLocation(currentEarthquake);
        date.setText(currentEarthquake.getDate());

        return listItemView;
    }

    private void setMagnitude(Earthquake currentEarthquake) {
        magnitude.setText(formatMagnitude(currentEarthquake));
        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudefloor = (int) Math.floor(magnitude);
        switch (magnitudefloor){
            case 1:
                return ContextCompat.getColor(getContext(), R.color.magnitude1);
            case 2:
                return ContextCompat.getColor(getContext(), R.color.magnitude2);
            case 3:
                return ContextCompat.getColor(getContext(), R.color.magnitude3);
            case 4:
                return ContextCompat.getColor(getContext(), R.color.magnitude4);
            case 5:
                return ContextCompat.getColor(getContext(), R.color.magnitude5);
            case 6:
                return ContextCompat.getColor(getContext(), R.color.magnitude6);
            case 7:
                return ContextCompat.getColor(getContext(), R.color.magnitude7);
            case 8:
                return ContextCompat.getColor(getContext(), R.color.magnitude8);
            case 9:
                return ContextCompat.getColor(getContext(), R.color.magnitude9);
            default:
                return ContextCompat.getColor(getContext(), R.color.magnitude10plus);
        }
    }

    private String formatMagnitude(Earthquake currentEarthquake) {
        return new DecimalFormat("0.0").format(currentEarthquake.getMagnitude());
    }

    private void parseStringLocation(Earthquake earthquake) {
        String location = earthquake.getLocation();

        if(location.contains("of")) {
            String[] strings = location.split("of");
            locationOffset.setText(strings[0]+" of");
            primaryLocation.setText(strings[1]);
        }else {
            locationOffset.setVisibility(View.GONE);
            primaryLocation.setText(location);
        }
    }
}
