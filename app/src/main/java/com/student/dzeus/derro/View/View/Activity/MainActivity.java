package com.student.dzeus.derro.View.View.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.student.dzeus.derro.R;
import com.student.dzeus.derro.View.Model.Company;
import com.student.dzeus.derro.View.Model.LocationItem;
import com.student.dzeus.derro.View.ViewModel.CompanyListViewModel;
import com.student.dzeus.derro.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final static String TAG = "pory";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9999;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8888;
    private static boolean mLocationPermissionGranted;

    static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
    GoogleMap googleMap;
    MapFragment mapFragment;
    ActivityMainBinding binding;
    CompanyListViewModel viewModel;

    LatLng currentLocation;

    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(CompanyListViewModel.class);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
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
        });
    }


    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
//        MoveCamera(new LatLng(11.555976, 104.905556));

        viewModel.getListCompany().observe(this, new Observer<List<Company>>() {
            @Override
            public void onChanged(@Nullable List<Company> companies) {
                for (Company company : companies) {
                    for (LocationItem locationItem : company.getLocation()) {
                        LatLng latLng = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
                        AddMarkerOnMap(latLng, locationItem.getName());
                    }
                }
            }
        });

    }

    private void AddMarkerOnMap(LatLng latLng, String title) {
        this.googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }

    private void MoveCamera(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }




}
