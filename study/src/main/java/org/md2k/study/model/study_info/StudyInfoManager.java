package org.md2k.study.model.study_info;

import android.content.Context;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeString;
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
import org.md2k.study.config.StudyInfo;
import org.md2k.study.model.Model;
import org.md2k.utilities.Report.Log;

import java.io.FileNotFoundException;
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
public class StudyInfoManager extends Model {
    private static final String TAG = StudyInfoManager.class.getSimpleName();
    DataSourceBuilder dataSourceBuilder;
    DataSourceClient dataSourceClient;
    StudyInfo studyInfoDB;
    StudyInfo studyInfoFile;

    public StudyInfoManager(Context context, ConfigManager configManager, DataKitAPI dataKitAPI, Operation operation) {
        super(context, configManager, dataKitAPI, operation);
    }

    public void set() {
        studyInfoFile = configManager.getConfig().getStudy_info();
        dataSourceBuilder = createDataSourceBuilder();
        studyInfoDB = readFromDataKit();
        if (studyInfoDB == null && studyInfoFile != null) {
            writeToDataKit();
            studyInfoDB = readFromDataKit();
        }
        lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);
    }

    public void clear() {
        studyInfoDB = null;
        studyInfoFile = null;
    }

    public void start() {
        if (studyInfoDB == null) lastStatus = new Status(Status.DATAKIT_NOT_AVAILABLE);
        else if (!studyInfoDB.getId().equals(studyInfoFile.getId()) || !studyInfoDB.getName().equals(studyInfoFile.getName()))
            lastStatus = new Status(Status.CLEAR_OLD_DATA);
        else lastStatus = new Status(Status.SUCCESS);
        update();
    }

    @Override
    public void stop() {

    }

    public void update() {

    }

    public Status getStatus() {
        return lastStatus;
    }

    private StudyInfo readFromDataKit() {
        StudyInfo studyInfo = null;

        if (dataKitAPI.isConnected()) {
            dataSourceClient = dataKitAPI.register(dataSourceBuilder);
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeString dataTypeString = (DataTypeString) dataTypes.get(0);
                Gson gson = new Gson();
                studyInfo = gson.fromJson(dataTypeString.getSample(), StudyInfo.class);
            }
        }
        return studyInfo;
    }

    public boolean writeToDataKit() {
        Log.d(TAG, "StudyInfoManager...writeToDataKit()");
        if (!dataKitAPI.isConnected()) return false;
        Gson gson = new Gson();
        String sample = gson.toJson(studyInfoFile);
        dataSourceClient = dataKitAPI.register(dataSourceBuilder);
        DataTypeString dataTypeString = new DataTypeString(DateTime.getDateTime(), sample);
        dataKitAPI.insert(dataSourceClient, dataTypeString);
        studyInfoDB = studyInfoFile;
        return true;
    }

    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).setMetadata(METADATA.NAME, "Phone").build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.STUDY_INFO).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Study Info");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains study_id and study_name as a json object");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeString.class.getName());
        return dataSourceBuilder;
    }

    public String getStudy_id() {
        return studyInfoFile.getId();
    }

    public String getStudy_name() {
        return studyInfoFile.getName();
    }

}
