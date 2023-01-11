package org.huakai.cellular_pro.unlock;


import android.content.Context;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Config1Utils {

    private static Config1Utils configUtils;
    private static SharedPreferenceUtils sharedPreferenceUtils;

    private Config1Utils(Context outContext) {
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(outContext);
    }

    public static Config1Utils getInstance(Context outContext){
        if (configUtils == null) {
            synchronized (Config1Utils.class) {
                if (configUtils == null) {
                    configUtils = new Config1Utils(outContext);
                }
            }
        }
        return configUtils;
    }

    public boolean getBoolean(String name){
        return sharedPreferenceUtils.getBoolean(name, false);
    }

    public void put(String name, boolean value) {
        sharedPreferenceUtils.putBoolean(name, value);
    }

}
