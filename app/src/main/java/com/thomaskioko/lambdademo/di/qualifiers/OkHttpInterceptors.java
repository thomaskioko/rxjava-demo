package com.thomaskioko.lambdademo.di.qualifiers;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Thomas Kioko
 */

@Documented
@Qualifier
@Retention(RUNTIME)
public @interface OkHttpInterceptors {
}
