package com.example.loknath.mapdemo;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetDirectionData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googleDirectionData;
    String duration, distance;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng) objects[2];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionData =  downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionData;
    }

    @Override
    protected void onPostExecute(String s) {

        String[] directionsList;
        Dataperser perser = new Dataperser();
        directionsList = perser.parseDirections(s);
        displayDirection(directionsList);

    }

    private void displayDirection(String[] directionsList) {
        int count = directionsList.length;
        for(int i=0;i<count;i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(options);
            System.out.println(" Dataperser.time "+Dataperser.time);

        }
    }

}
