package com.thomaskioko.lambdademo.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Thomas Kioko
 */

public class StringUtils {

    private static Pattern mEmailPattern = android.util.Patterns.EMAIL_ADDRESS;

    /**
     * Helper method to validate email address
     *
     * @param email Email Address
     * @return {@link Boolean} Valid/Invalid email address.
     */
    public static boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email))
            return false;

        Matcher matcher = mEmailPattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Helper method to validate password
     *
     * @param password Password
     * @return {@link Boolean} Valid/Invalid password.
     */
    public static boolean validatePassword(String password) {
        return password.length() > 5;
    }

    /**
     * Helper method to validate password
     *
     * @param password        Password
     * @param confirmPassword Second Password
     * @return {@link Boolean} Valid/Invalid password.
     */
    public static boolean validatePasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
