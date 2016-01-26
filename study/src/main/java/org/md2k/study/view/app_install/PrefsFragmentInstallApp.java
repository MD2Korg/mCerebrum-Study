package org.md2k.study.view.app_install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.md2k.study.OnDataChangeListener;
import org.md2k.study.R;
import org.md2k.study.operation.OperationManager;
import org.md2k.study.operation.app_install.AppInstall;
import org.md2k.study.operation.app_install.AppsInstall;
import org.md2k.utilities.Report.Log;

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

    private static final String TAG = PrefsFragmentInstallApp.class.getSimpleName();
    Context context;
    AppsInstall appsInstall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        addPreferencesFromResource(R.xml.pref_app_install);
        appsInstall = OperationManager.getInstance(getActivity()).appsInstall;
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

    void setupButtons() {
        final Button buttonClose = (Button) getActivity().findViewById(R.id.button_2);
        buttonClose.setText("Close");
        buttonClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        final Button buttonUpdate = (Button) getActivity().findViewById(R.id.button_1);
        buttonUpdate.setText("Check Updates");
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateAppInstall();
            }
        });
    }

    void updateAppInstall() {
        Log.d(TAG, "updateAppInstall()...");
        for (int i = 0; i < appsInstall.getAppInstallList().size(); i++) {
            final int finalI = i;
            appsInstall.getAppInstallList().get(i).setLatestVersionName(context, new OnDataChangeListener() {
                @Override
                public void onDataChange(String str) {
                    Log.d(TAG, "updateAppInstall()..." + str);
                    updatePreference(appsInstall.getAppInstallList().get(finalI));
                }
            });
        }
    }

    void updatePreference(AppInstall appInstall) {
        Preference preference = findPreference(appInstall.getName());
        if (!appInstall.isInstalled()) {
            preference.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_red_50dp));
            preference.setSummary("Not Installed");
            if(appInstall.getLatestVersion()!=null)
                preference.setSummary("Not Installed (Version available:"+appInstall.getLatestVersion()+")");
        } else if (appInstall.isUpdateAvailable()) {
            preference.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_warning_amber_50dp));
            preference.setSummary(appInstall.getCurVersion() + " (Update Available: " + appInstall.getLatestVersion() + ")");
        } else {
            preference.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_ok_teal_50dp));
            preference.setSummary(appInstall.getCurVersion());
        }
        setEntries((ListPreference) preference, appInstall);
    }

    void setEntries(ListPreference listPreference, AppInstall appInstall) {
        if (!appInstall.isInstalled()) {
            String options[] = {"Install"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Install");

        } else if (appInstall.isUpdateAvailable()) {
            String options[] = {"Update", "Uninstall","Run"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Run");
        } else {
            String options[] = {"Uninstall", "Run"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Run");
        }

    }

    void setupAppInstall() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_app");
        preferenceCategory.removeAll();
        for (int i = 0; i < appsInstall.getAppInstallList().size(); i++) {
            ListPreference listPreference = new ListPreference(getActivity());
            listPreference.setTitle(appsInstall.getAppInstallList().get(i).getName());
            listPreference.setKey(appsInstall.getAppInstallList().get(i).getName());
            setEntries(listPreference, appsInstall.getAppInstallList().get(i));
            final int finalI = i;
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppInstall appInstall = appsInstall.getAppInstallList().get(finalI);
                    if (newValue.equals("Install") || newValue.equals("Update")) {
                        appInstall.downloadAndInstallApp(getActivity());
                    } else if (newValue.equals("Run")) {
                        appInstall.run(context);
                    } else if (newValue.equals("Uninstall")) {
                        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                                Uri.parse("package:"+appInstall.getPackage_name()));
                        startActivity(uninstallIntent);
                    }
                    return false;
                }
            });
            preferenceCategory.addPreference(listPreference);
            updatePreference(appsInstall.getAppInstallList().get(i));
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume()...");
        appsInstall.reset();
        setupAppInstall();
        OperationManager.getInstance(context).connect();
        super.onResume();
    }
}
