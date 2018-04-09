package net.derohimat.popularmovies.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import net.derohimat.baseapp.util.BasePreferenceUtils;
import net.derohimat.popularmovies.util.Constant;

public class PreferencesHelper extends BasePreferenceUtils {

    private static SharedPreferences mPref;

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS_DAILY = "prefs_daily";
    private static final String KEY_NOTIFICATIONS_UPCOMING = "prefs_upcoming";

    public PreferencesHelper(Context context) {
        mPref = getSharedPreference(context);
    }

    public long getUserId() {
        return mPref.getLong(KEY_USER_ID, 1);
    }

    public void setUserId(long userId) {
        mPref.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public String getLanguage() {
        return mPref.getString(KEY_LANGUAGE, Constant.LANG_EN);
    }

    public void setLanguage(String language) {
        mPref.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public boolean getDailyPrefs() {
        return mPref.getBoolean(KEY_NOTIFICATIONS_DAILY, false);
    }

    public void setDailyPrefs(boolean dailyPrefs) {
        mPref.edit().putBoolean(KEY_NOTIFICATIONS_DAILY, dailyPrefs).apply();
    }

    public boolean getUpcomingPrefs() {
        return mPref.getBoolean(KEY_NOTIFICATIONS_UPCOMING, false);
    }

    public void setUpcomingPrefs(boolean upcomingPrefs) {
        mPref.edit().putBoolean(KEY_NOTIFICATIONS_UPCOMING, upcomingPrefs).apply();
    }

}
