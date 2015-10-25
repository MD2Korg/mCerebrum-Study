package org.md2k.study.groups.service;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.md2k.study.OnDataUpdated;
import org.md2k.study.R;
import org.md2k.study.groups.AppInfo;
import org.md2k.study.groups.Children;
import org.md2k.study.groups.GroupManager;
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
public class ChildrenService extends Children {
    private static final String TAG = ChildrenService.class.getSimpleName();
    String package_name;
    String service;
    boolean serviceRunning=false;
    long serviceRunningTime=0;
    public ChildrenService(final Context context, final AppInfo appInfo, OnDataUpdated onDataUpdated){
        super(context, appInfo.name, onDataUpdated);
        package_name=appInfo.package_name;
        service =appInfo.service;
        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(package_name, service);
                Log.d(TAG,"-----> Service: onClickListener()");
                if(serviceRunning)
                    context.stopService(intent);
                else
                    context.startService(intent);
            }
        };
    }

    public void setServiceRunning(){
        serviceRunning= Apps.isServiceRunning(context, service);
        if(serviceRunning)
            serviceRunningTime=Apps.serviceRunningTime(context, service);
        updateStatus();
    }
    public String getName(){
        if(serviceRunning)
            return name+ " ("+formatString()+")";
        else return name;
    }
    private String formatString(){
        long runtime = serviceRunningTime / 1000;
        int second = (int) (runtime % 60);
        runtime /= 60;
        int minute = (int) (runtime % 60);
        runtime /= 60;
        int hour = (int) runtime;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void updateStatus(){
        Log.d(TAG,"service: "+serviceRunning);
        if(serviceRunning) {
            status = GroupManager.GREEN;
            super.updateStatus();
            buttonVisiblilty=View.VISIBLE;
            buttonText="Stop";

        }
        else {
            status=GroupManager.RED;
            super.updateStatus();
            buttonVisiblilty=View.VISIBLE;
            buttonText="Start";
        }
        if(onDataUpdated!=null) onDataUpdated.onChange();
    }
    public void refresh(){
        setServiceRunning();
    }
}
