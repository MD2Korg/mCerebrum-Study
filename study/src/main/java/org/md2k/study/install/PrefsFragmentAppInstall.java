package org.md2k.study.install;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.md2k.study.OnDataChangeListener;
import org.md2k.study.R;
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
public class PrefsFragmentAppInstall extends PreferenceFragment {

    private static final String TAG = PrefsFragmentAppInstall.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_app_install);
        setupAppInstall();
        setupButtons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);
        return v;
    }

    void setupButtons() {
        final Button buttonCancel = (Button) getActivity().findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        final Button buttonUpdate = (Button) getActivity().findViewById(R.id.button_save);
        buttonUpdate.setText("Check Updates");
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateAppInstall();
            }
        });

    }

    void updateAppInstall() {
        Log.d(TAG, "updateAppInstall()...");
        Apps apps = Apps.getInstance(getActivity());
        for (int i = 0; i < apps.appList.size(); i++) {
            final int finalI = i;
            apps.appList.get(i).refresh(getActivity(), new OnDataChangeListener() {
                @Override
                public void onDataChange(String str) {
                    Log.d(TAG, "updateAppInstall()..." + str);
                    updatePreference(Apps.getInstance(getActivity()).appList.get(finalI));
                }
            });
        }
    }

    void updatePreference(App app) {
        Preference preference = findPreference(app.getName());
        if (!app.isInstalled(getActivity())) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            preference.setSummary("Not Installed");
        } else if (app.isUpdateAvailable()) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_warning_amber_50dp));
            preference.setSummary(app.getCurVersion() + " (Update Available: " + app.getLatestVersion() + ")");
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(app.getCurVersion());
        }
        setEntries((ListPreference) preference,app);
    }
    void setEntries(ListPreference listPreference, App app){
        if (!app.isInstalled(getActivity())) {
            String options[]={"Install"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Install");

        } else if (app.isUpdateAvailable()) {
            String options[]={"Update","Run"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Run");

//                        app.downloadAndInstallApp(getActivity());
        } else {
            String options[]={"Run"};
            listPreference.setEntries(options);
            listPreference.setEntryValues(options);
            listPreference.setDefaultValue("Run");

//                        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getPackage_name());
//                        startActivity(launchIntent);
        }

    }
    void setupAppInstall() {
        Apps apps = Apps.getInstance(getActivity());
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_app");
        preferenceCategory.removeAll();
        for (int i = 0; i < apps.appList.size(); i++) {
            ListPreference listPreference = new ListPreference(getActivity());
            listPreference.setTitle(apps.appList.get(i).getName());
            listPreference.setKey(apps.appList.get(i).getName());
            setEntries(listPreference, apps.appList.get(i));
            final int finalI = i;
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String optionSelected = (String) newValue;
                    App app = Apps.getInstance(getActivity()).appList.get(finalI);
                    if (newValue.equals("Install") || newValue.equals("Update")) {
                        app.downloadAndInstallApp(getActivity());
                    } else if (newValue.equals("Run")) {
                        app.run(getActivity());
                    }
                    return false;
                }
            });
            preferenceCategory.addPreference(listPreference);
            updatePreference(apps.appList.get(i));
        }
    }
}
