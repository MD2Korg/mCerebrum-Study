package org.md2k.study.config;

import android.content.Context;
import android.content.pm.PackageManager;

import org.md2k.study.Constants;
import org.md2k.utilities.FileManager;
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
public class ConfigInfo {
    private static final String TAG = ConfigInfo.class.getSimpleName();
    String id;
    String name;
    int version_code;
    ArrayList<String> required_files;
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVersion_code() {
        return version_code;
    }

    public boolean isValid(Context context){
        Log.d(TAG,"isValid()...");
        if(!isValidVersion(context)) return false;
        Log.d(TAG,"isValid()...isValidVersion()=true");
        if(!isValidRequiredFiles()) return false;
        Log.d(TAG,"isValid()...isValidRequiredFiles()=true");
        Log.d(TAG,"isvalid()...true");
        return true;
    }
    private boolean isValidVersion(Context context){
        Log.d(TAG, "isValidVersion()...");
        try {
            int appVersion = (context.getPackageManager().getPackageInfo(context.getPackageName(), 0)).versionCode;
            Log.d(TAG,"isValidVersion()...appversion="+appVersion+" version_code="+version_code);
            if(version_code>appVersion) return false;
            if(version_code< Constants.CONFIG_MIN_VERSION) return false;
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private boolean isValidRequiredFiles(){
        Log.d(TAG, "isValidRequiredFiles()...");
        if(required_files==null) return true;
        for(int i=0;i<required_files.size();i++){
            if(!FileManager.isExist(Constants.CONFIG_DIRECTORY_BASE+required_files.get(i))) {
                Log.d(TAG,Constants.CONFIG_DIRECTORY_BASE+required_files.get(i));
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> getRequired_files() {
        return required_files;
    }
}
