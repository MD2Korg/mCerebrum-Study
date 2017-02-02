package org.md2k.study.model_view.config_info;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.WindowManager;
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
import org.md2k.utilities.UI.AlertDialogs;
import org.md2k.utilities.UI.OnClickListener;
import org.md2k.utilities.sharedpreference.SharedPreference;


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
    private AlertDialog alertDialog;

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

    private void showDownloadConfig() {
        Log.d(TAG, "showDownloadConfig()...");
        String filename= SharedPreference.readString(this, Constants.CONFIG_ZIP_FILENAME, null);
        if(filename==null) filename="";
        alertDialogEditText(this, "Download Configuration File", "Please enter the file name (example: default)", filename, R.drawable.ic_download_teal_48dp, "Ok", "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, String result) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    final String filename = result + ".zip";
                    if (result.length() > 0) {
                        Download download = new Download(ActivityConfigDownload.this, true, new OnCompletionListener() {
                            @Override
                            public void OnCompleted(int status) {
                                if (status == Download.SUCCESS) {
                                    ModelManager.getInstance(ActivityConfigDownload.this).clear();
                                    FileManager.unzip(Constants.TEMP_DIRECTORY + filename, Constants.CONFIG_DIRECTORY_ROOT);
                                    ModelManager.getInstance(ActivityConfigDownload.this).read();
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
                        try {
                            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                            int lastDot = version.lastIndexOf('.');
                            String configVersion = version.substring(0, lastDot);
                            download.execute(Constants.CONFIG_DOWNLOAD_LINK + configVersion + "/" + filename, filename);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else
                        showDownloadConfig();
                } else {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    dialog.cancel();
                    finish();
                }
            }
        });
    }

    private void alertDialogEditText(final Context context, String title, String message, String filename, int iconId, String positive, String negative, final OnClickListener onClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, org.md2k.utilities.R.style.app_theme_teal_light_dialog))
                .setTitle(title)
                .setIcon(iconId)
                .setMessage(message);
        final EditText input = new EditText(context);
        input.setSingleLine();
        input.setText(filename);
        alertDialogBuilder.setView(input);

        if (positive != null)
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = input.getText().toString().trim();
                    onClickListener.onClick(dialog, which, str);
                }
            });
        if (negative != null)
            alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickListener.onClick(dialog, which, null);
                }
            });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        AlertDialogs.AlertDialogStyle(context, alertDialog);
    }

    @Override
    public void onBackPressed() {
    }

    private void showDeleteDirectory() {
        Log.d(TAG, "showDeleteDirectory()...");
        AlertDialogs.AlertDialog(this, "Delete configuration files?", "Do you want to delete configuration files?", R.drawable.ic_delete_red_48dp, "Yes", "Cancel", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ModelManager.getInstance(ActivityConfigDownload.this).clear();
                    FileManager.deleteDirectory(Constants.CONFIG_DIRECTORY_BASE);
                    ModelManager.getInstance(ActivityConfigDownload.this).read();
                    ModelManager.getInstance(ActivityConfigDownload.this).set();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    dialog.cancel();
                    finish();

                } else {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    dialog.cancel();
                    finish();
                }

            }
        });
    }
}
