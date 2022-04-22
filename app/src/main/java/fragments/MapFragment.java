package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.envite.BuildConfig;
import com.example.envite.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private String placeId;
    private PlacesClient placesClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null){
            placeId = (String) arguments.get("placeId");
        }

        String apiKey = BuildConfig.MAPS_API_KEY;
        Places.initialize(getActivity().getApplicationContext(), apiKey);
        // Create a new PlacesClient instance
        placesClient = Places.createClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragmentContainer);

        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);

        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            LatLng latLng = place.getLatLng();
            String address = place.getAddress();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(8));
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                final int statusCode = apiException.getStatusCode();
                Snackbar.make(this.getActivity().findViewById(android.R.id.content),
                        "We could not open the map for this place", Snackbar.LENGTH_LONG).show();
                getActivity().onBackPressed();
            }
        });




    }
}