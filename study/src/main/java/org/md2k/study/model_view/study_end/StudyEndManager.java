package org.md2k.study.model_view.study_end;

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
import org.md2k.study.Constants;
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
public class StudyEndManager extends Model {
    private static final String TAG = StudyEndManager.class.getSimpleName();

    private long studyEndDB;
    private long studyEndNew;

    public StudyEndManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        status = new Status(rank, Status.STUDY_END_NOT_DEFINED);
        studyEndNew = -1;
        studyEndDB = -1;
    }

    public void clear() {
        studyEndNew = -1;
        studyEndDB = -1;
        status = new Status(rank, Status.STUDY_END_NOT_DEFINED);
    }

    void setStudyEnd(long studyEnd) {
        studyEndNew = studyEnd;
    }

    public void set() {
        readFromDataKit();
        update();
    }

    private void update() {
        Status lastStatus;
        if (studyEndDB == -1)
            lastStatus = new Status(rank, Status.STUDY_END_NOT_DEFINED);
        else lastStatus = new Status(rank, Status.SUCCESS);
        notifyIfRequired(lastStatus);
    }

    private boolean isValid() {
        if (studyEndNew == -1) return false;
        if (studyEndDB == -1) return true;
        if (studyEndDB == studyEndNew) return false;
        return true;
    }

    private void readFromDataKit() {
        try {
            studyEndDB = -1;
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeLong dataTypeLong = (DataTypeLong) dataTypes.get(0);
                studyEndDB = dataTypeLong.getSample();
            }
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
        }
    }

    private boolean writeToDataKit() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            if (!isValid()) return false;
            DataTypeLong dataTypeLong=new DataTypeLong(DateTime.getDateTime(), studyEndNew);
            DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
            dataKitAPI.insert(dataSourceClient, dataTypeLong);
            studyEndDB = studyEndNew;
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
            return false;
        }
        return true;
    }

    private DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.STUDY_END).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "End study");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Defines the time of end of the study");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, long.class.getName());
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(createDataDescriptors());
        return dataSourceBuilder;
    }

    private ArrayList<HashMap<String, String>> createDataDescriptors() {
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        dataDescriptors.add(createDescriptor("End Study"));
        return dataDescriptors;
    }

    private HashMap<String, String> createDescriptor(String name) {
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, name);
        dataDescriptor.put(METADATA.UNIT, "timestamp");
        dataDescriptor.put(METADATA.DESCRIPTION, name);
        dataDescriptor.put(METADATA.DATA_TYPE, long.class.getName());
        return dataDescriptor;
    }

    public void save() {
        if (writeToDataKit())
            studyEndDB = studyEndNew;
        set();
    }

    public Status getStatus() {
        String msg = "";
        if (studyEndNew != -1) msg = DateTime.convertTimeStampToDateTime(studyEndNew,"EEE, d MMM yyyy, HH:mm:ss");
        else if (studyEndDB != -1) msg = DateTime.convertTimeStampToDateTime(studyEndNew,"EEE, d MMM yyyy, HH:mm:ss");
        if (studyEndDB != -1 || studyEndNew != -1) return new Status(rank, Status.SUCCESS, msg);
        return new Status(rank, Status.STUDY_END_NOT_DEFINED);
    }
}

