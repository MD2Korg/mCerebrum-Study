package org.md2k.study.operation.app_install;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;

import org.md2k.utilities.Report.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
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
public class DownloadVersion extends AsyncTask<String, Integer, String> {
    private static final String TAG = DownloadVersion.class.getSimpleName();
    private OnTaskCompleted onTaskCompleted;
    private Context context;
    ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;

    public DownloadVersion(Context context,OnTaskCompleted onTaskCompleted) {
        Log.d(TAG,"DownloadVersion...");
        this.context=context;
        this.onTaskCompleted=onTaskCompleted;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog = ProgressDialog.show(context,
                "Download", " Downloading in progress..");
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... sUrl) {
        String versionName=null;
        try {
            URL url = new URL(sUrl[0]);
            Log.d(TAG,"dobackground()...url="+url);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            Log.d(TAG,"dobackground()...in");

            String str,str1="";
            while ((str = in.readLine()) != null) {
                str1+=str;
            }
            Log.d(TAG,"dobackground()...str1");

            if(str1.contains("<title>") && str1.contains("</title>")){
                int start_id=str1.indexOf("<title>")+7;
                int end_id=str1.indexOf("</title>");

                str=str1.substring(start_id,end_id);
                String[] s=str.split(" ");
                if(s.length>=2)
                    versionName=s[1];
            }
            in.close();
        } catch (Exception e) {
            Log.e(TAG,"error="+e.toString());
        }
        Log.d(TAG, "version=" + versionName);
        return versionName;
    }
    @Override
    protected void onPostExecute(String versionNumber) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        onTaskCompleted.onTaskCompleted(versionNumber);
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }
}
