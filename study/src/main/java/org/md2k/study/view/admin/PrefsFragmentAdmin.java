package org.md2k.study.view.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import org.md2k.study.R;
import org.md2k.study.ServiceSystemHealth;
import org.md2k.study.Status;
import org.md2k.study.config.CView;
import org.md2k.study.config.ViewContent;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.view.report.ActivityReport;
import org.md2k.study.view.study_setup.ActivityStudySetup;
import org.md2k.study.view.system.ActivitySystem;
import org.md2k.study.view.test.ActivityTest;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

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
    ModelManager modelManager;
    boolean isSystem = false;
    boolean isStudySetup = false;
    boolean isReport = false;
    boolean isTest = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(ServiceSystemHealth.INTENT_NAME));
        modelManager = ModelManager.getInstance(getActivity());
        addPreferencesFromResource(R.xml.pref_admin);
        ((PreferenceCategory) findPreference("key_category")).removeAll();
        ArrayList<ViewContent> viewContents = modelManager.getConfigManager().getConfig().getAdmin_view().getView_contents();
        for (int i = 0; i < viewContents.size(); i++) {
            if (!viewContents.get(i).isEnable()) continue;
            switch (viewContents.get(i).getId()) {
                case CView.SYSTEM:
                    prepareSystem(viewContents.get(i));
                    break;
                case CView.STUDY_SETUP:
                    prepareStudySetup(viewContents.get(i));
                    break;
                case CView.REPORT:
                    prepareReport(viewContents.get(i));
                    break;
                case CView.TEST:
                    prepareTest(viewContents.get(i));
                    break;
            }
        }
        setupCloseButton();
    }

    void prepareSystem(ViewContent viewContent) {
        isSystem = true;
        Preference preference = new Preference(getActivity());
        preference.setKey(CView.SYSTEM);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivitySystem.class);
                getActivity().startActivity(intent);
                return false;
            }
        });
        ((PreferenceCategory) findPreference("key_category")).addPreference(preference);
    }

    void prepareStudySetup(ViewContent viewContent) {
        isStudySetup = true;
        Preference preference = new Preference(getActivity());
        preference.setKey(CView.STUDY_SETUP);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityStudySetup.class);
                getActivity().startActivity(intent);
                return false;
            }
        });
        ((PreferenceCategory) findPreference("key_category")).addPreference(preference);
    }

    void prepareTest(ViewContent viewContent) {
        isTest = true;
        Preference preference = new Preference(getActivity());
        preference.setKey(CView.TEST);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityTest.class);
                getActivity().startActivity(intent);
                return false;
            }
        });
        ((PreferenceCategory) findPreference("key_category")).addPreference(preference);
    }

    void prepareReport(ViewContent viewContent) {
        isReport = true;
        Preference preference = new Preference(getActivity());
        preference.setKey(CView.REPORT);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityReport.class);
                getActivity().startActivity(intent);
                return false;
            }
        });
        ((PreferenceCategory) findPreference("key_category")).addPreference(preference);
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
        updatePreference();
        super.onResume();
    }

    public void updatePreference() {
        Status status = modelManager.getStatus();
        Log.d(TAG, "updatePreference()...status=" + status.log());
        if (status.getRank() >= Status.RANK_SYSTEM) {
            updatePreference(CView.SYSTEM, true, false, status.getMessage());
            updatePreference(CView.STUDY_SETUP, false, false, "");
            updatePreference(CView.TEST, false, false, "");
            updatePreference(CView.REPORT, false, false, "");

        } else if (status.getRank() >= Status.RANK_ADMIN_REQUIRED) {
            updatePreference(CView.SYSTEM, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.STUDY_SETUP, true, false, status.getMessage());
            updatePreference(CView.TEST, false, false, "");
            updatePreference(CView.REPORT, false, false, "");
        } else if (status.getRank() >= Status.RANK_ADMIN_OPTIONAL) {
            updatePreference(CView.SYSTEM, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.STUDY_SETUP, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.TEST, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.REPORT, false, false, "");
        } else {
            updatePreference(CView.SYSTEM, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.STUDY_SETUP, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.TEST, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.REPORT, true, true, "");
        }
    }

    void updatePreference(String key, boolean enable, boolean status, String message) {
        Preference preference = findPreference(key);
        if (preference == null) return;
        preference.setEnabled(enable);
        if (status)
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
        else {
            if (enable)
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            else
                preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_grey_50dp));
        }
        preference.setSummary(message);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePreference();
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void setupCloseButton() {
        final Button button = (Button) getActivity().findViewById(R.id.button_1);
        button.setText("Close");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
}
