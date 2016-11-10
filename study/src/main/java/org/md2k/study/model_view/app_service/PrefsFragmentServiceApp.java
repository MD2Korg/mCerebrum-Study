package org.md2k.study.model_view.app_service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelFactory;
import org.md2k.study.controller.ModelManager;

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
public class PrefsFragmentServiceApp extends PreferenceFragment {

    private static final String TAG = PrefsFragmentServiceApp.class.getSimpleName();
    private Context context;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity().getApplicationContext();
        addPreferencesFromResource(R.xml.pref_app_service);
        handler=new Handler();
    }
    @Override
    public void onStart(){
        setupServiceApp();
        setupButtons();
        super.onStart();
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
        final Button button3 = (Button) getActivity().findViewById(R.id.button_1);
        button3.setText("Close");
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        final Button button1 = (Button) getActivity().findViewById(R.id.button_3);
        button1.setText("Start All");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppServiceManager appServiceManager= (AppServiceManager) ModelManager.getInstance(getActivity()).getModel(ModelFactory.MODEL_APP_SERVICE);
                appServiceManager.startAll();
            }
        });
        final Button button2 = (Button) getActivity().findViewById(R.id.button_2);
        button2.setText("Stop All");
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppServiceManager appServiceManager= (AppServiceManager) ModelManager.getInstance(getActivity()).getModel(ModelFactory.MODEL_APP_SERVICE);
                appServiceManager.stopAll();
            }
        });
    }

    private void updatePreference(int i) {
        AppServiceManager appServiceManager= (AppServiceManager) ModelManager.getInstance(getActivity()).getModel(ModelFactory.MODEL_APP_SERVICE);
        SwitchPreference switchPreference = (SwitchPreference) findPreference(String.valueOf(i));
        Status status = appServiceManager.appServiceList.get(i).getStatus();
        if(status.getStatus()== Status.APP_NOT_INSTALLED) {
            switchPreference.setEnabled(false);
            switchPreference.setChecked(false);
            switchPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            switchPreference.setSummary(status.getMessage());
        }else if(status.getStatus()== Status.APP_NOT_RUNNING) {
            switchPreference.setEnabled(true);
            switchPreference.setChecked(false);
            switchPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            switchPreference.setSummary(status.getMessage());
        }else{
            switchPreference.setEnabled(true);
            switchPreference.setChecked(true);
            switchPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            switchPreference.setSummary(status.getMessage());
        }
    }
    private void setupServiceApp() {
        AppServiceManager appServiceManager= (AppServiceManager) ModelManager.getInstance(getActivity()).getModel(ModelFactory.MODEL_APP_SERVICE);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_app");
        preferenceCategory.removeAll();

        for (int i = 0; i < appServiceManager.appServiceList.size(); i++) {
            SwitchPreference switchPreference = new SwitchPreference(getActivity());
            switchPreference.setKey(String.valueOf(i));
            switchPreference.setTitle(appServiceManager.appServiceList.get(i).getName());
            switchPreference.setEnabled(false);
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppServiceManager appServiceManager= (AppServiceManager) ModelManager.getInstance(getActivity()).getModel(ModelFactory.MODEL_APP_SERVICE);
                    int i=Integer.parseInt(preference.getKey());
                    Status status = appServiceManager.appServiceList.get(i).getStatus();
                    if (status.getStatus() == Status.SUCCESS) {
                        appServiceManager.appServiceList.get(i).stop();
                        appServiceManager.appServiceList.get(i).setActive(false);
                        updatePreference(i);
                    } else if (status.getStatus() == Status.APP_NOT_RUNNING) {
                        appServiceManager.appServiceList.get(i).setActive(true);
                        appServiceManager.appServiceList.get(i).start();
                        updatePreference(i);
                    }
                    return false;
                }
            });
            preferenceCategory.addPreference(switchPreference);
            updatePreference(i);
        }
    }
    private Runnable serviceRunning=new Runnable() {
        @Override
        public void run() {
            AppServiceManager appServiceManager= (AppServiceManager) ModelManager.getInstance(getActivity()).getModel(ModelFactory.MODEL_APP_SERVICE);
            for(int i=0;i< appServiceManager.appServiceList.size();i++)
                updatePreference(i);
            handler.postDelayed(serviceRunning,1000);
        }
    };
    @Override
    public void onResume(){
        handler.post(serviceRunning);
        super.onResume();
    }
    @Override
    public void onPause(){
        handler.removeCallbacks(serviceRunning);
        super.onPause();
    }
}
