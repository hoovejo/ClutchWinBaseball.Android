package com.clutchwin.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.clutchwin.ClutchWinApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Helpers {

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    // private - already passed in the app context
    private static void writeObjectInInternalStorage(Context context, String filename, Object object) throws IOException {
        FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    public static Object readObjectFromInternalStorage(String filename) throws IOException, ClassNotFoundException{
        Context appContext = ClutchWinApplication.getInstance().getApplicationContext();
        FileInputStream fileInputStream = appContext.openFileInput(filename);
        return new ObjectInputStream(fileInputStream).readObject();
    }

    public static void updateFileState(Object object, Context context, String fileName) throws IOException{
        Context appContext = context.getApplicationContext();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(object);
        Helpers.writeObjectInInternalStorage(appContext, fileName, json);
    }

    public static void writeListToInternalStorage(Object object, String fileName) throws IOException{
        Context appContext = ClutchWinApplication.getInstance().getApplicationContext();
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(object).getAsJsonArray();
        Helpers.writeObjectInInternalStorage(appContext, fileName, jsonArray.toString());
    }

    public static boolean checkFileExists(Context context, String fileName){

        Context appContext = context.getApplicationContext();
        boolean returnValue = false;
        String[] files = appContext.fileList();
        if (files != null && files.length > 0){
            for (String file : files) {
                if (file != null && file.contains(fileName)){
                    returnValue = true;
                    break;
                }
            }
        }
        return returnValue;
    }

    //called from ClutchWinApplication passing in the app context from UnhandledException handler
    // injected using the decorator pattern
    public static void purgeAllCacheFiles(Context context){

        context.deleteFile(Config.TC_CacheFileKey);
        context.deleteFile(Config.TF_CacheFileKey);
        context.deleteFile(Config.TR_CacheFileKey);
        context.deleteFile(Config.TDD_CacheFileKey);

        context.deleteFile(Config.PC_CacheFileKey);
        context.deleteFile(Config.PY_CacheFileKey);
        context.deleteFile(Config.PT_CacheFileKey);
        context.deleteFile(Config.PB_CacheFileKey);
        context.deleteFile(Config.PP_CacheFileKey);
        context.deleteFile(Config.PR_CacheFileKey);
        context.deleteFile(Config.PDD_CacheFileKey);

        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_FILE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Config.TEAMS_SELECTED_NAVIGATION_ITEM, String.valueOf(999));
        editor.putString(Config.PLAYERS_SELECTED_NAVIGATION_ITEM, String.valueOf(999));
        editor.commit();

    }
}
