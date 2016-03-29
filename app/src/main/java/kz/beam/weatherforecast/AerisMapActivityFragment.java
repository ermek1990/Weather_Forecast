package kz.beam.weatherforecast;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.hamweather.aeris.communication.AerisCallback;
import com.hamweather.aeris.communication.EndpointType;
import com.hamweather.aeris.maps.AerisMapView;
import com.hamweather.aeris.maps.interfaces.OnAerisMapLongClickListener;
import com.hamweather.aeris.model.AerisResponse;
import com.hamweather.aeris.tiles.AerisTile;

/**
 * A placeholder fragment containing a simple view.
 */
public class AerisMapActivityFragment extends Fragment implements
        OnAerisMapLongClickListener, AerisCallback, OnMapReadyCallback {

    AerisMapView mapView = null;

    public AerisMapActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aeris_map, container, false);
        AerisMapActivity activity = (AerisMapActivity) getActivity();
        Double latitude = Double.parseDouble(activity.getLatitude());
        Double longitude = Double.parseDouble(activity.getLongitude());
        activity.initMap();
        mapView = (AerisMapView) view.findViewById(R.id.aerisfragment_map);
        mapView.init(savedInstanceState, AerisMapView.AerisMapType.GOOGLE);
        mapView.addLayer(AerisTile.RADSAT);
        mapView.animate();
        float zoomLevel = 10;
        LatLng mapPoint = new LatLng(latitude, longitude);
        mapView.moveToLocation(mapPoint, zoomLevel);
        mapView.setOnAerisMapLongClickListener(this);
        return view;
    }

    @Override
    public void onMapLongClick(double lat, double longitude) {}

    @Override
    public void onResult(EndpointType endpointType, AerisResponse aerisResponse) {}

    @Override
    public void onMapReady(GoogleMap googleMap) {}

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
