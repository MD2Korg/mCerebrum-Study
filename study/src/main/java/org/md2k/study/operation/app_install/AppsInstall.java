package org.md2k.study.operation.app_install;

import android.content.Context;

import org.md2k.study.Status;
import org.md2k.study.config.Application;
import org.md2k.study.config.ConfigManager;
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
public class AppsInstall{
    private static final String TAG = AppsInstall.class.getSimpleName() ;
    ArrayList<AppInstall> appInstallList;
    private static AppsInstall instance;
    Context context;
    public static AppsInstall getInstance(Context context){
        if(instance==null)
            instance=new AppsInstall(context);
        return instance;
    }
    public static void clear(){
        instance=null;
    }

    public ArrayList<AppInstall> getAppInstallList() {
        return appInstallList;
    }

    private AppsInstall(Context context) {
        this.context=context;
        ArrayList<Application> applications= ConfigManager.getInstance(context).getConfigList().getApplication();
        appInstallList=new ArrayList<>();
        for(int i=0;i<applications.size();i++){
            AppInstall appInstall =new AppInstall(applications.get(i).getName(),applications.get(i).getPackage_name(),applications.get(i).getDownload_link());
            appInstallList.add(appInstall);
            appInstallList.get(i).setVersionName(context);
        }
        Log.d(TAG, "appinstall=" + appInstallList.size());
    }
    public int size(){
        return appInstallList.size();
    }
    public int sizeInstalled(){
        int count=0;
        for(int i=0;i< appInstallList.size();i++){
            if(appInstallList.get(i).isInstalled(context))
                count++;
        }
        return count;
    }
    public Status getStatus(){
        int total = size();
        int install = sizeInstalled();
        int update = sizeUpdate();
        if (update == 0 && total == install)
            return new Status(Status.SUCCESS);
        else if (total != install)
            return new Status(Status.APP_NOT_INSTALLED);
        else return new Status(Status.APP_UPDATE_AVAILABLE);
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
