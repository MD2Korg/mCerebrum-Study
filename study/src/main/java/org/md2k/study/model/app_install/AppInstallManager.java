package org.md2k.study.model.app_install;

import android.content.Context;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.study.Status;
import org.md2k.study.config.Application;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

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
public class AppInstallManager extends Model {
    private static final String TAG = AppInstallManager.class.getSimpleName() ;
    ArrayList<AppInstall> appInstallList;
    public ArrayList<AppInstall> getAppInstallList() {
        return appInstallList;
    }

    public AppInstallManager(Context context, ConfigManager configManager,DataKitAPI dataKitAPI, Operation operation) {
        super(context,configManager, dataKitAPI, operation);
        appInstallList = new ArrayList<>();
    }
    public void stop() {
    }
    public void start(){
        update();
    }

    public void set() {
        ArrayList<Application> applications = configManager.getConfig().getApplication();
        for (int i = 0; i < applications.size(); i++) {
            AppInstall appInstall = new AppInstall(context, applications.get(i));
            appInstallList.add(appInstall);
        }
        lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);
    }
    public void clear(){
        appInstallList.clear();
    }
    public void update(){
        for(int i=0;i<appInstallList.size();i++)
            appInstallList.get(i).update();
        int total = size();
        int install = sizeInstalled();
        int update = sizeUpdate();
        if (update == 0 && total == install)
            lastStatus= new Status(Status.SUCCESS);
        else if (total != install)
            lastStatus= new Status(Status.APP_NOT_INSTALLED);
        else lastStatus= new Status(Status.APP_UPDATE_AVAILABLE);
    }
    public int size(){
        return appInstallList.size();
    }
    public int sizeInstalled(){
        int count=0;
        for(int i=0;i< appInstallList.size();i++){
            if(appInstallList.get(i).isInstalled())
                count++;
        }
        return count;
    }
    public Status getStatus(){
        return lastStatus;
    }
    public int sizeUpdate(){
        int count=0;
        for(int i=0;i< appInstallList.size();i++){
            if(appInstallList.get(i).isUpdateAvailable())
                count++;
        }
        return count;
    }
}
