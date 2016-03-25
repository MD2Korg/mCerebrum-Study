package org.md2k.study.model_view.datakit_connect;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.messagehandler.OnExceptionListener;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
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
public class DataKitConnectManager extends Model {
    private static final String TAG = DataKitConnectManager.class.getSimpleName();
    DataKitAPI dataKitAPI;

    public DataKitConnectManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
        status=new Status(rank,Status.DATAKIT_NOT_AVAILABLE);
        dataKitAPI=DataKitAPI.getInstance(modelManager.getContext());
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void set() {
        if (dataKitAPI.isConnected()) {
            notifyIfRequired(new Status(rank, Status.SUCCESS));
        }else{
            dataKitAPI.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    notifyIfRequired(new Status(rank, Status.SUCCESS));
                }
            }, new OnExceptionListener() {
                @Override
                public void onException(org.md2k.datakitapi.status.Status status) {
                    notifyIfRequired(new Status(rank, Status.DATAKIT_NOT_AVAILABLE));
                }
            });
        }
    }

    @Override
    public void clear() {
        if(dataKitAPI.isConnected()){
            dataKitAPI.disconnect();
            notifyIfRequired(new Status(rank,Status.DATAKIT_NOT_AVAILABLE));
        }
    }
}
