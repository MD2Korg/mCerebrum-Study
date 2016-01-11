package org.md2k.study.user.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
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

import org.md2k.study.OnDataChangeListener;
import org.md2k.study.R;
import org.md2k.study.Status;
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
public class PrefsFragmentServiceApp extends PreferenceFragment {

    private static final String TAG = PrefsFragmentServiceApp.class.getSimpleName();
    Context context;
    ServiceApps serviceApps;
    Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity().getApplicationContext();
        serviceApps=ServiceApps.getInstance(context);
        addPreferencesFromResource(R.xml.pref_app_service);
        setupServiceApp();
        setupButtons();
        handler=new Handler();
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
        final Button button3 = (Button) getActivity().findViewById(R.id.button_3);
        button3.setText("Close");
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        final Button button1 = (Button) getActivity().findViewById(R.id.button_1);
        button1.setText("Start All");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceApps.start();
            }
        });
        final Button button2 = (Button) getActivity().findViewById(R.id.button_2);
        button2.setText("Stop All");
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceApps.stop();
            }
        });
    }

    void updatePreference(int i) {
        SwitchPreference switchPreference = (SwitchPreference) findPreference(String.valueOf(i));
        Status status = serviceApps.serviceAppList.get(i).getStatus(context);
        if(status.getStatusCode()==Status.APP_NOT_INSTALLED) {
            switchPreference.setEnabled(false);
            switchPreference.setChecked(false);
            switchPreference.setIcon(ContextCompat.getDrawable(getActivity(),R.drawable.ic_error_red_50dp));
            switchPreference.setSummary(status.getStatusMessage());
        }else if(status.getStatusCode()==Status.APP_NOT_RUNNING) {
            switchPreference.setEnabled(true);
            switchPreference.setChecked(false);
            switchPreference.setIcon(ContextCompat.getDrawable(getActivity(),R.drawable.ic_error_red_50dp));
            switchPreference.setSummary(status.getStatusMessage());
        }else{
            switchPreference.setEnabled(true);
            switchPreference.setChecked(true);
            switchPreference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            switchPreference.setSummary(status.getStatusMessage());
        }
    }
    void setupServiceApp() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_app");
        preferenceCategory.removeAll();
        Log.d(TAG,"size="+serviceApps.serviceAppList.size());

        for (int i = 0; i < serviceApps.serviceAppList.size(); i++) {
            Log.d(TAG,"name="+serviceApps.serviceAppList.get(i).getName());
            SwitchPreference switchPreference = new SwitchPreference(getActivity());
            switchPreference.setKey(String.valueOf(i));
            switchPreference.setTitle(serviceApps.serviceAppList.get(i).getName());
            switchPreference.setEnabled(false);
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int i=Integer.parseInt(preference.getKey());
                    Status status = serviceApps.serviceAppList.get(i).getStatus(context);
                    if (status.getStatusCode() == Status.SUCCESS) {
                        serviceApps.serviceAppList.get(i).stop(context);
                        updatePreference(i);
                    } else if (status.getStatusCode() == Status.APP_NOT_RUNNING) {
                        serviceApps.serviceAppList.get(i).start(context);
                        updatePreference(i);
                    }
                    return false;
                }
            });
            preferenceCategory.addPreference(switchPreference);
            updatePreference(i);
        }
    }
    Runnable serviceRunning=new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<serviceApps.serviceAppList.size();i++)
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
