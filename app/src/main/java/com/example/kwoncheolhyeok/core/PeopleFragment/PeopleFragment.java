package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Event.RefreshLocationEvent;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BusProvider;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

public class PeopleFragment extends android.support.v4.app.Fragment {

    // 싱글톤 패턴
    @SuppressLint("StaticFieldLeak")
    private static PeopleFragment mInstance;

    public static PeopleFragment getInstance() {
        if (mInstance == null) mInstance = new PeopleFragment();
        return mInstance;
    }
    @SuppressLint("ValidFragment")
    private PeopleFragment(){}

    GridView gridView = null;
    ImageAdapter imageAdapter;
    private User mUser;
    Bus bus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        gridView = view.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getContext());
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent p = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
                p.putExtra("id", position);
                p.putExtra("item", imageAdapter.getItem(position));
                startActivity(p);
            }
        });

        refreshGrid(null);

        // 스와이프로 위치 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 위치 새로고침
                refreshGrid(null);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        mUser = DataContainer.getInstance().getUser();  // user 정보 가져옴

        bus = BusProvider.getInstance();

        try {
            bus.register(this); // Otto 등록
        } catch (IllegalAccessError e){
            e.printStackTrace();    // 이미 등록된경우
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    @Subscribe
    public void refreshGrid(RefreshLocationEvent pushEvent) {
        // 데이터 비움
        imageAdapter.clear();
        imageAdapter.notifyDataSetChanged();

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
            public void onKeyEntered(final String oUuid, final GeoLocation geoLocation) {
                DataContainer.getInstance().getUserRef(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User oUser = dataSnapshot.getValue(User.class);
                        if(isInBlock(oUser, oUuid)) return;  // 블러킹
                        if(!isInFilter(oUser)) return;  // 필터링

                        Log.d(getClass().toString(),String.format("Key %s entered the search area at [%f,%f]", oUuid, geoLocation.latitude, geoLocation.longitude));
                        addItemToGrid(oUuid, geoLocation, oUser);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
            }

            private void addItemToGrid(final String key, GeoLocation geoLocation, final User oUser) {
                // key로 프사url, 거리 가져옴
                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                targetLocation.setLongitude(geoLocation.longitude);

                final float distance = location.distanceTo(targetLocation);

                // grid에 사진, distance추가

                if(imageAdapter != null){
                    imageAdapter.addItem(new ImageAdapter.Item(distance, key, oUser, oUser.getPicUrls().getPicUrl1()));
                    imageAdapter.notifyDataSetChanged();
//                    gridView.invalidateViews();
                    Log.d(getTag(), "addItemToGrid : " + key );
                } else {
                    Log.d(getTag(), "imageAdapter is null");
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
                // 아이템 삭제
                imageAdapter.removeItem(key);
                imageAdapter.notifyDataSetChanged();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("DefaultLocale")
            @Override
            public void onKeyMoved(String key, GeoLocation geoLocation) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, geoLocation.latitude, geoLocation.longitude));
                // 아이템 갱신
                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                targetLocation.setLongitude(geoLocation.longitude);
                ImageAdapter.Item item = imageAdapter.getItem(key);
                if(item == null) return;
                imageAdapter.removeItem(key);
                item.setDistance(location.distanceTo(targetLocation));
                imageAdapter.addItem(item);
                imageAdapter.notifyDataSetChanged();
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

    private boolean isInBlock(User oUser, String oUuid) {
        String mUuid = DataContainer.getInstance().getUid();
        return oUser.getBlockUsers().containsKey(mUuid) || mUser.getBlockUsers().containsKey(oUuid);
    }

    private boolean isInFilter(User oUser) {
        if(!mUser.isUseFilter()) return true;   // 필터 적용여부
        if(!(mUser.getAgeBoundary().getMin() <= oUser.getAge() && oUser.getAge() <= mUser.getAgeBoundary().getMax())) return false;
        if(!(mUser.getHeightBoundary().getMin() <= oUser.getHeight() && oUser.getHeight() <= mUser.getHeightBoundary().getMax())) return false;
        if(!(mUser.getWeightBoundary().getMin() <= oUser.getWeight() && oUser.getWeight() <= mUser.getWeightBoundary().getMax())) return false;
        int minBodyType = Arrays.asList(DataContainer.bodyTypes).indexOf(mUser.getBodyTypeBoundary().getMin());
        int maxBodyType = Arrays.asList(DataContainer.bodyTypes).indexOf(mUser.getBodyTypeBoundary().getMax());
        int bodyType = Arrays.asList(DataContainer.bodyTypes).indexOf(oUser.getBodyType());
        return minBodyType <= bodyType && bodyType <= maxBodyType;
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
                    if(getActivity()!= null) Toast.makeText(getActivity(),"There was an error saving the location to GeoFire: " + error,Toast.LENGTH_SHORT).show();
                } else {
                    if(getActivity()!= null) Toast.makeText(getActivity(),"Location saved on server successfully! ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}





