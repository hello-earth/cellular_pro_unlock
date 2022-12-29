package org.huakai.cellular_pro.unlock;



import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by La0s on 2018/6/20.
 */

public class CpocPatch implements IXposedHookLoadPackage {


    private void log(String a){
        XposedBridge.log("****cpocxposedplugin=> "+a);
        System.out.println(a);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals("com.scenix.mlearning")) {    //过滤包名
            log("the fake loader = " + loadPackageParam.classLoader.hashCode());
            XposedHelpers.findAndHookConstructor("javax.crypto.spec.SecretKeySpec", loadPackageParam.classLoader, Array.newInstance(byte.class,0).getClass(), String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log("_360Firm beforeHookedMethod 1");
                    //获取到Context对象，通过这个对象来获取classloader
                    byte[] context = (byte[]) param.args[0];
                    //获取classloader，之后hook加固后的就使用这个classloader
                    log("the key = "+ new String(context) + " and method="+param.args[1]);
                }
            });
            XposedHelpers.findAndHookConstructor("javax.crypto.spec.IvParameterSpec", loadPackageParam.classLoader, Array.newInstance(byte.class,0).getClass(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log("_360Firm beforeHookedMethod 2");
                    //获取到Context对象，通过这个对象来获取classloader
                    byte[] context = (byte[]) param.args[0];
                    //获取classloader，之后hook加固后的就使用这个classloader
                    log("iv = "+ new String(context) );
                }
            });
        }
    }
}
