package org.md2k.study.model_view.day_type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
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
import org.md2k.utilities.data_format.DayTypeInfo;

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
public class DayTypeManager extends Model {
    private static final String TAG = DayTypeManager.class.getSimpleName();
    DataSourceBuilder dataSourceBuilder;
    DataSourceClient dataSourceClient;
    DataKitAPI dataKitAPI;

    DayTypeInfo dayTypeDB;
    DayTypeInfo dayTypeNew;

    public DayTypeManager(ModelManager modelManager, String id, int rank) {
        super(modelManager,id,rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        status=new Status(rank, Status.DAY_TYPE_NOT_DEFINED);
        dayTypeNew = null;
        dayTypeDB = null;
    }

    public void clear(){
        dayTypeNew = null;
        dayTypeDB = null;
        status=new Status(rank, Status.DAY_TYPE_NOT_DEFINED);
    }
    public void setDayType(int dayType){
        dayTypeNew=new DayTypeInfo(dayType);
    }
    public void set(){
        dataKitAPI =DataKitAPI.getInstance(modelManager.getContext());
        dataSourceBuilder = createDataSourceBuilder();
        readFromDataKit();
        update();
    }
    public void update(){
        Status lastStatus;
        if (dayTypeDB==null)
            lastStatus= new Status(rank,Status.DAY_TYPE_NOT_DEFINED);
        else lastStatus= new Status(rank,Status.SUCCESS);
        notifyIfRequired(lastStatus);
    }

    public boolean isValid() {
        if (dayTypeNew == null) return false;
        if (dayTypeDB == null) return true;
        if (dayTypeDB.equals(dayTypeNew)) return false;
        return true;
    }

    private void readFromDataKit() {
        dayTypeDB = null;
        if (dataKitAPI.isConnected()) {
            dataSourceClient = dataKitAPI.register(dataSourceBuilder);
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient, 1);
            if (dataTypes.size() != 0) {
                DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataTypes.get(0);
                Gson gson = new Gson();
                dayTypeDB = gson.fromJson(dataTypeJSONObject.getSample().toString(), DayTypeInfo.class);
            }
        }
    }

    private boolean writeToDataKit() {
        if (!dataKitAPI.isConnected()) return false;
        if (!isValid()) return false;
        Gson gson = new Gson();
        JsonObject sample = new JsonParser().parse(gson.toJson(dayTypeNew)).getAsJsonObject();
        dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
        DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), sample);
        dataKitAPI.insert(dataSourceClient, dataTypeJSONObject);
        dayTypeDB = dayTypeNew;
        return true;
    }
    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.TYPE_OF_DAY).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Pre/Post Quit Day");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Defines type of Day");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeJSONObject.class.getName());
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(createDataDescriptors());
        return dataSourceBuilder;
    }

    ArrayList<HashMap<String, String>> createDataDescriptors() {
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        dataDescriptors.add(createDescriptor("Pre/Post Quit Day"));
        return dataDescriptors;
    }

    HashMap<String, String> createDescriptor(String name) {
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, name);
        dataDescriptor.put(METADATA.UNIT, "String");
        dataDescriptor.put(METADATA.DESCRIPTION, name);
        dataDescriptor.put(METADATA.DATA_TYPE, DayTypeInfo.class.getName());
        return dataDescriptor;
    }
    public void save(){
        if(writeToDataKit())
            dayTypeDB=dayTypeNew;
        set();
    }
    public Status getStatus(){
        String msg="";
        if(dayTypeNew!=null) msg=dayTypeNew.getDay_type_name();
        else if(dayTypeDB!=null) msg=dayTypeDB.getDay_type_name();
        if(dayTypeDB!=null || dayTypeNew!=null) return new Status(rank,Status.SUCCESS, msg);
        return new Status(rank,Status.DAY_TYPE_NOT_DEFINED);
    }
}

