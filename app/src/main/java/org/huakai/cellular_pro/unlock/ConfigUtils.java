package org.huakai.cellular_pro.unlock;


import org.json.JSONException;
import org.json.JSONObject;

public class ConfigUtils {

    private static JSONObject config;

    public static JSONObject readConfig() {
        StringBuilder builder = FileUtils.readFile("/storage/emulated/0/Android/protect_config.json");
        if(builder != null) {
            try {
                config = new JSONObject(builder.toString());
                return  config;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        config = new JSONObject();
        return config;
    }

    public static void saveConfig() throws Exception {
        if(config !=null ) {
            FileUtils.writeFile("/storage/emulated/0/Android/protect_config.json", config.toString());
        } else {
            throw new Exception("you can not save a config without initial");
        }
    }

    public static boolean getBoolean(String name){
        if(config != null ) {
            try {
                return config.getBoolean(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
