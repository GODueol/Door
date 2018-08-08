package com.teamdoor.android.door.MessageList;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teamdoor.android.door.Entity.User;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.internal.operators.observable.ObservableFromIterable;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by godueol on 2018. 8. 8..
 */

public class RxFirebaseModel {
    public static final int CHILD_ADD = 1;
    public static final int CHILD_CHANGE = 2;
    public static final int CHILD_REMOVE = 3;
    public static final int CHILD_MOVE = 4;
    public static final int CHILD_CANCLE = 5;

    public static final int CHILD_SINGLE = 6;
    public static final int CHILD_MULTI = 7;


    public Observable<FirebaseData> getFirebaseData(Query query) {

        Observable<FirebaseData> observable = Observable.create((ObservableEmitter<FirebaseData> data) -> {
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    data.onNext(new FirebaseData(dataSnapshot, CHILD_ADD));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    data.onNext(new FirebaseData(dataSnapshot, CHILD_CHANGE));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    data.onNext(new FirebaseData(dataSnapshot, CHILD_REMOVE));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    data.onNext(new FirebaseData(dataSnapshot, CHILD_MOVE));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    data.onError(new Throwable(databaseError.getMessage()));
                }
            });
        });

        Observable<FirebaseData> observable_add = observable
                .filter(firebaseData -> firebaseData.Type == CHILD_ADD);
        Observable<FirebaseData> observable_change = observable
                .filter(firebaseData -> firebaseData.Type == CHILD_CHANGE);
        Observable<FirebaseData> observable_remove = observable
                .filter(firebaseData -> firebaseData.Type == CHILD_REMOVE);
        Observable<FirebaseData> observable_move = observable
                .filter(firebaseData -> firebaseData.Type == CHILD_MOVE);

        return Observable.concat(observable_add, observable_change, observable_move, observable_remove);
    }


    public <T> Observable<T> getFirebaseSingleData(Query query, Class<T> c, int type) {

        ReplaySubject<DataSnapshot> subject = ReplaySubject.create();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subject.onNext(dataSnapshot);
                //Subject끝내주기
                subject.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //emitter.onError(new Throwable(databaseError.getMessage()));
            }
        });


        Observable<T> observableOne = subject.filter(ignore -> type == CHILD_SINGLE)
                .filter(DataSnapshot::exists)
                .map(data -> data.getValue(c));

        Observable<T> observableMany = subject.filter(ignore -> type == CHILD_MULTI)
                .filter(DataSnapshot::exists)
                .map(DataSnapshot::getChildren)
                .flatMap(Observable::fromIterable)
                .map(data -> data.getValue(c));

        List<Observable<T>> observablesList = new ArrayList<>();
        observablesList.add(observableMany);
        observablesList.add(observableOne);
        return Observable.amb(observablesList);
    }


    class FirebaseData {
        DataSnapshot dataSnapshot;
        int Type;

        FirebaseData(DataSnapshot d, int Type) {
            this.dataSnapshot = d;
            this.Type = Type;
        }

        public DataSnapshot getDataSnapshot() {
            return dataSnapshot;
        }

        public int getType() {
            return Type;
        }

        public void setDataSnapshot(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        public void setType(int type) {
            Type = type;
        }
    }

    ;
}
