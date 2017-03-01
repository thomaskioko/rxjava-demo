package com.thomaskioko.lambdademo.di.modules;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.thomaskioko.lambdademo.di.qualifiers.ActivityContext;
import com.thomaskioko.lambdademo.di.scopes.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * @author Thomas Kioko
 */
@Module
public class ActivityModule {

    private final AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Provides
    @PerActivity
    @ActivityContext
    Context provideActivityContext() { return mActivity; }
}
