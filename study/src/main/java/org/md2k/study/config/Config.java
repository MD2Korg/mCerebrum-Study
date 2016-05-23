package org.md2k.study.config;

import org.md2k.datakitapi.source.datasource.DataSource;

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
public class Config {
    private ConfigInfo config_info;
    private ConfigStudyInfo study_info;
    private ArrayList<ConfigApp> apps;
    private ArrayList<ConfigAction> actions;
    private ConfigView admin_view;
    private ConfigView user_view;
    private ArrayList<DataSource> data_quality;
    private ArrayList<ConfigDataQualityView> data_quality_view;
    private ConfigDayStartEnd day_start;
    private ConfigDayStartEnd day_end;

    public ArrayList<ConfigApp> getApps() {
        ArrayList<ConfigApp> appList=new ArrayList<>();
        if(apps==null || apps.size()==0)
            return appList;
        else {
            for(int i=0;i<apps.size();i++){
                if(apps.get(i).isEnable())
                    appList.add(apps.get(i));
            }
        }
        return appList;
    }
    public ConfigApp getApps(String id){
        if(apps==null || apps.size()==0)
            return null;
        else {
            for(int i=0;i<apps.size();i++){
                if(apps.get(i).isEnable() && apps.get(i).getId().equals(id))
                    return apps.get(i);
            }
        }
        return null;
    }

    public ConfigAction getAction(String id){
        for(int i=0;i< actions.size();i++)
            if(actions.get(i).getId().equals(id)) return actions.get(i);
        return null;
    }

    public ConfigInfo getConfig_info() {
        return config_info;
    }

    public ConfigStudyInfo getStudy_info() {
        return study_info;
    }

    public ArrayList<ConfigAction> getActions() {
        return actions;
    }

    public ConfigView getAdmin_view() {
        return admin_view;
    }

    public ConfigView getUser_view() {
        return user_view;
    }

    public ArrayList<DataSource> getData_quality() {
        return data_quality;
    }

    public ArrayList<ConfigDataQualityView> getData_quality_view() {
        return data_quality_view;
    }

    public ConfigDayStartEnd getDay_start() {
        return day_start;
    }

    public ConfigDayStartEnd getDay_end() {
        return day_end;
    }
}
