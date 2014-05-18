package com.clutchwin.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;
import com.clutchwin.viewmodels.PlayersResultsViewModel;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;
import com.clutchwin.viewmodels.PlayersYearsViewModel;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsResultsViewModel;
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

    private static void writeObjectInInternalStorage(Context context, String filename, Object object) throws IOException {
        FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    public static Object readObjectFromInternalStorage(Context context, String filename) throws IOException, ClassNotFoundException{
        FileInputStream fileInputStream = context.openFileInput(filename);
        return new ObjectInputStream(fileInputStream).readObject();
    }

    public static void updateFileState(Object object, Context context, String fileName) throws IOException{
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(object);
        Helpers.writeObjectInInternalStorage(context, fileName, json);
    }

    public static void writeListToInternalStorage(Object object, Context context, String fileName) throws IOException{
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(object).getAsJsonArray();
        Helpers.writeObjectInInternalStorage(context, fileName, jsonArray.toString());
    }

    public static boolean checkFileExists(Context context, String fileName){
        boolean returnValue = false;
        String[] files = context.fileList();
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

    public static void purgeAllCacheFiles(Context context){

        context.deleteFile(TeamsContextViewModel.CacheFileKey);
        context.deleteFile(TeamsFranchisesViewModel.CacheFileKey);
        context.deleteFile(TeamsResultsViewModel.CacheFileKey);
        context.deleteFile(TeamsDrillDownViewModel.CacheFileKey);

        context.deleteFile(PlayersContextViewModel.CacheFileKey);
        context.deleteFile(PlayersYearsViewModel.CacheFileKey);
        context.deleteFile(PlayersTeamsViewModel.CacheFileKey);
        context.deleteFile(PlayersBattersViewModel.CacheFileKey);
        context.deleteFile(PlayersPitchersViewModel.CacheFileKey);
        context.deleteFile(PlayersResultsViewModel.CacheFileKey);
        context.deleteFile(PlayersDrillDownViewModel.CacheFileKey);

    }
}
