package org.md2k.study;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.study.admin.ActivitySettings;
import org.md2k.study.admin.AdminManager;
import org.md2k.study.applications.AppAdapter;
import org.md2k.study.applications.Apps;
import org.md2k.study.interventionapp.ActivityInterventionApp;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;
import org.md2k.utilities.datakit.DataKitHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ActivityMain extends AppCompatActivity {
    public static final String TAG = ActivityMain.class.getSimpleName();
    DataKitHandler dataKitHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyDefaultSettings();
        setContentView(R.layout.activity_main);
        setupButtonFix();
        Intent intent = new Intent(getApplicationContext(), ServiceSystemHealth.class);
        startService(intent);
    }
    void copyDefaultSettings(){
        File directory = new File(Constants.CONFIG_DIRECTORY);
        directory.mkdirs();
        copy(Constants.DEFAULT_CONFIG_PHONESENSOR_FILENAME);
        copy(Constants.DEFAULT_CONFIG_PLOTTER_FILENAME);
    }
    void copy(String filename){
        AssetManager assetManager = getAssets();
        InputStream in;
        OutputStream out;
        String outDir= Constants.CONFIG_DIRECTORY ;
        File outFile = new File(outDir, filename);
        outFile.delete();
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch(IOException e) {
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("system_health"));

        dataKitHandler=DataKitHandler.getInstance(getBaseContext());
        if(!dataKitHandler.isConnected()) {
            dataKitHandler.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    AdminManager.getInstance(getBaseContext()).readFromDB();
                    setupApplications();
                }
            });
        }
        super.onResume();
    }
    void updateSystemHealth(Status status){
        ImageView imageViewOk=(ImageView) findViewById(R.id.imageViewOk);
        ImageView imageViewWarning=(ImageView) findViewById(R.id.imageViewWarning);
        ImageView imageViewError=(ImageView) findViewById(R.id.imageViewError);
        Button buttonFix=(Button)findViewById(R.id.button_fix);
        TextView textViewMessage = (TextView) findViewById(R.id.textView_message);
        switch(status.getStatusCode()) {
            case Status.SUCCESS:
                imageViewOk.setImageResource(R.drawable.ic_ok_teal_50dp);
                imageViewWarning.setImageResource(R.drawable.ic_warning_grey_50dp);
                imageViewError.setImageResource(R.drawable.ic_error_grey_50dp);
                buttonFix.setVisibility(View.INVISIBLE);
                textViewMessage.setText(status.getStatusMessage());
                textViewMessage.setTextColor(ContextCompat.getColor(this,R.color.teal_700));
                break;
            case Status.APP_NOT_INSTALLED:
            case Status.SLEEPSTART_NOT_DEFINED:
            case Status.SLEEPEND_NOT_DEFINED:
            case Status.USERID_NOT_DEFINED:
                imageViewOk.setImageResource(R.drawable.ic_ok_grey_50dp);
                imageViewWarning.setImageResource(R.drawable.ic_warning_grey_50dp);
                imageViewError.setImageResource(R.drawable.ic_error_red_50dp);
                buttonFix.setVisibility(View.VISIBLE);
                buttonFix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPasswordDialog();
                    }
                });
                textViewMessage.setText(status.getStatusMessage());
                textViewMessage.setTextColor(ContextCompat.getColor(this, R.color.red_700));
                break;
            default:
            imageViewOk.setImageResource(R.drawable.ic_ok_grey_50dp);
            imageViewWarning.setImageResource(R.drawable.ic_warning_amber_50dp);
            imageViewError.setImageResource(R.drawable.ic_error_grey_50dp);
            buttonFix.setVisibility(View.VISIBLE);
            break;
        }
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    void setupButtonFix() {
        (findViewById(R.id.button_fix)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivitySystemHealth.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
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
    public void showPasswordDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.this);
        alertDialog.setTitle("PASSWORD - 1234 (will be removed)");
        alertDialog.setMessage("Enter Password (or contact study coordinator)");

        final EditText input = new EditText(ActivityMain.this);
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
                            if (Constants.PASSWORD.equals(password)) {
                                Toast.makeText(getApplicationContext(),
                                        "Password Matched", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
                                startActivity(intent);
//                                startActivityForResult(myIntent1, 0);
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

        alertDialog.show();
    }



    void setupApplications() {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        final Apps apps = new Apps();

        AppAdapter appAdapter = new AppAdapter(ActivityMain.this, apps.getAllItemObject());
        gridview.setAdapter(appAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (apps.getApp(position).getContent().equals("Intervention")) {
                    Intent launchIntent = new Intent(getApplicationContext(), ActivityInterventionApp.class);
                    startActivity(launchIntent);
                } else if (apps.getApp(position).getContent().equals("Privacy Control")) {
                    Intent intent = new Intent();
                    intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityPrivacy");
                    startActivity(intent);
                } else if (apps.getApp(position).getContent().equals("Report")) {

                } else if (apps.getApp(position).getContent().equals("Report")) {
//                    Intent intent = new Intent();
//                    intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityPrivacy");
//                    startActivity(intent);

                }
            }
        });
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Status status= (Status) intent.getSerializableExtra("status");
            Log.d(TAG, "received..."+status.getStatusMessage());
            updateSystemHealth(status);
        }
    };
}
