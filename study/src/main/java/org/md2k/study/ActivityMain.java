package org.md2k.study;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.Model;
import org.md2k.study.model.app_service.AppServiceManager;
import org.md2k.study.model.day_start_end.DayStartEndInfoManager;
import org.md2k.study.model.study_info.StudyInfoManager;
import org.md2k.study.view.user.AppAdapter;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.AlertDialogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityMain extends ActivityDataQuality {
    public static final String TAG = ActivityMain.class.getSimpleName();
    public static final String INTENT_NAME = "UPDATE_VIEW";
    public static final String TYPE = "TYPE";
    public static final String VALUE = "VALUE";
    public static final int STATUS = 0;
    public static final int DATA_QUALITY = 1;
    public static final int PRIVACY = 2;
    public static final int DAY_START_END=3;
    public GridView gridViewApplication;
    public AppAdapter appAdapter;
    Status lastStatus=new Status(Status.SUCCESS);
    ArrayList<Model> userApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isError) finish();
        else {
            setTitle(((StudyInfoManager) ModelManager.getInstance(this).getModel(ModelManager.MODEL_STUDY_INFO)).getStudy_name());
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                    new IntentFilter(INTENT_NAME));
            initializeDayStartEnd();
            initializeUserApp();
        }
    }
    void initializeUserApp(){
        userApps=getModels(userManager.getModels());
        gridViewApplication = (GridView) findViewById(R.id.gridview);
        appAdapter = new AppAdapter(ActivityMain.this, getModels(userApps));
        gridViewApplication.setAdapter(appAdapter);
        gridViewApplication.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lastStatus.getStatusCode() != Status.SUCCESS)
                    Toast.makeText(ActivityMain.this, "Please configure the study first...", Toast.LENGTH_SHORT).show();
                else {
                    String packageName = userApps.get(position).getOperation().getPackage_name();

                    String className = userApps.get(position).getOperation().getClass_name();
                    if (packageName != null && className != null) {
                        Intent intent = new Intent();
                        intent.setClassName(packageName, className);
                        startActivity(intent);
                    } else if (packageName != null) {
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                        startActivity(LaunchIntent);
                    } else if (className != null) {
                        try {
                            Class<?> c = Class.forName(className);
                            Intent intent = new Intent(ActivityMain.this, c);
                            startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    public void initializeDayStartEnd(){
        DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) userManager.getModels(ModelManager.MODEL_DAY_START_END);
        if (dayStartEndInfoManager == null) {
            findViewById(R.id.linear_layout_header_day).setVisibility(View.GONE);
            findViewById(R.id.linear_layout_content_day).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linear_layout_header_day).setVisibility(View.VISIBLE);
            findViewById(R.id.linear_layout_content_day).setVisibility(View.VISIBLE);
        }
    }
    public String getDateTime(long timestamp){
        Date date = new Date (timestamp);
        return new SimpleDateFormat("hh-mm-ss a").format(date);
    }
    public void updateDayStartEnd(Status status){
        final DayStartEndInfoManager dayStartEndInfoManager= (DayStartEndInfoManager) userManager.getModels(ModelManager.MODEL_DAY_START_END);
        Button button= (Button) findViewById(R.id.button_day_start_end);
        if(status.getStatusCode()==Status.DAY_ERROR){
            button.setEnabled(false);
            button.setText("System Error");
        }
        else if(status.getStatusCode()==Status.DAY_START_NOT_AVAILABLE){
            button.setText("Start Day");
            button.setEnabled(true);
            ((TextView)findViewById(R.id.text_view_day_start)).setText("x");
            ((TextView)findViewById(R.id.text_view_day_end)).setText("x");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogs.showAlertDialogConfirm(ActivityMain.this, "Start the Day?", "You may receive Survey/Intervention from now on...", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                dayStartEndInfoManager.setDayStartTime(DateTime.getDateTime());
                                dayStartEndInfoManager.saveDayStart();
                                startService(intentServiceSystemHealth);
                                Toast.makeText(ActivityMain.this, "Your day started...thank you", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }else if(status.getStatusCode()==Status.DAY_COMPLETED){
            ((TextView)findViewById(R.id.text_view_day_start)).setText(getDateTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView)findViewById(R.id.text_view_day_end)).setText(getDateTime(dayStartEndInfoManager.getDayEndTime()));
            button.setText("Day Ended");
            button.setEnabled(false);
        }else {
            button.setText("End Day");
            button.setEnabled(true);
            ((TextView)findViewById(R.id.text_view_day_start)).setText(getDateTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView)findViewById(R.id.text_view_day_end)).setText("-");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogs.showAlertDialogConfirm(ActivityMain.this, "End of Day?", "You will not receive any Survey/Intervention today.", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                dayStartEndInfoManager.setDayEndTime(DateTime.getDateTime());
                                dayStartEndInfoManager.saveDayEnd();
                                startService(intentServiceSystemHealth);
                                Toast.makeText(ActivityMain.this, "Your day ended...see you tomorrow", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }
    }

    private ArrayList<Model> getModels(ArrayList<Model> all) {
        ArrayList<Model> selected = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getOperation().getId().equals(ModelManager.MODEL_DATA_QUALITY) || all.get(i).getOperation().getId().equals(ModelManager.MODEL_DAY_START_END))
                continue;
            selected.add(all.get(i));
        }
        return selected;
    }

    public void updateStatus(Status status) {
        ((TextView) findViewById(R.id.textView_status)).setText(status.getStatusMessage());
        Button button = (Button) findViewById(R.id.button_status);
        if (status.getStatusCode() == Status.SUCCESS) {
            findViewById(R.id.layout_health).setBackground(ContextCompat.getDrawable(this, R.color.teal_50));
            ((TextView) findViewById(R.id.textView_status)).setTextColor(ContextCompat.getColor(this, R.color.teal_700));
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_teal));
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok_teal_50dp, 0);
            button.setText("OK");
            button.setOnClickListener(null);

        } else {
            findViewById(R.id.layout_health).setBackground(ContextCompat.getDrawable(this, R.color.red_200));
            ((TextView) findViewById(R.id.textView_status)).setTextColor(ContextCompat.getColor(this, R.color.red_900));
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_red));
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_grey_50dp, 0);
            button.setText("FIX");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPasswordDialog();
                }
            });
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(TYPE, -1)) {
                case DATA_QUALITY:
                    updateDataQuality((Status[]) intent.getParcelableArrayExtra(VALUE));
                    break;
                case PRIVACY:
                    initializeUserApp();
                    break;
                case STATUS:
                    lastStatus=intent.getParcelableExtra(VALUE);
                    updateStatus(lastStatus);
                    initializeUserApp();
                    break;
                case DAY_START_END:
                    updateDayStartEnd(intent.<Status>getParcelableExtra(VALUE));
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        if (!isError)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
