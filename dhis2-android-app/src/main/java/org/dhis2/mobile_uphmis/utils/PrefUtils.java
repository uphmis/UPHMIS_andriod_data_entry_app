/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile_uphmis.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class PrefUtils {
    private static final String APP_DATA = "APP_DATA";
    private static final String USER_DATA = "USER_DATA";
    private static final String SERVER_DATA = "SERVER_DATA";
    private static final String RESOURCE_STATE = "RESOURCE_STATE";
    private static final String OFFLINE_REPORTS_INFO = "OFFLINE_REPORTS_INFO";

    private static final String FORMS_DOWNLOAD_STATE = "areFormsDownloaded";
    private static final String CREDENTIALS = "credentials";
    private static final String LOGGED_IN = "loggedIn";
    private static final String ACCOUNT_NEEDS_UPDATE = "accountNeedsUpdate";
    private static final String URL = "url";
    private static final String USER_NAME = "userName";
    private static final String LOCALE = "locale";
    private static final String ORG = "orgid";
    private static final String DSTRICT_PARENT = "dis_org";
    private static final String SERVER_VERSION = "serverVersion";
    private static final String USER_LOCALE = "hi";

    private static final String MINMAX = "minmax";
    private static final String MINMAX_MINIMUM = "minimum";
    private static final String MINMAX_MAXIMUM = "maximum";
    private static final String SCROLL = "scroll";


    private PrefUtils() { }

    public static enum Resources {
        DATASETS,
        SINGLE_EVENTS_WITHOUT_REGISTRATION,
        PROFILE_DETAILS
    }

    public static enum State {
        OUT_OF_DATE,
        UP_TO_DATE,
        REFRESHING,
        ATTEMPT_TO_REFRESH_IS_MADE
    }
    //@Sou changes to save user-locale
    public static void initAppData(Context context, String creds, String username, String url, String locale,String minimum,String maximum,String org,String dis_org) {
        Editor userData = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit();
        userData.putString(CREDENTIALS, creds);
        userData.putString(USER_NAME, username);
        userData.putString(URL, url);
        userData.putString(LOCALE, locale);
        userData.putString(MINMAX_MINIMUM, minimum);
        userData.putString(MINMAX_MAXIMUM, maximum);
        userData.putString(ORG, org);
        userData.putString(DSTRICT_PARENT, dis_org);
        userData.commit();


        Editor appData = context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).edit();
        appData.putBoolean(LOGGED_IN, true);
        appData.putBoolean(FORMS_DOWNLOAD_STATE, false);
        appData.putBoolean(ACCOUNT_NEEDS_UPDATE, false);
        appData.commit();

        // initialize shared preference for future work
        context.getSharedPreferences(OFFLINE_REPORTS_INFO, Context.MODE_PRIVATE).edit().commit();
    }

    public static void initScrollData(Context context, String scroll) {
        Editor userData = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit();
        userData.putString(SCROLL, scroll);
        userData.commit();

        Editor appData = context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).edit();
        appData.putBoolean(LOGGED_IN, true);
        appData.putBoolean(FORMS_DOWNLOAD_STATE, false);
        appData.putBoolean(ACCOUNT_NEEDS_UPDATE, false);
        appData.commit();
        // initialize shared preference for future work
        context.getSharedPreferences(OFFLINE_REPORTS_INFO, Context.MODE_PRIVATE).edit().commit();
    }


    public static void initServerData(Context context, String serverVersion) {
        Log.d(PrefUtils.class.getName(), "Server version: " + serverVersion);
        Editor serverData = context.getSharedPreferences(SERVER_DATA, Context.MODE_PRIVATE).edit();
        serverData.putString(SERVER_VERSION, serverVersion);
        serverData.commit();
    }


    public static boolean isUserLoggedIn(Context context) {
        return context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).getBoolean(LOGGED_IN, false);
    }

    public static String getCredentials(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(CREDENTIALS, null);
    }


    public static String getServerURL(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(URL, null);
    }

    public static String getUserName(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(USER_NAME, null);
    }

    //@Sou get user_locale
    public static String getLocale(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(LOCALE, null);
    }

    public static String getDstrictParent(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(DSTRICT_PARENT, null);
    }
    //@Sou get scroll
    public static String getScroll(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(SCROLL, null);
    }

    //@Sou get user_locale
    public static String getOrg(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(ORG, null);
    }
    //@Sou get get_minimum
    public static String getMinmaxMinimum(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(MINMAX_MINIMUM, null);
    }
    //@Sou get_maximum
    public static String getMinmaxMaximum(Context context) {
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).getString(MINMAX_MAXIMUM, null);
    }


    public static String getServerVersion(Context context) {
        return context.getSharedPreferences(SERVER_DATA, Context.MODE_PRIVATE).getString(SERVER_VERSION, null);
    }

    public static void eraseData(Context context) {
        context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences(OFFLINE_REPORTS_INFO, Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences(SERVER_DATA, Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences(RESOURCE_STATE, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static void setResourceState(Context context, Resources res, State state) {
        Editor editor = context.getSharedPreferences(RESOURCE_STATE, Context.MODE_PRIVATE).edit();
        editor.putString(res.toString(), state.toString()).commit();
    }

    public static State getResourceState(Context context, Resources res) {
        String state = context.getSharedPreferences(RESOURCE_STATE, Context.MODE_PRIVATE)
                .getString(res.toString(), State.OUT_OF_DATE.toString());
        return State.valueOf(state);
    }

    public static void resetResourcesState(Context context) {
        context.getSharedPreferences(RESOURCE_STATE, Context.MODE_PRIVATE).edit().clear().commit();
    }

    @Deprecated
    public static boolean areFormsAvailable(Context context) {
        return context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).getBoolean(
                FORMS_DOWNLOAD_STATE, false);
    }

    @Deprecated
    public static boolean accountInfoNeedsUpdate(Context context) {
        return context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).getBoolean(
                ACCOUNT_NEEDS_UPDATE, false);
    }

    @Deprecated
    public static void setAccountUpdateFlag(Context context, boolean flag) {
        Editor editor = context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).edit();
        editor.putBoolean(ACCOUNT_NEEDS_UPDATE, flag).commit();
    }

    @Deprecated
    public static void setFormsDownloadStateFlag(Context context, boolean downloaded) {
        Editor editor = context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).edit();
        editor.putBoolean(FORMS_DOWNLOAD_STATE, downloaded).commit();
    }

    @Deprecated
    public static void saveOfflineReportInfo(Context context, String id, String jsonReportInfo) {
        Editor editor = context.getSharedPreferences(OFFLINE_REPORTS_INFO, Context.MODE_PRIVATE).edit();
        editor.putString(id, jsonReportInfo);
        editor.commit();
    }

    @Deprecated
    public static void removeOfflineReportInfo(Context context, String id) {
        context.getSharedPreferences(OFFLINE_REPORTS_INFO, Context.MODE_PRIVATE).edit().remove(id).commit();
    }

    @Deprecated
    public static String getOfflineReportInfo(Context context, String id) {
        return context.getSharedPreferences(OFFLINE_REPORTS_INFO, Context.MODE_PRIVATE).getString(id, null);
    }
}
