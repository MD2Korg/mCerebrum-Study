package org.md2k.study.user.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.study.Constants;
import org.md2k.study.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
public class ServiceApps {
    ArrayList<ServiceApp> serviceAppList = new ArrayList<>();
    private static ServiceApps instance;
    Context context;
    public static ServiceApps getInstance(Context context){
        if(instance==null)
            instance=new ServiceApps(context);
        return instance;
    }

    private ServiceApps(Context context) {
        this.context=context;
        readFile(context);
    }
    public int size(){
        return serviceAppList.size();
    }
    public void start(){
        for(int i=0;i<serviceAppList.size();i++)
            serviceAppList.get(i).start(context);
    }
    public void stop(){
        for(int i=0;i<serviceAppList.size();i++)
            serviceAppList.get(i).stop(context);
    }
    public Status getStatus(){
        int status=Status.SUCCESS;
        for(int i=0;i<serviceAppList.size();i++){
            Status curStatus=serviceAppList.get(i).getStatus(context);
            if(curStatus.getStatusCode()==Status.APP_NOT_INSTALLED) {
                status = Status.APP_NOT_INSTALLED;
                break;
            }else if(curStatus.getStatusCode()==Status.APP_NOT_RUNNING){
                status=Status.APP_NOT_RUNNING;
            }
        }
        return new Status(status);
    }

    public void readFile(Context context) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(Constants.FILENAME_SERVICE)));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<ServiceApp>>() {
            }.getType();
            serviceAppList = gson.fromJson(br, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
