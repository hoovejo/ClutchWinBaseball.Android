package com.clutchwin.cachetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersTeamsCacheAsyncTask extends AsyncTask<Void, Void, List<PlayersTeamsViewModel.Team>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersTeamsCacheAsyncTask() {}

    @Override
    protected List<PlayersTeamsViewModel.Team> doInBackground(Void... params) {

        List<PlayersTeamsViewModel.Team> list = null;
        Object outObject;

        try {

            outObject = Helpers.readObjectFromInternalStorage(Config.PT_CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersTeamsViewModel.Team>>(){}.getType();
            list = gson.fromJson(jsonArray.toString(), listType);

        } catch (Exception e) {
            Log.e("PlayersTeamsCache", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersTeamsCacheFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersTeamsViewModel.Team> result) {

        if(onCompleteListener != null){
            if(result == null) {
                onCompleteListener.onPlayersTeamsCacheFailure();
            } else {
                onCompleteListener.onPlayersTeamsCacheComplete(result);
            }
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersTeamsCacheComplete(List<PlayersTeamsViewModel.Team> list);
        public void onPlayersTeamsCacheFailure();
    }
}

