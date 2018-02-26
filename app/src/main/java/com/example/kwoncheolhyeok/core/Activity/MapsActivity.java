package com.example.kwoncheolhyeok.core.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {


    private GoogleMap mGoogleMap;
    private LatLng mLatLng;
    private GPSInfo mGPSInfo;
    public SearchView addrText;
    public ImageButton search;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        addrText = (SearchView) findViewById(R.id.addrText);
        addrText.onActionViewExpanded();


        search = (ImageButton) findViewById(R.id.search_map);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
        setMarkerCustom();
        setMarker(latLng,address);
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        float zlevel = mGoogleMap.getCameraPosition().zoom;
        String address = addrConvertor.getAddress(getApplicationContext(), latLng);

        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zlevel));
        setMarker(latLng,address);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng latLng = marker.getPosition();
        Intent p = new Intent(getApplicationContext(), MainActivity.class);
        p.putExtra("latLng", latLng);
        startActivity(p);
    }

    public void setMarker(LatLng latLng, String address){
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(address)
                .snippet(getString(R.string.mapSpiner))
        ).showInfoWindow();
    }
    public void setMarkerCustom() {
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                // 위쪽 주소 텍스트 자바로만 수정가능
                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setText(marker.getTitle());

                // 아래쪽 이주변회원 검색 텍스트 자바로만 수정가능
                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(getResources().getColor(R.color.skyblue));
                snippet.setGravity(Gravity.CENTER);
                snippet.setTypeface(null, Typeface.BOLD);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    View.OnClickListener search_addr = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String str = addrText.getQuery().toString();
            float zlevel = mGoogleMap.getCameraPosition().zoom;
            Location loc = addrConvertor.findGeoPoint(getApplicationContext(), str);
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (latLng.longitude == 0 && latLng.latitude == 0) {
                Toast.makeText(getApplicationContext(), "검색 실패", Toast.LENGTH_SHORT).show();
            } else {
                String address = addrConvertor.getAddress(getApplicationContext(), latLng);
                mGoogleMap.clear();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zlevel));

                setMarker(latLng,address);
            }
        }
    };
}
