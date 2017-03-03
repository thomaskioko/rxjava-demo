package com.thomaskioko.lambdademo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.thomaskioko.lambdademo.R;
import com.thomaskioko.lambdademo.model.User;

import java.util.concurrent.TimeUnit;

import butterknife.BindInt;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.thomaskioko.lambdademo.utils.StringUtils.validateEmail;
import static com.thomaskioko.lambdademo.utils.StringUtils.validatePassword;
import static com.thomaskioko.lambdademo.utils.StringUtils.validatePasswordMatch;


/**
 * @author kioko
 */

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.txt_input_full_names)
    TextInputLayout mFullNamesInputLayout;
    @Bind(R.id.txt_input_email)
    TextInputLayout mEmailInputLayout;
    @Bind(R.id.txt_input_password)
    TextInputLayout mPasswordInputLayout;
    @Bind(R.id.txt_input_confirm_password)
    TextInputLayout mConfirmPasswordInputLayout;
    @Bind(R.id.et_full_names)
    EditText mFullNameEdiText;
    @Bind(R.id.et_password)
    EditText mPasswordEditText;
    @Bind(R.id.et_confirm_password)
    EditText mConfirmPasswordEditText;
    @Bind(R.id.et_email)
    EditText mEmailEditText;
    @Bind(R.id.linear_layout_sign_in)
    LinearLayout mSignInLinearLayout;
    @Bind(R.id.btn_register)
    Button mButtonRegister;
    @Bind(R.id.btn_sign_in)
    Button mButtonSignIn;

    @BindInt(R.integer.debounce_length)
    int mDebounceLength;

    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_create_account));
            actionBar.setHomeButtonEnabled(true);
        }


        Observable<CharSequence> fullNameObservable = RxTextView.textChanges(mFullNameEdiText);
        Observable<CharSequence> emailObservable = RxTextView.textChanges(mEmailEditText);
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEditText);
        Observable<CharSequence> confirmPasswordObservable = RxTextView.textChanges(mConfirmPasswordEditText);

        Subscription fullNamesSubscription = fullNameObservable
                .doOnNext(charSequence -> hideErrorMessage(mFullNamesInputLayout))
                .debounce(mDebounceLength, TimeUnit.SECONDS) //Emit next item after 400 sec.
                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if (charSequence.toString().isEmpty()) {
                        showErrorMessage(mEmailInputLayout, getString(R.string.error_message_invalid_names));
                    } else {
                        hideErrorMessage(mEmailInputLayout);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                });

        Subscription emailSubscription = emailObservable
                .doOnNext(charSequence -> hideErrorMessage(mEmailInputLayout))
                .debounce(mDebounceLength, TimeUnit.MILLISECONDS)
                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
                .observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(charSequence -> {
                            boolean isEmailValid = validateEmail(charSequence.toString());
                            if (!isEmailValid) {
                                showErrorMessage(mEmailInputLayout, getString(R.string.error_message_invalid_email));
                            } else {
                                hideErrorMessage(mEmailInputLayout);
                            }
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                        });

        Subscription passwordSubscription = passwordObservable
                .doOnNext(charSequence -> hideErrorMessage(mPasswordInputLayout))
                .debounce(mDebounceLength, TimeUnit.MILLISECONDS)
                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
                .observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(charSequence -> {
                            boolean isPasswordValid = validatePassword(charSequence.toString());
                            if (!isPasswordValid) {
                                showErrorMessage(mPasswordInputLayout, getString(R.string.error_message_invalid_password));
                            } else {
                                hideErrorMessage(mPasswordInputLayout);
                            }
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                        });

        Subscription confirmPasswordSubscription = confirmPasswordObservable
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(mDebounceLength, TimeUnit.SECONDS)
                .doOnNext(charSequence -> hideErrorMessage(mConfirmPasswordInputLayout))
                .subscribe(charSequence -> {
                            boolean isPasswordValid = validatePassword(charSequence.toString());
                            if (!isPasswordValid) {
                                showErrorMessage(mConfirmPasswordInputLayout, getString(R.string.error_message_password_different));
                            } else {
                                hideErrorMessage(mConfirmPasswordInputLayout);
                            }
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                        });

        mCompositeSubscription.add(fullNamesSubscription);
        mCompositeSubscription.add(emailSubscription);
        mCompositeSubscription.add(passwordSubscription);
        mCompositeSubscription.add(confirmPasswordSubscription);

        Subscription validatePasswordsSubscription = Observable.combineLatest(passwordObservable, confirmPasswordObservable,
                (password, confirmPassword) -> validatePasswordMatch(password.toString(), confirmPassword.toString()))
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        showErrorMessage(mConfirmPasswordInputLayout, getString(R.string.error_message_password_different));
                    } else {
                        hideErrorMessage(mConfirmPasswordInputLayout);
                    }
                });

        Subscription fieldValidationSubscription = Observable.combineLatest(
                fullNameObservable, emailObservable, passwordObservable, confirmPasswordObservable,
                (fullNames, email, password, confirmPassword) ->
                        (!fullNames.toString().equals("")) &&
                                validateEmail(email.toString()) &&
                                validatePassword(password.toString()) &&
                                validatePasswordMatch(password.toString(), confirmPassword.toString())

        )
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        enableSignIn();
                    } else {
                        disableSignIn();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                });


        mCompositeSubscription.add(validatePasswordsSubscription);
        mCompositeSubscription.add(fieldValidationSubscription);

        RxView.clicks(mButtonSignIn)
                .subscribe(aVoid -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_register;
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
        mSignInLinearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        mButtonRegister.setEnabled(true);
        mButtonRegister.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        mRealm.beginTransaction();

        User user = mRealm.createObject(User.class);
        user.setFullNames(mFullNameEdiText.getText().toString());
        user.setEmail(mEmailEditText.getText().toString());
        user.setPassword(mPasswordEditText.getText().toString());

        mRealm.commitTransaction();

        RxView.clicks(mButtonRegister)
                .subscribe(aVoid -> {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                });


    }

    /**
     * Helper method to disable the sign in button
     */
    private void disableSignIn() {
        mSignInLinearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_400));
        mButtonRegister.setEnabled(false);
        mButtonRegister.setTextColor(ContextCompat.getColor(this, R.color.grey_500));
    }
}
