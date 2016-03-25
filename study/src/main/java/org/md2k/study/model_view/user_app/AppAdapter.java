package org.md2k.study.model_view.user_app;

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
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;

import java.util.List;

public class AppAdapter extends BaseAdapter {

    private static final String TAG = AppAdapter.class.getSimpleName();
    private LayoutInflater layoutinflater;
    private List<Model> models;
    private Context context;

    public AppAdapter(Context context, List<Model> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        models = customizedListView;
    }

    @Override
    public int getCount() {
        return models.size();
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
        listViewHolder.textInListView.setText(models.get(position).getAction().getName());
        listViewHolder.imageInListView.setImageDrawable(getIconFromApplication(models.get(position).getAction().getIcon()));
        return convertView;
    }

    private Drawable getIconFromApplication(String icon) {
        Resources resources=context.getResources();
        Log.d(TAG, "icon=" + icon);
        int resourceId=resources.getIdentifier(icon,"drawable",context.getPackageName());
        return resources.getDrawable(resourceId);
    }

    static class ViewHolder {
        TextView textInListView;
        ImageView imageInListView;
    }

}