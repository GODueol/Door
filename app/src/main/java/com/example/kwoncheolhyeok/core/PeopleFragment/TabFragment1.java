package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TabFragment1 extends android.support.v4.app.Fragment {

    GridView gridView = null;
    ImageAdapter imageAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getContext());
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent p = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
                p.putExtra("id", position);

                startActivity(p);

            }
        });

        refreshLocation();

        // 스와이프로 위치 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 위치 새로고침
                imageAdapter.clear();
                gridView.invalidateViews();

                refreshLocation();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void refreshLocation() {
        // 현재 자신의 위치를 가져옴
        saveMyGPS();

        // 현재 자신의 위치에 가까운 리스트 가져옴
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
        GeoFire geoFire = new GeoFire(ref);
        final Location location = GPSInfo.getmInstance(getActivity()).getGPSLocation();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 300);

        // 쿼리받은 값을 처리
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onKeyEntered(String key, GeoLocation geoLocation) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, geoLocation.latitude, geoLocation.longitude));
                // key로 프사url, 거리 가져옴
                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                targetLocation.setLongitude(geoLocation.longitude);

                float distance = location.distanceTo(targetLocation);

                // TODO : grid에 사진, distance추가
                if(imageAdapter != null){
                    imageAdapter.addItem(new ImageAdapter.Item(distance, key));
                    gridView.invalidateViews();
                } else {
                    Log.d(getTag(), "imageAdapter is null");
                }

            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    private void saveMyGPS() {
        // Get GPS
        Location location = GPSInfo.getmInstance(getActivity()).getGPSLocation();

        // 데이터베이스에 저장
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
        GeoFire geoFire = new GeoFire(ref);

        geoFire.setLocation(DataContainer.getInstance().getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(getContext(),"There was an error saving the location to GeoFire: " + error,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"Location saved on server successfully! ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}






