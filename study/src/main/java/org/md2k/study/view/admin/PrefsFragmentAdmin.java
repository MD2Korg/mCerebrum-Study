package org.md2k.study.view.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
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
import org.md2k.study.model_view.Model;
import org.md2k.study.view.configure_app.ActivityConfigureApp;
import org.md2k.study.view.configure_study.ActivityConfigureStudy;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(ServiceSystemHealth.INTENT_NAME));
        modelManager = ModelManager.getInstance(getActivity());
        addPreferencesFromResource(R.xml.pref_admin);

        ArrayList<ViewContent> viewContents = modelManager.getConfigManager().getConfig().getAdmin_view().getView_contents();
        for (int i = 0; i < viewContents.size(); i++) {
            if (!viewContents.get(i).isEnable()) continue;
            switch (viewContents.get(i).getId()) {
                case CView.CONFIGURE_APP:
                    prepareConfigureApp(viewContents.get(i));
                    break;
                case CView.CONFIGURE_STUDY:
                    prepareConfigureStudy(viewContents.get(i));
                    break;
                case CView.START_STUDY:
                    prepareStartStudy(viewContents.get(i));
                    break;
                case CView.STOP_STUDY:
                    prepareStopStudy(viewContents.get(i));
                    break;
                case CView.OTHER:
                    prepareOther(viewContents.get(i));
                    break;
            }
        }
        setupCloseButton();
    }

    void prepareStartStudy(ViewContent viewContent) {
        Preference preference = findPreference(CView.START_STUDY);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ServiceSystemHealth.RANK_LIMIT = Status.RANK_SUCCESS;
                modelManager.clear();
                modelManager.remove();
                modelManager.set();
                return false;
            }
        });
    }

    void prepareStopStudy(ViewContent viewContent) {
        Preference preference = findPreference(CView.STOP_STUDY);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ServiceSystemHealth.RANK_LIMIT = Status.RANK_ADMIN_OPTIONAL;
                modelManager.clear();
                modelManager.remove();
                modelManager.set();
                return false;
            }
        });
    }

    void prepareConfigureApp(ViewContent viewContent) {
        Preference preference = findPreference(CView.CONFIGURE_APP);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityConfigureApp.class);
                getActivity().startActivity(intent);
                return false;
            }
        });
    }

    void prepareConfigureStudy(ViewContent viewContent) {
        Preference preference = findPreference(CView.CONFIGURE_STUDY);
        preference.setTitle(viewContent.getName());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityConfigureStudy.class);
                getActivity().startActivity(intent);
                return false;
            }
        });
    }
    void prepareOther(ViewContent viewContent){
        PreferenceCategory preferenceCategory= (PreferenceCategory) findPreference(viewContent.getId());
        preferenceCategory.removeAll();
        ArrayList<String> views= viewContent.getValues();
        for(int i=0;i<views.size();i++){
            Log.d(TAG, "onCreate()...id=" + views.get(i));
            final Model model = modelManager.getModel(views.get(i));
            if(model==null) continue;
            Preference preference=new Preference(getActivity());
            preference.setKey(model.getAction().getId());
            preference.setTitle(model.getAction().getName());
            Resources resources=getActivity().getResources();
            Log.d(TAG, "id=" + model.getAction().getId() + " " + model.getAction().getIcon());
            int resourceId=resources.getIdentifier(model.getAction().getIcon(),"drawable",getActivity().getPackageName());
            preference.setIcon(ContextCompat.getDrawable(getActivity(), resourceId));
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (model.getAction().getPackage_name() != null && model.getAction().getClass_name() != null) {
                        Intent intent = new Intent();
                        intent.setClassName(model.getAction().getPackage_name(), model.getAction().getClass_name());
                        startActivity(intent);
                    }
                    return false;
                }
            });
            preferenceCategory.addPreference(preference);
        }
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
            updatePreference(CView.CONFIGURE_APP, true, false, status.getMessage());
            updatePreference(CView.CONFIGURE_STUDY, false, false, "");
            updatePreference(CView.START_STUDY, false, false, "");
            updatePreference(CView.STOP_STUDY, false, false, "");
            updatePreference(CView.OTHER, false, false, "");

        } else if (status.getRank() >= Status.RANK_ADMIN_REQUIRED) {
            updatePreference(CView.CONFIGURE_APP, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.CONFIGURE_STUDY, true, false, status.getMessage());
            updatePreference(CView.START_STUDY, false, false, "");
            updatePreference(CView.STOP_STUDY, false, false, "");
            updatePreference(CView.OTHER, false, false, "");
        } else if (status.getRank() >= Status.RANK_ADMIN_OPTIONAL) {
            updatePreference(CView.CONFIGURE_APP, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.CONFIGURE_STUDY, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.START_STUDY, true, false, "");
            updatePreference(CView.STOP_STUDY, false, false, "");
            updatePreference(CView.OTHER, false, false, "");
        } else {
            updatePreference(CView.CONFIGURE_APP, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.CONFIGURE_STUDY, true, true, new Status(0, Status.SUCCESS).getMessage());
            updatePreference(CView.START_STUDY, false, false, "");
            updatePreference(CView.STOP_STUDY, true, true, "");
            updatePreference(CView.OTHER, true, true, "");
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
