package org.md2k.study.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.view.ContextThemeWrapper;

import org.md2k.study.Constants;
import org.md2k.study.R;
import org.md2k.utilities.Report.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
public class Download extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = Download.class.getSimpleName();
    public static final int SUCCESS = 0;
    ProgressDialog mProgressDialog;
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    OnCompletionListener onCompletionListener;
    boolean isProgressShow;

    public Download(Context context, boolean isProgressShow, OnCompletionListener onCompletionListener) {
        this.context = context;
        this.isProgressShow = isProgressShow;

        this.onCompletionListener = onCompletionListener;
        try {
            if (isProgressShow) {
                mProgressDialog = new ProgressDialog(new ContextThemeWrapper(context, R.style.app_theme_teal_light_dialog));
                mProgressDialog.setTitle("Download");
                mProgressDialog.setMessage("Download in progress...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setProgress(0);
                mProgressDialog.setMax(100);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel(true);
                    }
                });
                mProgressDialog.show();
            }
        }catch(Exception e){
            mProgressDialog=null;
        }
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG,"onCancelled()...");
        mWakeLock.release();
        if(isProgressShow && mProgressDialog!=null)
            mProgressDialog.dismiss();
        onCompletionListener.OnCompleted(org.md2k.study.Status.DOWNLOAD_ERROR);
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(String... str) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        Integer status=SUCCESS;
        try {
            URL url = new URL(str[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                status= org.md2k.study.Status.CONNECTION_ERROR;
                connection=null;
                return status;
            }

            int fileLength = connection.getContentLength();
            Log.d(TAG,"fileLength="+fileLength);
//            if(fileLength!=0) mProgressDialog.setIndeterminate(false);
            input = connection.getInputStream();
            File dir = new File(Constants.TEMP_DIRECTORY);
            if (!dir.exists()) dir.mkdirs();
            output = new FileOutputStream(Constants.TEMP_DIRECTORY + str[1]);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                Log.d(TAG,"count="+count);
                if (isCancelled()) {
                    if(input!=null)
                        input.close();
                    input=null;
                    if(output!=null)
                        output.close();
                    output=null;
                    if(connection!=null)
                        connection.disconnect();
                    connection=null;
                    status=org.md2k.study.Status.DOWNLOAD_ERROR;
                    return status;
                }
                total += count;
                if (isProgressShow && mProgressDialog!=null) {
                    if (fileLength > 0)
                        mProgressDialog.setProgress((int) (total * 100.0 / fileLength));
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            status= org.md2k.study.Status.CONNECTION_ERROR;
            return status;
        } finally {
            try {
                if (output != null)
                    output.close();
                output=null;
                if (input != null)
                    input.close();
                input=null;
            } catch (IOException ignored) {
                status= org.md2k.study.Status.CONNECTION_ERROR;
                return status;
            }

            if (connection != null)
                connection.disconnect();
            connection=null;
        }
        return status;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected void onPostExecute(Integer status) {
        mWakeLock.release();
        if(isProgressShow && mProgressDialog!=null)
            mProgressDialog.dismiss();
        onCompletionListener.OnCompleted(status);
    }
}
