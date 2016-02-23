package org.md2k.study.view.admin;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.config.Operation;
import org.md2k.study.controller.AdminManager;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.Model;
import org.md2k.study.model.app_service.AppServiceManager;
import org.md2k.study.model.clear_config.ClearConfigManager;
import org.md2k.study.model.config_info.ConfigInfoManager;
import org.md2k.study.model.sleep_info.SleepInfoManager;
import org.md2k.study.model.study_info.StudyInfoManager;
import org.md2k.study.model.user_info.UserInfoManager;
import org.md2k.study.model.wakeup_info.WakeupInfoManager;
import org.md2k.study.system_health.ServiceSystemHealth;
import org.md2k.study.view.app_install.ActivityInstallApp;
import org.md2k.study.view.app_service.ActivityService;
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
    AdminManager adminManager;
    boolean isRefresh = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminManager = AdminManager.getInstance(getActivity());
        addPreferencesFromResource(R.xml.pref_settings);
        createPreference();
    }

    void createPreference(){
        ((PreferenceCategory) findPreference("info")).removeAll();
        ((PreferenceCategory) findPreference("setup")).removeAll();
        ((PreferenceCategory) findPreference("status")).removeAll();
        ((PreferenceCategory) findPreference("reset")).removeAll();
        for(int i=0;i<adminManager.getModels().size();i++){
            Operation operation=adminManager.getModels().get(i).getOperation();
            if(operation.getId().equals(ModelManager.MODEL_USER_INFO)){
                EditTextPreference preference = new EditTextPreference(getActivity());
                preference.setTitle(operation.getName());
                preference.setKey(operation.getId());
                ((PreferenceCategory) findPreference(operation.getType())).addPreference(preference);

            }else {
                Preference preference = new Preference(getActivity());
                preference.setTitle(operation.getName());
                preference.setKey(operation.getId());
                ((PreferenceCategory) findPreference(operation.getType())).addPreference(preference);
            }
        }
        setupCloseButton();
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
            adminManager.reset();
            isRefresh = false;
        }
        updatePreference();
        super.onResume();
    }
    public void updatePreference(){
        for(int i=0;i<adminManager.getModels().size();i++){
            switch(adminManager.getModels().get(i).getOperation().getId()){
                case ModelManager.MODEL_APP_INSTALL:
                    setupAppInstall(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_APP_SETTINGS:
                    setupAppSettings(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_CONFIG_INFO:
                    setupConfigInfo(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_STUDY_INFO:
                    setupStudyInfo(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_USER_INFO:
                    setupUserInfo(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_SLEEP_INFO:
                    setupSleepInfo(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_WAKEUP_INFO:
                    setupWakeupInfo(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_CLEAR_CONFIG:
                    setupClearConfig(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_CLEAR_DATABASE:
                    setupClearData(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_APP_SERVICE:
                    setupAppService(adminManager.getModels().get(i));
                    break;
                case ModelManager.MODEL_NOTIFICATION_TEST:
                    setupNotificationTest(adminManager.getModels().get(i));

            }
        }
        setSaveButton();
    }

    void setupStudyInfo(Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        Status status = model.getStatus();
        setupIcon(preference, status);
        preference.setTitle(((StudyInfoManager) model).getStudy_name());
        setupIcon(preference, status);
    }

    void setupConfigInfo(Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        Status status = model.getStatus();
        setupIcon(preference, status);
        preference.setSummary("" + ((ConfigInfoManager) model).getVersion() + "  (" + status.getStatusMessage() + ")");
    }

    void setupIcon(Preference preference, Status status) {
        preference.setSummary(status.getStatusMessage());
        if (status.getStatusCode() == Status.SUCCESS)
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
        else
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
    }

    void setupUserInfo(Model model) {
        final UserInfoManager userInfoManager = (UserInfoManager) model;
        EditTextPreference editTextPreference = (EditTextPreference) findPreference(model.getOperation().getId());
        editTextPreference.setDialogTitle(model.getOperation().getName());
        Status status = model.getStatus();
        if (status.getStatusCode() == Status.DATAKIT_NOT_AVAILABLE) {
            editTextPreference.setSummary(status.getStatusMessage());
            editTextPreference.setEnabled(false);
            editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else if (status.getStatusCode() == Status.SUCCESS) {
            editTextPreference.setSummary(userInfoManager.getUserId() + " (To change, clear data first)");
            editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            editTextPreference.setEnabled(false);
        } else {
            editTextPreference.setEnabled(true);
            if (userInfoManager.getUserId() == null) {
                editTextPreference.setSummary("");
                editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            } else {
                editTextPreference.setSummary(userInfoManager.getUserId());
                editTextPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            }
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String userID = ((String) newValue).trim();
                    if (userID.length() != 0) {
                        userInfoManager.setUserId(userID);
                        preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                        preference.setSummary(userID);
                    }
                    return false;
                }
            });
        }
    }

    void setupClearConfig(Model model) {
        final ClearConfigManager clearConfigManager=(ClearConfigManager) model;
        Preference preference = findPreference(model.getOperation().getId());
        preference.setIcon(ContextCompat.getDrawable(getActivity(),R.drawable.ic_delete_blue_48dp));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialogs.showAlertDialogConfirm(getActivity(), "Delete Configuration Files", "Delete All Configuration File? These can't be recoverd after delete.", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            ((AppServiceManager)ModelManager.getInstance(getActivity()).getModel(ModelManager.MODEL_APP_SERVICE)).stop();
                            clearConfigManager.delete();
                            isRefresh = true;
                            Toast.makeText(getActivity(), "File Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
            }
        });
    }

    void setupClearData(Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_blue_48dp));
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
    void setupNotificationTest(final Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notification_teal_48dp));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialogs.showAlertDialogConfirm(getActivity(), "Test Notification (Only for testing)", "The Survey/Intervention may not be triggered automatically after this.", "Yes", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            Intent intent = new Intent();
                            intent.setClassName(model.getOperation().getPackage_name(), model.getOperation().getClass_name());
                            startActivity(intent);
                        }
                    }
                });

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
                for(int i=0;i<adminManager.getModels().size();i++){
                    adminManager.getModels().get(i).save();
                }
                Toast.makeText(getActivity(), "Saved...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ServiceSystemHealth.INTENT_NAME);
                intent.putExtra(ServiceSystemHealth.TYPE, ServiceSystemHealth.USERINFO_WAKEUP_SLEEP);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                isRefresh=true;
            }
        });
    }

    void setupWakeupInfo(final Model model) {
        WakeupInfoManager wakeupInfoManager=(WakeupInfoManager) model;
        Preference preference = findPreference(model.getOperation().getId());

        Status status = model.getStatus();
        if (status.getStatusCode() == Status.DATAKIT_NOT_AVAILABLE) {
            preference.setSummary(status.getStatusMessage());
            preference.setEnabled(false);
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else {
            preference.setEnabled(true);
            if (wakeupInfoManager.getWakeupTimeDB() == -1 && wakeupInfoManager.getWakeupTimeNew() == -1) {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
                preference.setSummary("");
            } else if (wakeupInfoManager.getWakeupTimeNew() != -1) {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                preference.setSummary(formatTime(wakeupInfoManager.getWakeupTimeNew()));
            } else {
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                preference.setSummary(formatTime(wakeupInfoManager.getWakeupTimeDB()));
            }
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimePicker(preference,model);
                    return false;
                }
            });
        }
    }

    void setupSleepInfo(final Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        SleepInfoManager sleepInfoManager=(SleepInfoManager) model;
        Status status = model.getStatus();
        if (status.getStatusCode() == Status.DATAKIT_NOT_AVAILABLE) {
            preference.setSummary(status.getStatusMessage());
            preference.setEnabled(false);
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        } else
            preference.setEnabled(true);
        if (sleepInfoManager.getSleepTimeDB() == -1 && sleepInfoManager.getSleepTimeNew() == -1) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            preference.setSummary("");
        } else if (sleepInfoManager.getSleepTimeNew() != -1) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(formatTime(sleepInfoManager.getSleepTimeNew()));
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(formatTime(sleepInfoManager.getSleepTimeDB()));
        }

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimePicker(preference, model);
                return false;
            }
        });
    }

    void setupAppSettings(Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        Status status = model.getStatus();
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

    void setupAppInstall(Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        Status status = model.getStatus();
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
    void setupAppService(Model model) {
        Preference preference = findPreference(model.getOperation().getId());
        Status status = model.getStatus();
        preference.setSummary(status.getStatusMessage());
        if (status.getStatusCode() == Status.SUCCESS) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
        } else if (status.getStatusCode() == Status.APP_NOT_RUNNING) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityService.class);
                startActivity(intent);
                return false;
            }
        });
    }

    void showTimePicker(final Preference preference, final Model model) {
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
                    SleepInfoManager sleepInfoManager=(SleepInfoManager)model;
                    sleepInfoManager.setSleepTimeNew(selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000);
                    preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                    preference.setSummary(formatTime(sleepInfoManager.getSleepTimeNew()));
                } else {
                    WakeupInfoManager wakeupInfoManager=(WakeupInfoManager)model;
                    wakeupInfoManager.setWakeupTimeNew(selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000);
                    preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
                    preference.setSummary(formatTime(wakeupInfoManager.getWakeupTimeNew()));
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
        Intent intent = new Intent(ServiceSystemHealth.INTENT_NAME);
        intent.putExtra(ServiceSystemHealth.TYPE, ServiceSystemHealth.ADMIN);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        super.onStop();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
