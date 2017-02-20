package com.thomaskioko.lambdademo;

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

import com.jakewharton.rxbinding.widget.RxTextView;

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
import static com.thomaskioko.lambdademo.utils.StringUtils.validatePasswordMatch;


/**
 * @author kioko
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_input_full_names)
    TextInputLayout mFullNamesInputLayout;
    @BindView(R.id.txt_input_email)
    TextInputLayout mEmailInputLayout;
    @BindView(R.id.txt_input_password)
    TextInputLayout mPasswordInputLayout;
    @BindView(R.id.txt_input_confirm_password)
    TextInputLayout mConfirmPasswordInputLayout;
    @BindView(R.id.et_full_names)
    EditText mFullNameEdiText;
    @BindView(R.id.et_password)
    EditText mPasswordEditText;
    @BindView(R.id.et_confirm_password)
    EditText mConfirmPasswordEditText;
    @BindView(R.id.et_email)
    EditText mEmailEditText;
    @BindView(R.id.linear_layout_sign_in)
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
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_create_account));
        }

        Observable<CharSequence> fullNameObservable = RxTextView.textChanges(mFullNameEdiText);
        Observable<CharSequence> emailObservable = RxTextView.textChanges(mEmailEditText);
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEditText);
        Observable<CharSequence> confirmPasswordObservable = RxTextView.textChanges(mConfirmPasswordEditText);

        Subscription emailSubscription = emailObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(charSequence -> hideErrorMessage(mEmailInputLayout))
                .debounce(mDebounceLength, TimeUnit.SECONDS) //Emit next item after 400 sec.
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
                .debounce(mDebounceLength, TimeUnit.SECONDS)
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

        Subscription confirmPasswordSubscription = confirmPasswordObservable
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(mDebounceLength, TimeUnit.SECONDS)
                .doOnNext(charSequence -> hideErrorMessage(mConfirmPasswordInputLayout))
                .subscribe(charSequence -> {
                            if (!validatePassword(charSequence.toString())) {
                                showErrorMessage(mConfirmPasswordInputLayout, getString(R.string.error_message_password_different));
                            } else {
                                hideErrorMessage(mConfirmPasswordInputLayout);
                            }
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                        });

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
                emailObservable, passwordObservable, confirmPasswordObservable,
                (email, password, confirmPassword) ->
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
        mSignInLinearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        mButtonSignIn.setEnabled(true);
        mButtonSignIn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
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
