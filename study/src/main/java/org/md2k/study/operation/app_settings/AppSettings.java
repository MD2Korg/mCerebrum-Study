package org.md2k.study.operation.app_settings;


import org.md2k.datakitapi.source.AbstractObject;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.utilities.Files;

import java.io.File;
import java.io.FileNotFoundException;
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
public class AppSettings {
    private static final String TAG = AppSettings.class.getSimpleName();
    private String name;
    private String package_name;
    private String default_config;
    private String config;
    private String settings;

    public AppSettings(String name, String package_name, String default_config, String config, String settings) {
        this.name = name;
        this.package_name = package_name;
        this.default_config = default_config;
        this.config = config;
        this.settings = settings;
    }

    public Status getStatus(){
        if(!isEqual()) return new Status(Status.APP_CONFIG_ERROR);
        return new Status(Status.SUCCESS);
    }
    boolean isDataSourceMatch(DataSource dataSource, ArrayList<DataSource> dataSourcesDefault){
        for(int i=0;i<dataSourcesDefault.size();i++){
            DataSource dataSourceDefault=dataSourcesDefault.get(i);
            if(isEqualDataSource(dataSource, dataSourceDefault)) return true;
        }
        return false;
    }
    boolean isEqualDataSource(DataSource dataSource, DataSource dataSourceDefault){
        if(!isFieldMatch(dataSource.getId(), dataSourceDefault.getId())) return false;
        if(!isFieldMatch(dataSource.getType(), dataSourceDefault.getType())) return false;
        if(!isMetaDataMatch(dataSource.getMetadata(), dataSourceDefault.getMetadata())) return false;
        if(!isObjectMatch(dataSource.getPlatform(), dataSourceDefault.getPlatform())) return false;
        if(!isObjectMatch(dataSource.getPlatformApp(), dataSourceDefault.getPlatformApp())) return false;
        if(!isObjectMatch(dataSource.getApplication(), dataSourceDefault.getApplication())) return false;
        return true;
    }
    boolean isObjectMatch(AbstractObject object,AbstractObject objectDefault){
        if(objectDefault==null) return true;
        if(object==null) return false;
        if(!isFieldMatch(object.getId(), objectDefault.getId())) return false;
        if(!isFieldMatch(object.getType(), objectDefault.getType())) return false;
        return true;
    }
    boolean isMetaDataMatch(HashMap<String, String> metadata, HashMap<String,String> metadataDefault){
        String valueDefault, value;
        if(metadataDefault==null) return true;
        if(metadata==null) return false;
        for(String key:metadataDefault.keySet()){
            if(!metadata.containsKey(key)) return false;
            valueDefault=metadataDefault.get(key);
            value=metadata.get(key);
            if(!value.equals(valueDefault))return false;
        }
        return true;
    }
    boolean isFieldMatch(String value, String valueDefault){
        if(valueDefault==null) return true;
        if(value==null) return false;
        if(value.equals(valueDefault)) return true;
        return false;
    }
    boolean isEqual(){
        if(default_config==null) return true;
        if(config==null) return false;
        String outDir= Constants.CONFIG_DIRECTORY+package_name+ File.separator;
        try {
            ArrayList<DataSource> dataSourcesDefault = Files.readJSONArray(outDir,default_config,DataSource.class);
            ArrayList<DataSource> dataSources = Files.readJSONArray(outDir,config,DataSource.class);
            if(dataSources.size()!=dataSourcesDefault.size()) return false;
            for(int i=0;i<dataSources.size();i++){
                if(!isDataSourceMatch(dataSources.get(i), dataSourcesDefault))
                    return false;
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public String getDefault_config() {
        return default_config;
    }

    public String getConfig() {
        return config;
    }

    public String getSettings() {
        return settings;
    }
}
