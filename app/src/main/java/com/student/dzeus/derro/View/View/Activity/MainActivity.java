package com.student.dzeus.derro.View.View.Activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.student.dzeus.derro.Base.Network.RetrofitClient;
import com.student.dzeus.derro.R;
import com.student.dzeus.derro.View.Model.Company;
import com.student.dzeus.derro.View.Model.LocationItem;
import com.student.dzeus.derro.View.Model.RouteResponse.RouteResponse;
import com.student.dzeus.derro.View.ViewModel.CompanyListViewModel;
import com.student.dzeus.derro.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, LocationListener, View.OnClickListener, PlaceSelectionListener {
    private final static String TAG = "pory";

    private static final float DEFAULT_ZOOM = 15f;
    private final int REQUEST_CODE_LOCATION = 1;

    //vars

    GoogleMap googleMap;

    ActivityMainBinding binding;
    CompanyListViewModel viewModel;
    LocationManager locationManager;

    LatLng closest;
    LatLng current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(CompanyListViewModel.class);


        binding.btnFind.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        initMap();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(this);

        String[] tester = {"All", "Wing", "Pipay", "Smartluy"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tester);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.appCompatSpinner.setAdapter(adapter);
        binding.appCompatSpinner.setOnItemSelectedListener(this);


    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
//        MoveCamera(new LatLng(11.555976, 104.905556));
        Log.d(TAG, " initializing map Ready");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);

//            googleMap.getUiSettings().setMyLocationButtonEnabled(false);


        viewModel.getListCompany().observe(this, companies -> {
            Log.d(TAG, "onChanged: " + companies.size());
            for (Company company : companies) {
                for (LocationItem locationItem : company.getLocation()) {
                    LatLng latLng = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
                    AddMarkerOnMap(latLng, locationItem.getName(), company.getCompany_id());
                    viewModel.getLatLngList().add(latLng);
                }
            }
        });


    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.marker_img);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerBitmapFromView(String url, final LatLng latLng, final String title) {
        CircularProgressDrawable loading = new CircularProgressDrawable(this);
        loading.setStrokeWidth(5f);
        loading.setCenterRadius(25);
        loading.start();

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.marker_img);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);

        return returnedBitmap;
    }


    private void AddMarkerOnMap(LatLng latLng, String title, int id) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title);
        markerOptions.position(latLng);
        if (id == 1) {
            markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.wing))));
        } else if (id == 2) {
            markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.pipay))));

        } else if (id == 3) {
            markerOptions.icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.smartluy))));

        }else{

        }
        this.googleMap.addMarker(markerOptions);
    }

    private void AddMarkerOnMap(LatLng latLng, String title, float alpha) {
        this.googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .alpha(alpha)

        );
    }

    private void MoveCamera(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void FillterMarker(int id) {

        Log.d(TAG, "FillterMarker: "+id);
        viewModel.getLatLngList().clear();

        if (viewModel.hasCompany()) {
                googleMap.clear();
                if (id == 0) {
                    for (Company company : viewModel.getListCompany().getValue()) {
                        for (LocationItem locationItem : company.getLocation()) {
                            LatLng latLng = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
                            AddMarkerOnMap(latLng, locationItem.getName(), company.getCompany_id());
                            viewModel.getLatLngList().add(latLng);

                        }
                    }
                } else {
                    for (LocationItem locationItem : viewModel.fillterCompany(id).getLocation()) {
                        LatLng latLng = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
                        AddMarkerOnMap(latLng, locationItem.getName(), locationItem.getCompanyId());
                        viewModel.getLatLngList().add(latLng);

                    }
                }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged: ");
        current = new LatLng(location.getLatitude(), location.getLongitude());
        MoveCamera(current);
//        locationManager.removeUpdates(this);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: ");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: ");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find:
//                Glide.with(this)
//                        .load("http://10.0.2.2:8000/storage/aa.png")
//                        .into(binding.test);
                Log.d(TAG, "onClick: fillter");
                getDistance(current,viewModel.getLatLngList().get(0));
                AddMarkerOnMap(FindCloestToCurrentLocation(viewModel.getLatLngList()),"Closet",4);
                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
        Log.i(TAG, "Place: " + place.toString());
        MoveCamera(place.getLatLng());
    }

    @Override
    public void onError(Status status) {
        // TODO: Handle the error.
        Log.i(TAG, "An error occurred: " + status);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        FillterMarker(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {

            loadUserLocation();
        }
    }

    private void loadUserLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_CODE_LOCATION);
            }
            return;
        }

        Task<Location> locationTask = locationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {
                    Log.d("pory", "onComplete: " + location.toString());
                    current = new LatLng(location.getLatitude(), location.getLongitude());
                    MoveCamera(current);
                }
            } else {
                Log.d("pory", "Error: " + task.getException());
            }
        });
    }


    private LatLng FindCloestToCurrentLocation(List<LatLng> latLngList){
        LatLng closet = new LatLng(0,0);
        float[] results = new float[1];
        float closetdistance= 100000000000000000f;
        for(LatLng latLng : latLngList){
            Location.distanceBetween(current.latitude, current.longitude,
                    latLng.latitude, latLng.longitude, results);
            if(closetdistance>results[0]){
                closetdistance = results[0];
                closet = latLng;
                Log.d(TAG, "FindCloestToCurrentLocation: closest found " + closet);
            }
        }

        return closet;
    }

    public void getDistance(LatLng src , LatLng des){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/directions/json?/json?origin=" + src.latitude + "," + src.longitude+ "&destination=" + des.latitude+ "," + des.longitude+ "&sensor=false&units=metric&mode=driving&key="+getResources().getString(R.string.google_maps_key);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Gson gson = new Gson();
                        RouteResponse routeResponse = gson.fromJson(response,RouteResponse.class);

                        //maybe try looping and send all latlng one by one and compare it
                        Log.d(TAG, "onResponse: "+routeResponse.getRoutes().get(0).getLegs().get(0).getDistance());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: "+error.getMessage());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void Test(){

    }

}
