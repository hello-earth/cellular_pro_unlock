package org.huakai.cellular_pro.unlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * sp工具类：常用
 */
public class SharedPreferenceUtils {
    private static SharedPreferenceUtils sInstance;
    private static SharedPreferences sSettings;

    public static synchronized SharedPreferenceUtils getInstance(Context outContext) {
        if (sInstance == null) {
            sInstance = new SharedPreferenceUtils(outContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File dataDir = new File(outContext.getApplicationInfo().dataDir);
                File prefsDir = new File(dataDir, "shared_prefs");
                File prefsFile = new File(prefsDir, "protect_config.xml");
                if (prefsFile.exists()) {
                    prefsFile.setReadable(true, false);
                    prefsFile.setExecutable(true, false);
                    prefsFile.setWritable(true, false);
                }
            }
        }
        return sInstance;
    }

    private SharedPreferenceUtils(Context outContext) {
        sSettings = outContext.getSharedPreferences("protect_config", 0);
    }


    public void putString(String key, String value) {
        sSettings.edit().putString(key, value).apply();
    }


    public String getString(String key) {
        return sSettings.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return sSettings.getString(key, defValue);
    }

    public Double getDouble(String key) {
        String retStr = getString(key, null);
        Double ret = null;
        try {
            ret = Double.parseDouble(retStr);
        } catch (Exception e) {
        }
        return ret;
    }

    public void putBoolean(String key, boolean bool) {
        synchronized (SharedPreferenceUtils.class) {
            sSettings.edit().putBoolean(key, bool).apply();
        }
    }

    public void putInteger(String key, int integer) {
        synchronized (SharedPreferenceUtils.class) {
            sSettings.edit().putInt(key, integer).apply();
        }
    }

    public void putLong(String key, long lon) {
        synchronized (SharedPreferenceUtils.class) {
            sSettings.edit().putLong(key, lon).apply();
        }
    }

    public Boolean getBoolean(String key, boolean defValue) {
        synchronized (SharedPreferenceUtils.class) {
            return sSettings.getBoolean(key, defValue);
        }

    }

    public int getInteger(String key, int defValue) {
        synchronized (SharedPreferenceUtils.class) {
            return sSettings.getInt(key, defValue);
        }
    }

    public long getLong(String key, long defValue) {
        synchronized (SharedPreferenceUtils.class) {
            return sSettings.getLong(key, defValue);
        }
    }


    public void putHashMap(String key, HashMap<String, String> map) {
        JSONObject ret = new JSONObject(map);
        synchronized (SharedPreferenceUtils.class) {
            sSettings.edit().putString(key, ret.toString()).apply();
        }
    }

    public HashMap<String, String> getHashMap(String key) {
        return getHashMapByKey(key);
    }

    public HashMap<String, String> getHashMapByKey(String key) {
        HashMap<String, String> ret = new HashMap<>();

        String mapStr = getString(key, "{}");
        JSONObject mapJson;
        try {
            mapJson = new JSONObject(mapStr);
        } catch (Exception e) {
            return ret;
        }

        Iterator<String> it = mapJson.keys();
        while (it.hasNext()) {
            String theKey = it.next();
            String theValue = mapJson.optString(theKey);
            ret.put(theKey, theValue);
        }

        return ret;
    }

    public void putArrayList(String key, ArrayList<String> list) {
        JSONArray ret = new JSONArray(list);
        putString(key, ret.toString());
    }

    public ArrayList<String> getArrayList(String key) {
        ArrayList<String> ret = new ArrayList<>();

        String listStr = getString(key, "{}");
        JSONArray listJson;
        try {
            listJson = new JSONArray(listStr);
        } catch (Exception e) {
            return ret;
        }

        for (int i = 0; i < listJson.length(); i++) {
            String temp = listJson.optString(i);
            ret.add(temp);
        }

        return ret;
    }


    public void removeByKey(String key) {
        synchronized (SharedPreferenceUtils.class) {
            sSettings.edit().remove(key).apply();
        }
    }

    public void putJsonArray(String key, JSONArray value) {
        putString(key, value.toString());
    }

    public JSONArray getJsonArray(String key) {
        JSONArray ret;
        String jsonArrayStr = getString(key);
        try {
            ret = new JSONArray(jsonArrayStr);
        } catch (JSONException e) {
            ret = null;
        }
        return ret;
    }

    public void putObject(String key, Object obj) {
        String toSave = JSON.toJSONString(obj);
        putString(key, toSave);
    }

    public <T> T getObject(String key, Class<T> cla) {
        String temp = getString(key);

        if (temp == null || temp.trim().length() == 0) {
            return null;
        }

        return JSON.parseObject(temp, cla);
    }
}
