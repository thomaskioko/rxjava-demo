package com.thomaskioko.lambdademo.di.compoments;

import android.content.Context;

import com.thomaskioko.lambdademo.di.modules.AppModule;
import com.thomaskioko.lambdademo.di.qualifiers.AppContext;
import com.thomaskioko.lambdademo.di.scopes.PerApplication;

import dagger.Component;
import io.realm.Realm;

/**
 * @author Thomas Kioko
 */
@PerApplication
@Component(modules = {AppModule.class})
public interface AppComponent {

    @AppContext
    Context context();

    Realm realm();
}
