package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersResultsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersResultsCacheAsyncTask extends AsyncTask<Void, Void, List<PlayersResultsViewModel.PlayersResult>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersResultsCacheAsyncTask() {}

    @Override
    protected List<PlayersResultsViewModel.PlayersResult> doInBackground(Void... params) {

        List<PlayersResultsViewModel.PlayersResult> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.PR_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersResultsViewModel.PlayersResult>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("PlayersResultsCache", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersResultsCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersResultsViewModel.PlayersResult> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onPlayersResultsCacheFailure();
            } else {
                onCompleteListener.onPlayersResultsCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersResultsCacheComplete(List<PlayersResultsViewModel.PlayersResult> list);
        public void onPlayersResultsCacheFailure();
    }
}
