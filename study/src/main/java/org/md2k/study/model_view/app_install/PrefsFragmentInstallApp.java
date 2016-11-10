package org.md2k.study.model_view.app_install;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.md2k.study.OnDataChangeListener;
import org.md2k.study.R;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.utilities.OnCompletionListener;

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
public class PrefsFragmentInstallApp extends PreferenceFragment {

    private Context context;
    private AppInstallManager appInstallManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        addPreferencesFromResource(R.xml.pref_app_install);
        appInstallManager = (AppInstallManager) ModelManager.getInstance(getActivity()).getModel(ModelFactory.MODEL_APP_INSTALL);
        setupButtons();
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

    private void setupButtons() {
        final Button buttonClose = (Button) getActivity().findViewById(R.id.button_1);
        buttonClose.setText(R.string.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                appInstallManager.set();
                getActivity().finish();
            }
        });
        final Button buttonFixAll = (Button) getActivity().findViewById(R.id.button_2);
        buttonFixAll.setText(R.string.buton_fix_all);
        buttonFixAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                appInstallManager.fix(getActivity(), onCompletionListener);
            }
        });
        final Button buttonUpdate = (Button) getActivity().findViewById(R.id.button_3);
        buttonUpdate.setText(R.string.button_check_update);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Checking update...", Toast.LENGTH_SHORT).show();
                appInstallManager.updateVersionAll(0, new OnDataChangeListener() {
                    @Override
                    public void onDataChange(int now, String str) {
                        try {
                            if (getActivity() == null) return;
                            if (now >= appInstallManager.getAppInstallList().size())
                                Toast.makeText(getActivity(), "Checking updates...done", Toast.LENGTH_SHORT).show();
                            else updatePreference(appInstallManager.getAppInstallList().get(now));
                        } catch (Exception ignored) {

                        }
                    }
                });
            }
        });
    }
/*
    void setPermission(AppInstall appInstall) {
        try {
            Intent intent = new Intent();
//            intent.setAction(appInstall.app.getPermission());
//            intent.setClassName("org.md2k.study", "org.md2k.study.ActivityPermission");
//        intent.setClassName(appInstall.getPackage_name(), appInstall.app.getPermission());
            intent.setComponent(new ComponentName(appInstall.getPackage_name(), "org.md2k.utilities.permission.ActivityPermission"));
            startActivityForResult(intent, PERMISSION_REQUEST);
        } catch (Exception e) {
            isFixAll=false;
            isRefreshRequired=true;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getBooleanExtra(PermissionInfo.INTENT_RESULT, false) == true) {
                    AppInstall appInstall = appInstallManager.getAppInstallList().get(fixIndex);
                    appInstall.setPermission();
                    updatePreference(appInstall);
                    fixIndex++;
                }
            }
        }
    }
*/
private void updatePreference(AppInstall appInstall) {
        Preference preference = findPreference(appInstall.getName());
        if (!appInstall.isInstalled()) {
            preference.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_red_50dp));
            preference.setSummary("Not Installed");
            if (appInstall.getLatestVersion() != null)
                preference.setSummary("Not Installed (Version available:" + appInstall.getLatestVersion() + ")");
        } else if (appInstall.isUpdateAvailable()) {
            preference.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_warning_amber_50dp));
            preference.setSummary(appInstall.getCurVersion() + " (Update Available: " + appInstall.getLatestVersion() + ")");
        } else if (!appInstall.hasPermission()) {
            preference.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_warning_amber_50dp));
            preference.setSummary(appInstall.getCurVersion() + " - permission denied");
        } else {
            preference.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_ok_teal_50dp));
            preference.setSummary(appInstall.getCurVersion());
        }
        setEntries((ListPreference) preference, appInstall);
    }

    private void setEntries(ListPreference listPreference, AppInstall appInstall) {
        if (!appInstall.isInstalled()) {
            String options[] = {"Install"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Install");

        } else if (appInstall.isUpdateAvailable()) {
            String options[] = {"Update", "Uninstall", "Run"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Run");
        } else {
            String options[] = {"Uninstall", "Run", "Permission"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Permission");
        }

    }

    private void setupAppInstall() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_app");
        preferenceCategory.removeAll();
        for (int i = 0; i < appInstallManager.getAppInstallList().size(); i++) {
            ListPreference listPreference = new ListPreference(getActivity());
            listPreference.setTitle(appInstallManager.getAppInstallList().get(i).getName());
            listPreference.setKey(appInstallManager.getAppInstallList().get(i).getName());
            setEntries(listPreference, appInstallManager.getAppInstallList().get(i));
            final int finalI = i;
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.equals("Install") || newValue.equals("Update")) {
                        appInstallManager.install(getActivity(), finalI, onCompletionListener);

                    } else if (newValue.equals("Run")) {
                        appInstallManager.getAppInstallList().get(finalI).run(context);
                    } else if (newValue.equals("Uninstall")) {
                        appInstallManager.uninstall(finalI,onCompletionListener);
                    } else if(newValue.equals("Permission")){
                        appInstallManager.permission(finalI,onCompletionListener);
                    }
                    return false;
                }
            });
            preferenceCategory.addPreference(listPreference);
            updatePreference(appInstallManager.getAppInstallList().get(i));
        }
    }
    private OnCompletionListener onCompletionListener=new OnCompletionListener() {
        @Override
        public void OnCompleted(int status) {
            ModelManager.getInstance(getActivity()).clear();
            ModelManager.getInstance(getActivity()).set();
        }
    };

    @Override
    public void onResume() {
/*        if(isFixAll && fixIndex<appInstallManager.getAppInstallList().size()){
            fix();
        }else if(isFixAll && fixIndex==appInstallManager.getAppInstallList().size()){
            isFixAll=false;
            ModelManager.getInstance(getActivity()).clear();
            ModelManager.getInstance(getActivity()).set();
        }
        else if (isRefreshRequired) {
            ModelManager.getInstance(getActivity()).clear();
            ModelManager.getInstance(getActivity()).set();
        }
*/
        setupAppInstall();
        super.onResume();
    }
}
