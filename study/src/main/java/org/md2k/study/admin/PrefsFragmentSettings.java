package org.md2k.study.admin;

import android.app.AlertDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.study.R;
import org.md2k.study.ServiceSystemHealth;
import org.md2k.study.Status;
import org.md2k.study.admin.app_install.ActivityInstallApp;
import org.md2k.study.admin.app_reset.ResetInfo;
import org.md2k.study.admin.app_settings.ActivityAppSettings;
import org.md2k.study.user.UserManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.AlertDialogs;
import org.md2k.utilities.datakit.DataKitHandler;

import java.util.Calendar;

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
public class PrefsFragmentSettings extends PreferenceFragment {

    private static final String TAG = PrefsFragmentSettings.class.getSimpleName();
    DataKitHandler dataKitHandler;
    AdminManager adminManager;
    Boolean isDatabaseCleaned=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserManager.getInstance(getActivity()).stopDataCollection();
        Intent intent = new Intent(getActivity().getApplicationContext(), ServiceSystemHealth.class);
        getActivity().stopService(intent);


        dataKitHandler = DataKitHandler.getInstance(getActivity());
        adminManager = AdminManager.getInstance(getActivity());
        addPreferencesFromResource(R.xml.pref_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);
        return v;
    }

    void setupUserID() {
        EditTextPreference editTextPreference = (EditTextPreference) findPreference("key_user_id");
        if (!DataKitHandler.getInstance(getActivity()).isConnected()) {
            editTextPreference.setEnabled(false);
        } else {
            editTextPreference.setEnabled(true);
            if (adminManager.studyInfoManager.getUserIdInDB() == null && adminManager.studyInfoManager.getUserIdNew() == null) {
                editTextPreference.setSummary("");
                editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            } else if (adminManager.studyInfoManager.getUserIdNew() != null) {
                editTextPreference.setSummary(adminManager.studyInfoManager.getUserIdNew());
                editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            } else {
                editTextPreference.setSummary(adminManager.studyInfoManager.getUserIdInDB());
                editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            }
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String userID = ((String) newValue).trim();
                    if (userID.length() != 0) {
                        adminManager.studyInfoManager.setUserIdNew(userID);
                        preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                        preference.setSummary(userID);
                    }
                    return false;
                }
            });
        }
    }

    void setupPreference() {
        setupPreferenceApplication();
        setupPreferenceSettings();
        setupUserID();
        setupWakeupTime();
        setupSleepTime();
        setupReset();
        setBackButton();
        setSaveButton();
        setStatus();

    }

    void setupReset() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_reset");
        preferenceCategory.removeAll();
        for (int i = 0; i < adminManager.resetInfoManager.size(); i++) {
            final ResetInfo resetInfo = adminManager.resetInfoManager.get(i);
            Preference preference = new Preference(getActivity());
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_red_48dp));
            if (resetInfo.isValidFileName()) {
                if (resetInfo.isFileExists())
                    preference.setSummary("File Exists");
                else preference.setSummary("File Not Available");
            }
            preference.setKey(resetInfo.getId());
            preference.setTitle(resetInfo.getName());
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (resetInfo.getClass_name() != null) {
                        if("org.md2k.datakit.ActivityDataKitSettings".equals(resetInfo.getClass_name())) isDatabaseCleaned=true;
                        Intent intent = new Intent();
                        intent.setClassName(resetInfo.getPackage_name(), resetInfo.getClass_name());
                        startActivity(intent);
                    } else if (resetInfo.getFilename() != null && resetInfo.isFileExists()) {
                        AlertDialogs.showAlertDialogConfirm(getActivity(), "Delete File", resetInfo.getName() + " files?", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == AlertDialog.BUTTON_POSITIVE) {
                                    if (resetInfo.deleteFile())
                                        Toast.makeText(getActivity(), "File Deleted", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getActivity(), "Can't delete the file.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    return false;
                }
            });
            preferenceCategory.addPreference(preference);
        }
    }

    void setStatus() {
        Preference preference = findPreference("key_status");
        Status status = adminManager.getStatus();
        if (status.getStatusCode() != Status.SUCCESS) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            preference.setSummary(status.getStatusMessage());
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(status.getStatusMessage());
        }
    }

    private void setBackButton() {
        final Button button = (Button) getActivity().findViewById(R.id.button_2);
        button.setText("Close");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void setSaveButton() {
        final Button button = (Button) getActivity().findViewById(R.id.button_1);
        button.setText("Save");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adminManager.studyInfoManager.writeToDataKit();
                adminManager.sleepInfoManager.writeToDataKit();
                Toast.makeText(getActivity(), "Saved...", Toast.LENGTH_LONG).show();
                setupPreference();
            }
        });
    }

    void setupWakeupTime() {
        Preference preference = findPreference("key_wakeup_time");
        if (!DataKitHandler.getInstance(getActivity()).isConnected()) {
            preference.setEnabled(false);
        } else {
            preference.setEnabled(true);
            if (adminManager.sleepInfoManager.getSleepEndTimeDB() == -1 && adminManager.sleepInfoManager.getSleepEndTimeNew() == -1) {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
                preference.setSummary("");
            } else if (adminManager.sleepInfoManager.getSleepEndTimeNew() != -1) {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                preference.setSummary(formatTime(adminManager.sleepInfoManager.getSleepEndTimeNew()));
            } else {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                preference.setSummary(formatTime(adminManager.sleepInfoManager.getSleepEndTimeDB()));
            }
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimePicker(preference);
                    return false;
                }
            });
        }
    }

    void setupSleepTime() {
        Preference preference = findPreference("key_sleep_time");
        if (!DataKitHandler.getInstance(getActivity()).isConnected()) {
            preference.setEnabled(false);
            return;
        } else
            preference.setEnabled(true);
        if (adminManager.sleepInfoManager.getSleepStartTimeDB() == -1 && adminManager.sleepInfoManager.getSleepStartTimeNew() == -1) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            preference.setSummary("");
        } else if (adminManager.sleepInfoManager.getSleepStartTimeNew() != -1) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(formatTime(adminManager.sleepInfoManager.getSleepStartTimeNew()));
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(formatTime(adminManager.sleepInfoManager.getSleepStartTimeDB()));
        }

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "sleepClick...");
                showTimePicker(preference);
                return false;
            }
        });
    }

    void setupPreferenceSettings() {
        Preference preference = findPreference("key_settings");
        Status status = adminManager.settingsApps.getStatus();
        preference.setSummary(status.getStatusMessage());
        if (status.getStatusCode() == Status.SUCCESS) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
        } else if (status.getStatusCode() == Status.APP_CONFIG_ERROR) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_warning_amber_50dp));
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityAppSettings.class);
                startActivity(intent);
                return false;
            }
        });

    }

    void setupPreferenceApplication() {
        Preference preference = findPreference("key_app");
        Status status = adminManager.installApps.getStatus();
        preference.setSummary(status.getStatusMessage());
        if (status.getStatusCode() == Status.SUCCESS) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
        } else if (status.getStatusCode() == Status.APP_NOT_INSTALLED) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_warning_amber_50dp));
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityInstallApp.class);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        if(isDatabaseCleaned) {
            AdminManager.getInstance(getActivity().getBaseContext()).reset();
            isDatabaseCleaned=false;
        }
        setupPreference();
        if (!dataKitHandler.isConnected()) {
            dataKitHandler.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    setupPreference();
                }
            });
        }
        super.onResume();
    }

    void showTimePicker(final Preference preference) {
        int hour, minute;
        if(preference.getKey().contains("sleep")){
            hour=22;minute=0;
        }else{
            hour=8;minute=0;
        }
//        Calendar currentTime = Calendar.getInstance();
//        int hour = 8;//currentTime.get(Calendar.HOUR_OF_DAY);
//        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (preference.getKey().contains("sleep")) {
                    adminManager.sleepInfoManager.setSleepStartTimeNew(selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000);
                    preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                    preference.setSummary(formatTime(adminManager.sleepInfoManager.getSleepStartTimeNew()));
                } else {
                    adminManager.sleepInfoManager.setSleepEndTimeNew(selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000);
                    preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                    preference.setSummary(formatTime(adminManager.sleepInfoManager.getSleepEndTimeNew()));
                }
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    String formatTime(long timestamp) {
        long hourOfDay, minute;
        timestamp = timestamp / (60 * 1000);
        minute = timestamp % 60;
        timestamp /= 60;
        hourOfDay = timestamp;
        if (hourOfDay > 12)
            return String.format("%02d:%02d pm", hourOfDay - 12, minute);
        else if (hourOfDay == 12)
            return String.format("%02d:%02d pm", 12, minute);
        else {
            if (hourOfDay != 0)
                return String.format("%02d:%02d am", hourOfDay, minute);
            else
                return String.format("%02d:%02d am", 12, minute);
        }
    }
    @Override
    public void onStop(){
        Intent intent = new Intent(getActivity().getApplicationContext(), ServiceSystemHealth.class);
        getActivity().startService(intent);
        super.onStop();
    }
}
