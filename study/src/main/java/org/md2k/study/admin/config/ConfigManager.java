package org.md2k.study.admin.config;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.study.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
public class ConfigManager {
    private static ConfigManager instance;
    Context context;
    ArrayList<ConfigurationFile> configurationFileArrayList;

    public static ConfigManager getInstance(Context context){
        if(instance==null)
            instance=new ConfigManager(context);
        return instance;
    }

    private ConfigManager(Context context) {
        this.context=context;
        readFile();
        copyConfigFiles();
    }
    void copyConfigFiles(){
        File outDir = new File(Constants.CONFIG_DIRECTORY);
        outDir.mkdirs();
        if(configurationFileArrayList==null) return;
        for(int i=0;i<configurationFileArrayList.size();i++){
            copy(Constants.CONFIG_DIRECTORY,configurationFileArrayList.get(i).filename);

        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    void copy(String outDir,String filename){
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        File outFile = new File(outDir, filename);
        outFile.delete();
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch(IOException ignored) {
        }
    }

    public void readFile() {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(Constants.FILENAME_CONFIG)));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<ConfigurationFile>>() {
            }.getType();
            configurationFileArrayList = gson.fromJson(br, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ConfigurationFile{
        String filename;
    }
}
