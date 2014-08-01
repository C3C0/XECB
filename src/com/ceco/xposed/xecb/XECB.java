package com.ceco.xposed.xecb;

import android.telephony.TelephonyManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XECB implements IXposedHookLoadPackage {
    private static final String TAG = "XECB";
    private static final String PACKAGE_NAME = "com.android.keyguard";
    private static final String CLASS_LOCK_PATTERN_UTILS = "com.android.internal.widget.LockPatternUtils";
    private static final boolean DEBUG = false;

    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!PACKAGE_NAME.equals(lpparam.packageName)) return;

        try {
            if (DEBUG) log("Hooking updateEmergencyCallButtonState");
            XposedBridge.hookAllMethods(XposedHelpers.findClass(CLASS_LOCK_PATTERN_UTILS, lpparam.classLoader),
                    "updateEmergencyCallButtonState", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                    if (param.args.length >= 3 && param.args[1] instanceof Integer &&
                            param.args[2] instanceof Boolean) {
                        if ((Integer) param.args[1] != TelephonyManager.CALL_STATE_OFFHOOK) {
                            if (DEBUG) log("Forcing ECB visibility to hidden");
                            param.args[2] = false;
                        }
                    } else {
                        log("Unsupported method params");
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
