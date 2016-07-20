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
    private String id;
    private String version;
    private String name;
    private String filename;
    private boolean auto_update;
    private String logo;
    private String title;

    private ArrayList<String> required_files;

    public boolean isValid(Context context){
        if(!isValidVersion(context)) {
            return false;
        }
        if(!isValidRequiredFiles()) {
//            Toast.makeText(context, "Error: Required file not exists ...",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private boolean isValidVersion(Context context){
        try {
            if(version==null) return false;
            String appVersion = (context.getPackageManager().getPackageInfo(context.getPackageName(), 0)).versionName;
            String[] vals1 = appVersion.split("\\.");
            if(vals1.length!=3) return false;
            String[] vals2 = version.split("\\.");
            if(vals2.length!=3) return false;
            if(!vals1[0].equals(vals2[0])) return false;
            if(!vals1[1].equals(vals2[1])) return false;
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private boolean isValidRequiredFiles(){
        if(required_files==null) return true;
        for(int i=0;i<required_files.size();i++){
            if(!FileManager.isExist(Constants.CONFIG_DIRECTORY_BASE+required_files.get(i))) {
                Log.d(TAG,Constants.CONFIG_DIRECTORY_BASE+required_files.get(i));
                return false;
            }
        }
        return true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getRequired_files() {
        return required_files;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isAuto_update() {
        return auto_update;
    }

    public String getVersion() {
        return version;
    }

    public String getLogo() {
        return logo;
    }

    public String getTitle() {
        return title;
    }
}
