package org.md2k.study.model.app_install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.md2k.study.Constants;
import org.md2k.study.OnDataChangeListener;
import org.md2k.study.Status;
import org.md2k.study.config.Application;
import org.md2k.study.utilities.Download;
import org.md2k.study.utilities.OnCompletionListenter;
import org.md2k.utilities.Apps;
import org.md2k.utilities.Files;
import org.md2k.utilities.Report.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;


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
    Application application;
    private String curVersion;
    private String latestVersion;
    private boolean installed;
    Context context;

    AppInstall(Context context, Application application) {
        this.context = context;
        this.application = application;
        curVersion=null;
        latestVersion=null;
        installed=false;
    }
    public void set(){
        installed = Apps.isPackageInstalled(context, application.getPackage_name());
        setVersionName();
    }
    public void clear(){
    }
    public void update(){
        installed = Apps.isPackageInstalled(context, application.getPackage_name());
        setVersionName();
    }

    public void downloadAndInstallApp(final Context context) {
        final String filename = "file_" + application.getId()+latestVersion + ".apk";
        if (application.getDownload_link().startsWith("market")) {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(application.getDownload_link()));
            context.startActivity(goToMarket);
        } else {
            if (latestVersion == null) {
                setLatestVersionName(context, new OnDataChangeListener() {
                    @Override
                    public void onDataChange(String str) {
                        String link = application.getDownload_link() +
                                "/download/" + latestVersion +
                                "/" + application.getId() +
                                latestVersion + ".apk";
                        download(context, filename, link, new OnCompletionListenter() {
                            @Override
                            public void OnCompleted(Status status) {
                                if(status.getStatusCode()==Status.SUCCESS){
                                    Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(new File(Constants.TEMP_DIRECTORY+filename)), "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        });

                    }
                });
            } else {
                String link = application.getDownload_link() +
                        "/download/" + latestVersion +
                        "/" + application.getId() +
                        latestVersion + ".apk";
                download(context, filename, link, new OnCompletionListenter() {
                    @Override
                    public void OnCompleted(Status status) {
                        if(status.getStatusCode()==Status.SUCCESS){
                            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(Constants.TEMP_DIRECTORY+filename)), "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    private void download(Context context, String filename, String link, OnCompletionListenter onCompletionListenter) {
        Download download = new Download(context, onCompletionListenter);
        download.execute(link, filename);
    }

    public void run(Context context) {
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(application.getPackage_name());
        context.startActivity(LaunchIntent);
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setLatestVersionName(final Context context, final OnDataChangeListener onDataChangeListener) {
        String link = application.getDownload_link() + "/latest";
        final String filename = "version_" + UUID.randomUUID().toString() + ".txt";
        if (application.getPackage_name().startsWith("market")) {
            onDataChangeListener.onDataChange(latestVersion);
            return;
        }
        download(context, filename, link, new OnCompletionListenter() {
            @Override
            public void OnCompleted(Status status) {
                if (status.getStatusCode() == Status.SUCCESS) {
                    latestVersion = retrieveLatestVersion(Constants.TEMP_DIRECTORY+filename);
                    onDataChangeListener.onDataChange(latestVersion);
                } else
                    Toast.makeText(context, status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Files.delete(Constants.TEMP_DIRECTORY+filename);
            }
        });
    }

    String retrieveLatestVersion(String filename) {
        BufferedReader in;
        String versionName=null;
        try {
            in = new BufferedReader(new FileReader(filename));
            String str, str1 = "";
            while ((str = in.readLine()) != null) {
                str1 += str;
                if (str1.contains("<title>") && str1.contains("</title>")) {
                    int start_id = str1.indexOf("<title>") + 7;
                    int end_id = str1.indexOf("</title>");

                    str = str1.substring(start_id, end_id);
                    String[] s = str.split(" ");
                    if (s.length >= 2) {
                        versionName = s[1];
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
        return !curVersion.equals(latestVersion);
    }

    public void setVersionName() {
        if (installed)
            curVersion = Apps.getVersionName(context, application.getPackage_name());
        else curVersion = null;
    }

    public String getName() {
        return application.getName();
    }

    public String getPackage_name() {
        return application.getPackage_name();
    }

    public String getDownload_link() {
        return application.getDownload_link();
    }

    public String getCurVersion() {
        return curVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
