package org.md2k.study.model_view.user_app;

import org.md2k.study.Status;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
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
public class UserAppManager extends Model {
    private static final String TAG = UserAppManager.class.getSimpleName();
    ArrayList<Model> userApps;


    public UserAppManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        userApps=new ArrayList<>();
    }

    public void set() {
        Log.d(TAG,"set()..");
        userApps.clear();
        ArrayList<String> modelNames=modelManager.getConfigManager().getConfig().getUser_view().getView_contents(ModelFactory.MODEL_USER_APP).getValues();
        for(int i=0;i<modelNames.size();i++){
            Log.d(TAG,"set()..."+modelNames.get(i));
            Model model=modelManager.getModel(modelNames.get(i));
            if(model!=null)
                userApps.add(modelManager.getModel(modelNames.get(i)));
        }
        Log.d(TAG,"set()..size="+userApps.size());
        Status lastStatus=new Status(rank,Status.SUCCESS);;
        notifyIfRequired(lastStatus);
    }
    public void clear(){
        status=new Status(rank, Status.NOT_DEFINED);
    }

    public ArrayList<Model> getUserApps() {
        return userApps;
    }
}
