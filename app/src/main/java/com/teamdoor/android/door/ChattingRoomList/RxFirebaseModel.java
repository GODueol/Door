package com.teamdoor.android.door.ChattingRoomList;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.subjects.ReplaySubject;


public class RxFirebaseModel {
    public static final int CHILD_NULL = 0;
    public static final int CHILD_ADD = 1;
    public static final int CHILD_CHANGE = 2;
    public static final int CHILD_REMOVE = 3;
    public static final int CHILD_MOVE = 4;
    public static final int CHILD_CANCLE = 5;

    public static final int CHILD_SINGLE = 6;
    public static final int CHILD_MULTI = 7;
    ChildEventListener listener;

    @SuppressLint("CheckResult")
    public <T> Observable<FirebaseData> getFirebaseChildeEvent(Query query, Class<T> c) {

        return Observable.create((ObservableEmitter<FirebaseData> data) -> {
            listener = query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    data.onNext(new FirebaseData<>(dataSnapshot.getValue(c), dataSnapshot.getKey(), CHILD_ADD));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    data.onNext(new FirebaseData<>(dataSnapshot.getValue(c), dataSnapshot.getKey(), CHILD_CHANGE));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    data.onNext(new FirebaseData<>(dataSnapshot.getValue(c), dataSnapshot.getKey(), CHILD_REMOVE));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    data.onNext(new FirebaseData<>(dataSnapshot.getValue(c), dataSnapshot.getKey(), CHILD_MOVE));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    data.onError(new Throwable(databaseError.getMessage()));
                }
            });
        }).doFinally(() -> query.removeEventListener(listener));
    }

    public Observable<DataSnapshot> getFirebaseForSingleValue(Query query) {

        ReplaySubject<DataSnapshot> subject = ReplaySubject.create();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    subject.onNext(dataSnapshot);
                subject.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subject.onError(new Throwable(databaseError.getMessage()));
            }
        });

        return subject;
    }


    public <T> Observable<T> getFirebaseForSingleValue(Query query, Class<T> c, int type) {

        ReplaySubject<DataSnapshot> subject = ReplaySubject.create();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    subject.onNext(dataSnapshot);
                subject.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subject.onError(new Throwable(databaseError.getMessage()));
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


    class FirebaseData<T> {
        private T vaule;
        private String key;
        private int Type;

        FirebaseData(T d, String key, int Type) {
            this.vaule = d;
            this.key = key;
            this.Type = Type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public T getVaule() {
            return (T) vaule;
        }

        public int getType() {
            return Type;
        }

        public void setVaule(T vaule) {
            this.vaule = vaule;
        }

        public void setType(int type) {
            Type = type;
        }
    }
}
