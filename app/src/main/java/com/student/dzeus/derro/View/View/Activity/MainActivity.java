package com.student.dzeus.derro.View.View.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener,OnMapReadyCallback, AdapterView.OnItemSelectedListener, LocationListener, View.OnClickListener, PlaceSelectionListener {
    private final static String TAG = "pory";

    private static final float DEFAULT_ZOOM = 15f;
    private final int REQUEST_CODE_LOCATION = 1;

    //vars

    GoogleMap googleMap;

    ActivityMainBinding binding;
    CompanyListViewModel viewModel;
    LocationManager locationManager;

    LatLng closest;
    LatLng current,partnerLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(CompanyListViewModel.class);


        binding.btnFind.setOnClickListener(this);
        binding.btnFindNearFriend.setOnClickListener(this);

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
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style_json));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
        this.googleMap = googleMap;
//        MoveCamera(new LatLng(11.555976, 104.905556));
        Log.d(TAG, " initializing map Ready");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_CODE_LOCATION);
            }
            return;
        }
        loadUserLocation();



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

        } else {

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

        Log.d(TAG, "FillterMarker: " + id);
        viewModel.getLatLngList().clear();

        if (viewModel !=null && viewModel.getListCompany() != null && viewModel.getListCompany().getValue()!=null  ) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                loadUserLocation();

        }
    }

    @SuppressLint("MissingPermission")
    private void loadUserLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        boolean isStyle = this.googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map_json)));
        Log.d(TAG, "onMapReady: "+isStyle);
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMapLongClickListener(this);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100,this);

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
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private LatLng FindCloestToCurrentLocation(LatLng src ,List<LatLng> latLngList) {
        LatLng closet = new LatLng(0, 0);
        float[] results = new float[1];
        float closetdistance = 100000000000000000f;
        for (LatLng latLng : latLngList) {
            Location.distanceBetween(src.latitude, src.longitude,
                    latLng.latitude, latLng.longitude, results);
            if (closetdistance > results[0]) {
                closetdistance = results[0];
                closet = latLng;
                Log.d(TAG, "FindCloestToCurrentLocation: closest found " + closet);
            }
        }

        AddMarkerOnMap(closet, "Closest", 4);
        return closet;
    }

    private float FindCloestForEachCompany(LatLng src ,List<LocationItem> locationItems) {

        List<LatLng> latLngList = new ArrayList<>();

        for(LocationItem locationItem : locationItems){
            latLngList.add(new LatLng(locationItem.getLatitude(),locationItem.getLongitude()));
        }

        LatLng closet = new LatLng(0, 0);
        float[] results = new float[1];
        float closetdistance = 100000000000000000f;
        for (LatLng latLng : latLngList) {
            Location.distanceBetween(src.latitude, src.longitude,
                    latLng.latitude, latLng.longitude, results);
            if (closetdistance > results[0]) {
                closetdistance = results[0];
                closet = latLng;
                Log.d(TAG, "FindCloestToCurrentLocation: closest found " + closet);
            }
        }

        AddMarkerOnMap(closet, "Closest", 4);
        return closetdistance;
    }

    private void FindClosestFromPointsToEachCompany(LatLng point1, LatLng point2 , List<Company> companies){

        float[][] closestFromEachcompany = new float[5][5];
        int i = 0;
        for (Company company: companies) {
            closestFromEachcompany[i][0] = FindCloestForEachCompany(point1,company.getLocation());
            closestFromEachcompany[i][1] = FindCloestForEachCompany(point2,company.getLocation());
            i++;

        }

        Log.d(TAG, "FindClosestFromPointsToEachCompany: "+closestFromEachcompany.toString());

        int closeIdex=0;
        for(int j = 1 ; j < 5 ; j++){
            if(closestFromEachcompany[j][1] < closestFromEachcompany[j-1][1] && closestFromEachcompany[j][0] < closestFromEachcompany[j-1][0]){
                closeIdex = j;
            }
        }

        Log.d(TAG, "FindClosestFromPointsToEachCompany: Closest Comapany id"+ closeIdex);

    }

    public void getDistance(LatLng src, LatLng des) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + src.latitude + "," + src.longitude + "&destination=" + des.latitude + "," + des.longitude + "*&mode=driving&key=" + getResources().getString(R.string.google_maps_key);

        Log.d(TAG, "getDistance: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Gson gson = new Gson();
                    Log.d(TAG, "onResponse: " + response);
                    RouteResponse routeResponse = gson.fromJson(response,RouteResponse.class);
                    Log.d(TAG, "onResponse: " + routeResponse);
                    
                    if(!routeResponse.getRoutes().isEmpty()||routeResponse.getGeocodedWaypoints()!=null) {
                        List<LatLng> list = decodePoly(routeResponse.getRoutes().get(0).getOverviewPolyline().getPoints());
//
                        for (int z = 0; z < list.size() - 1; z++) {
                            LatLng source = list.get(z);
                            LatLng dest = list.get(z + 1);
                            Polyline line = googleMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(source.latitude, source.longitude),
                                            new LatLng(dest.latitude, dest.longitude))
                                    .width(5).color(Color.BLUE).geodesic(true));
                        }
                    }
//                    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//                    for (int z = 0; z < list.size(); z++) {
//                        LatLng point = list.get(z);
//                        options.add(point);
//                    }
//                    line = myMap.addPolyline(options);
                    //maybe try looping and send all latlng one by one and compare it
//                        Log.d(TAG, "onResponse: "+routeResponse.getRoutes().get(0).getLegs().get(0).getDistance());

                }, error -> Log.d(TAG, "Error: " + error.getMessage()));

// Add the request to the RequestQueue.

        queue.add(stringRequest);
    }

    private CompanyClosestTo ClosestOfCompany(Company company,LatLng point1, LatLng point2){


        LatLng closestToSrc = new LatLng(0,0);
        LatLng closestToDes = new LatLng(0,0);
        List<LatLng> latLngList = new ArrayList<>();

        for(LocationItem locationItem : company.getLocation()){
            latLngList.add(new LatLng(locationItem.getLatitude(),locationItem.getLongitude()));
        }

        float[] distancePoint1 = new float[1];
        float[] distancePoint2 = new float[1];
        float closetdistance1 = 100000000000000000f;
        float closetdistance2 = 100000000000000000f;
        for (LatLng latLng : latLngList) {
            /// Point 1 Src
            Location.distanceBetween(point1.latitude, point1.longitude,
                    latLng.latitude, latLng.longitude, distancePoint1);
            if (closetdistance1 > distancePoint1[0]) {
                closetdistance1 = distancePoint1[0];
                closestToSrc = latLng;
            }
            /// Point 2 Des
            Location.distanceBetween(point2.latitude, point2.longitude,
                    latLng.latitude, latLng.longitude, distancePoint2);
            if (closetdistance2 > distancePoint2[0]) {
                closetdistance2 = distancePoint2[0];
                closestToDes = latLng;
            }

        }
        //Log.d(TAG, "Closest to Des is " + closestToDes + " distance : "+ distancePoint2[0]);
        //Log.d(TAG, "Closest to Src is " + closestToSrc + " distance : "+ distancePoint1[0]);
        //AddMarkerOnMap(closestToSrc,"Clostest to Src",0.2f);
        //AddMarkerOnMap(closestToDes,"Clostest to Des",0.2f);

        return new CompanyClosestTo(distancePoint1[0],distancePoint2[0],company.getCompanyName(),closestToSrc);

    }

    private class CompanyClosestTo{
        public CompanyClosestTo(float src, float des,String name, LatLng closest){
            this.des = des;
            this.src = src;
            this.name = name;
            this.closest = closest;
        }
        public CompanyClosestTo(){

        }
        public LatLng closest;
        public String name;
        public float src;
        public float des;

        public float getSumDistance(){
            return src+des;
        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        partnerLocation = latLng;
        Log.d(TAG, "onMapClick: "+latLng);
        AddMarkerOnMap(partnerLocation,"Partner",4);
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
    int index = 0;
    List<CompanyClosestTo> companyDistanceList = new ArrayList<>();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find:
//                Glide.with(this)
//                        .load("http://10.0.2.2:8000/storage/aa.png")
//                        .into(binding.test);
                Log.d(TAG, "onClick: fillter");
                getDistance(current,companyDistanceList.get(index).closest);
                AddMarkerOnMap(companyDistanceList.get(index).closest, "Closet", 4);
                break;

            case R.id.btn_find_near_friend:


//                AddMarkerOnMap(FindCloestToCurrentLocation(partnerLocation,viewModel.getLatLngList()), "Closet", 4);


                companyDistanceList.clear();
                for(int i = 0; i < viewModel.getListCompany().getValue().size(); i++){
                    CompanyClosestTo company = ClosestOfCompany(viewModel.getListCompany().getValue().get(i),current,partnerLocation);
                    Log.d(TAG, "onClick: "+company.name+":"+company.getSumDistance());
                    companyDistanceList.add(company);
                }

                Collections.sort(companyDistanceList, (o1, o2) -> Float.compare(o1.getSumDistance(),o2.getSumDistance()));


                for(int i = 1 ; i < companyDistanceList.size(); i++){
                    if(companyDistanceList.get(i).getSumDistance() < companyDistanceList.get(i-1).getSumDistance()){
                        index = i;
                        //Log.d(TAG, "onClick: "+i);
                    }
                }
                Log.d(TAG, "Closest Point: "+companyDistanceList.get(index).name+":"+companyDistanceList.get(index).getSumDistance());
                Log.d(TAG, "Sort Array: "+companyDistanceList.toString());

                AddMarkerOnMap(current,"Me",1f);
                AddMarkerOnMap(partnerLocation,"You",1f);
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
}
