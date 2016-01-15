package org.md2k.study.operation.app_settings;

import android.content.Context;

import org.md2k.study.Status;
import org.md2k.study.config.Application;
import org.md2k.study.config.ConfigManager;

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
public class AppsSettings {
    ArrayList<AppSettings> appSettingsList;
    private static AppsSettings instance;
    Context context;
    public static AppsSettings getInstance(Context context){
        if(instance==null)
            instance=new AppsSettings(context);
        return instance;
    }
    public static void clear(){
        instance=null;
    }

    public Status getStatus() {
        for (int i = 0; i < appSettingsList.size(); i++)
            if (!appSettingsList.get(i).isEqual())
                return new Status(Status.APP_CONFIG_ERROR);
        return new Status(Status.SUCCESS);
    }

    private AppsSettings(Context context) {
        this.context = context;
        ArrayList<Application> applications= ConfigManager.getInstance(context).getConfigList().getApplication();
        appSettingsList=new ArrayList<>();
        for(int i=0;i<applications.size();i++){
            if(applications.get(i).getSettings()!=null){
                Application application=applications.get(i);
                AppSettings appSettings=new AppSettings(application.getName(),application.getPackage_name(),application.getDefault_config(),application.getConfig(),application.getSettings());
                appSettingsList.add(appSettings);
            }
        }
    }

    public ArrayList<AppSettings> getAppSettingsList() {
        return appSettingsList;
    }
}
