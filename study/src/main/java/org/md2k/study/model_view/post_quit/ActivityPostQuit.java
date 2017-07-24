package org.md2k.study.model_view.post_quit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.utilities.dialog.Dialog;
import org.md2k.utilities.dialog.DialogCallback;


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
public class ActivityPostQuit extends AppCompatActivity {
    long dateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDate();
    }

    private void getDate() {
        Dialog d = new Dialog();
        d.DatePicker(this, "Post Quit Start (Date)", null, null, new DialogCallback() {
            @Override
            public void onDialogCallback(Dialog.DialogResponse which, String[] result) {
                if (which == Dialog.DialogResponse.POSITIVE) {
                    dateTime = Long.parseLong(result[0]);
                    getTime();
                } else {
                    finish();
                }
            }
        }).show();
    }

    private void getTime() {
        Dialog d = new Dialog();
        d.TimePicker(this, "Post Quit Start (Time)", null, null, new DialogCallback() {
            @Override
            public void onDialogCallback(Dialog.DialogResponse which, String[] result) {
                if (which == Dialog.DialogResponse.POSITIVE) {
                    dateTime += Long.parseLong(result[0]);
                    PostQuitManager postQuitManager = (PostQuitManager) ModelManager.getInstance(ActivityPostQuit.this).getModel(ModelFactory.MODEL_POST_QUIT);
                    postQuitManager.setPostQuit(dateTime);
                    finish();
                }
            }
        }).show();
    }
}
