package org.huakai.cellular_pro.unlock;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Field;
import android.content.Context;
import android.util.Log;


public class CellularPatch implements IXposedHookLoadPackage {

    private static void log(String msg) {
        Log.i("CellularPatch",msg);
        XposedBridge.log(msg);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (loadPackageParam.packageName.indexOf("make.more.r2d2.cellular_pro")!=-1) {
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
