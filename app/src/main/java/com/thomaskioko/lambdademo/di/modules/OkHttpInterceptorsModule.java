package com.thomaskioko.lambdademo.di.modules;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.thomaskioko.lambdademo.BuildConfig;
import com.thomaskioko.lambdademo.di.qualifiers.OkHttpInterceptors;
import com.thomaskioko.lambdademo.di.qualifiers.OkHttpNetworkInterceptors;
import com.thomaskioko.lambdademo.di.scopes.PerApplication;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * This class contains network logging interceptors.
 *
 * @author Thomas Kioko
 */

@Module
public class OkHttpInterceptorsModule {

    @Provides
    @PerApplication
    @NonNull
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor mHttpLoggingInterceptor = new HttpLoggingInterceptor();

        mHttpLoggingInterceptor.
                setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return mHttpLoggingInterceptor;
    }

    @Provides
    @OkHttpInterceptors
    @PerApplication
    @NonNull
    HttpLoggingInterceptor provideOkHttpInterceptors(@NonNull HttpLoggingInterceptor httpLoggingInterceptor) {
        return httpLoggingInterceptor;
    }

    @Provides
    @OkHttpNetworkInterceptors
    @PerApplication
    @NonNull
    StethoInterceptor provideOkHttpNetworkInterceptors() {
        return new StethoInterceptor();
    }
}
