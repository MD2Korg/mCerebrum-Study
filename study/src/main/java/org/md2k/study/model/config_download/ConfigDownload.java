package org.md2k.study.model.config_download;

import android.content.Context;
import android.os.AsyncTask;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.model.Model;
import org.md2k.study.utilities.Download;
import org.md2k.study.utilities.OnCompletionListenter;
import org.md2k.utilities.Files;
import org.md2k.utilities.Report.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


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
public class ConfigDownload extends Model {
    private static final String TAG = ConfigDownload.class.getSimpleName();
    String name;

    public ConfigDownload(Context context, ConfigManager configManager, DataKitAPI dataKitAPI, Operation operation) {
        super(context, configManager, dataKitAPI, operation);
    }

    public void start() {
        lastStatus=new Status(Status.SUCCESS);update();
    }

    public void stop() {

    }

    public void update() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void set() {
        lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);
    }

    public boolean isDirectoryExist() {
        return Files.isExist(Constants.CONFIG_DIRECTORY_BASE);
    }

    private void download(Context context, OnCompletionListenter onCompletionListenter) {
        String filename = "config_" + UUID.randomUUID() + ".zip";
        String downloadLinkName = Constants.CONFIG_DOWNLOAD_LINK + name + ".zip";
        Download download = new Download(context, onCompletionListenter);
        download.execute(downloadLinkName, filename);
    }

    @Override
    public Status getStatus() {
        return new Status(Status.SUCCESS);
    }
    private void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        Log.d(TAG, "Creating dir " + dir.getName());
        if (!dir.mkdirs()) {
            throw new RuntimeException("Cannot create dir " + dir);
        }
    }
    public void unzip(String tempFileName, String destinationPath) {
        try {

            int index = destinationPath.lastIndexOf("/");
            String fileString = destinationPath.substring(index);

            File extFile = new File(fileString);
            if(!extFile.exists()) {
                createDir(extFile);
            }

            byte[] buffer = new byte[1024];

            FileInputStream fin = new FileInputStream(tempFileName);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry zipentry = null;
            if (!(zin.available() == 0)) {
                while ((zipentry = zin.getNextEntry()) != null) {
                    String zipName = zipentry.getName();
                    if (zipName.startsWith("/")) {
                        zipName = zipentry.getName();
                    } else if (zipName.startsWith("\\")) {
                        zipName = zipentry.getName();
                    } else {
                        zipName = "/" + zipentry.getName();
                    }

                    String fileName = destinationPath + zipName;
                    fileName = fileName.replace("\\", "/");
                    fileName = fileName.replace("//", "/");

                    if (zipentry.isDirectory()) {
                        createDir(new File(fileName));
                        continue;
                    }

                    String name = zipentry.getName();
                    int start, end = 0;
                    while (true) {

                        start = name.indexOf('\\', end);
                        end = name.indexOf('\\', start + 1);
                        if (start > 0)
                            "check".toString();
                        if (end > start && end > -1 && start > -1) {
                            String dir = name.substring(1, end);

                            createDir(new File(destinationPath + '/' + dir));
                            // name = name.substring(end);
                        } else
                            break;
                    }

                    File file = new File(fileName);

                    FileOutputStream tempDexOut = new FileOutputStream(file);
                    int BytesRead = 0;

                    if (zipentry != null) {
                        if (zin != null) {
                            while ((BytesRead = zin.read(buffer)) != -1) {
                                tempDexOut.write(buffer, 0, BytesRead);
                            }
                            tempDexOut.flush();
                            tempDexOut.close();
                            Log.d(TAG,"filename="+file.getAbsolutePath()+" name="+file.getName()+" size="+file.length());
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }
}
