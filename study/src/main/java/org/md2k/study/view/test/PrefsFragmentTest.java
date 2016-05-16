package org.md2k.study.view.test;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.md2k.study.R;
import org.md2k.study.controller.ModelManager;

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
public class PrefsFragmentTest extends PreferenceFragment {
    private static final String TAG = PrefsFragmentTest.class.getSimpleName();
    ModelManager modelManager;
    ArrayList<Preference> preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        modelManager= ModelManager.getInstance(getActivity());
        addPreferencesFromResource(R.xml.pref_test);
        ArrayList<String> views= modelManager.getConfigManager().getConfig().getAdmin_view().getView_contents(ConfigView.TEST).getValues();
        preferences=new ArrayList<>();
        for(int i=0;i<views.size();i++){
            Log.d(TAG, "onCreate()...id=" + views.get(i));
            final Model model = modelManager.getModel(views.get(i));
            if(model==null) continue;
            Preference preference=new Preference(getActivity());
            preference.setKey(model.getAction().getId());
            preference.setTitle(model.getAction().getName());
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
            ((PreferenceCategory)findPreference("test")).addPreference(preference);
            preferences.add(preference);
        }
        setupCloseButton();
        */
    }

    @Override
    public void onDestroy(){
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
        super.onResume();
    }
}
