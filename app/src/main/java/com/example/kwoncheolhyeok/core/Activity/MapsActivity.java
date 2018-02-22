package com.example.kwoncheolhyeok.core.Activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.PeopleFragment;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.example.kwoncheolhyeok.core.Util.addrConvertor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by KwonCheolHyeok on 2016-11-25.
 */

public class MapsActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mGoogleMap;
    private LatLng mLatLng;
    private GPSInfo mGPSInfo;
    public EditText addrText;
    public ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_activity);
        addrText = (EditText) findViewById(R.id.addrText);
        search = (ImageView) findViewById(R.id.search_map);
        mGPSInfo = new GPSInfo(getApplicationContext());

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        /********************** 요기까지 지도설정 *******************/

        search.setOnClickListener(search_addr);
        /* 자동완성 기능
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                float zlevel = mGoogleMap.getCameraPosition().zoom;
                String address = place.getAddress().toString();

                mGoogleMap.clear();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zlevel));
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(address)
                ).showInfoWindow();
                Log.i("app", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i("app", "An error occurred: " + status);
            }
        });

       */
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "맵 로드 실패", Toast.LENGTH_SHORT).show();
    }

    /**
     * 초기 맵생성기 실행되는 구현메소드
     *
     * @param googleMap 구글맵
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mLatLng = mGPSInfo.getLatLng();
        createGoogleMap(mGoogleMap, mLatLng);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
        mGoogleMap.setOnMapClickListener(this);
    }

    /**
     * GoogleMap 생성 메소드
     *
     * @param gMap   구글맵
     * @param latLng 리턴 위치값
     */
    public void createGoogleMap(GoogleMap gMap, LatLng latLng) {
        mGoogleMap = gMap;

        this.mLatLng = latLng;
        float zlevel = mGoogleMap.getCameraPosition().zoom;
        String address = addrConvertor.getAddress(getApplicationContext(), mLatLng);

        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, zlevel));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(mLatLng)
                .title(address)
        ).showInfoWindow();
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        float zlevel = mGoogleMap.getCameraPosition().zoom;
        String address = addrConvertor.getAddress(getApplicationContext(), latLng);

        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zlevel));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(address)
        ).showInfoWindow();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng latLng = marker.getPosition();
        Intent p = new Intent(getApplicationContext(), MainActivity.class);
        p.putExtra("latLng", latLng);
        startActivity(p);
    }

    View.OnClickListener search_addr = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String str = addrText.getText().toString();
            float zlevel = mGoogleMap.getCameraPosition().zoom;
            Location loc = addrConvertor.findGeoPoint(getApplicationContext(), str);
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            if(latLng.longitude==0&&latLng.latitude==0) {
                Toast.makeText(getApplicationContext(), "검색 실패", Toast.LENGTH_SHORT).show();
            }else{
                String adress = addrConvertor.getAddress(getApplicationContext(), latLng);
                mGoogleMap.clear();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zlevel));
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(adress)
                ).showInfoWindow();
            }
        }
    };
}
