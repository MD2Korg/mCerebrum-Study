package org.md2k.study.config;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.study.Constants;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.notification.NotificationRequests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

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
public class ConfigManager {
    private static final String TAG = ConfigManager.class.getSimpleName();
    private Config config;
    private NotificationRequests notificationRequests;
    private boolean isExist;
    Context context;

    public ConfigManager(Context context) {
        Log.d(TAG, "ConfigManager()...");
        this.context=context;
        isExist=false;
        isExist=read();
        if(isExist) {
            if(config.getConfig_info()==null) {
                isExist=false;
                Log.d(TAG,"read()...getConfig_info()=null");
            }
        }
        notificationRequests=new ConfigNotification().notificationRequests;
    }

    private boolean read() {
        BufferedReader br;
        config=null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.CONFIG_DIRECTORY + Constants.CONFIG_FILENAME)));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Config>() {
            }.getType();
            config = gson.fromJson(br, collectionType);
            if(config.getUser_view()==null || config.getUser_view().getView_contents()==null)
                return false;
            else return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Config getConfig() {
        return config;
    }

    public NotificationRequests getNotificationRequests() {
        return notificationRequests;
    }

    public boolean isValid() {
        return isExist && config.getConfig_info().isValid(context);
    }
    public boolean isExist(){
        return isExist;
    }
}
