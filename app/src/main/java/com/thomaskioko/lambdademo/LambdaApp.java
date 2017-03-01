package com.thomaskioko.lambdademo;

import android.app.Application;
import android.content.res.Resources;

import com.facebook.stetho.Stetho;
import com.thomaskioko.lambdademo.di.compoments.AppComponent;
import com.thomaskioko.lambdademo.di.modules.AppModule;

import io.realm.Realm;
import timber.log.Timber;

/**
 * @author Thomas Kioko
 */

public class LambdaApp extends Application {

    private static LambdaApp sLambdaApp = null;
    private static AppComponent sAppComponent = null;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            Timber.plant(new Timber.DebugTree());
        }

        Realm.init(this);

        sLambdaApp = this;
        sLambdaApp = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static LambdaApp getInstance() {
        return sLambdaApp;
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    public static Realm getRealm() {
        return sAppComponent.realm();
    }

    public static Resources getRes() {
        return sLambdaApp.getResources();
    }
}
