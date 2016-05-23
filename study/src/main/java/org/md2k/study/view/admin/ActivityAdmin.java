package org.md2k.study.view.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.config_info.ActivityConfigDownload;

public class ActivityAdmin extends AppCompatActivity {
    AlertDialog alertDialog;
    boolean passwordFirst=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart(){
        super.onStart();
        Status status=ModelManager.getInstance(this).getStatus();
        if(status.getStatus()== Status.CONFIG_FILE_NOT_EXIST) {
            passwordFirst=false;
            Intent intent = new Intent(this, ActivityConfigDownload.class);
            intent.putExtra(Status.class.getSimpleName(), status);
            startActivityForResult(intent,1);
        }
        else {
            if (passwordFirst==false && isPasswordRequired()) {
                showPasswordWindow();
            } else {
                setContentView(R.layout.activity_admin);
                getFragmentManager().beginTransaction().replace(R.id.layout_preference_fragment,
                        new PrefsFragmentAdmin()).commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    boolean isPasswordRequired() {
        String password = getPassword();
        if (password == null || password.length() == 0) return false;
        return true;
    }

    String getPassword() {
        return ModelManager.getInstance(this).getConfigManager().getConfig().getAdmin_view().getPassword();
    }

    public void showPasswordWindow() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Admin Access");
        alertDialogBuilder.setMessage("Enter Password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setIcon(R.drawable.ic_id_teal_48dp);

        alertDialogBuilder.setPositiveButton("Go", null);

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog=alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String password = input.getText().toString();
                        if (getPassword().equals(password)) {
                            setContentView(R.layout.activity_admin);
                            passwordFirst=true;
                            getFragmentManager().beginTransaction().replace(R.id.layout_preference_fragment,
                                    new PrefsFragmentAdmin()).commit();
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setDisplayShowTitleEnabled(true);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            }
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Wrong Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialog.show();
        TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
