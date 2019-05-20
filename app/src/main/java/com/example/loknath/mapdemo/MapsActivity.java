package com.example.loknath.mapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;

    public final int REQUEST_LOCATION_CODE = 99;

    // current location marker
    private Marker currentLocationMarker;

    //Location Search
    private EditText locationtext;
    private Button search;

    int PROXYMITY_RADIUS = 10000;

    //current Location
    double latitude,longitude;

    //Destination Location
    double end_latitude,end_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        search = findViewById(R.id.search);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if (client!= null){
                            buildGoogleApiClint();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this,"Permission Dined",Toast.LENGTH_LONG).show();
                }
                return;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClint();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);

    }

    public void onClick(View v){

        Object dataTransfer[] = new Object[2];
        GetNearByPlaceData getNearByPlaceData = new GetNearByPlaceData();
        switch (v.getId())
        {

            case R.id.search:
                locationtext = findViewById(R.id.location);
                String location = locationtext.getText().toString().trim();
                List<Address> addressList = null;
                MarkerOptions mo = new MarkerOptions();

                if (!location.equals("")){
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location,5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (int i=0;i<addressList.size();i++){
                        Address myAddress = addressList.get(i);
                        LatLng latLng = new LatLng(myAddress.getLatitude(),myAddress.getLongitude());
                        mo.position(latLng);
                        String duration = Dataperser.time;
                        mo.title("Desteni: ").snippet(duration);
                        mMap.addMarker(mo);
                        System.out.println("Desteni: "+duration);
                    }
                }
                end_latitude = mo.getPosition().latitude;
                end_longitude = mo.getPosition().longitude;
                break;
            case R.id.hospital:
                    mMap.clear();
                    String hospital = "hospital";
                    String url = getUrl(latitude, longitude, hospital);
                    dataTransfer[0] = mMap;
                     dataTransfer[1] = url;

                    getNearByPlaceData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this,"Showing Nearby Hospitals",Toast.LENGTH_LONG).show();
                    break;

            case R.id.restruant:
                mMap.clear();
                String restruant = "restruant";
                url = getUrl(latitude, longitude, restruant);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"Showing Nearby restruant",Toast.LENGTH_LONG).show();
                break;

            case R.id.school:
                //mMap.clear();
                String school = "school";
                url = getUrl(latitude, longitude, school);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this,"Showing Nearby school",Toast.LENGTH_LONG).show();
                break;

            case R.id.to:
               /**
                -----------------------Calculate Distance between to locations------------------
                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(end_latitude,end_longitude));
                markerOptions.title("Destination");
                markerOptions.draggable(true);

                float results[] = new float[10];
                Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results);
                markerOptions.snippet("Distance = " + results[0]);
                mMap.addMarker(markerOptions);
               ------------------------------------------------------------------------------------ */
               dataTransfer = new Object[3];
               url = getDirectionUrl();
                GetDirectionData getDirectionData = new GetDirectionData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(end_latitude,end_longitude);
                getDirectionData.execute(dataTransfer);
                break;
        }
    }

    private String getDirectionUrl(){
        System.out.print("lat: "+latitude+" "+"lan: "+longitude+" "+"end_lat: "+end_latitude+" "+end_longitude);
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin="+latitude+","+longitude);
        googleDirectionUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionUrl.append("&key="+"AIzaSyDEjgdweR1B-7IVy_9Gem0bsvDbo5dw4GY");  // google direction Api key

        return googleDirectionUrl.toString();

    }

    private String getUrl(double latitude,double longitude,String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXYMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyDEjgdweR1B-7IVy_9Gem0bsvDbo5dw4GY"); // google place api key

        return  googlePlaceUrl.toString();
    }

    protected synchronized void buildGoogleApiClint(){
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,this);
        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation = location;

        if (currentLocationMarker!=null){
            currentLocationMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);

        latitude= location.getLatitude();
        longitude = location.getLongitude();


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
       // end_latitude = marker.getPosition().latitude;
      //  end_longitude = marker.getPosition().longitude;
    }
}