package org.md2k.study;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.study.config.ConfigManager;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.controller.UserManager;
import org.md2k.study.model.study_info.StudyInfoManager;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.study.view.config_download.ActivityConfigDownload;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;

public class ActivityBase extends AppCompatActivity {
    ModelManager modelManager;
    UserManager userManager;
    UserManager adminManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modelManager = ModelManager.getInstance(getApplicationContext());
        userManager=modelManager.getUserManager();
        adminManager=modelManager.getAdminManager();
        if (modelManager.isValid()) {
            modelManager.set();
            modelManager.start();
        }
    }
    @Override
    public void onStart() {
        if (modelManager.isValid()) {
            modelManager.update();
            setTitle(((StudyInfoManager) ModelManager.getInstance(this).getModel(ModelManager.MODEL_STUDY_INFO)).getStudy_name());
        }
        else {
            Toast.makeText(this, "ERROR: Incorrect configuration file...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ActivityConfigDownload.class);
            startActivity(intent);
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_about:
                intent = new Intent(this, ActivityAbout.class);
                try {
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_CODE, String.valueOf(this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_NAME, this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                break;
            case R.id.action_copyright:
                intent = new Intent(this, ActivityCopyright.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                showPasswordDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showPasswordDialog() {
        if(adminManager.getUser().getPassword()!=null && adminManager.getUser().getPassword().length()>0) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityBase.this);
            alertDialog.setTitle("PASSWORD (Admin Access)");
            alertDialog.setMessage("Enter Password");
            final EditText input = new EditText(ActivityBase.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.ic_id_teal_48dp);

            alertDialog.setPositiveButton("Go",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            if (adminManager.getUser().getPassword().equals(password)) {
                                Toast.makeText(getApplicationContext(),
                                        "Password Matched", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityBase.this, ActivityAdmin.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
//        alertDialog.show();
            AlertDialog dialog = alertDialog.show();
            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);
            dialog.show();
        }else{
            Intent intent = new Intent(ActivityBase.this, ActivityAdmin.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
