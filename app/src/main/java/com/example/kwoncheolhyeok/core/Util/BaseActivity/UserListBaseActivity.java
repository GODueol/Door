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

/**
 * Created by kimbyeongin on 2018-01-07.
 */

@SuppressLint("Registered")
public class UserListBaseActivity extends AppCompatActivity {
    public ValueEventListener listener;
    public Query ref;

    public void setRecyclerView(final ArrayList<UserListAdapter.Item> items, final UserListAdapter adapter, final String field, int item_menu) {
        adapter.setItemMenu(item_menu, field);
        items.clear();
        items.add(new UserListAdapter.Item(true));
        if(ref != null && listener != null) ref.removeEventListener(listener);  // 이전 리스너 해제
        ref = DataContainer.getInstance().getMyUserRef().child(field).orderByValue();
        listener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(field, "DataChange : " + dataSnapshot.getKey() + ',' + dataSnapshot.getValue());
                items.clear();
                items.add(new UserListAdapter.Item(true));
                if(dataSnapshot.getValue() == null) adapter.notifyDataSetChanged();

                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("snapshot", snapshot.getValue().toString());
                    final String oUuid = snapshot.getKey();
                    DataContainer.getInstance().getUsersRef().child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User oUser = dataSnapshot.getValue(User.class);
                            items.add(1, new UserListAdapter.Item(oUser, (long)snapshot.getValue(), oUuid));
//                            items.offerFirst(new UserListAdapter.Item(oUser, (long)snapshot.getValue(), oUuid));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
