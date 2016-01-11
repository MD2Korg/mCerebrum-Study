package org.md2k.study.admin.app_settings;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.study.Constants;
import org.md2k.study.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
public class SettingsApps {
    ArrayList<SettingsApp> settingsAppList = new ArrayList<>();
    private static SettingsApps instance;
    Context context;

    public static SettingsApps getInstance(Context context) {
        if (instance == null)
            instance = new SettingsApps(context);
        return instance;
    }

    public Status getStatus() {
        for (int i = 0; i < settingsAppList.size(); i++)
            if (!settingsAppList.get(i).isEqual())
                return new Status(Status.APP_CONFIG_ERROR);
        return new Status(Status.SUCCESS);
    }

    private SettingsApps(Context context) {
        this.context = context;
        settingsAppList = readFile(context);
        copyFiles();
    }

    void copyFiles() {
        File directory = new File(Constants.CONFIG_DIRECTORY);
        directory.mkdirs();
        for (int i = 0; i < settingsAppList.size(); i++)
            settingsAppList.get(i).copy(context);
    }

    public ArrayList<SettingsApp> readFile(Context context) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(Constants.FILENAME_SETTINGS)));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<SettingsApp>>() {
            }.getType();
            settingsAppList = gson.fromJson(br, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settingsAppList;
    }
}
