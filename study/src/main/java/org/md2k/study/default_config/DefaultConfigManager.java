package org.md2k.study.default_config;

import android.content.Context;
import android.content.res.AssetManager;

import org.md2k.study.Constants;
import org.md2k.utilities.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
public class DefaultConfigManager {
    public static boolean prepareConfig(Context context) {
        return isExist(context) || copy(context);
    }
    public static boolean isExist(Context context){
        String directory= Constants.CONFIG_DIRECTORY+context.getPackageName();
        File dir=new File(directory);
        if(!dir.exists()) return false;
        String filename=directory+File.separator+Constants.FILENAME_CONFIG_STUDY;
        File file=new File(filename);
        return file.exists();
    }
    private static boolean copy(Context context){
        String directory= Constants.CONFIG_DIRECTORY+context.getPackageName();
        File outDir=new File(directory);
        outDir.mkdirs();
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        File outFile = new File(outDir, Constants.FILENAME_CONFIG_STUDY);
        try {
            in = assetManager.open("mCerebrum"+File.separator+context.getPackageName()+File.separator+Constants.FILENAME_CONFIG_STUDY);
            out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            return true;
        } catch(IOException e) {
            return false;
        }
    }
}
