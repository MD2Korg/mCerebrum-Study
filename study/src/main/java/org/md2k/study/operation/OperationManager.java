package org.md2k.study.operation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.study.Status;
import org.md2k.study.operation.app_install.AppsInstall;
import org.md2k.study.operation.app_settings.AppsSettings;
import org.md2k.study.operation.clear_config.AppsClear;
import org.md2k.study.operation.config.ConfigManager;
import org.md2k.study.operation.service.AppsService;
import org.md2k.study.operation.study_info.StudyInfoManager;
import org.md2k.study.operation.user.UserApps;
import org.md2k.study.operation.user_info.UserInfoManager;
import org.md2k.study.operation.wakeup_sleep_time.SleepInfoManager;
import org.md2k.utilities.datakit.DataKitHandler;

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
    private static OperationManager instance=null;
    DataKitHandler dataKitHandler;
    Context context;

    public ConfigManager configManager;
    public AppsInstall appsInstall;
    public AppsSettings appsSettings;
    public UserInfoManager userInfoManager;
    public StudyInfoManager studyInfoManager;
    public SleepInfoManager sleepInfoManager;
    public AppsClear appsClear;
    public UserApps userApps;
    public AppsService appsService;

    public static OperationManager getInstance(Context context){
        if(instance==null)
            instance=new OperationManager(context);
        return instance;
    }
    private OperationManager(Context context){
        this.context=context;
        dataKitHandler=DataKitHandler.getInstance(context);
        reset();
    }
    private void clear(){
        ConfigManager.clear();
        AppsInstall.clear();
        AppsSettings.clear();
        StudyInfoManager.clear();
        UserInfoManager.clear();
        SleepInfoManager.clear();
        AppsClear.clear();
        UserApps.clear();
        AppsService.clear();
    }
    private void create(){
        configManager=ConfigManager.getInstance(context);
        appsInstall = AppsInstall.getInstance(context);
        appsSettings =AppsSettings.getInstance(context);
        studyInfoManager = StudyInfoManager.getInstance(context);
        userInfoManager = UserInfoManager.getInstance(context);
        sleepInfoManager=SleepInfoManager.getInstance(context);
        appsClear=AppsClear.getInstance(context);
        userApps=UserApps.getInstance(context);
        appsService=AppsService.getInstance(context);
    }

    public void reset(){
        clear();
        create();
        connect();
    }
    public void connect(){
      if(!dataKitHandler.isConnected()) {
          dataKitHandler.connect(new OnConnectionListener() {
              @Override
              public void onConnected() {
                  reset();
                  Status status = getStatus();
                  Intent intent = new Intent("system_health");
                  intent.putExtra("status", status);
                  LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
              }
          });
      }
    }
    public Status getStatus(){
        Status status=configManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return status;
        status=studyInfoManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return status;
        status=appsInstall.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return status;
        status=appsSettings.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return status;
        status=userInfoManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return status;
        status=sleepInfoManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return status;
        status=appsService.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return status;
        return new Status(Status.SUCCESS);
    }
    public boolean isStudySetupValid(){
        Status status=configManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return false;
        status=studyInfoManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return false;
        status=appsInstall.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return false;
        status=appsSettings.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return false;
        status=userInfoManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return false;
        status=sleepInfoManager.getStatus();
        if(status.getStatusCode()!=Status.SUCCESS) return false;
        return true;
    }
}
