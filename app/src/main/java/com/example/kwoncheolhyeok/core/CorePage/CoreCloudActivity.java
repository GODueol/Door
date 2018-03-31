package com.example.kwoncheolhyeok.core.CorePage;

import com.example.kwoncheolhyeok.core.Entity.CoreCloud;
import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Event.SomeoneBlocksMeEvent;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kwoncheolhyeok.core.Util.DataContainer.SecToDay;

/**
 * Created by Kwon on 2018-03-16.
 */

public class CoreCloudActivity extends CoreActivity {

    HashMap<String, CoreListItem> coreListItemMap = new HashMap<>();

    public void setContentView() {
        setContentView(R.layout.core_cloud_activity_main);
    }

    public void setFab(){
        // Fab 버튼 없음
    }

    public void addPostsToList(final ArrayList<CoreListItem> list) {
        list.clear();
        postQuery = DataContainer.getInstance().getCoreCloudRef().orderByChild("createDate");

        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CoreCloud coreCloud = dataSnapshot.getValue(CoreCloud.class);

                // 하루 지난 포스트는 안보임
                long diff;
                try {
                    assert coreCloud != null;
                    diff = UiUtil.getInstance().getCurrentTime(CoreCloudActivity.this) - coreCloud.getAttachDate();
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    return;
                }
                if(diff > (SecToDay)) return;

                // 블럭된 유저는 안보이도록
                if(DataContainer.getInstance().isBlockWithMe(coreCloud.getcUuid())){
                    return;
                }

                final String postKey = dataSnapshot.getKey();
                // create 순으로 List Add
                coreListItemMap.put(postKey, new CoreListItem(null, null, postKey, coreCloud.getcUuid()));

                list.add(0, coreListItemMap.get(postKey)); // 최신순
//                list.add(coreListItemMap.get(postKey)); // 오래된순

                setData(dataSnapshot, coreCloud, postKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CoreCloud coreCloud = dataSnapshot.getValue(CoreCloud.class);
                final String postKey = dataSnapshot.getKey();

                // Set Post 데이터
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

            private void setData(DataSnapshot dataSnapshot, CoreCloud coreCloud, final String postKey) {
                // Set Post 데이터
                FirebaseDatabase.getInstance().getReference().child("posts").child(coreCloud.getcUuid()).child(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CoreListItem coreListItem = coreListItemMap.get(postKey);
                                int position = list.lastIndexOf(coreListItem);
                                coreListItem.setCorePost(dataSnapshot.getValue(CorePost.class));
                                if(coreListItem.getUser() != null) coreListAdapter.notifyItemChanged(position);
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
                                if(coreListItem.getCorePost() != null) coreListAdapter.notifyItemChanged(position);
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

}