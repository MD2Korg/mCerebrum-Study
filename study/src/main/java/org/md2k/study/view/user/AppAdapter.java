package org.md2k.study.view.user;

/**
 * Created by smhssain on 11/4/2015.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.md2k.study.R;
import org.md2k.study.operation.user_app.UserApp;
import org.md2k.utilities.Report.Log;

import java.util.List;

public class AppAdapter extends BaseAdapter {

    private static final String TAG = AppAdapter.class.getSimpleName();
    private LayoutInflater layoutinflater;
    private List<UserApp> userApps;
    private Context context;

    public AppAdapter(Context context, List<UserApp> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userApps = customizedListView;
    }

    @Override
    public int getCount() {
        return userApps.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.listview_with_text_image, parent, false);
            listViewHolder.textInListView = (TextView) convertView.findViewById(R.id.textView);
            listViewHolder.imageInListView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }

        listViewHolder.textInListView.setText(userApps.get(position).getName());

        listViewHolder.imageInListView.setImageDrawable(getIconFromApplication(userApps.get(position).getIcon()));

        return convertView;
    }

    private Drawable getIconFromApplication(String icon) {
        Resources resources=context.getResources();
        int resourceId=resources.getIdentifier(icon,"drawable",context.getPackageName());
        Log.d(TAG, "icon=" + icon);
        return resources.getDrawable(resourceId);
    }

    static class ViewHolder {
        TextView textInListView;
        ImageView imageInListView;
    }

}