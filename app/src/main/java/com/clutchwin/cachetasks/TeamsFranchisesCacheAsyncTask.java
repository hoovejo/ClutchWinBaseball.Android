package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class TeamsFranchisesCacheAsyncTask extends AsyncTask<Void, Void, List<TeamsFranchisesViewModel.Franchise>> {

    private OnLoadCompleteListener onCompleteListener;

    public TeamsFranchisesCacheAsyncTask() {}

    @Override
    protected List<TeamsFranchisesViewModel.Franchise> doInBackground(Void... params) {

        List<TeamsFranchisesViewModel.Franchise> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.TF_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<TeamsFranchisesViewModel.Franchise>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("TeamsFranchisesCacheAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsFranchisesCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<TeamsFranchisesViewModel.Franchise> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onTeamsFranchisesCacheFailure();
            } else {
                onCompleteListener.onTeamsFranchisesCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsFranchisesCacheComplete(List<TeamsFranchisesViewModel.Franchise> result);
        public void onTeamsFranchisesCacheFailure();
    }
}

