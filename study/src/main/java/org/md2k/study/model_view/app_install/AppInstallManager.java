package org.md2k.study.model_view.app_install;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigApp;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
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
    public AppInstall getAppInstallList(String id){
        for(int i=0;i<appInstallList.size();i++)
            if(appInstallList.get(i).app.getId().equals(id))
                return appInstallList.get(i);
        return null;
    }

    public AppInstallManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        appInstallList = new ArrayList<>();
        ArrayList<ConfigApp> apps = modelManager.getConfigManager().getConfig().getApps();
        for (int i = 0; i < apps.size(); i++) {
            AppInstall appInstall = new AppInstall(modelManager.getContext(), apps.get(i));
            appInstallList.add(appInstall);
        }
        status=new Status(rank, Status.APP_NOT_INSTALLED);
    }

    public void set() throws DataKitException {
        Log.d(TAG, "set()...");
        Status curStatus;
        for(int i=0;i<appInstallList.size();i++)
            appInstallList.get(i).update();
        int total = size();
        int install = sizeInstalled();
        int update = sizeUpdate();
        if (update == 0 && total == install)
            curStatus= new Status(rank,Status.SUCCESS);
        else if (total != install)
            curStatus= new Status(rank,Status.APP_NOT_INSTALLED);
        else curStatus= new Status(rank,Status.APP_UPDATE_AVAILABLE);
        Log.d(TAG,"status = "+status.log()+" latestStatus="+curStatus.log());
        notifyIfRequired(curStatus);
    }
    public void clear(){
        status=new Status(rank,Status.APP_NOT_INSTALLED);
    }
    private int size(){
        return appInstallList.size();
    }
    private int sizeInstalled(){
        int count=0;
        for(int i=0;i< appInstallList.size();i++){
            if(appInstallList.get(i).isInstalled())
                count++;
        }
        return count;
    }
    private int sizeUpdate(){
        int count=0;
        for(int i=0;i< appInstallList.size();i++){
            if(appInstallList.get(i).isUpdateAvailable())
                count++;
        }
        return count;
    }
}
