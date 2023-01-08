package org.huakai.cellular_pro.unlock;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Switch protectSwitch, restartSwitch, packageSwitch, serviceSwitch, showIconSwitch;

    private static ConfigUtils configUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                    return;
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111111);
            }
        }
        init();
    }

    private void init(){
        configUtils = ConfigUtils.getInstance();
        protectSwitch = findViewById(R.id.protect_switch);
        restartSwitch = findViewById(R.id.restart_switch);
        packageSwitch = findViewById(R.id.package_switch);
        serviceSwitch = findViewById(R.id.service_switch);
        showIconSwitch = findViewById(R.id.main_button);
        protectSwitch.setChecked(configUtils.getBoolean("use_protect_list"));
        restartSwitch.setChecked(configUtils.getBoolean("use_restart_list"));
        packageSwitch.setChecked(configUtils.getBoolean("use_package_list"));
        serviceSwitch.setChecked(configUtils.getBoolean("use_service_list"));
        showIconSwitch.setChecked(configUtils.getBoolean("show_icon"));
    }

    private void hide() {
        getPackageManager().setComponentEnabledSetting(new ComponentName(MainActivity.this, "org.huakai.cellular_pro.unlock.MainActivity_Alias"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void show() {
        getPackageManager().setComponentEnabledSetting(new ComponentName(MainActivity.this, "org.huakai.cellular_pro.unlock.MainActivity_Alias"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void onSwitchClick(View view) throws Exception {
        boolean isChecked = ((Switch) view).isChecked();
        switch (view.getId()){
            case R.id.protect_switch:
                configUtils.put("use_protect_list", isChecked);
                break;
            case R.id.restart_switch:
                configUtils.put("use_restart_list", isChecked);
                break;
            case R.id.package_switch:
                configUtils.put("use_package_list", isChecked);
                break;
            case R.id.service_switch:
                configUtils.put("use_service_list", isChecked);
                break;
            case R.id.main_button:
                if(isChecked)
                    hide();
                else
                    show();
                configUtils.put("show_icon", isChecked);
                break;
            default:
                break;
        }
        configUtils.saveConfig();
    }

}