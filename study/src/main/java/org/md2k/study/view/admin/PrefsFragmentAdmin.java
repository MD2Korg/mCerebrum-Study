package org.md2k.study.view.admin;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.operation.OperationManager;
import org.md2k.study.view.app_install.ActivityInstallApp;
import org.md2k.study.view.app_settings.ActivityAppSettings;
import org.md2k.utilities.Apps;
import org.md2k.utilities.UI.AlertDialogs;

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
public class PrefsFragmentAdmin extends PreferenceFragment {

    private static final String TAG = PrefsFragmentAdmin.class.getSimpleName();
    OperationManager operationManager;
    boolean isRefresh = false;
    DataKitAPI dataKitAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        operationManager = OperationManager.getInstance(getActivity());
        dataKitAPI = DataKitAPI.getInstance(getActivity());
        operationManager.appsService.stop();
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

    @Override
    public void onResume() {
        if (isRefresh) {
            operationManager.sleepInfoManager.reset(getActivity());
            operationManager.userInfoManager.reset(getActivity());
            operationManager.studyInfoManager.reset(getActivity());
            isRefresh = false;
        }
        setupPreference();
        super.onResume();
    }

    void setupPreference() {
        setupConfiguration();
        setupStudyInfo();
        setupAppInstall();
        setupAppSettings();
        setupUserID();
        setupWakeupTime();
        setupSleepTime();
        setupClearData();
        setupClearConfig();
        setupCloseButton();
        setSaveButton();
    }

    void setupStudyInfo() {
        Preference preference = findPreference("study_info");
        Status status = operationManager.studyInfoManager.getStatus();
        setupIcon(preference, status);
        preference.setTitle(operationManager.studyInfoManager.getStudy_name());
        setupIcon(preference, status);
    }

    void setupConfiguration() {
        Preference preference = findPreference("config_info");
        Status status = operationManager.appConfigManager.getStatus();
        setupIcon(preference, status);
        preference.setSummary("" + operationManager.appConfigManager.getVersion() + "  (" + status.getStatusMessage() + ")");
    }

    void setupIcon(Preference preference, Status status) {
        preference.setSummary(status.getStatusMessage());
        if (status.getStatusCode() == Status.SUCCESS)
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
        else
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
    }

    void setupUserID() {
        final EditTextPreference editTextPreference = (EditTextPreference) findPreference("user_id");
        Status status = operationManager.userInfoManager.getStatus();
        if (status.getStatusCode() == Status.DATAKIT_NOT_AVAILABLE) {
            editTextPreference.setSummary(status.getStatusMessage());
            editTextPreference.setEnabled(false);
            editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else if (status.getStatusCode() == Status.SUCCESS) {
            editTextPreference.setSummary(operationManager.userInfoManager.getUserId() + " (To change, clear data first)");
            editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            editTextPreference.setEnabled(false);
        } else {
            editTextPreference.setEnabled(true);
            if (operationManager.userInfoManager.getUserId() == null) {
                editTextPreference.setSummary("");
                editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            } else {
                editTextPreference.setSummary(operationManager.userInfoManager.getUserId());
                editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            }
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String userID = ((String) newValue).trim();
                    if (userID.length() != 0) {
                        operationManager.userInfoManager.setUserId(userID);
                        preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                        preference.setSummary(userID);
                    }
                    return false;
                }
            });
        }
    }

    void setupClearConfig() {
        Preference preference = findPreference("clear_config");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialogs.showAlertDialogConfirm(getActivity(), "Delete Configuration Files", "Delete All Configuration File? These can't be recoverd after delete.", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            operationManager.appsClear.delete();
                            isRefresh=true;
                            Toast.makeText(getActivity(), "File Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
            }
        });
    }

    void setupClearData() {
        Preference preference = findPreference("clear_data");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!Apps.isPackageInstalled(getActivity(), "org.md2k.datakit")) {
                    Toast.makeText(getActivity(), "ERROR: Please install \"DataKit\" app first...", Toast.LENGTH_SHORT).show();
                    return false;
                }
                isRefresh = true;
                Intent intent = new Intent();
                intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityDataKitSettings");
                startActivity(intent);
                return false;
            }
        });
    }

    private void setupCloseButton() {
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
                operationManager.userInfoManager.writeToDataKit();
                operationManager.sleepInfoManager.writeToDataKit();
                Toast.makeText(getActivity(), "Saved...", Toast.LENGTH_LONG).show();
                isRefresh=true;
            }
        });
    }

    void setupWakeupTime() {
        Preference preference = findPreference("wakeup_time");
        Status status = operationManager.sleepInfoManager.getStatus();
        if (status.getStatusCode() == Status.DATAKIT_NOT_AVAILABLE) {
            preference.setSummary(status.getStatusMessage());
            preference.setEnabled(false);
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else {
            preference.setEnabled(true);
            if (operationManager.sleepInfoManager.getSleepEndTimeDB() == -1 && operationManager.sleepInfoManager.getSleepEndTimeNew() == -1) {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
                preference.setSummary("");
            } else if (operationManager.sleepInfoManager.getSleepEndTimeNew() != -1) {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                preference.setSummary(formatTime(operationManager.sleepInfoManager.getSleepEndTimeNew()));
            } else {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                preference.setSummary(formatTime(operationManager.sleepInfoManager.getSleepEndTimeDB()));
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
        Preference preference = findPreference("sleep_time");
        Status status = operationManager.sleepInfoManager.getStatus();
        if (status.getStatusCode() == Status.DATAKIT_NOT_AVAILABLE) {
            preference.setSummary(status.getStatusMessage());
            preference.setEnabled(false);
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else
            preference.setEnabled(true);
        if (operationManager.sleepInfoManager.getSleepStartTimeDB() == -1 && operationManager.sleepInfoManager.getSleepStartTimeNew() == -1) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            preference.setSummary("");
        } else if (operationManager.sleepInfoManager.getSleepStartTimeNew() != -1) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(formatTime(operationManager.sleepInfoManager.getSleepStartTimeNew()));
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(formatTime(operationManager.sleepInfoManager.getSleepStartTimeDB()));
        }

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimePicker(preference);
                return false;
            }
        });
    }

    void setupAppSettings() {
        Preference preference = findPreference("app_settings");
        Status status = operationManager.appsSettings.getStatus();
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

    void setupAppInstall() {
        Preference preference = findPreference("app_install");
        Status status = operationManager.appsInstall.getStatus();
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

    void showTimePicker(final Preference preference) {
        int hour, minute;
        if (preference.getKey().contains("sleep")) {
            hour = 22;
            minute = 0;
        } else {
            hour = 8;
            minute = 0;
        }
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (preference.getKey().contains("sleep")) {
                    operationManager.sleepInfoManager.setSleepStartTimeNew(selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000);
                    preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                    preference.setSummary(formatTime(operationManager.sleepInfoManager.getSleepStartTimeNew()));
                } else {
                    operationManager.sleepInfoManager.setSleepEndTimeNew(selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000);
                    preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                    preference.setSummary(formatTime(operationManager.sleepInfoManager.getSleepEndTimeNew()));
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
        //TODO: service start??
//        Intent intent = new Intent(getActivity().getApplicationContext(), ServiceSystemHealth.class);
//        getActivity().startService(intent);
        super.onStop();
    }
    @Override
    public void onDestroy(){
        if(operationManager.isStudySetupValid())
            operationManager.appsService.start();
        super.onDestroy();
    }

}
