package com.thomaskioko.lambdademo.model;

import io.realm.RealmObject;

/**
 * @author Thomas Kioko
 */

public class User extends RealmObject {


    private String fullNames;
    private String email;
    private String password;

    public String getFullNames() {
        return fullNames;
    }

    public void setFullNames(String fullNames) {
        this.fullNames = fullNames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
