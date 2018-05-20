package com.teamcore.android.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.teamcore.android.core.Entity.SummaryUser;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Event.RefreshLocationEvent;
import com.teamcore.android.core.Event.SomeoneBlocksMeEvent;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BusProvider;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.GPSInfo;
import com.teamcore.android.core.Util.SharedPreferencesUtil;
import com.teamcore.android.core.Util.UiUtil;

import java.util.Arrays;
import java.util.Map;

public class PeopleFragment extends android.support.v4.app.Fragment {

    GridView gridView = null;
    ImageAdapter imageAdapter;
    private User mUser;
    private ValueEventListener userListener;
    private DatabaseReference userRef;
    private GeoQuery geoQuery;
    private SharedPreferencesUtil SPUtil;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        SPUtil = new SharedPreferencesUtil(getContext());
        setmInterstitialAd();
        gridView = view.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getContext());
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            GridItem item = imageAdapter.getItem(position);
            // block 유저한테는 못들어가게함
            if (DataContainer.getInstance().isBlockWithMe(item.getUuid())) return;
            Intent p = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
            p.putExtra("id", position);
            p.putExtra("item", item);
            startActivity(p);
            SPUtil.increaseAds(mInterstitialAd, "mainGrid");
        });

        // 스와이프로 위치 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        SwipeRefreshLayout.OnRefreshListener onRefreshListener = () -> {
            // 위치 새로고침
            imageAdapter.clear();
            refreshGrid(null);

            mSwipeRefreshLayout.setRefreshing(false);
        };
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        mUser = DataContainer.getInstance().getUser();  // User 정보 가져옴

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUser = dataSnapshot.getValue(User.class);
                if(mUser == null) return;

                User compUser = DataContainer.getInstance().getUser();

                // blockedMe 확인
                boolean b = isEqualMap(mUser.getBlockMeUsers(), compUser.getBlockMeUsers());
                if (!b) {
                    refreshGrid(null, GPSInfo.getmInstance(getActivity()).getGPSLocation());
                    BusProvider.getInstance().post(new SomeoneBlocksMeEvent());
                }

                DataContainer.getInstance().setUser(mUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef = DataContainer.getInstance().getMyUserRef();
        userRef.addValueEventListener(userListener);

        try {
            BusProvider.getInstance().register(this); // Otto 등록
        } catch (IllegalAccessError e) {
            e.printStackTrace();    // 이미 등록된경우
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private boolean isEqualMap(Map<String, Long> map1, Map<String, Long> map2) {
        return map1.size() == map2.size();
    }

    public void refreshGrid(RefreshLocationEvent pushEvent, final Location location) {

        // 현재 자신의 위치에 가까운 리스트 가져옴
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
        GeoFire geoFire = new GeoFire(ref);
//        final Location location = GPSInfo.getmInstance(getActivity()).getGPSLocation();
        geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), DataContainer.RadiusMax);

        // 쿼리받은 값을 처리
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onKeyEntered(final String oUuid, final GeoLocation geoLocation) {

                DataContainer.getInstance().getUserRef(oUuid).child("summaryUser").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SummaryUser oSummary = dataSnapshot.getValue(SummaryUser.class);

                        try {
                            if (isInBlock(oUuid) || !isInFilter(oSummary)) {
                                onKeyExited(oUuid);
                                return;
                            }
                        } catch (NotSetAutoTimeException e) {
                            e.printStackTrace();
                        }

                        Log.d(getClass().toString(), String.format("Key %s entered the search area at [%f,%f]", oUuid, geoLocation.latitude, geoLocation.longitude));
                        addItemToGrid(oUuid, geoLocation, oSummary);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
            }

            private void addItemToGrid(final String key, GeoLocation geoLocation, final SummaryUser summary) {

                // key로 프사url, 거리 가져옴
                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                targetLocation.setLongitude(geoLocation.longitude);

                final float distance = location.distanceTo(targetLocation);

                // grid에 사진, distance추가

                if (imageAdapter != null) {
                    imageAdapter.addItem(new GridItem(distance, key, summary, summary.getPictureUrl()));
                    imageAdapter.notifyDataSetChanged();
//                    gridView.invalidateViews();
                    Log.d(getTag(), "addItemToGrid : " + key);
                } else {
                    Log.d(getTag(), "imageAdapter is null");
                }
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
                // 아이템 삭제
                imageAdapter.remove(key);
                imageAdapter.notifyDataSetChanged();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onKeyMoved(String key, GeoLocation geoLocation) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, geoLocation.latitude, geoLocation.longitude));
                // 아이템 갱신
                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                targetLocation.setLongitude(geoLocation.longitude);
                GridItem item = imageAdapter.getItem(key);
                if (item == null) return;
                imageAdapter.remove(key);
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

    @Subscribe
    public void refreshGrid(RefreshLocationEvent pushEvent) {

        // 현재 자신의 위치를 가져옴
        saveMyGPS();

        // 현재 자신의 위치에 가까운 리스트 가져옴
        final Location location = GPSInfo.getmInstance(getActivity()).getGPSLocation();
        refreshGrid(pushEvent, location);

    }

    private boolean isInBlock(String oUuid) {
        return mUser.getBlockUsers().containsKey(oUuid) || mUser.getBlockMeUsers().containsKey(oUuid);
    }

    private boolean isInFilter(SummaryUser summaryUser) throws NotSetAutoTimeException {
        // 로그인을 1개월 이상 하지 않을 시 그리드에서 사라지게
        if (summaryUser.getLoginDate() != 0 && UiUtil.getInstance().getCurrentTime(getContext()) - summaryUser.getLoginDate() > DataContainer.SecToDay * 31)
            return false;

        if (!mUser.isUseFilter()) return true;   // 필터 적용여부
        if (!(mUser.getAgeBoundary().getMin() <= summaryUser.getAge() && summaryUser.getAge() <= mUser.getAgeBoundary().getMax()))
            return false;
        if (!(mUser.getHeightBoundary().getMin() <= summaryUser.getHeight() && summaryUser.getHeight() <= mUser.getHeightBoundary().getMax()))
            return false;
        if (!(mUser.getWeightBoundary().getMin() <= summaryUser.getWeight() && summaryUser.getWeight() <= mUser.getWeightBoundary().getMax()))
            return false;
        int minBodyType = Arrays.asList(DataContainer.bodyTypes).indexOf(mUser.getBodyTypeBoundary().getMin());
        int maxBodyType = Arrays.asList(DataContainer.bodyTypes).indexOf(mUser.getBodyTypeBoundary().getMax());
        int bodyType = Arrays.asList(DataContainer.bodyTypes).indexOf(summaryUser.getBodyType());
        return minBodyType <= bodyType && bodyType <= maxBodyType;
    }

    private void saveMyGPS() {
        // Get GPS
        Location location = GPSInfo.getmInstance(getActivity()).getGPSLocation();

        // 데이터베이스에 저장
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
        GeoFire geoFire = new GeoFire(ref);

        geoFire.setLocation(DataContainer.getInstance().getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()), (key, error) -> {
            if (error != null) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "There was an error saving the location to GeoFire: " + error, Toast.LENGTH_SHORT).show();
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Location saved on server successfully! ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            LatLng latLng = getArguments().getParcelable("latlng");

            Location location = new Location("");
            assert latLng != null;
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            refreshGrid(null, location);
        } catch (Exception e) {
            Log.d("people", "익셉션");
            refreshGrid(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (geoQuery != null) geoQuery.removeAllListeners();
        if (userRef != null && userListener != null) userRef.removeEventListener(userListener);

        try {
            BusProvider.getInstance().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setmInterstitialAd(){
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}






