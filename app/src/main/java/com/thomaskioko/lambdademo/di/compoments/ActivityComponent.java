package com.thomaskioko.lambdademo.di.compoments;

import com.thomaskioko.lambdademo.di.modules.ActivityModule;
import com.thomaskioko.lambdademo.di.scopes.PerActivity;
import com.thomaskioko.lambdademo.ui.LoginActivity;
import com.thomaskioko.lambdademo.ui.MainActivity;
import com.thomaskioko.lambdademo.ui.RegisterActivity;

import dagger.Component;

/**
 * @author Thomas Kioko
 */
@PerActivity
@Component(dependencies = AppComponent.class ,modules = {ActivityModule.class})
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(RegisterActivity activity);
}
