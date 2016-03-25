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
    ConfigInfo config_info;
    StudyInfo study_info;
    ArrayList<App> apps;
    ArrayList<Action> actions;
    CView admin_view;
    CView user_view;
    ArrayList<DataSource> data_quality;

    public ConfigInfo getConfig_info() {
        return config_info;
    }

    public ArrayList<App> getApps() {
        ArrayList<App> appList=new ArrayList<>();
        if(apps==null || apps.size()==0)
            return appList;
        else {
            for(int i=0;i<apps.size();i++){
                if(apps.get(i).enable)
                    appList.add(apps.get(i));
            }
        }
        return appList;
    }
    public App getApps(String id){
        if(apps==null || apps.size()==0)
            return null;
        else {
            for(int i=0;i<apps.size();i++){
                if(apps.get(i).enable && apps.get(i).id.equals(id))
                    return apps.get(i);
            }
        }
        return null;
    }

    public StudyInfo getStudy_info() {
        return study_info;
    }

    public CView getAdmin_view() {
        return admin_view;
    }

    public CView getUser_view() {
        return user_view;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }
    public Action getAction(String id){
        for(int i=0;i< actions.size();i++)
            if(actions.get(i).getId().equals(id)) return actions.get(i);
        return null;
    }

    public ArrayList<DataSource> getData_quality() {
        return data_quality;
    }
}
