package com.thomaskioko.lambdademo.data;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.thomaskioko.lambdademo.model.User;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author Thomas Kioko
 */

public class RealmManager {

    private static RealmManager instance;
    private final Realm realm;

    public RealmManager(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmManager with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmManager(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmManager with(Activity activity) {

        if (instance == null) {
            instance = new RealmManager(activity.getApplication());
        }
        return instance;
    }

    public static RealmManager with(Application application) {

        if (instance == null) {
            instance = new RealmManager(application);
        }
        return instance;
    }

    public static RealmManager getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return Realm.getDefaultInstance();
    }

    //find all objects in the Book.class
    public RealmResults<User> getUsers() {

        return realm.where(User.class).findAll();
    }

    //query a single item with the given id
    public User getUser(String id) {

        return realm.where(User.class).equalTo("id", id).findFirst();
    }

    public RealmResults<User> queryUser(String email) {

        return realm.where(User.class)
                .contains("email", email)
                .findAll();

    }

    public void deleteUser() {
        realm.beginTransaction();
        realm.delete(User.class);
        realm.commitTransaction();

    }

    //clear all objects from Book.class
    public void deleteAllRecords() {

        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
}
