package com.km1930.dynamicbicycleclient.serialndk;


import android.util.Log;

import java.io.File;

/**
 * Created by liuenbao on 5/26/16.
 */
public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    public static boolean hasRootPermission() {
        boolean rooted = true;
        try {
            File su = new File("/system/bin/su");
            if (su.exists() == false) {
                su = new File("/system/xbin/su");
                if (su.exists() == false) {
                    rooted = false;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't obtain root - Here is what I know: " + e.getMessage());
            rooted = false;
        }
        return rooted;
    }

    public static String getSuPath() {

        File su = new File("/system/bin/su");
        String suPath = null;
        if (su.exists()) {
            suPath = "/system/bin/su";
        } else {
            su = new File("/system/xbin/su");
            if (su.exists()) {
                suPath = "/system/xbin/su";
            } else {
                suPath = null;
            }
        }
        return suPath;
    }
}
