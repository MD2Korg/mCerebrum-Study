package org.md2k.study.operation.config;

import android.content.Context;

import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.AdminSettings;
import org.md2k.study.config.ConfigInfo;
import org.md2k.utilities.Files;

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
public class ConfigManager {
    String id;
    String name;
    String version;
    ArrayList<String> files;
    private static ConfigManager instance;
    public static ConfigManager getInstance(Context context){
        if(instance==null) instance=new ConfigManager(context);
        return instance;
    }
    public static void clear(){
        instance=null;
    }

    public ConfigManager(Context context) {
        ConfigInfo configInfo=org.md2k.study.config.ConfigManager.getInstance(context).getConfigList().getConfig_info();
        this.id = configInfo.getId();
        this.name =configInfo.getName();
        this.version = configInfo.getVersion();
        this.files = configInfo.getFiles();
    }
    public Status getStatus(){
        String directory= Constants.CONFIG_DIRECTORY;
        for(int i=0;i<files.size();i++){
            if(!Files.isExist(directory+files.get(i)))
                return new Status(Status.CONFIG_FILE_NOT_EXIST);
        }
        return new Status(Status.SUCCESS);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public ArrayList<String> getFiles() {
        return files;
    }
}
