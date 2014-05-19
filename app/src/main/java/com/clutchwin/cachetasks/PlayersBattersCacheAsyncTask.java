package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersBattersCacheAsyncTask extends AsyncTask<Void, Void, List<PlayersBattersViewModel.Batter>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersBattersCacheAsyncTask(){}

    @Override
    protected List<PlayersBattersViewModel.Batter> doInBackground(Void... params) {

        List<PlayersBattersViewModel.Batter> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.PB_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersBattersViewModel.Batter>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("PlayersBattersCacheAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersBattersCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersBattersViewModel.Batter> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onPlayersBattersCacheFailure();
            } else {
                onCompleteListener.onPlayersBattersCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersBattersCacheComplete(List<PlayersBattersViewModel.Batter> result);
        public void onPlayersBattersCacheFailure();
    }
}

