package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.clutchwin.viewmodels.PlayersContextViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersBattersAsyncTask extends AsyncTask<Void, Void, List<PlayersBattersViewModel.Batter>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;

    public PlayersBattersAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
    }

    @Override
    protected List<PlayersBattersViewModel.Batter> doInBackground(Void... params) {

        List<PlayersBattersViewModel.Batter> batterList = null;

        try {
            //http://clutchwin.com/api/v1/players.json?
            final String baseUrl = Config.RosterSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.TeamIdKey).append(playersContextViewModel.getTeamId())
                    .append(Config.SeasonIdKey).append(playersContextViewModel.getYearId());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            batterList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersBattersViewModel.Batter[].class));

            try {
                if(batterList != null && batterList.size() > 0) {
                    Helpers.writeListToInternalStorage(batterList, context, Config.PB_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersBattersAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersBattersAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onBatterServiceFailure();
            }

            context = null;
        }
        return batterList;
    }

    @Override
    protected void onPostExecute(List<PlayersBattersViewModel.Batter> result) {

        if(onCompleteListener != null){
            onCompleteListener.onBatterServiceComplete(result);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onBatterServiceComplete(List<PlayersBattersViewModel.Batter> result);
        public void onBatterServiceFailure();
    }
}

