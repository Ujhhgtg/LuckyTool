package com.luckyzyx.luckytool.hook.DisableFlagSecure;

import static com.luckyzyx.luckytool.utils.SPUtilsKt.ModulePrefs;

import android.annotation.SuppressLint;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceControl;

import androidx.annotation.RequiresApi;

import com.luckyzyx.luckytool.BuildConfig;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressLint("PrivateApi")
public class DisableFlagSecure implements IXposedHookLoadPackage {
    private static final String SYSTEMUI = "com.android.systemui";
    private static final String OPLUS_APPPLATFORM = "com.oplus.appplatform";
    private static final String OPLUS_SCREENSHOT = "com.oplus.screenshot";
    final XSharedPreferences prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, ModulePrefs);
    private final static Method deoptimizeMethod;
    
    static {
        Method m = null;
        try {
            //noinspection JavaReflectionMemberAccess
            m = XposedBridge.class.getDeclaredMethod("deoptimizeMethod", Member.class);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
        deoptimizeMethod = m;
    }
    
    static void deoptimizeMethod(Class<?> c, String n) throws InvocationTargetException, IllegalAccessException {
        for (Method m : c.getDeclaredMethods()) {
            if (deoptimizeMethod != null && m.getName().equals(n)) {
                deoptimizeMethod.invoke(null, m);
                Log.d("DisableFlagSecure", "deoptimized " + m);
            }
        }
    }
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.isFirstApplication) return;
        
        boolean isEnable = prefs.getBoolean("disable_flag_secure", false);
        if (!isEnable) return;
        
        var classloader = loadPackageParam.classLoader;
        var packName = loadPackageParam.packageName;
        switch (packName) {
            case "android": {
                try {
                    deoptimizeSystemServer(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("DisableFlagSecure: deoptimize system server failed ->" + t);
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    // Screen record detection (V~Baklava)
                    try {
                        hookWindowManagerService(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("hook WindowManagerService failed ->" + t);
                    }
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Screenshot detection (U~Baklava)
                    try {
                        hookActivityTaskManagerService(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook ActivityTaskManagerService failed ->" + t);
                    }
                }
                
                // ScreenCapture in WindowManagerService (S~Baklava)
                try {
                    hookScreenCapture(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("DisableFlagSecure: hook ScreenCapture failed ->" + t);
                }
                
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Blackout permission check (S~T)
                    try {
                        hookActivityManagerService(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook ActivityManagerService failed ->" + t);
                    }
                }
                
                // WifiDisplay (S~Baklava) / OverlayDisplay (S~Baklava) / VirtualDisplay (U~Baklava)
                try {
                    hookDisplayControl(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("DisableFlagSecure: hook DisplayControl failed ->" + t);
                }
                
                // VirtualDisplay with MediaProjection (S~Baklava)
                try {
                    hookVirtualDisplayAdapter(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("DisableFlagSecure: hook VirtualDisplayAdapter failed ->" + t);
                }
                
                // secureLocked flag
                try {
                    // Screenshot
                    hookWindowState(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("DisableFlagSecure: hook WindowState failed ->" + t);
                }
                
                // oplus dumpsys
                // dumpsys window screenshot systemQuickTileScreenshotOut display_id=0
                try {
                    hookOplus(classloader);
                } catch (Throwable t) {
                    if (!(t instanceof ClassNotFoundException)) {
                        XposedBridge.log("hook Oplus failed ->" + t);
                    }
                }
                break;
            }
            case OPLUS_SCREENSHOT:
                // Oplus Screenshot 15.0.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    try {
                        hookOplusScreenCapture(classloader);
                    } catch (Throwable t) {
                        if (!(t instanceof ClassNotFoundException)) {
                            XposedBridge.log("hook OplusScreenCapture failed ->" + t);
                        }
                    }
                }
            case OPLUS_APPPLATFORM: {
                // Flyme SystemUI Ext 10.3.0
                // OPlus AppPlatform 13.1.0 / 14.0.0
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        hookScreenshotHardwareBuffer(classloader);
                    }
                } catch (Throwable t) {
                    if (!(t instanceof ClassNotFoundException)) {
                        XposedBridge.log("hook ScreenshotHardwareBuffer failed ->" + t);
                    }
                }
            }
            case SYSTEMUI: {
                if (OPLUS_APPPLATFORM.equals(packName) ||
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) {
                    // ScreenCapture in App (S~T) (OPlus S-U)
                    try {
                        hookScreenCapture(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook ScreenCapture failed ->" + t);
                    }
                }
                break;
            }
        }
    }
    
    public void deoptimizeSystemServer(ClassLoader classLoader) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        var cls = classLoader.loadClass("com.android.server.wm.WindowStateAnimator");
        deoptimizeMethod(cls, "createSurfaceLocked");
        
        cls = classLoader.loadClass("com.android.server.wm.WindowManagerService");
        deoptimizeMethod(cls, "relayoutWindow");
        
        for (int i = 0; i < 20; i++) {
            try {
                var clazz = classLoader.loadClass("com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda" + i);
                if (BiConsumer.class.isAssignableFrom(clazz)) {
                    deoptimizeMethod(clazz, "accept");
                }
            } catch (ClassNotFoundException ignored) {
            }
            try {
                var clazz = classLoader.loadClass("com.android.server.wm.DisplayContent$$ExternalSyntheticLambda" + i);
                if (BiPredicate.class.isAssignableFrom(clazz)) {
                    deoptimizeMethod(clazz, "test");
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
    }
    
    @SuppressLint("ObsoleteSdkInt")
    private void hookWindowState(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var windowStateClazz = classLoader.loadClass("com.android.server.wm.WindowState");
        var systemServerCl = windowStateClazz.getClassLoader();
        Method isSecureLockedMethod = windowStateClazz.getDeclaredMethod("isSecureLocked");
        XposedBridge.hookMethod(isSecureLockedMethod, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    var walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
                    var match = walker.walk(frames -> frames
                            .anyMatch(frame -> frame.getDeclaringClass() != null &&
                                    frame.getDeclaringClass().getClassLoader() == systemServerCl &&
                                    (frame.getMethodName().equals("setInitialSurfaceControlProperties") ||
                                            frame.getMethodName().equals("createSurfaceLocked"))));
                    if (match) return;
                } else {
                    var stackTrace = new Throwable().getStackTrace();
                    for (var frame : stackTrace) {
                        var name = frame.getMethodName();
                        try {
                            if ((name.equals("setInitialSurfaceControlProperties") ||
                                    name.equals("createSurfaceLocked")) &&
                                    classLoader.loadClass(frame.getClassName()).getClassLoader() == systemServerCl) {
                                return;
                            }
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                }
                param.setResult(false);
            }
        });
    }
    
    private void hookOplus(ClassLoader classLoader) throws ClassNotFoundException {
        // caller: com.android.server.wm.OplusLongshotWindowDump#dumpWindows
        var longshotMainClazz = classLoader.loadClass("com.android.server.wm.OplusLongshotMainWindow");
        hookMethods(longshotMainClazz, XC_MethodReplacement.returnConstant(false), "hasSecure");
    }
    
    private void hookMethods(Class<?> clazz, XC_MethodHook hook, String... names) {
        var list = Arrays.asList(names);
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> list.contains(method.getName()))
                .forEach(method -> XposedBridge.hookMethod(method, hook));
    }
    
    private static Field captureSecureLayersField;
    
    private void hookScreenCapture(ClassLoader classLoader) throws ClassNotFoundException, NoSuchFieldException {
        Class<?> screenCaptureClazz;
        Class<?> captureArgsClazz;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.BAKLAVA + 1) {
            screenCaptureClazz = classLoader.loadClass("android.window.ScreenCaptureInternal");
            captureArgsClazz = classLoader.loadClass("android.window.ScreenCaptureInternal$CaptureArgs");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            screenCaptureClazz = classLoader.loadClass("android.window.ScreenCapture");
            captureArgsClazz = classLoader.loadClass("android.window.ScreenCapture$CaptureArgs");
        } else {
            screenCaptureClazz = SurfaceControl.class;
            captureArgsClazz = classLoader.loadClass("android.view.SurfaceControl$CaptureArgs");
        }
        captureSecureLayersField = captureArgsClazz.getDeclaredField(Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA &&
                Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.BAKLAVA + 1 ? "mSecureContentPolicy" : "mCaptureSecureLayers");
        captureSecureLayersField.setAccessible(true);
        var beforeHooks = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var captureArgs = param.args[0];
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA &&
                            Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.BAKLAVA + 1) {
                        captureSecureLayersField.set(captureArgs, 1);
                    } else {
                        captureSecureLayersField.set(captureArgs, true);
                    }
                } catch (IllegalAccessException t) {
                    XposedBridge.log("DisableFlagSecure: ScreenCaptureHooker failed ->" + t);
                }
            }
        };
        hookMethods(screenCaptureClazz, beforeHooks, "nativeCaptureDisplay");
        hookMethods(screenCaptureClazz, beforeHooks, "nativeCaptureLayers");
    }
    
    private void hookDisplayControl(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var displayControlClazz = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ?
                classLoader.loadClass("com.android.server.display.DisplayControl") : SurfaceControl.class;
        var systemServerCl = displayControlClazz.getClassLoader();
        var method = displayControlClazz.getDeclaredMethod(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM ?
                        "createVirtualDisplay" :
                        "createDisplay", String.class, boolean.class);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    var stackTrace = new Throwable().getStackTrace();
                    for (var frame : stackTrace) {
                        var name = frame.getMethodName();
                        try {
                            if (name.equals("createVirtualDisplayLocked") &&
                                    classLoader.loadClass(frame.getClassName()).getClassLoader() == systemServerCl) {
                                return;
                            }
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                }
                param.args[1] = true;
            }
        });
    }
    
    private void hookVirtualDisplayAdapter(ClassLoader classLoader) throws ClassNotFoundException {
        var displayControlClazz = classLoader.loadClass("com.android.server.display.VirtualDisplayAdapter");
        hookMethods(displayControlClazz, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var caller = (int) param.args[2];
                if (caller >= 10000 && param.args[1] == null) {
                    // not os and not media projection
                    return;
                }
                for (int i = 3; i < param.args.length; i++) {
                    var arg = param.args[i];
                    if (arg instanceof Integer) {
                        var flags = (int) arg;
                        flags |= DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE;
                        param.args[i] = flags;
                        return;
                    }
                }
                XposedBridge.log("flag not found in CreateVirtualDisplayLockedHooker");
            }
        }, "createVirtualDisplayLocked");
    }
    
    @RequiresApi(Build.VERSION_CODES.S)
    private void hookScreenshotHardwareBuffer(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var screenshotHardwareBufferClazz = classLoader.loadClass(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ?
                        "android.window.ScreenCapture$ScreenshotHardwareBuffer" :
                        "android.view.SurfaceControl$ScreenshotHardwareBuffer");
        var method = screenshotHardwareBufferClazz.getDeclaredMethod("containsSecureLayers");
        XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(false));
    }
    
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void hookActivityTaskManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var activityTaskManagerServiceClazz = classLoader.loadClass("com.android.server.wm.ActivityTaskManagerService");
        var iBinderClazz = classLoader.loadClass("android.os.IBinder");
        var iScreenCaptureObserverClazz = classLoader.loadClass("android.app.IScreenCaptureObserver");
        var method = activityTaskManagerServiceClazz.getDeclaredMethod("registerScreenCaptureObserver", iBinderClazz, iScreenCaptureObserverClazz);
        XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING);
    }
    
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private void hookWindowManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var windowManagerServiceClazz = classLoader.loadClass("com.android.server.wm.WindowManagerService");
        var iScreenRecordingCallbackClazz = classLoader.loadClass("android.window.IScreenRecordingCallback");
        var method = windowManagerServiceClazz.getDeclaredMethod("registerScreenRecordingCallback", iScreenRecordingCallbackClazz);
        XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(false));
    }
    
    private void hookActivityManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var activityTaskManagerServiceClazz = classLoader.loadClass("com.android.server.am.ActivityManagerService");
        var method = activityTaskManagerServiceClazz.getDeclaredMethod("checkPermission", String.class, int.class, int.class);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var permission = param.args[0];
                if ("android.permission.CAPTURE_BLACKOUT_CONTENT".equals(permission)) {
                    param.args[0] = "android.permission.READ_FRAME_BUFFER";
                }
            }
        });
    }
    
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private void hookOplusScreenCapture(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var oplusScreenCaptureClazz = classLoader.loadClass("com.oplus.screenshot.OplusScreenCapture$CaptureArgs$Builder");
        var method = oplusScreenCaptureClazz.getDeclaredMethod("setUid", long.class);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                param.args[0] = -1;
            }
        });
    }
}
