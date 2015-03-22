package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class TeamsDrillDownCacheAsyncTask extends AsyncTask<Void, Void, List<TeamsDrillDownViewModel.TeamsDrillDown>> {

    private OnLoadCompleteListener onCompleteListener;

    public TeamsDrillDownCacheAsyncTask() {}

    @Override
    protected List<TeamsDrillDownViewModel.TeamsDrillDown> doInBackground(Void... params) {

        List<TeamsDrillDownViewModel.TeamsDrillDown> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.TDD_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<TeamsDrillDownViewModel.TeamsDrillDown>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("TeamsDrillDownCache", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsDrillDownCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<TeamsDrillDownViewModel.TeamsDrillDown> result) {

        if(onCompleteListener != null) {
            if(result == null) {
                onCompleteListener.onTeamsDrillDownCacheFailure();
            } else {
                onCompleteListener.onTeamsDrillDownCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsDrillDownCacheComplete(List<TeamsDrillDownViewModel.TeamsDrillDown> result);
        public void onTeamsDrillDownCacheFailure();
    }
}

