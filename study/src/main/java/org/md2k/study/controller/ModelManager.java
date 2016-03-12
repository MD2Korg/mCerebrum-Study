package org.md2k.study.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.messagehandler.OnExceptionListener;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;
import org.md2k.study.model.app_install.AppInstallManager;
import org.md2k.study.model.app_run.AppRunManager;
import org.md2k.study.model.app_settings.AppSettingsManager;
import org.md2k.study.model.clear_config.ClearConfigManager;
import org.md2k.study.model.clear_data.ClearDataManager;
import org.md2k.study.model.config_download.ConfigDownload;
import org.md2k.study.model.config_info.ConfigInfoManager;
import org.md2k.study.model.data_quality.DataQualityManager;
import org.md2k.study.model.day_start_end.DayStartEndInfoManager;
import org.md2k.study.model.privacy_control.PrivacyControlManager;
import org.md2k.study.model.app_service.AppServiceManager;
import org.md2k.study.model.sleep_info.SleepInfoManager;
import org.md2k.study.model.study_info.StudyInfoManager;
import org.md2k.study.model.study_start_end.StudyStartEndInfoManager;
import org.md2k.study.model.user_info.UserInfoManager;
import org.md2k.study.model.wakeup_info.WakeupInfoManager;
import org.md2k.utilities.Apps;
import org.md2k.utilities.Files;

import java.util.HashMap;

import org.md2k.study.system_health.ServiceSystemHealth;

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
public class ModelManager {
    private static final String TAG = ModelManager.class.getSimpleName();
    public static final String MODEL_CONFIG_INFO = "config_info";
    public static final String MODEL_STUDY_INFO = "study_info";
    public static final String MODEL_APP_INSTALL = "app_install";
    public static final String MODEL_APP_SETTINGS = "app_settings";
    public static final String MODEL_APP_SERVICE = "app_service";
    public static final String MODEL_USER_INFO = "user_info";
    public static final String MODEL_WAKEUP_INFO = "wakeup_info";
    public static final String MODEL_SLEEP_INFO = "sleep_info";
    public static final String MODEL_CLEAR_DATABASE = "clear_database";
    public static final String MODEL_CLEAR_CONFIG = "clear_config";
    public static final String MODEL_DATA_QUALITY = "data_quality";
    public static final String MODEL_PRIVACY = "privacy";
    public static final String MODEL_INTERVENTION = "intervention";
    public static final String MODEL_SMOKING_SELF_REPORT = "smoking_self_report";
    public static final String MODEL_PLOTTER = "plotter";
    public static final String MODEL_NOTIFICATION_TEST = "notification_test";
    public static final String MODEL_DAY_START_END = "day_start_end";
    public static final String MODEL_CONFIG_DOWNLOAD = "config_download";
    public static final String MODEL_STUDY_START_END="study_start_end";

    private static ModelManager instance = null;
    Context context;
    HashMap<String, Model> modelHashMap;
    DataKitAPI dataKitAPI;
    AUManager adminManager;
    AUManager userManager;
    ConfigManager configManager;
    boolean valid;

    public static ModelManager getInstance(Context context) {
        if (instance == null)
            instance = new ModelManager(context);
        return instance;
    }

    private ModelManager(Context context) {
        this.context = context;
        modelHashMap = new HashMap<>();
        dataKitAPI = DataKitAPI.getInstance(context);
        configManager = new ConfigManager(context);
        adminManager = new AUManager(context, true);
        userManager = new AUManager(context, false);
        valid = true;
    }

    public boolean isValid() {
        if (!Files.isExist(Constants.CONFIG_DIRECTORY + Constants.CONFIG_FILENAME))
            valid = false;
        return valid;
    }

    public boolean set() {
        if (!configManager.read()) {
            valid = false;
        } else {
            adminManager.set(configManager);
            userManager.set(configManager);
            valid = true;
        }
        return valid;
    }

    public void update() {
        adminManager.update();
        userManager.update();
    }

    public void start() {
        adminManager.start();
        userManager.start();
    }

    public void stop() {
        for (String key : modelHashMap.keySet())
            modelHashMap.get(key).stop();
    }

    public void clear() {
        modelHashMap.clear();
        adminManager.clear();
        userManager.clear();
    }


    public AUManager getAdminManager() {
        return adminManager;
    }

    public AUManager getUserManager() {
        return userManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Model getModel(String id) {
        if (!modelHashMap.containsKey(id)) {
            modelHashMap.put(id, createModel(id, dataKitAPI));
        }
        return modelHashMap.get(id);
    }


    public boolean isConnected() {
        return dataKitAPI.isConnected();
    }

    public Status getStatus() {
        if (isConnected()) return new Status(Status.SUCCESS);
        return new Status(Status.DATAKIT_NOT_AVAILABLE);
    }

    public boolean isInstalled() {
        return Apps.isPackageInstalled(context, "org.md2k.datakit");
    }

    public void connect() {
        if (!dataKitAPI.isConnected()) {

            dataKitAPI.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    Intent intent = new Intent(ServiceSystemHealth.INTENT_NAME);
                    intent.putExtra(ServiceSystemHealth.TYPE, ServiceSystemHealth.CONNECTED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }, new OnExceptionListener() {
                @Override
                public void onException(org.md2k.datakitapi.status.Status status) {
                    Intent intent = new Intent(ServiceSystemHealth.INTENT_NAME);
                    intent.putExtra(ServiceSystemHealth.TYPE, ServiceSystemHealth.NOT_CONNECTED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

//                  Toast.makeText(context, "DataKit Connection Error. Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public Model createModel(String id, DataKitAPI dataKitAPI) {
        Operation operation = null;
        if (isValid()) operation = configManager.getConfig().getOperation(id);
        switch (id) {
            case MODEL_CONFIG_DOWNLOAD:
                return new ConfigDownload(context, configManager, dataKitAPI, operation);
            case MODEL_CONFIG_INFO:
                return new ConfigInfoManager(context, configManager, dataKitAPI, operation);
            case MODEL_STUDY_INFO:
                return new StudyInfoManager(context, configManager, dataKitAPI, operation);
            case MODEL_APP_INSTALL:
                return new AppInstallManager(context, configManager, dataKitAPI, operation);
            case MODEL_APP_SETTINGS:
                return new AppSettingsManager(context, configManager, dataKitAPI, operation);
            case MODEL_USER_INFO:
                return new UserInfoManager(context, configManager, dataKitAPI, operation);
            case MODEL_WAKEUP_INFO:
                return new WakeupInfoManager(context, configManager, dataKitAPI, operation);
            case MODEL_SLEEP_INFO:
                return new SleepInfoManager(context, configManager, dataKitAPI, operation);
            case MODEL_CLEAR_CONFIG:
                return new ClearConfigManager(context, configManager, dataKitAPI, operation);
            case MODEL_DATA_QUALITY:
                return new DataQualityManager(context, configManager, dataKitAPI, operation);
            case MODEL_CLEAR_DATABASE:
                return new ClearDataManager(context, configManager, dataKitAPI, operation);
            case MODEL_INTERVENTION:
            case MODEL_SMOKING_SELF_REPORT:
            case MODEL_PLOTTER:
            case MODEL_NOTIFICATION_TEST:
                return new AppRunManager(context, configManager, dataKitAPI, operation);
            case MODEL_PRIVACY:
                return new PrivacyControlManager(context, configManager, dataKitAPI, operation);
            case MODEL_DAY_START_END:
                return new DayStartEndInfoManager(context, configManager, dataKitAPI, operation);
            case MODEL_STUDY_START_END:
                return new StudyStartEndInfoManager(context, configManager, dataKitAPI, operation);
            case MODEL_APP_SERVICE:
                return new AppServiceManager(context, configManager, dataKitAPI, operation);
        }
        return null;
    }

}
