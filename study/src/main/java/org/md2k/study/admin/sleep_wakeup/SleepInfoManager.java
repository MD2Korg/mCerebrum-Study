package org.md2k.study.admin.sleep_wakeup;

import android.content.Context;

import com.google.gson.Gson;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.datatype.DataTypeLongArray;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.utilities.datakit.DataKitHandler;

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
public class SleepInfoManager {
    DataKitHandler dataKitHandler;
    DataSourceBuilder dataSourceBuilder;
    DataSourceClient dataSourceClient;
    long sleepStartTime=-1;
    long sleepEndTime=-1;

    public SleepInfoManager(Context context) {
        dataKitHandler = DataKitHandler.getInstance(context);
        dataSourceBuilder = createDataSourceBuilder();
        readStudyInfoFromDataKit();
    }
    public long getSleepStartTime() {
        return sleepStartTime;
    }

    public void setSleepStartTime(long sleepStartTime) {
        this.sleepStartTime = sleepStartTime;
    }

    public long getSleepEndTime() {
        return sleepEndTime;
    }

    public void setSleepEndTime(long sleepEndTime) {
        this.sleepEndTime = sleepEndTime;
    }
    public Status getStatus(){
        if(sleepStartTime==-1)
            return new Status(Status.SLEEP_NOT_DEFINED);
        if(sleepEndTime==-1)
            return new Status(Status.WAKEUP_NOT_DEFINED);
        return new Status(Status.SUCCESS);
    }
    public Status getStatusSleepStart(){
        if(sleepStartTime==-1)
            return new Status(Status.SLEEP_NOT_DEFINED);
        return new Status(Status.SUCCESS);
    }
    public Status getStatusSleepEnd(){
        if(sleepEndTime==-1)
            return new Status(Status.WAKEUP_NOT_DEFINED);
        return new Status(Status.SUCCESS);
    }

    private void readStudyInfoFromDataKit() {
        if(dataKitHandler.isConnected()) {
            dataSourceClient = dataKitHandler.register(dataSourceBuilder);
            ArrayList<DataType> dataTypes = dataKitHandler.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeLongArray dataTypeLongArray = (DataTypeLongArray) dataTypes.get(0);
                sleepStartTime=dataTypeLongArray.getSample()[0];
                sleepEndTime=dataTypeLongArray.getSample()[1];
            }
            else{
                sleepEndTime=-1;
                sleepStartTime=-1;
            }
        }
    }

    public boolean writeToDataKit(){
        if(!dataKitHandler.isConnected()) return false;
        long samples[]=new long[2];
        samples[0]=sleepStartTime;
        samples[1]=sleepEndTime;
        DataTypeLongArray dataTypeLongArray=new DataTypeLongArray(DateTime.getDateTime(),samples);
        dataSourceClient = dataKitHandler.register(dataSourceBuilder);
        dataKitHandler.insert(dataSourceClient,dataTypeLongArray);
        return true;
    }
    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.SLEEP).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Sleep");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains sleep start time & sleep end time");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeLongArray.class.getName());
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(createDataDescriptors());
        return dataSourceBuilder;
    }
    ArrayList<HashMap<String, String>>  createDataDescriptors() {
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        dataDescriptors.add(createDescriptor("Sleep Start time"));
        dataDescriptors.add(createDescriptor("Sleep End time"));
        return dataDescriptors;
    }
    HashMap<String, String> createDescriptor(String name){
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, name);
        dataDescriptor.put(METADATA.MIN_VALUE, String.valueOf(0));
        dataDescriptor.put(METADATA.MAX_VALUE, String.valueOf(24*60*60*1000));
        dataDescriptor.put(METADATA.UNIT, String.valueOf("millisecond"));
        dataDescriptor.put(METADATA.DESCRIPTION, name);
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        return dataDescriptor;
    }
}
