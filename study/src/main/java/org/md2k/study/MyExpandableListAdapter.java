package org.md2k.study;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.md2k.study.groups.Children;
import org.md2k.study.groups.AppInfo;
import org.md2k.study.groups.Group;
import org.md2k.study.groups.GroupManager;
import org.md2k.study.groups.app.ChildrenApp;
import org.md2k.study.groups.app.GroupApp;
import org.md2k.study.groups.service.ChildrenService;
import org.md2k.study.groups.service.GroupService;

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

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = MyExpandableListAdapter.class.getSimpleName();
    private final SparseArray<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public MyExpandableListAdapter(Activity act, SparseArray<Group> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }
/*    void showSettings(View convertView, Children children) {
/*        ((TextView) convertView.findViewById(R.id.textView1)).setText(children.packageName);
        switch (children.status) {
            case Group.GREEN:
                ((ImageView) convertView.findViewById(R.id.imageViewDetailsStatus)).setImageDrawable(this.activity.getResources().getDrawable(R.drawable.green));
                convertView.findViewById(R.id.buttonDetailsFix).setVisibility(View.INVISIBLE);
//                ((Button) convertView.findViewById(R.id.buttonDetailsFix)).setBackground(activity.getResources().getDrawable(R.drawable.button_green));
//                convertView.findViewById(R.id.buttonDetailsFix).setOnClickListener(new OnClickListenerService(appInfo));
                break;
            case Group.YELLOW:
                ((ImageView) convertView.findViewById(R.id.imageViewDetailsStatus)).setImageDrawable(this.activity.getResources().getDrawable(R.drawable.yellow));
                convertView.findViewById(R.id.buttonDetailsFix).setVisibility(View.VISIBLE);
                ((Button) convertView.findViewById(R.id.buttonDetailsFix)).setText("Fix");
//                convertView.findViewById(R.id.buttonDetailsFix).setOnClickListener(new OnClickListenerService(appInfo));
                break;
            case Group.RED:
                ((ImageView) convertView.findViewById(R.id.imageViewDetailsStatus)).setImageDrawable(this.activity.getResources().getDrawable(R.drawable.red));
                convertView.findViewById(R.id.buttonDetailsFix).setVisibility(View.VISIBLE);
                ((Button) convertView.findViewById(R.id.buttonDetailsFix)).setText("Fix");
                ((Button) convertView.findViewById(R.id.buttonDetailsFix)).setBackground(activity.getResources().getDrawable(R.drawable.button_red));
//                convertView.findViewById(R.id.buttonDetailsFix).setOnClickListener(new OnClickListenerService(appInfo));
                break;
        }
    }
*/

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Children children = (Children) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        ((TextView) convertView.findViewById(R.id.textView1)).setText(children.getName());
        convertView.findViewById(R.id.buttonDetailsFix).setVisibility(children.buttonVisiblilty);
        ((ImageView) convertView.findViewById(R.id.imageViewDetailsStatus)).setImageDrawable(this.activity.getResources().getDrawable(children.statusImage));
        ((Button) convertView.findViewById(R.id.buttonDetailsFix)).setText(children.buttonText);
        convertView.findViewById(R.id.buttonDetailsFix).setBackground(activity.getResources().getDrawable(children.buttonBackground));
        convertView.findViewById(R.id.buttonDetailsFix).setOnClickListener(children.onClickListener);

/*        switch (groupPosition) {
            case GroupManager.GROUP_APP:
                showApp(convertView, (ChildrenApp)children);
                break;
            case GroupManager.GROUP_SERVICE:
                showService(convertView, (ChildrenService)children);
                break;
            case GroupManager.GROUP_DEVICE_SETTINGS:
//                showSettings(convertView,children);
                break;
            case GroupManager.GROUP_SENSOR_QUALITY:
                break;
            case GroupManager.GROUP_STORAGE:
                break;
        }
*/
/*        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupPosition==0) {
                    AppInfo appInfo= AppInfoList.getInstance(activity).find(children.packageName);
                    if(appInfo.getCurrentVersionNameLong()!=-1){
                        Intent LaunchIntent = activity.getPackageManager().getLaunchIntentForPackage(appInfo.getPackage_name());
                        activity.startActivity( LaunchIntent );
                    }
                }
            }
        });
*/        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.getName());
        switch (group.status) {
            case GroupManager.GREEN:
                ((CheckedTextView) convertView).setCheckMarkDrawable(R.drawable.green);
                break;
            case GroupManager.YELLOW:
                ((CheckedTextView) convertView).setCheckMarkDrawable(R.drawable.yellow);
                break;
            case GroupManager.RED:
                ((CheckedTextView) convertView).setCheckMarkDrawable(R.drawable.red);
                break;
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
