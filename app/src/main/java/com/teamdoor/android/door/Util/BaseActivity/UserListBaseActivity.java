package com.teamdoor.android.door.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.FriendsActivity.UserListAdapter;
import com.teamdoor.android.door.Util.DataContainer;
import com.teamdoor.android.door.Util.FireBaseUtil;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Created by kimbyeongin on 2018-01-07.
 */

@SuppressLint("Registered")
public class UserListBaseActivity extends BaseActivity {
    public ValueEventListener listener;
    public Query ref;
    protected SharedPreferencesUtil SPUtil;

    public void setRecyclerView(final ArrayList<UserListAdapter.Item> items, final UserListAdapter adapter, final String field, int item_menu, Query ref){
        Log.d("KBJ", "setRecyclerView!!");
        adapter.setItemMenu(item_menu, field);
        //items.clear();
        //items.add(new UserListAdapter.Item(true));

        // removeListener
        removeListener();

        this.ref = ref;

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("KBJ",field +  ", DataChange : " + dataSnapshot.getKey() + ", size : " + dataSnapshot.getChildrenCount() + ", " + dataSnapshot.getValue());
                items.clear();
                items.add(new UserListAdapter.Item(true));
                if(dataSnapshot.getValue() == null) {
                    adapter.notifyDataSetChanged();
                    return;
                }

                if(field.equals("Find User")){

                    for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String oUuid = snapshot.getKey();

                        FireBaseUtil.getInstance().syncUser(mUser -> {
                            if(DataContainer.getInstance().isBlockWithMe(oUuid)) return;
                            User oUser = snapshot.getValue(User.class);
                            if(adapter.isReverse) items.add(new UserListAdapter.Item(oUser, oUser.getLoginDate(), oUuid));
                            else items.add(1, new UserListAdapter.Item(oUser, oUser.getLoginDate(), oUuid));
                            adapter.notifyDataSetChanged();
                        });

                    }
                } else {

                    final HashMap<String, UserListAdapter.Item> stringItemHashMap = new HashMap<>();  // 순서를 위한 맵

                    ArrayList<Task<User>> tasks = new ArrayList<>();

                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("KBJ",field + ", snapshot key : " + snapshot.getKey() + ", value : " +  snapshot.getValue().toString());
                        final String oUuid = snapshot.getKey();
                        UserListAdapter.Item item = new UserListAdapter.Item(new User(), (long) snapshot.getValue(), oUuid);
                        stringItemHashMap.put(oUuid, item);
                        if (adapter.isReverse)
                            items.add(item);
                        else
                            items.add(1, item);

                        Log.d("KBJ", field + ", items.size() : " + items.size());

                        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();

                        DataContainer.getInstance().getUsersRef().child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User oUser = dataSnapshot.getValue(User.class);

                                if(oUser == null) {
                                    // 유저정보가 없는 경우 (유저가 삭제된 케이스)
                                    items.remove(stringItemHashMap.get(oUuid));

                                } else if(stringItemHashMap.containsKey(oUuid)) {
                                    stringItemHashMap.get(oUuid).setUser(oUser);

                                    int index = items.indexOf(stringItemHashMap.get(oUuid));
                                    Log.d("KBJ", field + ", index : " + index + ", item : " + item);
                                }
                                taskCompletionSource.setResult(null);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                taskCompletionSource.setException(databaseError.toException());
                            }
                        });
                        tasks.add(taskCompletionSource.getTask());
                    }

                    Tasks.whenAll(tasks).addOnSuccessListener(aVoid -> adapter.notifyDataSetChanged());
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
