package org.md2k.study.model_view.app_install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.md2k.study.Constants;
import org.md2k.study.OnDataChangeListener;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.utilities.Download;
import org.md2k.study.utilities.OnCompletionListener;
import org.md2k.utilities.Apps;
import org.md2k.utilities.FileManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.sharedpreference.SharedPreference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
class AppInstall {
    private static final String TAG = AppInstall.class.getSimpleName();
    private ConfigApp app;
    private Context context;
    private String curVersion;
    private String latestVersion;
    private boolean installed;
    private OnCompletionListener onCompletionListener;

    AppInstall(Context context, ConfigApp app) {
        this.context = context;
        this.app = app;
        curVersion = null;
        latestVersion = null;
        installed = false;
    }

    public void set() {
        installed = Apps.isPackageInstalled(context, app.getPackage_name());
        curVersion = setVersionName();
        latestVersion = SharedPreference.readString(context,app.getId(),null);
    }

    public void clear() {
    }

    void uninstall(OnCompletionListener onCompletionListener){
        Log.d(TAG,"uninstall.."+app.getPackage_name());
        this.onCompletionListener=onCompletionListener;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        context.registerReceiver(br, intentFilter);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                Uri.parse("package:" + getPackage_name()));
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }

    void install(final Context context, OnCompletionListener onCompletionListenerr) {
        Log.d(TAG,"install.."+app.getPackage_name());
        this.onCompletionListener=onCompletionListenerr;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        context.registerReceiver(br, intentFilter);
        final String filename = "file_" + app.getId() + latestVersion + ".apk";
        if (app.getDownload_link().startsWith("market")) {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(app.getDownload_link()));
            goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(goToMarket);
        } else if (app.getDownload_link().endsWith(".apk")) {
            String link = app.getDownload_link();
            download(context, filename, link, true, new OnCompletionListener() {
                @Override
                public void OnCompleted(int curStatus) {
                    if (curStatus == Status.SUCCESS) {
                        Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Constants.TEMP_DIRECTORY + filename)), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else{
                        onCompletionListener.OnCompleted(Status.DOWNLOAD_ERROR);
                    }
                }
            });
        } else {
            setLatestVersionName(context, new OnDataChangeListener() {
                @Override
                public void onDataChange(int now, String str) {
                    String link = app.getDownload_link() +
                            "/download/" + latestVersion +
                            "/" + app.getId() +
                            latestVersion + ".apk";
                    download(context, filename, link, true, new OnCompletionListener() {
                        @Override
                        public void OnCompleted(int curStatus) {
                            if (curStatus == Status.SUCCESS) {
                                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(Constants.TEMP_DIRECTORY + filename)), "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }else{
                                onCompletionListener.OnCompleted(Status.DOWNLOAD_ERROR);
                            }
                        }
                    });
                }
            });
        }
    }

    private void download(Context context, String filename, String link, boolean isProgressShow, OnCompletionListener onCompletionListenter) {
        Download download = new Download(context, isProgressShow, onCompletionListenter);
        download.execute(link, filename);
    }

    public void run(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(app.getPackage_name());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setLatestVersionName(final Context context, final OnDataChangeListener onDataChangeListener) {
        String link = app.getDownload_link() + "/latest";
        final String filename = "version_" + UUID.randomUUID().toString() + ".txt";
        if (app.getDownload_link().startsWith("market")){
            latestVersion = curVersion;
            onDataChangeListener.onDataChange(0, "no_version");
        } else if(app.getDownload_link().endsWith(".apk")) {
            String[] linkPart=app.getDownload_link().split("/");
            latestVersion=linkPart[linkPart.length-2];
            SharedPreference.write(context, app.getId(), latestVersion);
            onDataChangeListener.onDataChange(0, latestVersion);
        } else{
            download(context, filename, link, false, new OnCompletionListener() {
                @Override
                public void OnCompleted(int curStatus) {
                    if (curStatus == Status.SUCCESS) {
                        latestVersion = retrieveAndVerifyLatestVersion(Constants.TEMP_DIRECTORY + filename);
                        SharedPreference.write(context, app.getId(), latestVersion);
                        onDataChangeListener.onDataChange(0, latestVersion);
                    } else {
                        latestVersion = curVersion;
                        onDataChangeListener.onDataChange(0, null);
                    }
                    //    Toast.makeText(context, new Status(Status.RANK_SUCCESS, curStatus).getMessage(), Toast.LENGTH_LONG).show();
                    FileManager.deleteFile(Constants.TEMP_DIRECTORY + filename);
                }
            });
        }
    }

    private String retrieveAndVerifyLatestVersion(String filename) {
        String curLatestVersion = retrieveLatestVersion(filename);
        if (curLatestVersion == null) return curLatestVersion;
        String[] vals1 = curLatestVersion.split("\\.");
//        if (vals1.length != 3) return null;
        return curLatestVersion;
    }

    private String retrieveLatestVersion(String filename) {
        BufferedReader in;
        String versionName = null;
        try {
            in = new BufferedReader(new FileReader(filename));
            String str, str1 = "";
            while ((str = in.readLine()) != null) {
                str1 += str;
                if (str1.contains("<title>") && str1.contains("</title>")) {

                    Matcher matcher = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+").matcher(str);
                    if (matcher.find()) {
                        versionName = matcher.group(0).toString();
                        break;
                    }

                }
            }
            in.close();
        } catch (FileNotFoundException e1) {
            return null;

        } catch (IOException e1) {
            return null;
        }
        return versionName;
    }

    public boolean isUpdateAvailable() {
        if (curVersion == null) return false;
        if (latestVersion == null) return false;
        String[] vals1 = curVersion.split("\\.");
        String[] vals2 = latestVersion.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            return Integer.parseInt(vals1[i]) <= Integer.parseInt(vals2[i]);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        else {
            if (vals1.length <= vals1.length) return false;
            else return true;
        }
    }

    private String setVersionName() {
        if (installed)
            return Apps.getVersionName(context, app.getPackage_name());
        else return null;
    }

    public String getName() {
        return app.getName();
    }

    private String getPackage_name() {
        return app.getPackage_name();
    }

    String getDownload_link() {
        return app.getDownload_link();
    }

    String getCurVersion() {
        return curVersion;
    }

    String getLatestVersion() {
        return latestVersion;
    }

    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"broadcast...package install/uninstall status = "+intent.getAction());
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_CHANGED:
                case Intent.ACTION_PACKAGE_REPLACED:
                case Intent.ACTION_PACKAGE_REMOVED:
                    SharedPreference.write(context, app.getPackage_name()+"_"+getCurVersion(),"false");
                    set();
                    onCompletionListener.OnCompleted(Status.SUCCESS);
                    context.unregisterReceiver(br);
                    break;
            }
        }
    };

    boolean hasPermission() {
        if (app.getPermission() == null)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        String str = SharedPreference.readString(context,app.getPackage_name() + "_" + this.getCurVersion(),null);
        return str != null && str.equals("true");
    }

    public void permission(OnCompletionListener onCompletionListener) {
        Log.d(TAG,"permission.."+app.getPackage_name());
        if(hasPermission()) {
            onCompletionListener.OnCompleted(Status.SUCCESS);
            return;
        }
        this.onCompletionListener=onCompletionListener;
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiverPermission, new IntentFilter("permission_data"));
        Intent intent=new Intent(context, ActivityPermissionGet.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("package_name",app.getPackage_name());
        intent.putExtra("permission", app.getPermission());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private BroadcastReceiver broadcastReceiverPermission=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result=intent.getIntExtra("result",Status.APP_PERMISSION_NOT_APPROVED);
            Log.d(TAG,"broadcast permission..."+app.getPackage_name()+" ... result = "+result);
            if(result==Status.SUCCESS)
                SharedPreference.write(context, app.getPackage_name()+"_"+getCurVersion(),"true");
            else
                SharedPreference.write(context, app.getPackage_name()+"_"+getCurVersion(),"false");
            onCompletionListener.OnCompleted(result);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiverPermission);
        }
    };
}
