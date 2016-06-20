package org.md2k.study.model_view.selfreport_eating;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.utilities.UI.AlertDialogs;

import java.util.HashMap;


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
public class ActivitySelfReportEating extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final HashMap<String, String> parameters = ModelManager.getInstance(ActivitySelfReportEating.this).getConfigManager().getConfig().getAction(ModelFactory.MODEL_EATING_SELF_REPORT).getParameters();
        if (parameters.size() == 2) {
            AlertDialogs.AlertDialog(this, parameters.get("s1"), parameters.get("s2"), org.md2k.utilities.R.drawable.ic_eating_teal_48dp, "Ok", "Cancel", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        Toast.makeText(ActivitySelfReportEating.this, "Eating report saved...", Toast.LENGTH_SHORT).show();
                        SelfReportManagerEating selfReportManager = ((SelfReportManagerEating) ModelManager.getInstance(ActivitySelfReportEating.this).getModel(ModelFactory.MODEL_EATING_SELF_REPORT));
                        try {
                            selfReportManager.save(parameters.get("s2"));
                        } catch (DataKitException e) {
                            e.printStackTrace();
                        }
                    }
                    finish();
                }
            });
        } else {
            final String[] items = new String[parameters.size() - 2];
            for (int i = 2; i < parameters.size(); i++) {
                items[i - 2] = (parameters.get("s" + Integer.toString(i + 1)));
            }
            AlertDialogs.AlertDialogSingleChoice(this, parameters.get("s2"), items, 0, "Ok", "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -1) {
                        dialog.dismiss();
                        finish();
                    } else {
                        SelfReportManagerEating selfReportManager = ((SelfReportManagerEating) ModelManager.getInstance(ActivitySelfReportEating.this).getModel(ModelFactory.MODEL_EATING_SELF_REPORT));
                        try {
                            selfReportManager.save(parameters.get("s2") + " (" + items[which] + ")");
                        } catch (DataKitException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                }
            });

        }
    }
}
