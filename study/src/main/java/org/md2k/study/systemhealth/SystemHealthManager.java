package org.md2k.study.systemhealth;

import android.content.Context;
import android.util.SparseArray;

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
public class SystemHealthManager {
    public static final int GROUP_APP = 0;
    public static final int GROUP_SERVICE = 1;
    public static final int GROUP_DEVICE_SETTINGS = 2;
    public static final int GROUP_SENSOR_QUALITY = 3;
    public static final int GROUP_STORAGE = 4;
    public static final int GREEN = 0;
    public static final int YELLOW = 1;
    public static final int RED = 2;

    public SparseArray<Group> groups;
    public Context context;
    private static SystemHealthManager instance = null;

    public static SystemHealthManager getInstance(Context context) {
        if (instance == null)
            instance = new SystemHealthManager(context);
        return instance;
    }

    private SystemHealthManager(Context context) {
        this.context = context;
        groups = new SparseArray<>();
        ArrayList<AppInfo> appInfos = AppInfo.readFile(context);
//        ArrayList<DeviceInfo> deviceInfos=DeviceInfo.readFile(context);
//        ArrayList<SensorQualityInfo> sensorQualityInfos=SensorQualityInfo.readFile(context);
//        groups.append(GROUP_APP, createGroupApp(appInfos));
//        groups.append(GROUP_SERVICE,createGroupService(appInfos));
//        groups.append(GROUP_DEVICE_SETTINGS,createGroupDeviceSettings(deviceInfos));
//        groups.append(GROUP_SENSOR_QUALITY,createGroupSensorQuality(sensorQualityInfos));
//        groups.append(GROUP_STORAGE,createGroupApp(appInfos));
    }

/*    Group createGroupApp(ArrayList<AppInfo> appInfos) {
        Apps apps = new Apps(context, "Applications");
        for (int i = 0; i < appInfos.size(); i++) {
            apps.add(appInfos.get(i));
        }
        return apps;
    }
*/
    public void update() {
        groups.get(GROUP_APP).update();
    }

/*    Group createGroupService(ArrayList<AppInfo> appInfos){
        GroupService groupService=new GroupService(context, "Services");
        for(int i=0;i<appInfos.size();i++){
            if(appInfos.get(i).service!=null && appInfos.get(i).service!="")
                groupService.add(appInfos.get(i));
        }
        return groupService;
    }
    Group createGroupDeviceSettings(ArrayList<DeviceInfo> deviceInfos){
        GroupDeviceSettings groupDeviceSettings=new GroupDeviceSettings(context, "Device Settings", onDataUpdated);
        for(int i=0;i<deviceInfos.size();i++){
            groupDeviceSettings.add(deviceInfos.get(i));
        }
        return groupDeviceSettings;
    }
    Group createGroupSensorQuality(ArrayList<SensorQualityInfo> sensorQualityInfos){
        GroupSensorQuality groupSensorQuality=new GroupSensorQuality(context, "Data Quality", onDataUpdated);
        for(int i=0;i<sensorQualityInfos.size();i++){
            groupSensorQuality.add(sensorQualityInfos.get(i));
        }
        return groupSensorQuality;
    }
    */
}
