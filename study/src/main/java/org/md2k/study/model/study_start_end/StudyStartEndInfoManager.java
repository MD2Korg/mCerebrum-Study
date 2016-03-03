package org.md2k.study.model.study_start_end;

import android.content.Context;

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
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;

import java.util.ArrayList;
import java.util.Calendar;
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
public class StudyStartEndInfoManager extends Model {
    DataSourceBuilder dataSourceBuilderStudyStart;
    DataSourceClient dataSourceClientStudyStart;
    DataSourceBuilder dataSourceBuilderStudyEnd;
    DataSourceClient dataSourceClientStudyEnd;
    long studyStartTime;
    long studyEndTime;

    public StudyStartEndInfoManager(Context context, ConfigManager configManager, DataKitAPI dataKitAPI, Operation operation) {
        super(context, configManager, dataKitAPI, operation);
        dataSourceBuilderStudyStart = createDataSourceBuilderStudyStart();
        dataSourceBuilderStudyEnd = createDataSourceBuilderStudyEnd();
    }

    public void start() {
        update();
    }

    public void stop() {

    }

    public void set() {
        readStudyStartFromDataKit();
        readStudyEndFromDataKit();
        lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);
    }
    public void clear(){
        studyStartTime = -1;
        studyEndTime = -1;
    }

    public void update(){
        if (!dataKitAPI.isConnected()) lastStatus=new Status(Status.DATAKIT_NOT_AVAILABLE);
        else if (studyStartTime == -1)
            lastStatus= new Status(Status.STUDY_START_NOT_AVAILABLE);
        else if (studyStartTime < studyEndTime) {
            lastStatus= new Status(Status.SUCCESS,"Study is completed");
        }
        else lastStatus=new Status(Status.SUCCESS, "Study is running");
    }

    public Status getStatus() {
        update();
        return lastStatus;
    }

    private void readStudyStartFromDataKit() {
        studyStartTime=-1;
        if (dataKitAPI.isConnected()) {
            dataSourceClientStudyStart = dataKitAPI.register(dataSourceBuilderStudyStart);
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientStudyStart, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                studyStartTime = dataTypeLong.getSample();
            }
        }
    }

    private void readStudyEndFromDataKit() {
        studyEndTime=-1;
        if (dataKitAPI.isConnected()) {
            dataSourceClientStudyEnd = dataKitAPI.register(dataSourceBuilderStudyEnd);
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientStudyEnd, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                studyEndTime = dataTypeLong.getSample();
            }
        }
    }

    private boolean writeStudyStartToDataKit() {
        if (!dataKitAPI.isConnected()) return false;
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), studyStartTime);
        dataSourceClientStudyStart = dataKitAPI.register(dataSourceBuilderStudyStart);
        dataKitAPI.insert(dataSourceClientStudyStart, dataTypeLong);
        return true;
    }

    private boolean writeStudyEndToDataKit() {
        if (!dataKitAPI.isConnected()) return false;
        DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), studyEndTime);
        dataSourceClientStudyEnd = dataKitAPI.register(dataSourceBuilderStudyEnd);
        dataKitAPI.insert(dataSourceClientStudyEnd, dataTypeLong);
        return true;
    }

    DataSourceBuilder createDataSourceBuilderStudyStart() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.STUDY_START).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Study Start");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Represents when study started");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeLong.class.getName());
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, "Study Start");
        dataDescriptor.put(METADATA.MIN_VALUE, String.valueOf(0));
        dataDescriptor.put(METADATA.MAX_VALUE, String.valueOf(Long.MAX_VALUE));
        dataDescriptor.put(METADATA.UNIT, "millisecond");
        dataDescriptor.put(METADATA.DESCRIPTION, "Contains study start time in millisecond");
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        dataDescriptors.add(dataDescriptor);
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(dataDescriptors);
        return dataSourceBuilder;
    }

    DataSourceBuilder createDataSourceBuilderStudyEnd() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.STUDY_END).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Study End");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Represents when study ended");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeLong.class.getName());
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, "Study End");
        dataDescriptor.put(METADATA.MIN_VALUE, String.valueOf(0));
        dataDescriptor.put(METADATA.MAX_VALUE, String.valueOf(Long.MAX_VALUE));
        dataDescriptor.put(METADATA.UNIT, "millisecond");
        dataDescriptor.put(METADATA.DESCRIPTION, "Contains study end time in millisecond");
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        dataDescriptors.add(dataDescriptor);
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(dataDescriptors);
        return dataSourceBuilder;
    }

    public void saveStudyStart() {
        writeStudyStartToDataKit();
    }

    public void saveStudyEnd() {
        writeStudyEndToDataKit();
    }

    public void setStudyStartTime(long studyStartTime) {
        this.studyStartTime = studyStartTime;
    }

    public void setStudyEndTime(long studyEndTime) {
        this.studyEndTime = studyEndTime;
    }

    public long getStudyStartTime() {
        return studyStartTime;
    }

    public long getStudyEndTime() {
        return studyEndTime;
    }
}
