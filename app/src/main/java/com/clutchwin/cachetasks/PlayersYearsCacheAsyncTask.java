package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersYearsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersYearsCacheAsyncTask extends AsyncTask<Void, Void, List<PlayersYearsViewModel.Year>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersYearsCacheAsyncTask() {}

    @Override
    protected List<PlayersYearsViewModel.Year> doInBackground(Void... params) {

        List<PlayersYearsViewModel.Year> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.PY_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersYearsViewModel.Year>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("PlayersYearsCache", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersYearsCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersYearsViewModel.Year> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onPlayersYearsCacheFailure();
            } else {
                onCompleteListener.onPlayersYearsCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersYearsCacheComplete(List<PlayersYearsViewModel.Year> result);
        public void onPlayersYearsCacheFailure();
    }
}

