package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.ClutchWinApplication;
import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class PlayersTeamsAsyncTask extends AsyncTask<Void, Void, List<PlayersTeamsViewModel.Team>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersTeamsAsyncTask() {}

    @Override
    protected List<PlayersTeamsViewModel.Team> doInBackground(Void... params) {

        List<PlayersTeamsViewModel.Team> list = null;

        try {

            //http://clutchwin.com/api/v1/teams.json?
            final String baseUrl = Config.Teams;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.SeasonIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getYearId());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            list = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersTeamsViewModel.Team[].class));

            /*
            try {
                if(list != null && list.size() > 0 ) {
                    Helpers.writeListToInternalStorage(list, Config.PT_CacheFileKey);
                } else {
                    Helpers.deleteCacheFile(Config.PT_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersTeamsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }
            */

        } catch (Exception e) {
            Log.e("PlayersTeamsAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersTeamsServiceFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersTeamsViewModel.Team> result) {
        if(onCompleteListener != null){
            onCompleteListener.onPlayersTeamsServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersTeamsServiceComplete(List<PlayersTeamsViewModel.Team> result);
        public void onPlayersTeamsServiceFailure();
    }
}
