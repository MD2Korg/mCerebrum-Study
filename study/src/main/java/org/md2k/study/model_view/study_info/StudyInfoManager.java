package org.md2k.study.model_view.study_info;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigInfo;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.StudyInfo;

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
    StudyInfo studyInfoDB;
    StudyInfo studyInfoFile;


    public StudyInfoManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        status = new Status(rank, Status.CLEAR_OLD_DATA);
        studyInfoDB = null;
        studyInfoFile = null;
    }

    public void set() {
        Log.d(TAG, "set()...");
        ConfigInfo configInfo = modelManager.getConfigManager().getConfig().getConfig_info();
        studyInfoFile = new StudyInfo(configInfo.getId(), configInfo.getName(), configInfo.getVersion(), configInfo.getFilename());
        studyInfoDB = readFromDataKit();
        if (studyInfoDB == null && studyInfoFile != null) {
            writeToDataKit();
            studyInfoDB = studyInfoFile;
        }
        update();
    }

    public void clear() {
        studyInfoDB = null;
        studyInfoFile = null;
        status = new Status(rank, Status.CLEAR_OLD_DATA);
    }

    public void update() {
        Log.d(TAG, "update()...");
        Status lastStatus;
        if (studyInfoDB == null) lastStatus = new Status(rank, Status.DATAKIT_NOT_AVAILABLE);
        else if (!studyInfoDB.equals(studyInfoFile))
            lastStatus = new Status(rank, Status.CLEAR_OLD_DATA);
        else lastStatus = new Status(rank, Status.SUCCESS);
        Log.d(TAG, "lastStatus=" + lastStatus.log());
        notifyIfRequired(lastStatus);
    }

    private StudyInfo readFromDataKit() {
        StudyInfo studyInfo = null;
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());

            if (dataKitAPI.isConnected()) {
                DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
                if (dataTypes.size() != 0) {
                    DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataTypes.get(0);
                    Gson gson = new Gson();
                    studyInfo = gson.fromJson(dataTypeJSONObject.getSample().toString(), StudyInfo.class);
                }
            }
        } catch (Exception ignored) {
            studyInfo = new StudyInfo();
        }
        return studyInfo;
    }

    public boolean writeToDataKit() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(modelManager.getContext());
            Log.d(TAG, "StudyInfoManager...writeToDataKit()");
            if (!dataKitAPI.isConnected()) return false;
            Gson gson = new Gson();
            JsonObject sample = new JsonParser().parse(gson.toJson(studyInfoFile)).getAsJsonObject();

            DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
            DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), sample);
            dataKitAPI.insert(dataSourceClient, dataTypeJSONObject);
            studyInfoDB = studyInfoFile;
            return true;
        } catch (DataKitException e) {
            e.printStackTrace();
            return false;
        }
    }

    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).setMetadata(METADATA.NAME, "Phone").build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.STUDY_INFO).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Study Info");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains study_id and study_name as a json object");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeJSONObject.class.getName());
        return dataSourceBuilder;
    }

    public String getStudy_id() {
        if(studyInfoFile==null) {
            ConfigInfo configInfo = modelManager.getConfigManager().getConfig().getConfig_info();
            studyInfoFile = new StudyInfo(configInfo.getId(), configInfo.getName(), configInfo.getVersion(), configInfo.getFilename());
        }
        return studyInfoFile.getId();
    }

}
