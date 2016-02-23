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
    ArrayList<Application> application;
    ArrayList<Operation> operation;
    Admin admin;
    User user;
    ArrayList<DataSource> data_quality;

    public ConfigInfo getConfig_info() {
        return config_info;
    }

    public ArrayList<Application> getApplication() {
        return application;
    }

    public StudyInfo getStudy_info() {
        return study_info;
    }

    public Admin getAdmin() {
        return admin;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<DataSource> getData_quality() {
        return data_quality;
    }

    public ArrayList<Operation> getOperation() {
        return operation;
    }
    public Operation getOperation(String id){
        for(int i=0;i<operation.size();i++)
            if(operation.get(i).getId().equals(id)) return operation.get(i);
        return null;
    }
}
