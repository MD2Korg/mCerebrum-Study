package org.md2k.study.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import org.md2k.study.Constants;
import org.md2k.study.Status;
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
public class Download extends AsyncTask<String, Integer,Status> {
    private static final String TAG = Download.class.getSimpleName();
    ProgressDialog mProgressDialog;
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    OnCompletionListenter onCompletionListenter;

    public Download(Context context, OnCompletionListenter onCompletionListenter) {
        this.context = context;
        this.onCompletionListenter=onCompletionListenter;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Download");
        mProgressDialog.setMessage("Download in progress...");
//        mProgressDialog.setIndeterminate(true);
       mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);
//        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        mProgressDialog.show();
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG,"onCanceled");
    }
    @Override
    protected org.md2k.study.Status doInBackground(String... str) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(str[0]);
            Log.d(TAG,"URL="+str[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return new org.md2k.study.Status(org.md2k.study.Status.CONNECTION_ERROR);
            }

            int fileLength = connection.getContentLength();
//            if(fileLength!=0) mProgressDialog.setIndeterminate(false);
            Log.d(TAG,"filelength="+fileLength);
            Log.d(TAG,"filename="+str[1]);
            input = connection.getInputStream();
            Log.d(TAG, "Directory=" + Constants.TEMP_DIRECTORY);
            File dir=new File(Constants.TEMP_DIRECTORY);
            if(!dir.exists()) dir.mkdirs();
            output = new FileOutputStream(Constants.TEMP_DIRECTORY+str[1]);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    Log.d(TAG,"isCanceled...");
                    input.close();
                    output.close();
                    connection.disconnect();

//                    publishProgress(-1);
                    return new org.md2k.study.Status(org.md2k.study.Status.DOWNLOAD_ERROR);
                }
                total += count;
                if (fileLength > 0) // only if total length is known
                        mProgressDialog.setProgress((int) (total * 100.0 / fileLength));
//                    publishProgress((int) (total * 100.0 / fileLength));
                Log.d(TAG,"Total="+total+" progress="+(int) (total * 100.0 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return new org.md2k.study.Status(org.md2k.study.Status.SUCCESS);
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                return new org.md2k.study.Status(org.md2k.study.Status.DOWNLOAD_ERROR);

            }

            if (connection != null)
                connection.disconnect();
        }
        return new org.md2k.study.Status(org.md2k.study.Status.SUCCESS);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
//        mProgressDialog = ProgressDialog.show(context,
//                "Download", " Downloading in progress..");
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
/*        if(progress[0]==-1){
            mProgressDialog.cancel();
            super.onProgressUpdate(progress);
        }else {
            super.onProgressUpdate(progress);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }
*/    }

    @Override
    protected void onPostExecute(org.md2k.study.Status status) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        onCompletionListenter.OnCompleted(status);
    }
}
