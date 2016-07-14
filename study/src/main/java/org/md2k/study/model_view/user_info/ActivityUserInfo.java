package org.md2k.study.model_view.user_info;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.utilities.UI.AlertDialogs;
import org.md2k.utilities.UI.OnClickListener;


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
public class ActivityUserInfo extends AppCompatActivity {
    AlertDialog alertDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserInfoManager userInfoManager= (UserInfoManager) ModelManager.getInstance(this).getModel(ModelFactory.MODEL_USER_INFO);
        if(userInfoManager.isInDatabase){
            Toast.makeText(this,"UserID exists. To change it, clear all data...",Toast.LENGTH_LONG).show();
        }else
            showAlertDialog();
    }

    void showAlertDialog() {
        alertDialogEditText(this, "User ID", "Enter User ID", org.md2k.utilities.R.drawable.ic_user_teal_48dp, "Ok", "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, String result) {
                if(which==DialogInterface.BUTTON_POSITIVE){
                    if (result != null && result.length() != 0) {
                        UserInfoManager userInfoManager = (UserInfoManager) ModelManager.getInstance(ActivityUserInfo.this).getModel(ModelFactory.MODEL_USER_INFO);
                        userInfoManager.setUserId(result);
                        finish();
                    }
                }else{
                    dialog.cancel();
                    finish();
                }
            }
        });
    }
    public void alertDialogEditText(final Context context, String title, String message, int iconId, String positive, String negative, final OnClickListener onClickListener){
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(new ContextThemeWrapper(context, org.md2k.utilities.R.style.app_theme_teal_light_dialog))
                .setTitle(title)
                .setIcon(iconId)
                .setMessage(message);
        final EditText input = new EditText(context);
        input.setSingleLine();
        alertDialogBuilder.setView(input);

        if(positive!=null)
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = input.getText().toString().trim();
                    onClickListener.onClick(dialog,which, str);
                }
            });
        if(negative!=null)
            alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickListener.onClick(dialog,which, null);
                }
            });
        alertDialog=alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
//        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        AlertDialogs.AlertDialogStyle(context, alertDialog);
    }
    @Override
    public void onBackPressed() {
    }
}
