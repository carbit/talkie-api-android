package net.easyconn.sdk.talkie.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created user:young
 * Created data:17-5-16
 * Description:
 */

class SpUtil {

    static final String FILE_NAME = "SP_SDK_TALKIE";

    private static final String KEY_OPEN_ID = "OPEN_ID";

    private static final String KEY_TOKEN = "TOKEN";

    private static SharedPreferences getSharedPreferences(Context context) {
        if (context != null) {
            return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return null;
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        if (context != null) {
            return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        }
        return null;
    }

    static void remove(Context context, String key) {
        if (!TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if (editor != null) {
                editor.remove(key).apply();
            }
        }
    }

    static String getString(Context context, String key) {
        if (!TextUtils.isEmpty(key)) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            if (sharedPreferences != null) {
                return sharedPreferences.getString(key, null);
            }
        }
        return null;
    }

    static void putString(Context context, String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if (editor != null) {
                editor.putString(key, value).apply();
            }
        }
    }

    static String getOpenId(Context context) {
        return getString(context, KEY_OPEN_ID);
    }

    static void putOpenId(Context context, String openId) {
        putString(context, KEY_OPEN_ID, openId);
    }

    static void clearOpenId(Context context) {
        remove(context, KEY_OPEN_ID);
    }

    static String getToken(Context context) {
        return getString(context, KEY_TOKEN);
    }

    static void putToken(Context context, String token) {
        putString(context, KEY_TOKEN, token);
    }

    static void clearToken(Context context) {
        remove(context, KEY_TOKEN);
    }

    static boolean isLogin(Context context) {
        return !TextUtils.isEmpty(getOpenId(context));
    }

}
