package org.md2k.study.admin;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.admin.app_install.InstallApp;
import org.md2k.study.admin.app_install.InstallApps;
import org.md2k.study.admin.app_reset.ResetInfoManager;
import org.md2k.study.admin.app_settings.SettingsApps;
import org.md2k.study.admin.config.ConfigManager;
import org.md2k.study.admin.sleep_wakeup.SleepInfoManager;
import org.md2k.study.admin.study_info.StudyInfoManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
public class AdminManager {
    InstallApps installApps;
    StudyInfoManager studyInfoManager;
    SettingsApps settingsApps;
    SleepInfoManager sleepInfoManager;
    ResetInfoManager resetInfoManager;
    ConfigManager configManager;
    Context context;
    public AdminManager(Context context) {
        this.context=context;
        reset();
    }

    public void reset(){
        installApps = InstallApps.getInstance(context);
        settingsApps=SettingsApps.getInstance(context);
        studyInfoManager = new StudyInfoManager(context);
        sleepInfoManager=new SleepInfoManager(context);
        resetInfoManager=ResetInfoManager.getInstance(context);
        configManager=ConfigManager.getInstance(context);
    }
    public void readFromDB(){
        studyInfoManager = new StudyInfoManager(context);
        sleepInfoManager=new SleepInfoManager(context);
    }

    public Status getStatus(){
        if (installApps.getStatus().getStatusCode() == Status.APP_NOT_INSTALLED)
            return new Status(Status.APP_NOT_INSTALLED);
        if(settingsApps.getStatus().getStatusCode()==Status.APP_CONFIG_ERROR)
            return new Status(Status.APP_CONFIG_ERROR);
        if (studyInfoManager.getStatus().getStatusCode() == Status.USERID_NOT_DEFINED)
            return new Status(Status.USERID_NOT_DEFINED);
        return sleepInfoManager.getStatus();
    }
}
