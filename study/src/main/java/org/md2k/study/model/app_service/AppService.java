package org.md2k.study.model.app_service;

import android.content.Context;
import android.content.Intent;

import org.md2k.study.Status;
import org.md2k.utilities.Apps;


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
public class AppService {
    private static final String TAG = AppService.class.getSimpleName();
    private String name;
    private String package_name;
    private String service;

    public AppService(String name, String package_name, String service) {
        this.name = name;
        this.package_name = package_name;
        this.service = service;
    }

    public void start(Context context) {
        if(!isInstalled(context)) return;
        if(isRunning(context)) return;
        Intent intent = new Intent();
        intent.setClassName(package_name, service);
            context.startService(intent);
    }
    public void stop(Context context){
        if(!isInstalled(context)) return;
        if(!isRunning(context)) return;
        Intent intent = new Intent();
        intent.setClassName(package_name, service);
        context.stopService(intent);
    }
    public Status getStatus(Context context){
        if(!isInstalled(context)) return new Status(Status.APP_NOT_INSTALLED);
        if(!isRunning(context)) return new Status(Status.APP_NOT_RUNNING);
        return new Status(Status.SUCCESS);
    }

    public boolean isInstalled(Context context) {
        return Apps.isPackageInstalled(context, package_name);
    }
    public boolean isRunning(Context context){
        return Apps.isServiceRunning(context, service);

    }

    public String getName() {
        return name;
    }

    public String getPackage_name() {
        return package_name;
    }
}
