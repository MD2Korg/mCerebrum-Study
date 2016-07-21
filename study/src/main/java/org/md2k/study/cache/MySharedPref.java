package org.md2k.study.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by monowar on 7/20/16.
 */
public class MySharedPref {
    private static MySharedPref instance;
    private Context context;
    SharedPreferences sharedPref;
    public static MySharedPref getInstance(Context context){
        if(instance==null)
            instance=new MySharedPref(context);
        return instance;
    }
    private MySharedPref(Context context){
        this.context=context;
        sharedPref = context.getSharedPreferences("mCerebrum", Context.MODE_PRIVATE);
    }
    public void write(String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String read(String key){
        SharedPreferences.Editor editor = sharedPref.edit();
        return sharedPref.getString(key, null);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();

    }
}
