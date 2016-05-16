package org.md2k.study.model_view.app_settings;

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
public class AppSettingsManager extends Model {
    private static final String TAG = AppSettingsManager.class.getSimpleName();
    ArrayList<AppSettings> appSettingsList;

    public AppSettingsManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        appSettingsList = new ArrayList<>();
        status=new Status(rank, Status.APP_CONFIG_ERROR);
    }
    public void set(){
        Log.d(TAG, "set()...");
        appSettingsList.clear();
        ArrayList<ConfigApp> apps = modelManager.getConfigManager().getConfig().getApps();
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).getSettings() != null && apps.get(i).getSettings().trim().length() != 0) {
                ConfigApp app = apps.get(i);
                AppSettings appSettings = new AppSettings(app, rank);
                appSettingsList.add(appSettings);
            }
        }
        update();
    }


    public void clear() {
        appSettingsList.clear();
        status=new Status(rank, Status.APP_CONFIG_ERROR);
    }

    public ArrayList<AppSettings> getAppSettingsList() {
        return appSettingsList;
    }

    public void update() {
        Status curStatus= new Status(rank,Status.SUCCESS);
        for (int i = 0; i < appSettingsList.size(); i++)
            if (!appSettingsList.get(i).isEqual()) {
                curStatus = new Status(rank,Status.APP_CONFIG_ERROR);
                break;
            }
        Log.d(TAG,"status = "+status.log()+" latestStatus="+curStatus.log());
        notifyIfRequired(curStatus);
    }
}
