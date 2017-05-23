package com.thomaskioko.lambdademo.di.compoments;

import android.content.Context;

import com.thomaskioko.lambdademo.di.modules.AppModule;
import com.thomaskioko.lambdademo.di.modules.NetworkModule;
import com.thomaskioko.lambdademo.di.modules.OkHttpInterceptorsModule;
import com.thomaskioko.lambdademo.di.qualifiers.AppContext;
import com.thomaskioko.lambdademo.di.scopes.PerApplication;

import dagger.Component;
import io.realm.Realm;
import retrofit2.Retrofit;

/**
 * @author Thomas Kioko
 */
@PerApplication
@Component(modules = {
        AppModule.class,
        NetworkModule.class,
        OkHttpInterceptorsModule.class
})
public interface AppComponent {

    @AppContext
    Context context();

    Realm realm();

    Retrofit provideRetrofit();
}
