package org.md2k.study.model_view.clear_config;

import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;

import java.io.File;
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
public class ClearConfigManager extends Model {
    private static final String TAG =ClearConfigManager.class.getSimpleName() ;

    public ClearConfigManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        status=new Status(rank, Status.SUCCESS);
    }

    public void delete(){
        ArrayList<String> requiredFiles=modelManager.getConfigManager().getConfig().getConfig_info().getRequired_files();
        deleteDirectoryExcept(Constants.CONFIG_DIRECTORY_BASE, requiredFiles);
    }
    private void deleteDirectoryExcept(String directory, ArrayList<String> requiredFiles){
        deleteRecursiveExcept(new File(directory), requiredFiles);
    }
    private void deleteRecursiveExcept(File fileOrDirectory, ArrayList<String> requiredFiles){
        if(fileOrDirectory.isDirectory()){
            for(File child:fileOrDirectory.listFiles()){
                deleteRecursiveExcept(child, requiredFiles);
            }
        }
        if(fileOrDirectory.isDirectory()){
            if(fileOrDirectory.listFiles().length==0)
                fileOrDirectory.delete();
        }else{
            boolean flag=true;
            for(int i=0;i<requiredFiles.size();i++)
                if(fileOrDirectory.getAbsolutePath().equals(Constants.CONFIG_DIRECTORY_BASE+requiredFiles.get(i))) {
                    flag = false;
                    break;
                }
            if(flag) {
                Log.d(TAG, "file DELETE..." + fileOrDirectory.getAbsolutePath());
                fileOrDirectory.delete();
            }else{
                Log.d(TAG, "file NOT delete..." + fileOrDirectory.getPath());
            }
        }
    }

    @Override
    public void set() {

    }

    @Override
    public void clear() {

    }
}
