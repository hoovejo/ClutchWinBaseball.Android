package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersPitchersCacheAsyncTask extends AsyncTask<Void, Void, List<PlayersPitchersViewModel.Pitcher> > {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersPitchersCacheAsyncTask() {}

    @Override
    protected List<PlayersPitchersViewModel.Pitcher> doInBackground(Void... params) {

        List<PlayersPitchersViewModel.Pitcher> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.PP_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersPitchersViewModel.Pitcher>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("PlayersPitchersCacheAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersPitchersCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersPitchersViewModel.Pitcher> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onPlayersPitchersCacheFailure();
            } else {
                onCompleteListener.onPlayersPitchersCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersPitchersCacheComplete(List<PlayersPitchersViewModel.Pitcher> list);
        public void onPlayersPitchersCacheFailure();
    }
}
