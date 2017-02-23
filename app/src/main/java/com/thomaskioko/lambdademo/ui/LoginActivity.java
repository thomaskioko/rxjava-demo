package com.thomaskioko.lambdademo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.thomaskioko.lambdademo.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.thomaskioko.lambdademo.utils.StringUtils.validateEmail;
import static com.thomaskioko.lambdademo.utils.StringUtils.validatePassword;


/**
 * @author kioko
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.txt_input_layout_email)
    TextInputLayout mEmailInputLayout;
    @BindView(R.id.txt_input_layout_password)
    TextInputLayout mPasswordInputLayout;
    @BindView(R.id.et_password)
    EditText mPasswordEditText;
    @BindView(R.id.et_email)
    EditText mEmailEditText;
    @BindView(R.id.sign_in_ll)
    LinearLayout mSignInLinearLayout;
    @BindView(R.id.btn_sign_in)
    Button mButtonSignIn;
    @BindView(R.id.btn_sign_up)
    Button mButtonSignUp;
    @BindInt(R.integer.debounce_length)
    int mDebounceLength;


    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_sign_in));
        }


        Observable<CharSequence> emailObservable = RxTextView.textChanges(mEmailEditText);
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEditText);

        Subscription emailSubscription = emailObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(charSequence -> hideErrorMessage(mEmailInputLayout))
                .debounce(mDebounceLength, TimeUnit.SECONDS) //Emit an item after 400 sec.
                .subscribe(charSequence -> {
                    if (!validateEmail(charSequence.toString())) {
                        showErrorMessage(mEmailInputLayout, getString(R.string.error_message_invalid_email));
                    } else {
                        hideErrorMessage(mEmailInputLayout);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                });

        Subscription passwordSubscription = passwordObservable
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(mDebounceLength, TimeUnit.SECONDS) //Emit an item after 400 sec.
                .doOnNext(charSequence -> hideErrorMessage(mPasswordInputLayout))
                .subscribe(charSequence -> {
                            if (!validatePassword(charSequence.toString())) {
                                showErrorMessage(mPasswordInputLayout, getString(R.string.error_message_invalid_password));
                            } else {
                                hideErrorMessage(mPasswordInputLayout);
                            }
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                        });

        mCompositeSubscription.add(emailSubscription);
        mCompositeSubscription.add(passwordSubscription);

        Subscription fieldValidationSubscription = Observable.combineLatest(emailObservable, passwordObservable,
                (email, password) -> validateEmail(email.toString()) && validatePassword(password.toString()))
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        enableSignIn();
                    } else {
                        disableSignIn();
                    }

                }, throwable -> {
                    Timber.e(throwable.getMessage());
                });

        mCompositeSubscription.add(fieldValidationSubscription);

        RxView.clicks(mButtonSignUp)
                .subscribe(aVoid -> {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    /**
     * Helper method that displays error message.
     *
     * @param textInputLayout {@link TextInputLayout}
     * @param errorMessage    Error Message
     */
    private void showErrorMessage(TextInputLayout textInputLayout, String errorMessage) {
        if (textInputLayout.getChildCount() == 2)
            textInputLayout.getChildAt(1).setVisibility(View.VISIBLE);

        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(errorMessage);
    }

    /**
     * Helper method that hides error message.
     *
     * @param textInputLayout {@link TextInputLayout}
     */
    private void hideErrorMessage(TextInputLayout textInputLayout) {
        if (textInputLayout.getChildCount() == 2)
            textInputLayout.getChildAt(1).setVisibility(View.GONE);
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError(null);
    }

    /**
     * Helper method to enable the sign in button
     */
    private void enableSignIn() {
        mSignInLinearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mButtonSignIn.setEnabled(true);
        mButtonSignIn.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        RxView.clicks(mButtonSignIn)
                .subscribe(aVoid -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                });
    }

    /**
     * Helper method to disable the sign in button
     */
    private void disableSignIn() {
        mSignInLinearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_400));
        mButtonSignIn.setEnabled(false);
        mButtonSignIn.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
    }
}
