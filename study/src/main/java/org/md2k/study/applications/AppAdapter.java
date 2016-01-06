package org.md2k.study.applications;

/**
 * Created by smhssain on 11/4/2015.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.md2k.study.R;
import org.md2k.utilities.Report.Log;

import java.util.List;

public class AppAdapter extends BaseAdapter {

    private static final String TAG = AppAdapter.class.getSimpleName();
    private LayoutInflater layoutinflater;
    private List<App> listStorage;
    private Context context;

    public AppAdapter(Context context, List<App> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
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

        listViewHolder.textInListView.setText(listStorage.get(position).getContent());

        listViewHolder.imageInListView.setImageDrawable(getIconFromApplication(listStorage.get(position).getContent()));

        return convertView;
    }

    private Drawable getIconFromApplication(String name) {
        Log.d(TAG, "name=" + name);
        if (name.equals("Intervention"))
            return ContextCompat.getDrawable(context, R.drawable.ic_intervention_48dp);
            else if(name.equals("Privacy Control"))
            return ContextCompat.getDrawable(context, R.drawable.ic_lock_teal_48dp);
        else if(name.equals("Report"))
            return ContextCompat.getDrawable(context,R.drawable.ic_smoking_teal_48dp);
        else if(name.equals("Plotter")){
            return ContextCompat.getDrawable(context,R.drawable.ic_plot_teal_48dp);
        }
        else return null;
    }

    static class ViewHolder {
        TextView textInListView;
        ImageView imageInListView;
    }

}