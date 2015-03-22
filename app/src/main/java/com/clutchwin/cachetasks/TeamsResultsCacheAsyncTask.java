package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsResultsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class TeamsResultsCacheAsyncTask extends AsyncTask<Void, Void, List<TeamsResultsViewModel.TeamsResult>> {

    private OnLoadCompleteListener onCompleteListener;

    public TeamsResultsCacheAsyncTask() {}

    @Override
    protected List<TeamsResultsViewModel.TeamsResult> doInBackground(Void... params) {

        List<TeamsResultsViewModel.TeamsResult> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.TR_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<TeamsResultsViewModel.TeamsResult>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("TeamsResultsCache", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsResultsCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<TeamsResultsViewModel.TeamsResult> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onTeamsResultsCacheFailure();
            } else {
                onCompleteListener.onTeamsResultsCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsResultsCacheComplete(List<TeamsResultsViewModel.TeamsResult> result);
        public void onTeamsResultsCacheFailure();
    }
}

