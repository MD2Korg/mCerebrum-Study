package org.md2k.study.model_view.selfreport_eating;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.study.config.Config;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.selfreport.SelfReportManager;
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
    AlertDialog levelDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config config = ModelManager.getInstance(ActivitySelfReportEating.this).getConfigManager().getConfig();
        HashMap<String, String> parameters = config.getAction(ModelFactory.MODEL_EATING_SELF_REPORT).getParameters();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = parameters.get("s1");
        final String message = parameters.get("s2");
        if (parameters.size() > 2) {
            builder.setTitle(message);
            CharSequence[] items = new CharSequence[parameters.size() - 2];
            for (int i = 2; i < parameters.size(); i++) {
                items[i - 2] = (parameters.get("s" + Integer.toString(i + 1)));
            }
            builder.setSingleChoiceItems(items, -1, null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    finish();
                }
            });
            levelDialog = builder.create();
            levelDialog.show();
            levelDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ListView lw = levelDialog.getListView();
                    if(lw.getCheckedItemPosition()<0) return;
                    Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                    levelDialog.dismiss();
                    SelfReportManagerEating selfReportManager = ((SelfReportManagerEating) ModelManager.getInstance(ActivitySelfReportEating.this).getModel(ModelFactory.MODEL_EATING_SELF_REPORT));
                    try {
                        selfReportManager.save(checkedItem.toString());
                    } catch (DataKitException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });
        } else {
            AlertDialogs.showAlertDialogConfirm(ActivitySelfReportEating.this, title, message, "Yes", "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        Toast.makeText(ActivitySelfReportEating.this, "Eating report saved...", Toast.LENGTH_SHORT).show();
                        SelfReportManager selfReportManager = ((SelfReportManager) ModelManager.getInstance(ActivitySelfReportEating.this).getModel(ModelFactory.MODEL_EATING_SELF_REPORT));
                        try {
                            selfReportManager.save(message);
                        } catch (DataKitException e) {
                            e.printStackTrace();
                        }
                    }
                    finish();
                }
            });
        }
    }
}
