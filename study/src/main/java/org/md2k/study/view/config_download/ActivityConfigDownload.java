package org.md2k.study.view.config_download;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import org.md2k.study.Constants;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.config_download.ConfigDownload;
import org.md2k.study.utilities.Download;
import org.md2k.study.utilities.OnCompletionListenter;
import org.md2k.utilities.Files;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.AlertDialogs;

import java.io.IOException;
import java.util.UUID;


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
public class ActivityConfigDownload extends Activity {
    private static final String TAG = ActivityConfigDownload.class.getSimpleName();
    ConfigDownload configDownload;
    String m_Text = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configDownload = (ConfigDownload) ModelManager.getInstance(ActivityConfigDownload.this).getModel(ModelManager.MODEL_CONFIG_DOWNLOAD);
        if (configDownload.isDirectoryExist()) {
            showConfirmationDeleteDirectory();
        } else {
            showConfigurationText();
        }
    }

    public void showConfigurationText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Configuration File");
        builder.setMessage("Type file name (example: default)");
        builder.setIcon(R.drawable.ic_download_teal_48dp);
        builder.setCancelable(false);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString().trim();
                final String filename = "config_" + m_Text + ".zip";
                if (m_Text.length() > 0) {
                    Download download = new Download(ActivityConfigDownload.this, new OnCompletionListenter() {
                        @Override
                        public void OnCompleted(Status status) {
                            if (status.getStatusCode() == Status.SUCCESS) {
                                Toast.makeText(ActivityConfigDownload.this, "Success....Download configuration file", Toast.LENGTH_LONG).show();
                                configDownload.unzip(Constants.TEMP_DIRECTORY + filename, Constants.CONFIG_DIRECTORY_ROOT);
                                ModelManager modelManager = ModelManager.getInstance(ActivityConfigDownload.this);
                                modelManager.stop();
                                modelManager.clear();
                                if (modelManager.getConfigManager().read()) {
                                    modelManager.set();
                                    modelManager.start();
                                    finish();
                                } else {
                                    Toast.makeText(ActivityConfigDownload.this, "Error!!! Configuration file corrupted. Please try with different name", Toast.LENGTH_LONG).show();
                                    showConfigurationText();
                                }
                            } else {
                                Toast.makeText(ActivityConfigDownload.this, "Error!!! Download configuration file. Please Try again with different name", Toast.LENGTH_LONG).show();
                                showConfigurationText();
                            }
                        }
                    });
                    download.execute(Constants.CONFIG_DOWNLOAD_LINK + m_Text + ".zip", filename);
                } else
                    showConfigurationText();
            }
        });
        builder.show();

    }

    public void showConfirmationDeleteDirectory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete configuration directory?");
        builder.setMessage("Do you want to delete configuration directory?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Files.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
                showConfigurationText();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
}
