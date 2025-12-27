package com.luckyzyx.luckytool.hook.CorePatch;

import org.lsposed.lsparanoid.Obfuscate;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@Obfuscate
public class CorePatchForV extends CorePatchForU {
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        super.handleLoadPackage(loadPackageParam);
        
        var checkDowngradeAlt = XposedHelpers.findMethodExactIfExists("com.android.server.pm.PackageManagerServiceUtils",
                loadPackageParam.classLoader, "checkDowngrade", "com.android.server.pm.PackageSetting",
                "android.content.pm.PackageInfoLite");
        if (checkDowngradeAlt != null) {
            XposedBridge.hookMethod(checkDowngradeAlt, new ReturnConstant(prefs, "downgrade", null));
        }
    }
    
    @Override
    Class<?> getParsedPackage(ClassLoader classLoader) {
        return XposedHelpers.findClassIfExists("com.android.internal.pm.parsing.pkg.ParsedPackage", classLoader);
    }
}