package org.md2k.study.admin.study_info;

import android.content.Context;

import com.google.gson.Gson;

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
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.utilities.datakit.DataKitHandler;

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
public class StudyInfoManager {
    DataKitHandler dataKitHandler;
    DataSourceBuilder dataSourceBuilder;
    DataSourceClient dataSourceClient;
    StudyInfo studyInfoInDB;
    StudyInfo studyInfoNew;

    public StudyInfoManager(Context context) {
        dataKitHandler = DataKitHandler.getInstance(context);
        dataSourceBuilder = createDataSourceBuilder();
        readStudyInfoFromDataKit();
    }
    public void setUserIdNew(String userId){
        studyInfoNew=new StudyInfo();
        studyInfoNew.set(userId);
    }
    public Status getStatus(){
        if(getUserIdInDB()==null)
            return new Status(Status.USERID_NOT_DEFINED);
        return new Status(Status.SUCCESS);
    }
    public String getUserIdInDB(){
        if(studyInfoInDB==null) return null;
        return studyInfoInDB.getUser_id();
    }
    public String getUserIdNew(){
        if(studyInfoNew==null) return null;
        return studyInfoNew.getUser_id();
    }

    private void readStudyInfoFromDataKit() {
        studyInfoInDB=null;
        if(dataKitHandler.isConnected()) {
            dataSourceClient = dataKitHandler.register(dataSourceBuilder);
            ArrayList<DataType> dataTypes = dataKitHandler.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeString dataTypeString = (DataTypeString) dataTypes.get(0);
                Gson gson = new Gson();
                studyInfoInDB = gson.fromJson(dataTypeString.getSample(), StudyInfo.class);
                studyInfoNew=new StudyInfo();
                studyInfoNew.set(studyInfoInDB.getUser_id());
            }
        }
    }
    public boolean isValid(){
        if(studyInfoNew==null) return false;
        if(studyInfoNew.getUser_id().length()==0) return false;
        if(studyInfoInDB==null) return true;
        return !studyInfoInDB.getUser_id().equals(studyInfoNew.getUser_id());
    }

    public boolean writeToDataKit(){
        if(!dataKitHandler.isConnected()) return false;
        if(!isValid()) return false;
        Gson gson = new Gson();
        String sample=gson.toJson(studyInfoNew);
        dataSourceClient = dataKitHandler.register(dataSourceBuilder);
        DataTypeString dataTypeString=new DataTypeString(DateTime.getDateTime(),sample);
        dataKitHandler.insert(dataSourceClient,dataTypeString);
        return true;
    }
    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.STUDY_INFO).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Study Info");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Contains user_id and study_id as a json object");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeString.class.getName());
        return dataSourceBuilder;
    }
}
