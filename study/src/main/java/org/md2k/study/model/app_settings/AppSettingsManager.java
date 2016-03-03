package org.md2k.study.model.app_settings;

import android.content.Context;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.study.Status;
import org.md2k.study.config.Application;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;

import java.io.FileNotFoundException;
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
public class AppSettingsManager extends Model {
    ArrayList<AppSettings> appSettingsList;

    public AppSettingsManager(Context context, ConfigManager configManager, DataKitAPI dataKitAPI, Operation operation) {
        super(context, configManager, dataKitAPI, operation);
        appSettingsList = new ArrayList<>();
    }

    @Override
    public void start() {
        update();
    }

    public void set() {
        ArrayList<Application> applications = configManager.getConfig().getApplication();
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getSettings() != null && applications.get(i).getSettings().trim().length() != 0) {
                Application application = applications.get(i);
                AppSettings appSettings = new AppSettings(application);
                appSettingsList.add(appSettings);
            }
        }
        lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);
    }

    public void clear() {
        appSettingsList.clear();
    }

    public void stop() {
    }


    public ArrayList<AppSettings> getAppSettingsList() {
        return appSettingsList;
    }

    public void update() {
        lastStatus = new Status(Status.SUCCESS);
        for (int i = 0; i < appSettingsList.size(); i++)
            if (!appSettingsList.get(i).isEqual()) {
                lastStatus = new Status(Status.APP_CONFIG_ERROR);
            }
    }

    public Status getStatus() {
        return lastStatus;
    }
}
