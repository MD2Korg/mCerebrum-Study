package org.md2k.study.controller;

import android.content.Context;

import org.md2k.study.Status;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.User;
import org.md2k.study.model.Model;
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
public class UserManager{
    private static final String TAG = UserManager.class.getSimpleName();
    private static UserManager instance = null;
    User user;
    ArrayList<Model> models;

    public static UserManager getInstance(Context context){
        if (instance == null)
            instance = new UserManager(context);
        return instance;
    }

    private UserManager(Context context){
        user = ConfigManager.getInstance(context).getConfig().getUser();
        ModelManager modelManager=ModelManager.getInstance(context);
        models=new ArrayList<>();
        for(int i=0;i<user.getPanel().size();i++){
            models.add(modelManager.getModel(user.getPanel().get(i)));
        }
    }

    public void reset(){
        for(int i=0;i< models.size();i++)
            models.get(i).reset();
    }
    public Status getStatus() {
        Status status;
        for(int i=0;i< models.size();i++){
            status= models.get(i).getStatus();
            if(status.getStatusCode()!=Status.SUCCESS)
                return status;
        }
        return new Status(Status.SUCCESS);
    }

    public User getUser() {
        return user;
    }

    public ArrayList<Model> getModels() {
        return models;
    }
    public Model getModels(String modelId){
        if(models==null) return null;
        for(int i=0;i<models.size();i++)
            if(models.get(i).getOperation().getId().equals(modelId))
                return models.get(i);
        return null;
    }
}