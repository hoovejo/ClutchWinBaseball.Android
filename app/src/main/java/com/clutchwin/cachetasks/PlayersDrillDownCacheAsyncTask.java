package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersDrillDownCacheAsyncTask extends AsyncTask<Void, Void, List<PlayersDrillDownViewModel.PlayersDrillDown>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersDrillDownCacheAsyncTask() {}

    @Override
    protected List<PlayersDrillDownViewModel.PlayersDrillDown> doInBackground(Void... params) {

        List<PlayersDrillDownViewModel.PlayersDrillDown> list = null;
        Object outObject;

        try {
            outObject = Helpers.readObjectFromInternalStorage(Config.PDD_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersDrillDownViewModel.PlayersDrillDown>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);
        } catch (Exception e) {
            Log.e("PlayersDrillDownCacheAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersDrillDownCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersDrillDownViewModel.PlayersDrillDown> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onPlayersDrillDownCacheFailure();
            } else {
                onCompleteListener.onPlayersDrillDownCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersDrillDownCacheComplete(List<PlayersDrillDownViewModel.PlayersDrillDown> list);
        public void onPlayersDrillDownCacheFailure();
    }
}
