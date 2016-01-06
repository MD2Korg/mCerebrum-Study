package org.md2k.study;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TimePicker;

import org.md2k.study.install.ActivityAppInstall;
import org.md2k.study.install.Apps;
import org.md2k.study.settings.ActivityAppSettings;
import org.md2k.utilities.Report.Log;

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
    String userID=null;
    long wakeupTime;
    long sleepTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);
        return v;
    }
    void setupUserID(){
        EditTextPreference editTextPreference= (EditTextPreference) findPreference("key_user_id");
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                userID = (String) newValue;
                preference.setSummary(userID);
                return false;
            }
        });
    }
    void setupPreference(){
        setupPreferenceApplication();
        setupPreferenceSettings();
        setupUserID();
        setupWakeupTime();
        setupSleepTime();
    }
    void setupWakeupTime(){
        Preference preference=findPreference("key_wakeup_time");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "wakeupClick...");
                showTimePicker(preference);
                return false;
            }
        });
    }
    void setupSleepTime(){
        Preference preference=findPreference("key_sleep_time");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "sleepClick...");
                showTimePicker(preference);
                return false;
            }
        });
    }

    void setupPreferenceSettings(){
        Preference preference=findPreference("key_settings");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityAppSettings.class);
                startActivity(intent);
                return false;
            }
        });

    }
    void setupPreferenceApplication(){
        Preference preference=findPreference("key_app");
        Apps apps= Apps.getInstance(getActivity());
        int total=apps.size(getActivity());
        int install= apps.sizeInstalled(getActivity());
        int update=apps.sizeUpdate(getActivity());
        if(update==0 && total==install){
            preference.setIcon(ContextCompat.getDrawable(getActivity(),R.drawable.ic_ok_teal_50dp));
            preference.setSummary("success");
        }
        else if(total!=install){
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            preference.setSummary("Installed:"+install+ ",   Require:"+total);
        }
        else{
            preference.setIcon(ContextCompat.getDrawable(getActivity(),R.drawable.ic_warning_amber_50dp));
            preference.setSummary("update available");
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ActivityAppInstall.class);
                startActivity(intent);
                return false;
            }
        });
    }
    @Override
    public void onResume(){
        setupPreference();
        super.onResume();
    }
    void showTimePicker(final Preference preference){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                preference.setSummary(String.format("%02d:%02d",selectedHour,selectedMinute));
                if(preference.getKey().contains("sleep"))
                    sleepTime=selectedHour*60*60*1000+selectedMinute*60*1000;
                else
                    wakeupTime=selectedHour*60*60*1000+selectedMinute*60*1000;
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }
}
