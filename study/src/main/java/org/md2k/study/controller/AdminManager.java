package org.md2k.study.controller;

import android.content.Context;

import org.md2k.study.Status;
import org.md2k.study.config.Admin;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.model.Model;
import org.md2k.utilities.Report.Log;

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
public class AdminManager{
    private static final String TAG = AdminManager.class.getSimpleName();
    private static AdminManager instance = null;
    Admin admin;
    ArrayList<Model> models;

    public static AdminManager getInstance(Context context){
        if (instance == null)
            instance = new AdminManager(context);
        return instance;
    }

    private AdminManager(Context context){
        admin = ConfigManager.getInstance(context).getConfig().getAdmin();
        ModelManager modelManager=ModelManager.getInstance(context);
        models=new ArrayList<>();
        for(int i=0;i<admin.getPanel().size();i++){
            models.add(modelManager.getModel(admin.getPanel().get(i)));
        }
    }

    public void close() {
        instance = null;
    }

    public void reset(){
        for(int i=0;i< models.size();i++)
            models.get(i).reset();
    }

    public Status getStatus() {
        Status status;
        for(int i=0;i< models.size();i++){
            status= models.get(i).getStatus();
            Log.d(TAG, " model=" + models.get(i).getOperation().getId() + " status=" + status.getStatusMessage());
            if(status.getStatusCode()!=Status.SUCCESS)
                return status;
        }
        return new Status(Status.SUCCESS);
    }

    public Admin getAdmin() {
        return admin;
    }

    public ArrayList<Model> getModels() {
        return models;
    }
}
