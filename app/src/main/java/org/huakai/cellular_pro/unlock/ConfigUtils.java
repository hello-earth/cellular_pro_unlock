package org.huakai.cellular_pro.unlock;


import org.json.JSONException;
import org.json.JSONObject;

public class ConfigUtils {

    private static JSONObject config;
    private static ConfigUtils configUtils;
    private final static String configPath = "/data/data/org.huakai.cellular_pro.unlock/cache/protect_config.json";

    private ConfigUtils() {
        initConfig();
    }

    public static ConfigUtils getInstance(){
        if (configUtils == null) {
            synchronized (ConfigUtils.class) {
                if (configUtils == null) {
                    configUtils = new ConfigUtils();
                }
            }
        }
        return configUtils;
    }

    private void initConfig() {
        StringBuilder builder = FileUtils.readFile(configPath);
        if(builder != null) {
            try {
                config = new JSONObject(builder.toString());
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        config = new JSONObject();
    }

    public void saveConfig() throws Exception {
        if(config !=null ) {
            FileUtils.writeFile(configPath, config.toString());
        } else {
            throw new Exception("you can not save a config without initial");
        }
    }

    public boolean getBoolean(String name){
        if(config != null ) {
            try {
                return config.getBoolean(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void put(String name, boolean value) throws JSONException {
        config.put(name, value);
    }


}
