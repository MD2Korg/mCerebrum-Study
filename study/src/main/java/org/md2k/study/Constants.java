package org.md2k.study;

import android.content.Context;
import android.os.Environment;

/**
 * Created by smhssain on 7/8/2015.
 */
public class Constants {
    public static String FILENAME_APPINFO="app_info.json";
    public static String getInstallPath(Context context) {
        return Environment.getExternalStorageDirectory() + "/Android/data/" +context.getPackageName()+"/temp.apk";
    }
    public static String getInstallDir(Context context) {
        return Environment.getExternalStorageDirectory() + "/Android/data/" +context.getPackageName()+"/";
    }

}
