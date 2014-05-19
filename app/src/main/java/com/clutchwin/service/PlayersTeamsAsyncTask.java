package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersTeamsAsyncTask extends AsyncTask<Void, Void, List<PlayersTeamsViewModel.Team>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;

    public PlayersTeamsAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
    }

    @Override
    protected List<PlayersTeamsViewModel.Team> doInBackground(Void... params) {

        List<PlayersTeamsViewModel.Team> teamList = null;

        try {

            //http://clutchwin.com/api/v1/teams.json?
            final String baseUrl = Config.Teams;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.SeasonIdKey).append(playersContextViewModel.getYearId());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            teamList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersTeamsViewModel.Team[].class));

            try {
                if(teamList != null && teamList.size() > 0 ) {
                    Helpers.writeListToInternalStorage(teamList, context, Config.PT_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersTeamsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersTeamsAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersTeamsServiceFailure();
            }

            context = null;
        }
        return teamList;
    }

    @Override
    protected void onPostExecute(List<PlayersTeamsViewModel.Team> results) {
        if(onCompleteListener != null){
            onCompleteListener.onPlayersTeamsServiceComplete(results);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersTeamsServiceComplete(List<PlayersTeamsViewModel.Team> results);
        public void onPlayersTeamsServiceFailure();
    }
}
