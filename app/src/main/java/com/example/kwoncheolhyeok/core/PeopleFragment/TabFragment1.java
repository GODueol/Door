package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(getContext()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent p = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
                p.putExtra("id", position);

                startActivity(p);

            }
        });

        // 현재 자신의 위치를 가져옴
        saveMyGPS();

        // 현재 자신의 위치에 가까운 리스트 가져옴
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
        GeoFire geoFire = new GeoFire(ref);
        Location location = GPSInfo.getmInstance(getActivity()).getGPSLocation();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 0.6);

        // TODO : 쿼리받은 값을 처리
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
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

        return view;
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
                    Toast.makeText(getContext(),"Location saved on server successfully!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}






