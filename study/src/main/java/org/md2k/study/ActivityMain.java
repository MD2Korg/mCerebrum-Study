package org.md2k.study;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;


import org.md2k.study.model.Model;
import org.md2k.study.view.data_quality.UserViewDataQuality;
import org.md2k.study.view.day_start_end.UserViewDayStartEnd;
import org.md2k.study.view.privacy_control.UserViewPrivacyControl;
import org.md2k.study.view.status.UserViewStatus;
import org.md2k.study.view.study_start_end.UserViewStudyStartEnd;
import org.md2k.study.view.user.AppAdapter;
import org.md2k.study.view.user.UserView;
import org.md2k.study.view.user_app.UserViewUserApp;

import java.util.ArrayList;

public class ActivityMain extends ActivityBase {
    public static final String INTENT_NAME = "UPDATE_VIEW";
    public static final String TYPE = "TYPE";
    public static final String VALUE = "VALUE";
    public static final int STATUS = 0;
    public static final int DATA_QUALITY = 1;
    public static final int PRIVACY=2;
    public static final int DAY_START_END = 3;
    public GridView gridViewApplication;
    public AppAdapter appAdapter;
    Status lastStatus = new Status(Status.SUCCESS);
    ArrayList<Model> userApps;
    Intent intentServiceSystemHealth;
    ArrayList<UserView> userViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        intentServiceSystemHealth = new Intent(getApplicationContext(), ServiceSystemHealth.class);
//        Fabric.with(this, new Crashlytics());
//        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
//                new IntentFilter(INTENT_NAME));
        userViews=new ArrayList<>();
        userViews.add(new UserViewStatus(this));
        userViews.add(new UserViewDataQuality(this));
        userViews.add(new UserViewPrivacyControl(this));
        userViews.add(new UserViewStudyStartEnd(this));
        userViews.add(new UserViewDayStartEnd(this));
        userViews.add(new UserViewUserApp(this));
//        startService(intentServiceSystemHealth);
        LinearLayout linearLayoutMain= (LinearLayout) findViewById(R.id.linear_layout_main);
        linearLayoutMain.removeAllViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        for(int i=0;i<userViews.size();i++)
            userViews.get(i).addView();
        if (modelManager.isValid()) {

//            initializeDayStartEnd();
//            initializeStudyStartEnd();
//            updateUserApp();
        }
    }
/*
    void updateUserApp() {
        userApps = getModels(userManager.getModel());
        gridViewApplication = (GridView) findViewById(R.id.gridview);
        appAdapter = new AppAdapter(ActivityMain.this, getModels(userApps));
        gridViewApplication.setAdapter(appAdapter);
        gridViewApplication.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lastStatus.getStatusCode() != Status.SUCCESS && lastStatus.getStatusCode() != Status.DAY_START_NOT_AVAILABLE && lastStatus.getStatusCode() != Status.DAY_COMPLETED)
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

    public void initializeDayStartEnd() {
        DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) userManager.getModel(ModelManager.MODEL_DAY_START_END);
        if (dayStartEndInfoManager == null) {
            findViewById(R.id.linear_layout_header_day).setVisibility(View.GONE);
            findViewById(R.id.linear_layout_content_day).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linear_layout_header_day).setVisibility(View.VISIBLE);
            findViewById(R.id.linear_layout_content_day).setVisibility(View.VISIBLE);
        }
    }
    public void initializeStudyStartEnd() {
        StudyStartEndInfoManager studyStartEndInfoManager = (StudyStartEndInfoManager) userManager.getModel(ModelManager.MODEL_STUDY_START_END);
        if (studyStartEndInfoManager == null) {
            findViewById(R.id.linear_layout_header_study).setVisibility(View.GONE);
            findViewById(R.id.linear_layout_content_study).setVisibility(View.GONE);
        } else {
            findViewById(R.id.linear_layout_header_study).setVisibility(View.VISIBLE);
            findViewById(R.id.linear_layout_content_study).setVisibility(View.VISIBLE);
        }
    }

    public String getDateTime(long timestamp) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat("hh:mm:ss a").format(date);
    }

    public void updateDayStartEnd(Status status) {
        final DayStartEndInfoManager dayStartEndInfoManager = (DayStartEndInfoManager) userManager.getModel(ModelManager.MODEL_DAY_START_END);
        Button button = (Button) findViewById(R.id.button_day_start_end);
        if (status.getStatusCode() == Status.DAY_ERROR) {
            button.setEnabled(false);
            button.setText("Error");
        } else if (status.getStatusCode() == Status.DAY_START_NOT_AVAILABLE) {
            button.setText("Start Day");
            button.setEnabled(true);
            ((TextView) findViewById(R.id.text_view_day_start)).setText("N/A");
            ((TextView) findViewById(R.id.text_view_day_end)).setText("N/A");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogs.showAlertDialogConfirm(ActivityMain.this, "Start the Day?", "You may receive Survey/Intervention from now on...", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                dayStartEndInfoManager.setDayStartTime(DateTime.getDateTime());
                                dayStartEndInfoManager.saveDayStart();
                                sendMessage();
                                Toast.makeText(ActivityMain.this, "Your day started...thank you", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        } else if (status.getStatusCode() == Status.DAY_COMPLETED) {
            ((TextView) findViewById(R.id.text_view_day_start)).setText(getDateTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView) findViewById(R.id.text_view_day_end)).setText(getDateTime(dayStartEndInfoManager.getDayEndTime()));
            button.setText("Day Ended");
            button.setEnabled(false);
        } else {
            button.setText("End Day");
            button.setEnabled(true);
            ((TextView) findViewById(R.id.text_view_day_start)).setText(getDateTime(dayStartEndInfoManager.getDayStartTime()));
            ((TextView) findViewById(R.id.text_view_day_end)).setText("-");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogs.showAlertDialogConfirm(ActivityMain.this, "End of Day?", "You will not receive any Survey/Intervention today.", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                dayStartEndInfoManager.setDayEndTime(DateTime.getDateTime());
                                dayStartEndInfoManager.saveDayEnd();
                                sendMessage();
                                Toast.makeText(ActivityMain.this, "Your day ended...see you tomorrow", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }
    }
    public void updateStudyStartEnd() {
        final StudyStartEndInfoManager studyStartEndInfoManager = (StudyStartEndInfoManager) userManager.getModel(ModelManager.MODEL_STUDY_START_END);
        if(studyStartEndInfoManager==null) return;
        Button button = (Button) findViewById(R.id.button_study_start_end);

        if (adminManager.getStatus().getStatusCode() != Status.SUCCESS) {
            button.setEnabled(false);
            button.setText("Error");
        } else{
            Status status=studyStartEndInfoManager.getStatus();
            if (status.getStatusCode() == Status.STUDY_START_NOT_AVAILABLE) {
                button.setText("Start");
                button.setEnabled(true);
                ((TextView) findViewById(R.id.text_view_study_start)).setText("N/A");
                ((TextView) findViewById(R.id.text_view_study_end)).setText("N/A");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialogs.showAlertDialogConfirm(ActivityMain.this, "Start the study?", "Start the Study?", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == AlertDialog.BUTTON_POSITIVE) {
                                    studyStartEndInfoManager.setStudyStartTime(DateTime.getDateTime());
                                    studyStartEndInfoManager.saveStudyStart();
                                    updateStudyStartEnd();
                                }
                            }
                        });
                    }
                });
            } else if (status.getStatusMessage().equals("Study is completed")) {
                ((TextView) findViewById(R.id.text_view_study_start)).setText(getDateTime(studyStartEndInfoManager.getStudyStartTime()));
                ((TextView) findViewById(R.id.text_view_study_end)).setText(getDateTime(studyStartEndInfoManager.getStudyEndTime()));
                button.setText("Study Ended");
                button.setEnabled(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialogs.showAlertDialogConfirm(ActivityMain.this, "Start the study?", "Start the Study?", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == AlertDialog.BUTTON_POSITIVE) {
                                    studyStartEndInfoManager.setStudyStartTime(DateTime.getDateTime());
                                    studyStartEndInfoManager.saveStudyStart();
                                    updateStudyStartEnd();
                                }
                            }
                        });
                    }
                });

            } else {
                button.setText("End Study");
                button.setEnabled(true);
                ((TextView) findViewById(R.id.text_view_study_start)).setText(getDateTime(studyStartEndInfoManager.getStudyStartTime()));
                ((TextView) findViewById(R.id.text_view_study_end)).setText("-");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialogs.showAlertDialogConfirm(ActivityMain.this, "End of Study?", "Is this the end of the study?", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == AlertDialog.BUTTON_POSITIVE) {
                                    studyStartEndInfoManager.setStudyEndTime(DateTime.getDateTime());
                                    studyStartEndInfoManager.saveStudyEnd();
                                    updateStudyStartEnd();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    void sendMessage() {
        Intent intent = new Intent(ServiceSystemHealth.INTENT_NAME);
        intent.putExtra(ServiceSystemHealth.TYPE, ServiceSystemHealth.DAY_START_END);
        LocalBroadcastManager.getInstance(ActivityMain.this).sendBroadcast(intent);

    }

    private ArrayList<Model> getModels(ArrayList<Model> all) {
        ArrayList<Model> selected = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getOperation().getId().equals(ModelManager.MODEL_DATA_QUALITY) || all.get(i).getOperation().getId().equals(ModelManager.MODEL_DAY_START_END) || all.get(i).getOperation().getId().equals(ModelManager.MODEL_PRIVACY) || all.get(i).getOperation().getId().equals(ModelManager.MODEL_STUDY_START_END))
                continue;
            selected.add(all.get(i));
        }
        return selected;
    }

    public void updateStatus(Status status) {
        ((TextView) findViewById(R.id.textView_status)).setText(status.getStatusMessage());
        Button button = (Button) findViewById(R.id.button_status);
        if (status.getStatusCode() == Status.SUCCESS || status.getStatusCode() == Status.DAY_COMPLETED) {
            findViewById(R.id.layout_health).setBackground(ContextCompat.getDrawable(this, R.color.teal_50));
            ((TextView) findViewById(R.id.textView_status)).setTextColor(ContextCompat.getColor(this, R.color.teal_700));
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_teal));
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok_teal_50dp, 0);
            button.setText("OK");
            button.setEnabled(false);
            button.setOnClickListener(null);
//            button.setVisibility(View.INVISIBLE);

        } else {
            findViewById(R.id.layout_health).setBackground(ContextCompat.getDrawable(this, R.color.red_200));
            ((TextView) findViewById(R.id.textView_status)).setTextColor(ContextCompat.getColor(this, R.color.red_900));
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_red));
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_grey_50dp, 0);
            button.setText("FIX");
            if (status.getStatusCode() == Status.DAY_START_NOT_AVAILABLE) {
                button.setEnabled(false);

            } else {
                button.setEnabled(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPasswordDialog();
                    }
                });
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStudyStartEnd();
            switch (intent.getIntExtra(TYPE, -1)) {
                case DATA_QUALITY:
                    //updateDataQuality((Status[]) intent.getParcelableArrayExtra(VALUE));
                    break;
                case STATUS:
                    Status curStatus = intent.getParcelableExtra(VALUE);
                    if (lastStatus == null || curStatus.getStatusCode() != lastStatus.getStatusCode() || !curStatus.getStatusMessage().equals(lastStatus.getStatusMessage())) {
                        lastStatus = curStatus;
                        updateStatus(lastStatus);
                        updateUserApp();
                    }
                    break;
                case DAY_START_END:
                    updateDayStartEnd(intent.<Status>getParcelableExtra(VALUE));
                    break;
                case PRIVACY:
                    updatePrivacyUI();
            }
        }
    };
    @Override
    public void onResume(){
        updateStudyStartEnd();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        stopService(intentServiceSystemHealth);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    }
*/
}
