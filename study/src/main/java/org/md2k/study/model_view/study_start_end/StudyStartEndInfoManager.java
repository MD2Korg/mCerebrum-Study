package org.md2k.study.model_view.study_start_end;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.ServiceSystemHealth;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;

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
    private static final String TAG = StudyStartEndInfoManager.class.getSimpleName();
    long studyStartTime;
    long studyEndTime;

    public StudyStartEndInfoManager(ModelManager modelManager, String id, int rank) {
        super(modelManager,id,rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        studyStartTime=-1;
        studyEndTime=-1;
        status=new Status(rank, Status.NOT_DEFINED);
    }

    public void set() {
        Status lastStatus;
        readStudyStartFromDataKit();
        readStudyEndFromDataKit();
        if (studyStartTime == -1)
            lastStatus= new Status(rank, Status.STUDY_START_NOT_AVAILABLE);
        else if (studyStartTime < studyEndTime) {
            lastStatus= new Status(rank, Status.SUCCESS);
        }
        else lastStatus=new Status(rank,Status.SUCCESS);
        notifyIfRequired(lastStatus);

    }
    public void clear(){
        studyStartTime = -1;
        studyEndTime = -1;
        status=new Status(rank, Status.NOT_DEFINED);
    }

    private void readStudyStartFromDataKit()  {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            studyStartTime = -1;
            if (dataKitAPI.isConnected()) {
                DataSourceClient dataSourceClientStudyStart = dataKitAPI.register(createDataSourceBuilderStudyStart());
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientStudyStart, 1);
                if (dataTypes.size() != 0) {
                    DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                    studyStartTime = dataTypeLong.getSample();
                    if (!isToday(studyStartTime)) studyStartTime = -1;
                }
            }
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(ServiceSystemHealth.INTENT_RESTART));
        }
    }

    private void readStudyEndFromDataKit() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            studyEndTime = -1;
            if (dataKitAPI.isConnected()) {
                DataSourceClient dataSourceClientStudyEnd = dataKitAPI.register(createDataSourceBuilderStudyEnd());
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClientStudyEnd, 1);
                if (dataTypes.size() != 0) {
                    DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                    studyEndTime = dataTypeLong.getSample();
                }
            }
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(ServiceSystemHealth.INTENT_RESTART));
        }
    }
    boolean isToday(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        if (calendar.get(Calendar.YEAR) != calendarNow.get(Calendar.YEAR)) return false;
        if (calendar.get(Calendar.MONTH) != calendarNow.get(Calendar.MONTH)) return false;
        if (calendar.get(Calendar.DAY_OF_MONTH) != calendarNow.get(Calendar.DAY_OF_MONTH))
            return false;
        return true;
    }

    private boolean writeStudyStartToDataKit()  {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            if (!dataKitAPI.isConnected()) return false;
            DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), studyStartTime);
            DataSourceClient dataSourceClientStudyStart = dataKitAPI.register(createDataSourceBuilderStudyStart());
            dataKitAPI.insert(dataSourceClientStudyStart, dataTypeLong);
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(ServiceSystemHealth.INTENT_RESTART));
        }
        return true;
    }

    private boolean writeStudyEndToDataKit() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            if (!dataKitAPI.isConnected()) return false;
            DataTypeLong dataTypeLong = new DataTypeLong(DateTime.getDateTime(), studyEndTime);
            DataSourceClient dataSourceClientStudyEnd = dataKitAPI.register(createDataSourceBuilderStudyEnd());
            dataKitAPI.insert(dataSourceClientStudyEnd, dataTypeLong);
            return true;
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(ServiceSystemHealth.INTENT_RESTART));
            return false;
        }
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

    public void setStudyStartTime(long studyStartTime) {
        this.studyStartTime = studyStartTime;
        writeStudyStartToDataKit();
        reset();

    }

    public void setStudyEndTime(long studyEndTime) {
        this.studyEndTime = studyEndTime;
        writeStudyEndToDataKit();
        reset();
    }

    public long getStudyStartTime() {
        return studyStartTime;
    }

    public long getStudyEndTime() {
        return studyEndTime;
    }
    public Status getCurrentStatusDetails(){
        if(studyStartTime==-1 ) return new Status(rank,Status.STUDY_START_NOT_AVAILABLE);
        if(studyEndTime>studyStartTime) return new Status(rank,Status.STUDY_COMPLETED);
        return new Status(rank, Status.STUDY_RUNNING);
    }

}
