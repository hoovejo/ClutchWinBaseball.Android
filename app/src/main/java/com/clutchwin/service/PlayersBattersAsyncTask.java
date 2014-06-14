package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.ClutchWinApplication;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersBattersViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersBattersAsyncTask extends AsyncTask<Void, Void, List<PlayersBattersViewModel.Batter>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersBattersAsyncTask() {}

    @Override
    protected List<PlayersBattersViewModel.Batter> doInBackground(Void... params) {

        List<PlayersBattersViewModel.Batter> list = null;

        try {
            //http://clutchwin.com/api/v1/players.json?
            final String baseUrl = Config.RosterSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.TeamIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getTeamId())
                    .append(Config.SeasonIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getYearId());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            list = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersBattersViewModel.Batter[].class));

            try {
                if(list != null && list.size() > 0) {
                    Helpers.writeListToInternalStorage(list, Config.PB_CacheFileKey);
                } else {
                    Helpers.deleteCacheFile(Config.PB_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersBattersAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersBattersAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersBattersServiceFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersBattersViewModel.Batter> result) {

        if(onCompleteListener != null){
            onCompleteListener.onPlayersBattersServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersBattersServiceComplete(List<PlayersBattersViewModel.Batter> result);
        public void onPlayersBattersServiceFailure();
    }
}

