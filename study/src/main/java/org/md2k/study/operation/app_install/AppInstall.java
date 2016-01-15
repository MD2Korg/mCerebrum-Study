package org.md2k.study.operation.app_install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.md2k.study.OnDataChangeListener;
import org.md2k.utilities.Apps;
import org.md2k.utilities.Report.Log;


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
public class AppInstall {
    private static final String TAG = AppInstall.class.getSimpleName();
    private String name;
    private String package_name;
    private String download_link;
    private String curVersion = null;
    private String latestVersion = null;
    AppInstall(String name, String package_name, String download_link){
        this.name=name;
        this.package_name=package_name;
        this.download_link=download_link;
    }

    public void downloadAndInstallApp(final Context context) {
        if (download_link.startsWith("market")) {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(download_link));
            context.startActivity(goToMarket);
        } else {
            if(latestVersion==null){
                setLatestVersionName(context,new OnDataChangeListener() {
                    @Override
                    public void onDataChange(String str) {
                        download(context);
                    }
                });
            }
            else
                download(context);
        }
    }
    private void download(Context context){
        String downloadLinkName = download_link +
                "/download/" + latestVersion +
                "/" + name.toLowerCase() +
                latestVersion + ".apk";
        DownloadTask downloadTask = new DownloadTask(context);
        String[] s = new String[]{downloadLinkName};
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);

    }

    public void run(Context context) {
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(getPackage_name());
        context.startActivity( LaunchIntent );
    }

    public boolean isInstalled(Context context) {
        return Apps.isPackageInstalled(context, package_name);
    }

    public void setLatestVersionName(Context context, final OnDataChangeListener onDataChangeListener) {
        if (latestVersion != null) onDataChangeListener.onDataChange(latestVersion);
        if (!package_name.startsWith("market")) {
            DownloadVersion downloadVersion = new DownloadVersion(context,new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String versionName) {
                    latestVersion = versionName;
                    onDataChangeListener.onDataChange(versionName);
                }
            });
            downloadVersion.execute(download_link + "/latest");
        } else {
            onDataChangeListener.onDataChange(latestVersion);
        }
    }

    public void refresh(Context context, OnDataChangeListener onDataChangeListener) {
        Log.d(TAG, "app=" + name + " refresh()...");
        curVersion = null;
        latestVersion = null;
        setVersionName(context);
        setLatestVersionName(context,onDataChangeListener);
    }

    public boolean isUpdateAvailable() {
        if (curVersion == null) return false;
        if (latestVersion == null) return false;
        return !curVersion.equals(latestVersion);
    }

    public void setVersionName(Context context) {
        if (curVersion == null)
            if (isInstalled(context))
                curVersion = Apps.getVersionName(context, package_name);
    }

    public String getName() {
        return name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public String getDownload_link() {
        return download_link;
    }

    public String getCurVersion() {
        return curVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
