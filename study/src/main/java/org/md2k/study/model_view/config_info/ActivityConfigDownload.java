package org.md2k.study.model_view.config_info;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import org.md2k.study.Constants;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.utilities.Download;
import org.md2k.study.utilities.OnCompletionListener;
import org.md2k.utilities.FileManager;
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
public class ActivityConfigDownload extends Activity {
    private static final String TAG = ActivityConfigDownload.class.getSimpleName();
    String m_Text = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status status = getIntent().getParcelableExtra(Status.class.getSimpleName());
        if (status == null) {
            showDeleteDirectory();
        } else {
            Log.d(TAG, "onCreate()...rank=" + status.getRank() + " status=" + status.getStatus());
            if (FileManager.isExist(Constants.CONFIG_DIRECTORY_BASE)) {
                Log.d(TAG, "directory exists...deleting...");
                FileManager.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
            }
            showDownloadConfig();
        }
    }

    public void showDownloadConfig() {
        Log.d(TAG, "showDownloadConfig()...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Configuration File");
        builder.setMessage("Please enter the file name (example: default)");
        builder.setIcon(R.drawable.ic_download_teal_48dp);
        builder.setCancelable(false);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString().trim();
                final String filename = m_Text + ".zip";
                if (m_Text.length() > 0) {
                    Download download = new Download(ActivityConfigDownload.this, true, new OnCompletionListener() {
                        @Override
                        public void OnCompleted(int status) {
                            if (status == Download.SUCCESS) {
                                ModelManager.getInstance(ActivityConfigDownload.this).clear();
                                FileManager.unzip(Constants.TEMP_DIRECTORY + filename, Constants.CONFIG_DIRECTORY_ROOT);
                                ModelManager.getInstance(ActivityConfigDownload.this).remove();
                                ModelManager.getInstance(ActivityConfigDownload.this).set();
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                                Toast.makeText(ActivityConfigDownload.this, "Error!!! File not found...", Toast.LENGTH_LONG).show();
                                showDownloadConfig();
                            }
                        }
                    });
                    download.execute(Constants.CONFIG_DOWNLOAD_LINK + m_Text + ".zip", filename);
                } else
                    showDownloadConfig();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                dialog.cancel();
                finish();
            }
        });
        builder.show();

    }

    public void showDeleteDirectory() {
        Log.d(TAG, "showDeleteDirectory()...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete configuration files?");
        builder.setMessage("Do you want to delete configuration files?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ModelManager.getInstance(ActivityConfigDownload.this).clear();
                ModelManager.getInstance(ActivityConfigDownload.this).remove();
                FileManager.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
                ModelManager.getInstance(ActivityConfigDownload.this).set();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                dialog.cancel();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }
}
