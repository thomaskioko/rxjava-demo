package com.thomaskioko.lambdademo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.thomaskioko.lambdademo.LambdaApp;
import com.thomaskioko.lambdademo.di.compoments.ActivityComponent;
import com.thomaskioko.lambdademo.di.compoments.DaggerActivityComponent;

import javax.inject.Inject;

import io.realm.Realm;

/**
 * @author Thomas Kioko
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    Realm realm;

    private ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (realm != null) {
            realm.close();
        }
    }

    public abstract int getLayout();

    protected final ActivityComponent getActivityComponent() {

        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .appComponent(LambdaApp.getAppComponent())
                    .build();
        }

        return mActivityComponent;
    }
}
