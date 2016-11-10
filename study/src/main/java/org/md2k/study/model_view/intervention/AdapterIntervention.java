package org.md2k.study.model_view.intervention;

/**
 * Created by smhssain on 11/4/2015.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.md2k.study.R;
import org.md2k.study.config.ConfigApp;

import java.util.List;

class AdapterIntervention extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<ConfigApp> listStorage;
    private Context context;

    public AdapterIntervention(Context context, List<ConfigApp> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.listview_with_text_image, parent, false);
            listViewHolder.textInListView = (TextView)convertView.findViewById(R.id.textView);
            listViewHolder.imageInListView = (ImageView)convertView.findViewById(R.id.imageView);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        listViewHolder.textInListView.setText(listStorage.get(position).getName());

        listViewHolder.imageInListView.setImageDrawable(getIconFromApplication(listStorage.get(position).getPackage_name()));

        return convertView;
    }
    private Drawable getIconFromApplication(String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    static class ViewHolder{
        TextView textInListView;
        ImageView imageInListView;
    }
}