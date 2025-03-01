package com.luckyzyx.luckytool.hook.CorePatch;

import org.lsposed.lsparanoid.Obfuscate;

import de.robv.android.xposed.XposedHelpers;

@Obfuscate
public class CorePatchForV extends CorePatchForU {
    @Override
    Class<?> getParsedPackage(ClassLoader classLoader) {
        return XposedHelpers.findClassIfExists("com.android.internal.pm.parsing.pkg.ParsedPackage", classLoader);
    }
}