package org.lntu.online.storage;

import android.content.Context;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.lntu.online.model.entity.ClassTable;
import org.lntu.online.model.entity.LoginInfo;
import org.lntu.online.model.entity.Student;
import org.lntu.online.model.entity.UserType;

import java.util.Date;

public final class LoginShared {

    private LoginShared() {}

    private static final String TAG = "LoginShared";

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LOGIN_TOKEN = "loginToken";
    private static final String KEY_HOLD_ONLINE = "holdOnline";

    private static String userId;
    private static String loginToken;

    public static void login(Context context, LoginInfo info, boolean holdOnline) {
        SharedWrapper sharedWrapper = SharedWrapper.with(context, TAG);
        sharedWrapper.setString(KEY_USER_ID, info.getUserId());
        sharedWrapper.setString(KEY_LOGIN_TOKEN, info.getLoginToken());
        sharedWrapper.setBoolean(KEY_HOLD_ONLINE, holdOnline); // 是否保持在线状态
        userId = info.getUserId();
        loginToken = info.getLoginToken();
    }

    public static void logout(Context context) {
        SharedWrapper.with(context, TAG).clear();
        userId = null;
        loginToken = null;
    }

    public static String getUserId(Context context) {
        if (TextUtils.isEmpty(userId)) {
            userId = SharedWrapper.with(context, TAG).getString(KEY_USER_ID, null);
        }
        return userId;
    }

    public static String getLoginToken(Context context) {
        if (TextUtils.isEmpty(loginToken)) {
            loginToken = SharedWrapper.with(context, TAG).getString(KEY_LOGIN_TOKEN, null);
        }
        return loginToken;
    }

    public static boolean isHoldOnline(Context context) {
        return SharedWrapper.with(context, TAG).getBoolean(KEY_HOLD_ONLINE, false);
    }

}