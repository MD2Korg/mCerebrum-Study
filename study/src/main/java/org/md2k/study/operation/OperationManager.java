package org.md2k.study.operation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.messagehandler.OnExceptionListener;
import org.md2k.study.Status;
import org.md2k.study.operation.app_install.AppsInstall;
import org.md2k.study.operation.app_settings.AppsSettings;
import org.md2k.study.operation.clear_config.AppsClear;
import org.md2k.study.operation.app_config.AppConfigManager;
import org.md2k.study.operation.privacy_control.PrivacyControlManager;
import org.md2k.study.operation.service.AppsService;
import org.md2k.study.operation.study_info.StudyInfoManager;
import org.md2k.study.operation.user.UserApps;
import org.md2k.study.operation.user_info.UserInfoManager;
import org.md2k.study.operation.wakeup_sleep_time.SleepInfoManager;
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
public class OperationManager {
    private static final String TAG = OperationManager.class.getSimpleName();
    private static OperationManager instance = null;
    DataKitAPI dataKitAPI;
    Context context;

    public AppConfigManager appConfigManager;
    public AppsInstall appsInstall;
    public AppsSettings appsSettings;
    public UserInfoManager userInfoManager;
    public StudyInfoManager studyInfoManager;
    public SleepInfoManager sleepInfoManager;
    public AppsClear appsClear;
    public UserApps userApps;
    public AppsService appsService;
    public PrivacyControlManager privacyControlManager;

    public static OperationManager getInstance(Context context) {
        if (instance == null)
            instance = new OperationManager(context);
        return instance;
    }

    private OperationManager(Context context) {
        this.context = context;
        dataKitAPI = DataKitAPI.getInstance(context);
        create();
    }

    public void close() {
        Log.d(TAG, "OperationManager...close()...");
        if (dataKitAPI.isConnected())
            dataKitAPI.disconnect();
        dataKitAPI.close();
        instance = null;
    }

    private void create() {
        appConfigManager = new AppConfigManager(context);
        appsInstall = new AppsInstall(context);
        appsSettings = new AppsSettings(context);
        studyInfoManager = new StudyInfoManager(context);
        userInfoManager = new UserInfoManager(context);
        sleepInfoManager = new SleepInfoManager(context);
        appsClear = new AppsClear(context);
        userApps = new UserApps(context);
        appsService = new AppsService(context);
        privacyControlManager = new PrivacyControlManager(context);
    }

    public boolean isConnected() {
        return dataKitAPI.isConnected();
    }

    public void connect() {
        Log.d(TAG, "OperationManager ... connect()...");
        if (!dataKitAPI.isConnected()) {
            dataKitAPI.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "OperationManager ... connect()...connected");
                    userInfoManager.reset(context);
                    sleepInfoManager.reset(context);
                    studyInfoManager.reset(context);
                    privacyControlManager.reset(context);
                    Status status = getStatus();
                    Intent intent = new Intent("system_health");
                    intent.putExtra("status", status);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }, new OnExceptionListener() {
                @Override
                public void onException(org.md2k.datakitapi.status.Status status) {
                    Log.d(TAG, "OperationManager ... connect()...exception");

//                  Toast.makeText(context, "DataKit Connection Error. Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /*
        public void connect(Activity activity){
          if(!dataKitAPI.isConnected()) {
              mProgressDialog = ProgressDialog.show(activity,
                      "Connecting...", " Connecting DataKit...");
              mProgressDialog.setCancelable(true);
              mProgressDialog.show();

              dataKitAPI.connect(new OnConnectionListener() {
                  @Override
                  public void onConnected() {
                      Log.d(TAG, "datakit connected");
                      userInfoManager.reset(context);
                      sleepInfoManager.reset(context);
                      studyInfoManager.reset(context);
                      Status status = getStatus();
                      Intent intent = new Intent("system_health");
                      intent.putExtra("status", status);
                      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                      mProgressDialog.dismiss();
                  }
              }, new OnExceptionListener() {
                  @Override
                  public void onException(org.md2k.datakitapi.status.Status status) {
                      mProgressDialog.dismiss();
    //                  Toast.makeText(context, "DataKit Connection Error. Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                  }
              });
          }
        }
     */
    public Status getStatus() {
        Status status = appConfigManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return status;
        status = studyInfoManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return status;
        status = appsInstall.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return status;
        status = appsSettings.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return status;
        status = userInfoManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return status;
        status = sleepInfoManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return status;
        status = appsService.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return status;
        return new Status(Status.SUCCESS);
    }

    public boolean isStudySetupValid() {
        Status status = appConfigManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return false;
        status = studyInfoManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return false;
        status = appsInstall.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return false;
        status = appsSettings.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return false;
        status = userInfoManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return false;
        status = sleepInfoManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) return false;
        return true;
    }
}
