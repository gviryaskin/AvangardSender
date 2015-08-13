package com.gviryaskin.avangardsender.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gviryaskin.avangardsender.MainActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class History {
    private final static String TAG="history",
                                SPLIT="@";

    public static void saveToHistory(Context aContext, Bundle aBundle){
        SharedPreferences.Editor editor=aContext.getSharedPreferences(TAG,Context.MODE_PRIVATE).edit();
        StringBuilder builder=new StringBuilder();
        builder.append(aBundle.getString(MainActivity.KEY_TO)+SPLIT)
               .append(aBundle.getString(MainActivity.KEY_CUR_COLD) + SPLIT)
               .append(aBundle.getString(MainActivity.KEY_DIF_COLD) + SPLIT)
               .append(aBundle.getString(MainActivity.KEY_CUR_HOT) + SPLIT)
               .append(aBundle.getString(MainActivity.KEY_DIF_HOT) + SPLIT);
        editor.putString(aBundle.getString(MainActivity.KEY_TO),builder.toString()).commit();
    }

    public static ArrayList<Bundle> getHistory(Context aContext){
        SharedPreferences sharedPreferences = aContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        ArrayList<Bundle> list =new ArrayList<Bundle>();
        for(String key: sharedPreferences.getAll().keySet()){
            String value=sharedPreferences.getString(key,"");
            if(value.equals(""))
                continue;
            String[] values =value.split("\\"+SPLIT);
            Bundle bundle = new Bundle();
            int i=0;
            bundle.putString(MainActivity.KEY_TO,values[i++]);
            bundle.putString(MainActivity.KEY_CUR_COLD,values[i++]);
            bundle.putString(MainActivity.KEY_DIF_COLD,values[i++]);
            bundle.putString(MainActivity.KEY_CUR_HOT,values[i++]);
            bundle.putString(MainActivity.KEY_DIF_HOT,values[i++]);
            list.add(bundle);
        }
        Collections.sort(list, new Comparator<Bundle>() {
            @Override
            public int compare(Bundle lhs, Bundle rhs) {
                try {
                    Date ld = Formatter.DATE.parse(lhs.getString(MainActivity.KEY_TO));
                    Date rd = Formatter.DATE.parse(rhs.getString(MainActivity.KEY_TO));
                    return ld.compareTo(rd);
                } catch (ParseException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
        return list;
    }

    //TODO do here gson stuff
    //read from file
    //save to file
    private static void test(){
        Gson gson = new GsonBuilder().setDateFormat(Formatter.DATE.toPattern()).create();
        gson.toJson();
    }


}
