package com.teamdoor.android.door.CorePage;

import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.teamdoor.android.door.Entity.CoreCloud;
import com.teamdoor.android.door.Entity.CoreListItem;
import com.teamdoor.android.door.Entity.CorePost;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.Event.SomeoneBlocksMeEvent;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.DataContainer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kwon on 2018-03-16.
 */

public class CoreCloudActivity extends CoreActivity {

    HashMap<String, CoreListItem> coreListItemMap = new HashMap<>();
    private AdView mAdView;
    public void setContentView() {
        setContentView(R.layout.core_cloud_activity_main);
        mAdView = (AdView) findViewById(R.id.adView);

        if (!DataContainer.getInstance().isPlus) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.destroy();
            mAdView.setVisibility(View.GONE);
        }
    }


    // Friends check 안함
    public boolean isOldFriends(String uuid) {
        return true;
    }

    // Fab 버튼 없음
    public void setFab() {
    }

    public void addPostsToList(final ArrayList<CoreListItem> list) {
        list.clear();
        postQuery = DataContainer.getInstance().getCoreCloudRef().orderByChild("attachDate");

        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CoreCloud coreCloud = dataSnapshot.getValue(CoreCloud.class);

                // 블럭된 유저는 안보이도록
                assert coreCloud != null;
                if (DataContainer.getInstance().isBlockWithMe(coreCloud.getcUuid())) {
                    return;
                }

                final String postKey = dataSnapshot.getKey();
                // create 순으로 List Add
                coreListItemMap.put(postKey, new CoreListItem(null, null, postKey, coreCloud.getcUuid()));

                list.add(0, coreListItemMap.get(postKey)); // 최신순
//                list.add(coreListItemMap.get(postKey)); // 오래된순

                if (postKey != null) setData(dataSnapshot, coreCloud, postKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CoreCloud coreCloud = dataSnapshot.getValue(CoreCloud.class);
                final String postKey = dataSnapshot.getKey();

                // Set Post 데이터
                assert coreCloud != null;
                setData(dataSnapshot, coreCloud, postKey);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final String postKey = dataSnapshot.getKey();

                CoreListItem coreListItem = coreListItemMap.get(postKey);
                int position = list.lastIndexOf(coreListItem);
                list.remove(coreListItem);
                coreListItemMap.remove(postKey);
                coreListAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private void setData(DataSnapshot dataSnapshot, final CoreCloud coreCloud, final String postKey) {

                // Set Post 데이터
                if (coreCloud.getcUuid() == null) return;
                FirebaseDatabase.getInstance().getReference().child("posts").child(coreCloud.getcUuid()).child(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CoreListItem coreListItem = coreListItemMap.get(postKey);
                                int position = list.lastIndexOf(coreListItem);
                                coreListItem.setCorePost(dataSnapshot.getValue(CorePost.class));
                                //coreListItem.getCorePost().setWriteDate(coreCloud.getAttachDate()); // 날짜 표현을 Attach 시간으로 변경
                                if (coreListItem.getUser() != null)
                                    coreListAdapter.notifyItemChanged(position);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                // Set User 데이터
                DataContainer.getInstance().getUserRef(coreCloud.getcUuid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CoreListItem coreListItem = coreListItemMap.get(postKey);
                                int position = list.lastIndexOf(coreListItem);
                                coreListItem.setUser(dataSnapshot.getValue(User.class));
                                if (coreListItem.getCorePost() != null)
                                    coreListAdapter.notifyItemChanged(position);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

        };
        postQuery.addChildEventListener(listener);
    }

    @Subscribe
    public void FinishActivity(SomeoneBlocksMeEvent someoneBlocksMeEvent) {
        addPostsToList(list);
    }

    @Override
    public void onResume() {
        if (coreListAdapter != null) {
            coreListAdapter.Resume();
        }
        super.onResume();

        if(mAdView.isLoading()){
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if (coreListAdapter != null) {
            coreListAdapter.Pause();
        }
        if(mAdView.isLoading()){
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (coreListAdapter != null) {
            coreListAdapter.Destroy();
        }
        if(mAdView.isLoading()){
            mAdView.destroy();
        }
        super.onDestroy();
    }


}