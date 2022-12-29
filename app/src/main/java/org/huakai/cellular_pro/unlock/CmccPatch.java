package org.huakai.cellular_pro.unlock;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class CmccPatch implements IXposedHookLoadPackage {

    private ArrayList<String> ignorePkgs = new ArrayList();


    private static void log(String msg) {
        Log.d("CmccPatch", msg);
        XposedBridge.log("***=> "+msg);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (loadPackageParam.packageName.equals("android"))
        {
            ignorePkgs.add("com.cmcc.cmvideo");
            ignorePkgs.add("com.jy.xposed.skip");
            ignorePkgs.add("com.jin10");

            log("packageName=" + loadPackageParam.packageName);
            Class cls = XposedHelpers.findClassIfExists("com.android.server.hans.freeze.HansCGroup", loadPackageParam.classLoader);
            if(cls == null) return;
            log("found HansCGroup");
            final Class<?> OplusHansPackage= XposedHelpers.findClass("com.android.server.hans.OplusHansPackage", loadPackageParam.classLoader);
            final Class<?> ProcessInfo = XposedHelpers.findClass("com.android.server.am.ProcessInfo", loadPackageParam.classLoader);
            final Class<?> ProcessRecord = XposedHelpers.findClass("com.android.server.am.ProcessRecord", loadPackageParam.classLoader);
            final Class<?> KillContext = XposedHelpers.findClass("com.android.server.am.OplusOsenseKillAction$KillContext", loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod("com.android.server.am.OplusOsenseKillAction", loadPackageParam.classLoader,
                    "killOneProcessLocked", KillContext, ProcessInfo, String.class,
                    new XC_MethodHook(){
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object processInfo = param.args[1];
                            Object mToBeKill = null;
                            Field[] fields = processInfo.getClass().getDeclaredFields();	// 获取对象的所有属性
                            for (Field item : fields) {
                                String name = item.getName();
                                if("mToBeKill".equals(name)){
                                    mToBeKill = item.get(processInfo);
                                    break;
                                }
                            }
                            if (mToBeKill == null) return;

                            Method method = ProcessRecord.getMethod("toShortString", new Class[] {});
                            Object app = method.invoke(mToBeKill, new Object[]{});
                            for(String pkg : ignorePkgs){
                                if(app.toString().indexOf(pkg)!=-1) {
                                    log("ignore " + pkg + " killing action");
                                    param.setResult(null);
                                }
                            }
                        }
                    }
            );

            XposedHelpers.findAndHookMethod("com.android.server.hans.scene.HansSceneManager", loadPackageParam.classLoader,
                    "isImportantForSceneCombo", OplusHansPackage,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object oplusHansPackage = param.args[0];
                            Method method = OplusHansPackage.getMethod("getPkgName", new Class[]{});
                            String PkgName = (String)method.invoke(oplusHansPackage, new Object[]{});
                            if (ignorePkgs.contains(PkgName)) {
                                log("make " + PkgName + " important");
                                param.setResult(true);
                            }
                        }
                    }
            );


//            XposedHelpers.findAndHookMethod("s.h.e.l.l.S", loadPackageParam.classLoader,
//                    "l", Context.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            log("on attachBaseContext ");
//                            Context context = (Context) param.args[0];
//                            ClassLoader classLoader =context.getClassLoader();
//                            XposedHelpers.findAndHookMethod("com.android.server.am.ActivityManagerService", classLoader, "forceStopPackage", String.class, Integer.class,
//                                    new XC_MethodHook(){
//                                        @Override
//                                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                            super.afterHookedMethod(param);
//                                            log(param.args[0].toString());
//                                        }
//                                    }
//                            );
//                        }
//                    }
//            );
        }
        else if (loadPackageParam.packageName.indexOf("make.more.r2d2.cellular_pro")!=-1) {
            XposedHelpers.findAndHookMethod("com.stub.StubApp", loadPackageParam.classLoader,
                    "getOrigApplicationContext", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Context context = (Context) param.args[0];
                            ClassLoader classLoader =context.getClassLoader();
                            XposedHelpers.findAndHookMethod("make.more.r2d2.cellular_pro.fragment.ViewPagerFragment", classLoader, "isVip", new XC_MethodHook(){
                                        @Override
                                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                            super.afterHookedMethod(param);
                                            param.setResult(true);
                                        }
                                    }
                            );
                        }
                    }
            );
        }
    }
}
