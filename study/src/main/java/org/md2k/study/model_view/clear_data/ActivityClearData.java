package org.md2k.study.model_view.clear_data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import org.md2k.study.R;
import org.md2k.study.controller.ModelManager;


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
public class ActivityClearData extends Activity {
    private static final String TAG = ActivityClearData.class.getSimpleName();
    private ProgressDialog ringProgressDialog=null;
    private Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelManager.getInstance(this).clear();
        setContentView(R.layout.activity_clear_data);
        handler=new Handler();

        Button button= (Button) findViewById(R.id.button_1);
        button.setText(R.string.button_close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelManager.getInstance(ActivityClearData.this).set();
                ringProgressDialog = ProgressDialog.show(ActivityClearData.this, "Please wait ...", "System is resetting ...", true);
                ringProgressDialog.setCancelable(false);
                handler.postDelayed(runnable, 3000);
            }
        });
        Intent intent = new Intent();
        intent.putExtra("delete", true);
/*
        intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivitySettingsArchive");
        startActivity(intent);
        intent.putExtra("delete", true);
        intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivitySettingsDatabase");
        startActivity(intent);
        */
        intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivitySettings");
        startActivity(intent);
        intent.setClassName("org.md2k.streamprocessor", "org.md2k.streamprocessor.ActivitySettings");
        startActivity(intent);
    }
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(ringProgressDialog!=null && ringProgressDialog.isShowing()) ringProgressDialog.dismiss();
            finish();
        }
    };
    @Override
    public void onBackPressed(){

    }
}
