package com.thomaskioko.lambdademo.di.modules;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.thomaskioko.lambdademo.di.qualifiers.OkHttpInterceptors;
import com.thomaskioko.lambdademo.di.qualifiers.OkHttpNetworkInterceptors;
import com.thomaskioko.lambdademo.di.scopes.PerApplication;
import com.thomaskioko.lambdademo.utils.ApplicationConstants;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.thomaskioko.lambdademo.utils.ApplicationConstants.CONNECT_TIMEOUT;
import static com.thomaskioko.lambdademo.utils.ApplicationConstants.READ_TIMEOUT;
import static com.thomaskioko.lambdademo.utils.ApplicationConstants.WRITE_TIMEOUT;

/**
 * @author Thomas Kioko
 */

@Module
public class NetworkModule {

    /**
     * Configure OkHttpClient. This helps us override some of the default configuration. Like the
     * connection timeout.
     *
     * @return OkHttpClient
     */
    @Provides
    @PerApplication
    OkHttpClient okHttpClient(@OkHttpInterceptors HttpLoggingInterceptor httpLoggingInterceptor,
                              @OkHttpNetworkInterceptors @NonNull StethoInterceptor networkInterceptors) {

        return new OkHttpClient.Builder()
                .addNetworkInterceptor(networkInterceptors)
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @PerApplication
    Retrofit provideRestAdapter(@NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create()) // Serialize Objects
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //Set call to return {@link Observable}
                .build();
    }
}
