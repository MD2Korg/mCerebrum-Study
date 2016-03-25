package org.md2k.study.model_view;

import org.md2k.study.Status;
import org.md2k.study.config.Action;
import org.md2k.study.controller.ModelManager;
import org.md2k.utilities.Report.Log;

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
public abstract class Model {
    private static final String TAG = Model.class.getSimpleName();
    protected String id;
    protected ModelManager modelManager;
    protected int rank;
    protected Status status;
    protected Action action;

    public Model(ModelManager modelManager, String id, int rank) {
        this.id=id;
        this.modelManager=modelManager;
        this.rank = rank;
        if(modelManager.getConfigManager()!=null && modelManager.getConfigManager().isValid())
            action =modelManager.getConfigManager().getConfig().getAction(id);
        status=new Status(rank, Status.NOT_DEFINED);
    }

    public Action getAction() {
        return action;
    }

    public int getRank(){
        return rank;
    }

    public Status getStatus(){
        return status;
    }
    public abstract void set();
    public abstract void clear();
    public void reset(){
        clear();
        set();
    }
    public void notifyIfRequired(Status curStatus){
        if(curStatus==null) return;
        Log.d(TAG, "notifyIfRequired...old_status="+status.log()+" cur_status="+curStatus.log());
        if(status==null || status.getRank()!=curStatus.getRank() || status.getStatus()!=curStatus.getStatus()) {
            status=curStatus;
            Log.d(TAG,"notifyIfRequired..notified");
            modelManager.update();
        }
    }
    public void save(){

    }
}
