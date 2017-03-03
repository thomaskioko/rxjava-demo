package com.thomaskioko.lambdademo.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.thomaskioko.lambdademo.BuildConfig;
import com.thomaskioko.lambdademo.di.qualifiers.AppContext;
import com.thomaskioko.lambdademo.di.scopes.PerApplication;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author Thomas Kioko
 */
@Module
public class AppModule {
    private final Application mApp;

    public AppModule(Application app) {
        mApp = app;
    }

    @Provides
    @PerApplication
    @AppContext
    Context provideAppContext() {
        return mApp;
    }

    @Provides
    @PerApplication
    Resources provideResources() {
        return mApp.getResources();
    }

    @Provides
    @PerApplication
    static RealmConfiguration provideRealmConfiguration() {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder();

        if (BuildConfig.DEBUG) {
            builder = builder.deleteRealmIfMigrationNeeded();
        }
        return builder.build();
    }

    @Provides
    static Realm provideRealm() {
        return Realm.getDefaultInstance();
    }
}
