package org.md2k.study.groups.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import org.md2k.study.R;
import org.md2k.study.groups.Children;
import org.md2k.study.OnDataUpdated;
import org.md2k.study.OnTaskCompleted;
import org.md2k.study.groups.AppInfo;
import org.md2k.study.groups.GroupManager;
import org.md2k.utilities.Apps;

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
public class ChildrenApp extends Children {
    String package_name;
    String download_link;
    String currentVersionNameStr;
    long currentVersionNameLong;
    String latestVersionNameStr;
    long latestVersionNameLong;
    ChildrenApp(final Context context, AppInfo appInfo, OnDataUpdated onDataUpdated){
        super(context, appInfo.name, onDataUpdated);
        package_name=appInfo.package_name;
        download_link=appInfo.download_link;
        setCurrentVersionName();
        setLatestVersionName();
        updateStatus();
        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAndInstallApp(context);
            }
        };

    }
    void downloadAndInstallApp(Context context) {
        if (download_link.startsWith("market")) {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(download_link));
            context.startActivity(goToMarket);
        } else {
            String downloadLinkName = download_link +
                    "/download/" + latestVersionNameStr +
                    "/" + name.toLowerCase() +
                    latestVersionNameStr + ".apk";
            DownloadTask downloadTask = new DownloadTask(context);
            String[] s = new String[]{downloadLinkName};
            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
        }
    }
    long convertVersionStrToLong(String versionStr){
        long result=0, mult=1;
        if(versionStr==null) return -1;
        String parts[]=versionStr.split("\\.");
        if(parts.length<3) return -1;
        for(int i=parts.length-1;i>=0;i--){
            result=result+Integer.valueOf(parts[i])*mult;
            mult*=1000;
        }
        return result;
    }
    public void updateStatus(){
        if(currentVersionNameLong==-1) {
            status = GroupManager.RED;
            super.updateStatus();
            buttonText="Install";
        }
        else if(currentVersionNameLong<latestVersionNameLong) {
            status = GroupManager.YELLOW;
            super.updateStatus();
            buttonText="Update";
        }
        else {
            status=GroupManager.GREEN;
            super.updateStatus();
        }
        if(onDataUpdated!=null) onDataUpdated.onChange();
    }
    public void setLatestVersionName(){
        if (!package_name.startsWith("market")) {
            DownloadVersion downloadVersion = new DownloadVersion(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String versionName) {
                    long latestVersionLong=convertVersionStrToLong(versionName);
                    if(latestVersionNameLong!=latestVersionLong){
                        latestVersionNameLong=latestVersionLong;
                        latestVersionNameStr= versionName;
                        updateStatus();
                    }
                }
            });
            downloadVersion.execute(download_link + "/latest");
        }
        latestVersionNameLong=-1;
        latestVersionNameStr=null;
        updateStatus();
    }
    public void setCurrentVersionName() {
        String curVersionStr=null;
        long curVersionLong=-1;
        if(Apps.isPackageInstalled(context, package_name)) {
            curVersionStr = Apps.getVersionName(context, package_name);
            curVersionLong=convertVersionStrToLong(curVersionStr);
        }
        if(this.currentVersionNameLong!=curVersionLong){
            this.currentVersionNameLong=curVersionLong;
            this.currentVersionNameStr=curVersionStr;
            updateStatus();
        }
    }
/*    public void refresh(){
        setCurrentVersionName();
        setLatestVersionName();
    }
    */
}
