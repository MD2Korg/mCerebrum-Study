package org.md2k.study.model_view.wakeup_info;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;
import java.util.HashMap;

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
public class WakeupInfoManager extends Model {
    private static final String TAG = WakeupInfoManager.class.getSimpleName();
    DataSourceBuilder dataSourceBuilder;
    DataSourceClient dataSourceClient;
    DataKitAPI dataKitAPI;
    long wakeupTimeDB;
    long wakeupTimeNew;

    public WakeupInfoManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        status = new Status(rank, Status.WAKEUP_NOT_DEFINED);
        wakeupTimeNew = -1;
        wakeupTimeDB = -1;
    }

    public void clear() {
        wakeupTimeNew = -1;
        wakeupTimeDB = -1;
        status = new Status(rank, Status.WAKEUP_NOT_DEFINED);
    }

    public void set() {
        dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
        dataSourceBuilder = createDataSourceBuilder();
        readStudyInfoFromDataKit();
        update();
    }

    public void update() {
        Status lastStatus;
        if (wakeupTimeDB == -1)
            lastStatus = new Status(rank, Status.WAKEUP_NOT_DEFINED);
        else lastStatus = new Status(rank, Status.SUCCESS);
        notifyIfRequired(lastStatus);
    }


    public boolean isValid() {
        if (wakeupTimeNew == -1) return false;
        if (wakeupTimeDB == -1) return true;
        if (wakeupTimeDB == wakeupTimeNew) return false;
        return true;
    }

    private void readStudyInfoFromDataKit() {
        if (dataKitAPI.isConnected()) {
            dataSourceClient = dataKitAPI.register(dataSourceBuilder);
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                wakeupTimeDB = dataTypeLong.getSample();
            }
        }
    }


    private boolean writeToDataKit() {
        if (!dataKitAPI.isConnected()) return false;
        if (!isValid()) return false;
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), wakeupTimeNew);
        dataSourceClient = dataKitAPI.register(dataSourceBuilder);
        dataKitAPI.insert(dataSourceClient, dataTypeLong);
        wakeupTimeDB = wakeupTimeNew;
        return true;
    }

    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.WAKEUP).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Wake Up");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains wakeup time");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeLong.class.getName());
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(createDataDescriptors());
        return dataSourceBuilder;
    }

    ArrayList<HashMap<String, String>> createDataDescriptors() {
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        dataDescriptors.add(createDescriptor("Wakeup time"));
        return dataDescriptors;
    }

    HashMap<String, String> createDescriptor(String name) {
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, name);
        dataDescriptor.put(METADATA.MIN_VALUE, String.valueOf(0));
        dataDescriptor.put(METADATA.MAX_VALUE, String.valueOf(24 * 60 * 60 * 1000));
        dataDescriptor.put(METADATA.UNIT, "millisecond");
        dataDescriptor.put(METADATA.DESCRIPTION, name);
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        return dataDescriptor;
    }

    public void save() {
        if (writeToDataKit())
            wakeupTimeDB = wakeupTimeNew;
        set();
    }
    public Status getStatus(){
        String msg="";
        if(wakeupTimeNew!=-1) msg=formatTime(wakeupTimeNew);
        else if(wakeupTimeDB!=-1) msg=formatTime(wakeupTimeDB);
        if(wakeupTimeDB!=-1 || wakeupTimeNew!=-1) return new Status(rank,Status.SUCCESS, msg);
        return new Status(rank,Status.WAKEUP_NOT_DEFINED);
    }

    public long getWakeupTimeDB() {
        return wakeupTimeDB;
    }

    public long getWakeupTimeNew() {
        return wakeupTimeNew;
    }

    public void setWakeupTimeNew(long wakeupTimeNew) {
        this.wakeupTimeNew = wakeupTimeNew;
    }

    String formatTime(long timestamp) {
        long hourOfDay, minute;
        timestamp = timestamp / (60 * 1000);
        minute = timestamp % 60;
        timestamp /= 60;
        hourOfDay = timestamp;
        if (hourOfDay > 12)
            return String.format("%02d:%02d pm", hourOfDay - 12, minute);
        else if (hourOfDay == 12)
            return String.format("%02d:%02d pm", 12, minute);
        else {
            if (hourOfDay != 0)
                return String.format("%02d:%02d am", hourOfDay, minute);
            else
                return String.format("%02d:%02d am", 12, minute);
        }
    }

}