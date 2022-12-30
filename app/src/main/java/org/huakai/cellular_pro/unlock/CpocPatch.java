package org.huakai.cellular_pro.unlock;



import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by La0s on 2018/6/20.
 */

public class CpocPatch implements IXposedHookLoadPackage {

    private static Set<String> ignorePkgs = new HashSet<>();
    private static Set<String> ignoreProcesses = new HashSet<>();
    private static Set<String> ignoreServices = new HashSet<>();

    static {
        ignorePkgs.add("com.cmcc.cmvideo");
        ignorePkgs.add("com.jy.xposed.skip");
        ignorePkgs.add("com.jin10");
        ignorePkgs.add("com.termux");
        ignorePkgs.add("com.github.shadowsocks");
        ignorePkgs.add("com.tencent.mm");

        ignoreProcesses.add("com.coloros.smartsidebar:edgepanel");
        ignoreProcesses.add("com.tencent.mm:push");
        ignoreProcesses.add("com.jin10:pushcore");
        ignoreProcesses.add("com.jin10:channel");
        ignoreProcesses.add("com.github.shadowsocks:bg");


        ignoreServices.add("com.jin10/.lgd.biz.push.PushService");
    }

    private void log(String a){
        XposedBridge.log("****cpocxposedplugin=> "+a);
        System.out.println(a);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals("android")) {
            log("packageName=" + loadPackageParam.packageName);
            Class OplusHansManager = XposedHelpers.findClassIfExists("com.android.server.am.OplusHansManager", loadPackageParam.classLoader);
            if (OplusHansManager == null) return;
            log("found OplusHansManager");
            final Class<?> OplusHansPackage= XposedHelpers.findClass("com.android.server.hans.OplusHansPackage", loadPackageParam.classLoader);
            final Class<?> ServiceRecord = XposedHelpers.findClass("com.android.server.am.ServiceRecord", loadPackageParam.classLoader);
            final Class<?> ActiveServicesExtImpl = XposedHelpers.findClass("com.android.server.am.ActiveServicesExtImpl", loadPackageParam.classLoader);
            final Class<?> ProcessRecord = XposedHelpers.findClass("com.android.server.am.ProcessRecord", loadPackageParam.classLoader);


            //添加被保护的包名和被保护的进程信息
            XposedHelpers.findAndHookConstructor("com.android.server.am.ConfigUtil", loadPackageParam.classLoader,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws IllegalAccessException {
                    Field[] fields = param.thisObject.getClass().getDeclaredFields();	// 获取对象的所有属性
                    for (Field item : fields) {
                        String name = item.getName();
                        if ("mProtectPkg".equals(name)) {
                            item.setAccessible(true);
                            Set<String> mProtectPkg = (Set<String>)item.get(param.thisObject);
                            mProtectPkg.addAll(ignorePkgs);
                        } else if("mProtectProcess".equals(name)){
                            item.setAccessible(true);
                            Set<String> mProtectPkg = (Set<String>) item.get(param.thisObject);
                            mProtectPkg.clear();
                            mProtectPkg.addAll(ignoreProcesses);
                        }
                    }
//                    Set<String> mProtectPkg =  (Set<String>)XposedHelpers.getObjectField(param.thisObject, "mProtectPkg");
//                    for(String pkg : mProtectPkg){
//                        log("mProtectPkg element "+pkg);
//                    }
//                    Set<String> mProtectProcess =  (Set<String>)XposedHelpers.getObjectField(param.thisObject, "mProtectProcess");
//                    for(String pkg : mProtectProcess){
//                        log("mProtectProcess element "+pkg);
//                    }
                }
            });

            //添加可重起的包名
            XposedHelpers.findAndHookConstructor("com.android.server.am.OplusAppStartupConfig", loadPackageParam.classLoader,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            List mProtectPkg = Arrays.asList(new String[] {
                                    "com.tencent.mm",
                                    "com.tencent.mobileqq",
                                    "com.instagram.android",
                                    "jp.naver.line.android",
                                    "com.whatsapp",
                                    "com.path",
                                    "com.truecaller",
                                    "com.snapchat.android",
                                    "com.google.android.gm",
                                    "com.jy.xposed.skip",
                                    "com.jin10"
                            });
                            XposedHelpers.setObjectField(param.thisObject, "mDefaultRestartServiceWhiteListExp", mProtectPkg);
//                    Set<String> mProtectPkg =  (Set<String>)XposedHelpers.getObjectField(param.thisObject, "mProtectPkg");
//                    for(String pkg : mProtectPkg){
//                        log("mProtectPkg element "+pkg);
//                    }
//                    Set<String> mProtectProcess =  (Set<String>)XposedHelpers.getObjectField(param.thisObject, "mProtectProcess");
//                    for(String pkg : mProtectProcess){
//                        log("mProtectProcess element "+pkg);
//                    }
                        }
                    });



//            XposedHelpers.findAndHookMethod("com.android.server.hans.scene.HansSceneManager", loadPackageParam.classLoader,
//                    "freezeForPreload", OplusHansPackage,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            Object oplusHansPackage = param.args[0];
//                            Method method = OplusHansPackage.getMethod("getPkgName", new Class[]{});
//                            String PkgName = (String)method.invoke(oplusHansPackage, new Object[]{});
//                            if (ignorePkgs.contains(PkgName)) {
//                                method = OplusHansPackage.getMethod("getUid", new Class[]{});
//                                int PkgUid = (int)method.invoke(oplusHansPackage, new Object[]{});
//                                Object hansManager = XposedHelpers.callStaticMethod(OplusHansManager, "getInstance");
//                                XposedHelpers.callMethod(hansManager, "setNetCare", PkgUid);
//                                param.setResult(null);
//                                log("refuse freeze pgname=" + PkgName + " uid=" + PkgUid + " in freezeForPreload");
//                            }
//                        }
//                    }
//            );
//
//
//            XposedHelpers.findAndHookMethod("com.android.server.hans.scene.HansSceneManager", loadPackageParam.classLoader,
//                    "freezeForSceneCombo", OplusHansPackage,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            Object oplusHansPackage = param.args[0];
//                            Method method = OplusHansPackage.getMethod("getPkgName", new Class[]{});
//                            String PkgName = (String)method.invoke(oplusHansPackage, new Object[]{});
//                            if (ignorePkgs.contains(PkgName)) {
//                                method = OplusHansPackage.getMethod("getUid", new Class[]{});
//                                int PkgUid = (int)method.invoke(oplusHansPackage, new Object[]{});
//                                Object hansManager = XposedHelpers.callStaticMethod(OplusHansManager, "getInstance");
//                                XposedHelpers.callMethod(hansManager, "setNetCare", PkgUid);
//                                param.setResult(null);
//                                log("refuse freeze pgname=" + PkgName + " uid=" + PkgUid + " in freezeForSceneCombo");
//                            }
//                        }
//                    }
//            );
//
//            XposedHelpers.findAndHookMethod("com.android.server.hans.scene.HansSceneManager", loadPackageParam.classLoader,
//                    "freezeDirectlyForSceneCombo", OplusHansPackage,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            Object oplusHansPackage = param.args[0];
//                            Method method = OplusHansPackage.getMethod("getPkgName", new Class[]{});
//                            String PkgName = (String)method.invoke(oplusHansPackage, new Object[]{});
//                            if (ignorePkgs.contains(PkgName)) {
//                                method = OplusHansPackage.getMethod("getUid", new Class[]{});
//                                int PkgUid = (int)method.invoke(oplusHansPackage, new Object[]{});
//                                Object hansManager = XposedHelpers.callStaticMethod(OplusHansManager, "getInstance");
//                                XposedHelpers.callMethod(hansManager, "setNetCare", PkgUid);
//                                param.setResult(null);
//                                log("refuse freeze pgname=" + PkgName + " uid=" + PkgUid + " in freezeForSceneCombo");
//                            }
//                        }
//                    }
//            );


            //防止service被停止
            Method skipStopInBackgroundBegin = XposedHelpers.findMethodBestMatch(ActiveServicesExtImpl, "skipStopInBackgroundBegin",
                    ServiceRecord, Integer.class);
            if(skipStopInBackgroundBegin!=null) {
                log("found skipStopInBackgroundBegin method, hook it");
                XposedBridge.hookMethod(skipStopInBackgroundBegin, new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object paramServiceRecord = param.args[0];
                        Object shortInstanceName = null;
                        Field[] fields = paramServiceRecord.getClass().getDeclaredFields();	// 获取对象的所有属性
                        for (Field item : fields) {
                            String name = item.getName();
                            if("shortInstanceName".equals(name)){
                                item.setAccessible(true);
                                shortInstanceName = item.get(paramServiceRecord);
                                break;
                            }
                        }
                        if (shortInstanceName == null) return;

                        for(String service : ignoreServices){
                            if(shortInstanceName.toString().equals(service)) {
//                                log("keepalive [skipStopInBackgroundBegin]" + shortInstanceName);
                                param.setResult(true);
                            }
                        }
                    }
                });
            }


            //提高优先级防止被杀
            XposedHelpers.findAndHookMethod("com.android.server.hans.scene.HansSceneManager", loadPackageParam.classLoader,
                    "isImportantForSceneCombo", OplusHansPackage,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object oplusHansPackage = param.args[0];
                            Method method = OplusHansPackage.getMethod("getPkgName", new Class[]{});
                            String PkgName = (String)method.invoke(oplusHansPackage, new Object[]{});
                            if (ignorePkgs.contains(PkgName)) {
//                                log("make " + PkgName + " important");
                                param.setResult(true);
                            }
                        }
                    }
            );

            //防止被强杀 killing
            Method method = XposedHelpers.findMethodBestMatch(ProcessRecord, "killLocked", String.class, String.class, Integer.class, Integer.class, Boolean.class);
            if(method!=null){
                log("found killLocked method, hook it");
                XposedBridge.hookMethod( method, new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if(param.args[0].toString().indexOf("cpu")!=-1) {
                            Object processRecord = param.thisObject;
                            Method method = ProcessRecord.getMethod("toShortString", new Class[]{});
                            Object app = method.invoke(processRecord, new Object[]{});
                            for (String pkg : ignorePkgs) {
                                if (app.toString().indexOf(pkg) != -1) {
                                    log("refuse be killed reason=" + param.args[0] + " description=" + param.args[1] + " （by hook ProcessRecord）" + pkg);
                                    param.setResult(null);
                                }
                            }
                        }
                    }
                });
            }


        }

    }
}
