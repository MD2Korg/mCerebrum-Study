package org.md2k.study.model_view.app_service;

import android.content.Context;
import android.content.Intent;

import org.md2k.study.Status;
import org.md2k.utilities.Apps;
import org.md2k.utilities.Report.Log;


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
    private boolean active;
    Context context;
    int rank;

    public AppService(Context context, String name, String package_name, String service, int rank) {
        this.context = context;
        this.name = name;
        this.package_name = package_name;
        this.service = service;
        this.rank=rank;
        active = true;
    }

    public void start() {
        Log.d(TAG, "name:"+name+" packageName="+package_name+" service="+service+"isInstalled="+isInstalled()+" isRunning=" + isRunning()+" isActive="+active);
        if (!isInstalled()) return;
        if (isRunning()) return;
        if (!active) return;
        Intent intent = new Intent();
        intent.setClassName(package_name, service);
        context.startService(intent);
    }

    public void stop() {
        Log.d(TAG,"app_service...package_name="+package_name+" stop()");
        if (!isInstalled()) return;
        if (!isRunning()) return;
        Intent intent = new Intent();
        intent.setClassName(package_name, service);
        context.stopService(intent);
    }

    public Status getStatus() {
        if (!isInstalled()) return new Status(rank, Status.APP_NOT_INSTALLED);
        else if (!isRunning()) return new Status(rank, Status.APP_NOT_RUNNING);
        else return new Status(rank, Status.SUCCESS);
    }

    public boolean isInstalled() {
        return Apps.isPackageInstalled(context, package_name);
    }

    public boolean isRunning() {
        return Apps.isServiceRunning(context, service);

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public String getPackage_name() {
        return package_name;
    }
}
