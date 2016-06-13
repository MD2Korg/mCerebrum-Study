package org.md2k.study.model_view.app_install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.md2k.study.Constants;
import org.md2k.study.OnDataChangeListener;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.utilities.Download;
import org.md2k.study.utilities.OnCompletionListener;
import org.md2k.utilities.Apps;
import org.md2k.utilities.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    ConfigApp app;
    private String curVersion;
    private String latestVersion;
    private boolean installed;
    Context context;

    AppInstall(Context context, ConfigApp app) {
        this.context = context;
        this.app = app;
        curVersion = null;
        latestVersion = null;
        installed = false;
    }

    public void set() {
        installed = Apps.isPackageInstalled(context, app.getPackage_name());
        setVersionName();
    }

    public void clear() {
    }

    public void update() {
        installed = Apps.isPackageInstalled(context, app.getPackage_name());
        setVersionName();
    }

    public void downloadAndInstallApp(final Context context) {
        final String filename = "file_" + app.getId() + latestVersion + ".apk";
        if (app.getDownload_link().startsWith("market")) {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(app.getDownload_link()));
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
                    }
                }
            });
        } else {
            if (latestVersion == null) {
                setLatestVersionName(context, new OnDataChangeListener() {
                    @Override
                    public void onDataChange(String str) {
                        String link = app.getDownload_link() +
                                "/download/" + latestVersion +
                                "/" + app.getId() +
                                latestVersion + ".apk";
                        download(context, filename, link,true, new OnCompletionListener() {
                            @Override
                            public void OnCompleted(int curStatus) {
                                if (curStatus == Status.SUCCESS) {
                                    Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(new File(Constants.TEMP_DIRECTORY + filename)), "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                });
            } else {
                String link = app.getDownload_link() +
                        "/download/" + latestVersion +
                        "/" + app.getId() +
                        latestVersion + ".apk";
                download(context, filename, link, true,new OnCompletionListener() {
                    @Override
                    public void OnCompleted(int curStatus) {
                        if (curStatus == Status.SUCCESS) {
                            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(Constants.TEMP_DIRECTORY + filename)), "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    private void download(Context context, String filename, String link, boolean isProgressShow, OnCompletionListener onCompletionListenter) {
        Download download = new Download(context, isProgressShow, onCompletionListenter);
        download.execute(link, filename);
    }

    public void run(Context context) {
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(app.getPackage_name());
        context.startActivity(LaunchIntent);
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setLatestVersionName(final Context context, final OnDataChangeListener onDataChangeListener) {
        String link = app.getDownload_link() + "/latest";
        final String filename = "version_" + UUID.randomUUID().toString() + ".txt";
        if (app.getPackage_name().startsWith("market")) {
            onDataChangeListener.onDataChange(latestVersion);
            return;
        }
        download(context, filename, link,false, new OnCompletionListener() {
            @Override
            public void OnCompleted(int curStatus) {
                if (curStatus == Status.SUCCESS) {
                    latestVersion = retrieveAndVerifyLatestVersion(Constants.TEMP_DIRECTORY + filename);
                    onDataChangeListener.onDataChange(latestVersion);
                } else
                    Toast.makeText(context, new Status(Status.RANK_SUCCESS, curStatus).getMessage(), Toast.LENGTH_LONG).show();
                FileManager.deleteFile(Constants.TEMP_DIRECTORY + filename);
            }
        });
    }
    String retrieveAndVerifyLatestVersion(String filename){
        String curLatestVersion=retrieveLatestVersion(filename);
        if(curLatestVersion==null)  return curLatestVersion;
        String[] vals1 = curLatestVersion.split("\\.");
        if(vals1.length!=3) return null;
        return curLatestVersion;
    }

    String retrieveLatestVersion(String filename) {
        BufferedReader in;
        String versionName = null;
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
        String[] vals1 = curVersion.split("\\.");
        String[] vals2 = latestVersion.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
        {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length)
        {
            return Integer.parseInt(vals1[i]) <= Integer.parseInt(vals2[i]);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        else
        {
            if(vals1.length<=vals1.length) return false;
            else return true;
        }
    }

    public void setVersionName() {
        if (installed)
            curVersion = Apps.getVersionName(context, app.getPackage_name());
        else curVersion = null;
    }

    public String getName() {
        return app.getName();
    }

    public String getPackage_name() {
        return app.getPackage_name();
    }

    public String getDownload_link() {
        return app.getDownload_link();
    }

    public String getCurVersion() {
        return curVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
