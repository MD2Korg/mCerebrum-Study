package org.md2k.study.model_view.app_service;

import android.os.Handler;

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
public class AppServiceManager extends Model {
    private static final String TAG = AppServiceManager.class.getSimpleName();
    public ArrayList<AppService> appServiceList;
    Handler handler;

    public AppServiceManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG,"AppServiceManager="+this);
        appServiceList = new ArrayList<>();
        status=new Status(rank,Status.NOT_DEFINED);
        handler=new Handler();
        ArrayList<ConfigApp> apps = modelManager.getConfigManager().getConfig().getApps();
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).getService() != null) {
                AppService appService = new AppService(modelManager.getContext(), apps.get(i).getName(), apps.get(i).getPackage_name(), apps.get(i).getService(), rank);
                appServiceList.add(appService);
                appService.stop();

            }
        }

    }

    public void set() {
        Log.d(TAG,"set()...");
        handler.removeCallbacks(runnableServiceRun);
//        ArrayList<ConfigApp> apps = modelManager.getConfigManager().getConfig().getApps();
 //       appServiceList.clear();
 //       for (int i = 0; i < apps.size(); i++) {
 //           if (apps.get(i).getService() != null) {
 //               AppService appService = new AppService(modelManager.getContext(), apps.get(i).getName(), apps.get(i).getPackage_name(), apps.get(i).getService(), rank);
 //               appServiceList.add(appService);
 //           }
 //       }
        handler.post(runnableServiceRun);
    }

    public void clear() {
        Log.d(TAG,"clear()...");
        handler.removeCallbacks(runnableServiceRun);
        for (int i = 0; i < appServiceList.size(); i++)
            appServiceList.get(i).stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        appServiceList.clear();
        status=new Status(rank,Status.NOT_DEFINED);
    }
    Runnable runnableServiceRun=new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable ServiceRun...");
            Status curStatus=getCurrentStatus();
            if(!curStatus.equals(status)) {
                notifyIfRequired(curStatus);
            }
            handler.postDelayed(this,30000);
        }
    };

    public void stopAll(){
        for(int i=0;i<appServiceList.size();i++) {
            appServiceList.get(i).setActive(false);
            appServiceList.get(i).stop();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void startAll(){
        for(int i=0;i<appServiceList.size();i++) {
            appServiceList.get(i).setActive(true);
            appServiceList.get(i).start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Status getCurrentStatus() {
        for(int i=0;i<appServiceList.size();i++) {
            Status temp = appServiceList.get(i).getStatus();
            if (temp.getStatus() == Status.APP_NOT_RUNNING)
                appServiceList.get(i).start();
        }
        return new Status(rank,Status.SUCCESS);
    }
}
