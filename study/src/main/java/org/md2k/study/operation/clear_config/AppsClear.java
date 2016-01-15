package org.md2k.study.operation.clear_config;

import android.content.Context;

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
public class AppsClear {
    ArrayList<AppClear> appClearList;
    private static AppsClear instance;
    Context context;
    public static AppsClear getInstance(Context context){
        if(instance==null)
            instance=new AppsClear(context);
        return instance;
    }
    public static void clear(){
        instance=null;
    }

    public boolean isExists(){
        for(int i=0;i< appClearList.size();i++)
            if(appClearList.get(i).isExists())
                return true;
        return false;
    }
    public void delete(){
        for(int i=0;i< appClearList.size();i++)
            appClearList.get(i).delete();
    }

    private AppsClear(Context context) {
        this.context = context;
        ArrayList<Application> applications= ConfigManager.getInstance(context).getConfigList().getApplication();
        appClearList=new ArrayList<>();
        for(int i=0;i<applications.size();i++){
            if(applications.get(i).getConfig()!=null){
                Application application=applications.get(i);
                AppClear appClear =new AppClear(application.getName(),application.getPackage_name(),application.getConfig());
                appClearList.add(appClear);
            }
        }
    }
}
