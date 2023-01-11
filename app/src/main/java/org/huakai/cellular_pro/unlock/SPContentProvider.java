package org.huakai.cellular_pro.unlock;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SPContentProvider extends ContentProvider {

    private static ConfigUtils configUtils;
    private static final UriMatcher mMatcher;
    public static final String AUTOHORITY = "org.huakai.cellular_pro.unlock.provider";
    public static final int USE_PROTECT_LIST = 1;
    public static final int USE_RESTART_LIST = 2;
    public static final int USE_PACKAGE_LIST = 3;
    public static final int USE_SERVICE_LIST = 4;

    static{
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(AUTOHORITY,"use_protect_list", USE_PROTECT_LIST);
        mMatcher.addURI(AUTOHORITY, "use_restart_list", USE_RESTART_LIST);
        mMatcher.addURI(AUTOHORITY,"use_package_list", USE_PACKAGE_LIST);
        mMatcher.addURI(AUTOHORITY, "use_service_list", USE_SERVICE_LIST);
    }

    @Override
    public boolean onCreate() {
        if(configUtils==null){
            configUtils = ConfigUtils.getInstance();
        }
        return configUtils != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        boolean flag = false;
        switch (mMatcher.match(uri)) {
            case USE_PROTECT_LIST:
                flag = configUtils.getBoolean("use_protect_list");
                break;
            case USE_RESTART_LIST:
                flag =  configUtils.getBoolean("use_restart_list");
                break;
            case USE_PACKAGE_LIST:
                flag = configUtils.getBoolean("use_package_list");
                break;
            case USE_SERVICE_LIST:
                flag = configUtils.getBoolean("use_service_list");
                break;
        }
        return Boolean.toString(flag);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}