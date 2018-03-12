package com.example.kwoncheolhyeok.core.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.FriendsActivity.UserListAdapter;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kimbyeongin on 2018-01-07.
 */

@SuppressLint("Registered")
public class UserListBaseActivity extends AppCompatActivity {
    public ValueEventListener listener;
    public Query ref;

    public void setRecyclerView(final ArrayList<UserListAdapter.Item> items, final UserListAdapter adapter, final String field, int item_menu, Query ref){
        adapter.setItemMenu(item_menu, field);
        //items.clear();
        //items.add(new UserListAdapter.Item(true));

        // removeListener
        removeListener();

        this.ref = ref;

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(field, "DataChange : " + dataSnapshot.getKey() + ',' + dataSnapshot.getValue());
                items.clear();
                items.add(new UserListAdapter.Item(true));
                if(dataSnapshot.getValue() == null) adapter.notifyDataSetChanged();

                if(field.equals("Find User")){

                    for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String oUuid = snapshot.getKey();
                        User oUser = snapshot.getValue(User.class);
                        if(adapter.isReverse) items.add(new UserListAdapter.Item(oUser, oUser.getLoginDate(), oUuid));
                        else items.add(1, new UserListAdapter.Item(oUser, oUser.getLoginDate(), oUuid));
                        adapter.notifyDataSetChanged();
                    }
                } else {

                    final HashMap<String, UserListAdapter.Item> stringItemHashMap = new HashMap<>();  // 순서를 위한 맵

                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("snapshot", snapshot.getValue().toString());
                        final String oUuid = snapshot.getKey();
                        UserListAdapter.Item item = new UserListAdapter.Item(new User(), (long) snapshot.getValue(), oUuid);
                        stringItemHashMap.put(oUuid, item);
                        if (adapter.isReverse)
                            items.add(item);
                        else
                            items.add(1, item);

                        DataContainer.getInstance().getUsersRef().child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User oUser = dataSnapshot.getValue(User.class);

                                if(stringItemHashMap.containsKey(oUuid)) {
                                    stringItemHashMap.get(oUuid).setUser(oUser);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        addListener();
    }

    public void setRecyclerView(final ArrayList<UserListAdapter.Item> items, final UserListAdapter adapter, final String field, int item_menu) {
        setRecyclerView(items, adapter, field, item_menu, DataContainer.getInstance().getMyUserRef().child(field).orderByValue());
    }

    private void addListener() {
        if(ref != null && listener != null) ref.addValueEventListener(listener);  // 이전 리스너 등록
    }

    private void removeListener() {
        if(ref != null && listener != null) ref.removeEventListener(listener);  // 이전 리스너 해제
    }

    @Override
    protected void onDestroy() {
        removeListener();
        super.onDestroy();
    }
}
