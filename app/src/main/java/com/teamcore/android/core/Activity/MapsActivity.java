package com.teamcore.android.core.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.GPSInfo;
import com.teamcore.android.core.Util.addrConvertor;

/**
 * Created by KwonCheolHyeok on 2016-11-25.
 */

public class MapsActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {


    private GoogleMap mGoogleMap;
    private LatLng mLatLng;
    private GPSInfo mGPSInfo;
    public SearchView addrText;
    public ImageButton search;
    private Toolbar toolbar;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd noFillInterstitialAd;
    boolean isFillReward = false;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedVideoAd();
        setnoFillInterstitialAd();
        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);

                AdView mAdView = (AdView) findViewById(R.id.adView);
        checkCorePlus().done(isPlus -> {
            if (!isPlus) {
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdView.loadAd(adRequest);
            } else {
                mAdView.destroy();
                mAdView.setVisibility(View.GONE);
            }
        });

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
        Toast.makeText(getApplicationContext(), "구글 맵을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
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
        setMarker(latLng, address);
        mGoogleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        float zlevel = mGoogleMap.getCameraPosition().zoom;
        String address = addrConvertor.getAddress(getApplicationContext(), latLng);

        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zlevel));
        setMarker(latLng, address);
    }

    GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            LatLng latLng = marker.getPosition();
            i= new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("latLng", latLng);

            checkCorePlus().done(isPlus -> {
               if (!isPlus) {
                    FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.mapSearchCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int value;
                            try {
                                value = Integer.valueOf(dataSnapshot.getValue().toString());
                            } catch (Exception e) {
                                value = 0;
                            }
//                        Log.d("test", "몇개 : " + value);
                            if (value > 0) {
                                FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.mapSearchCount)).setValue(value - 1);
                                startActivity(i);
                                Toast.makeText(getApplicationContext(), "스와이프하시면 현재 위치로 되돌아갑니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                if (isFillReward) {
                                    startActivity(i);
                                    noFillInterstitialAd.show();
                                    finish();
                                } else {
                                    mRewardedVideoAd.show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    startActivity(i);
                    Toast.makeText(getApplicationContext(), "스와이프하시면 현재 위치로 되돌아갑니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        }
    };


    RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {

        }

        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoStarted() {

        }

        @Override
        public void onRewardedVideoAdClosed() {
            loadRewardedVideoAd();
            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.mapSearchCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int value = Integer.valueOf(dataSnapshot.getValue().toString());
//                            Log.d("test", "몇개 : " + value);
                        if (value > 0) {
                            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.mapSearchCount)).setValue(value - 1);
                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "스와이프하시면 현재 위치로 되돌아갑니다.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.d("test", "onRewardedVideoAdClosed");
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.mapSearchCount)).setValue(rewardItem.getAmount());
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            switch (i) {
                case 0:
                    // 에드몹 내부서버에러
                    Toast.makeText(getApplicationContext(), "내부서버에 문제가 있습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd();
                    break;
                case 2:
                    // 네트워크 연결상태 불량
                    Toast.makeText(getApplicationContext(), "네트워크 연결상태가 좋지 않습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd();
                    break;
                case 3:
                    // 에드몹 광고 인벤토리 부족
                    isFillReward = true;
                    break;
            }
        }
    };

    public void setMarker(LatLng latLng, String address) {
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
                snippet.setTextSize(17);
                snippet.setTypeface(null, Typeface.BOLD_ITALIC);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.adsMapSearching),
                new AdRequest.Builder()
                        .build());
    }

    public void setnoFillInterstitialAd() {
        noFillInterstitialAd = new InterstitialAd(this);
        noFillInterstitialAd.setAdUnitId(getString(R.string.noFillReward));
        noFillInterstitialAd.loadAd(new AdRequest.Builder().build());
        noFillInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                noFillInterstitialAd.loadAd(new AdRequest.Builder().build());
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
                Toast.makeText(getApplicationContext(), "검색이 되지 않습니다. 지역명을 다시 입력해주세요.", Toast.LENGTH_LONG).show();
            } else {
                String address = addrConvertor.getAddress(getApplicationContext(), latLng);
                mGoogleMap.clear();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zlevel));

                setMarker(latLng, address);
            }
        }
    };

    @Override
    public void onResume() {
        if(mRewardedVideoAd!=null) {
            mRewardedVideoAd.resume(this);
            mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if(mRewardedVideoAd!=null) {
            mRewardedVideoAd.pause(this);
            mRewardedVideoAd.setRewardedVideoAdListener(null);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(mRewardedVideoAd!=null) {
            mRewardedVideoAd.destroy(this);
            mRewardedVideoAd.setRewardedVideoAdListener(null);
        }
        super.onDestroy();
    }

}
