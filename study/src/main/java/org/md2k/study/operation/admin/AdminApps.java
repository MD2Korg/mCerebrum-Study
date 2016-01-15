package org.md2k.study.operation.admin;

import android.content.Context;

import org.md2k.study.config.AdminSettings;
import org.md2k.study.config.ConfigManager;
import org.md2k.utilities.Report.Log;

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
public class AdminApps {
    private static final String TAG = AdminApps.class.getSimpleName();
    ArrayList<AdminApp> adminApps;
    Context context;
    private static AdminApps instance;
    public static AdminApps getInstance(Context context){
        if(instance==null)
            instance=new AdminApps(context);
        return instance;
    }

    private AdminApps(Context context) {
        this.context = context;
        ArrayList<AdminSettings> admin_settings=ConfigManager.getInstance(context).getConfigList().getAdmin_settings();
        adminApps =new ArrayList<>();
        for(int i=0;i<admin_settings.size();i++){
            if(admin_settings.get(i).isValue()) {
                AdminApp adminApp=new AdminApp(admin_settings.get(i).getId(),admin_settings.get(i).isValue());
                adminApps.add(adminApp);
            }
        }
        Log.d(TAG, "adminApps=" + adminApps.size());
    }

    public List<AdminApp> getApp() {
        return adminApps;
    }
    public AdminApp getApp(int position){
        return adminApps.get(position);
    }
}
