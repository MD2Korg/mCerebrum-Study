package org.md2k.study.view.user;

/**
 * Created by smhssain on 11/4/2015.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.Model;
import org.md2k.study.model.privacy_control.PrivacyControlManager;
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
        listViewHolder.textInListView.setText(models.get(position).getOperation().getName());
        listViewHolder.imageInListView.setImageDrawable(getIconFromApplication(models.get(position).getOperation().getIcon()));
        if(models.get(position).getOperation().getId().equals(ModelManager.MODEL_PRIVACY)){
            PrivacyControlManager privacyControlManager=(PrivacyControlManager)models.get(position);
            if(privacyControlManager.getStatus().getStatusCode()== Status.PRIVACY_ACTIVE){
                long remainingTime=privacyControlManager.getPrivacyData().getStartTimeStamp()+privacyControlManager.getPrivacyData().getDuration().getValue()- DateTime.getDateTime();
                if(remainingTime>0) {
                    remainingTime/=1000;
                    int sec= (int) (remainingTime%60);
                    int min= (int) (remainingTime/60);
                    listViewHolder.imageInListView.setImageDrawable(getIconFromApplication("ic_lock_red_48dp"));
                    listViewHolder.textInListView.setText("Privacy\n" + String.format("%02d:%02d", min, sec));
                }
            }
        }
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