package com.example.loknath.mapdemo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dataperser {
public static String time;
    private HashMap<String, String>getDuration(JSONArray googleDirectionJson){
        HashMap<String,String> googleDirectionMap = new HashMap<>();
        String duration = "";
        String distance = "";

        Log.d("json responce", googleDirectionJson.toString());
        try {
            duration = googleDirectionJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionMap.put("duration", duration);
            googleDirectionMap.put("distance",distance);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleDirectionMap;

    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson){

        HashMap<String,String > googlePlaceMap = new HashMap<>();

        String placeName = "--NA--";
        String vicinity = "--NA--";
        String latitude = "";
        String longitude = "";
        String reference = "";

        Log.d("DataParser:","jsonobject ="+googlePlaceJson.toString());

        try {
            if (!googlePlaceJson.isNull("name")){
                placeName = (String) googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")){
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray){
        int count = jsonArray.length();
        List<HashMap<String,String>> placeList = new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for (int i=0;i<count;i++){
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placeList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placeList;
    }

    public List<HashMap<String,String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    public String[] parseDirections(String jsonData){

        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonData);
            System.out.println("jsonObject :"+jsonObject.toString());
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            time=jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    public String[] getPaths(JSONArray googleStepJson){
        int count = googleStepJson.length();
        System.out.println(" length ==> " + count);
        String[] polyline = new String[count];

        for (int i=0;i<count;i++){
            try {
                polyline[i] = getPath(googleStepJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polyline;
    }

    public String getPath(JSONObject googlePathJSON){
        String Polyline = "";
        try {
             Polyline = googlePathJSON.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Polyline;
    }

}
