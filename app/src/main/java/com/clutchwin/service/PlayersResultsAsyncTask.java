package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersResultsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersResultsAsyncTask extends AsyncTask<Void, Void, List<PlayersResultsViewModel.PlayersResult>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;

    public PlayersResultsAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
    }

    @Override
    protected List<PlayersResultsViewModel.PlayersResult> doInBackground(Void... params) {

        List<PlayersResultsViewModel.PlayersResult> playersResults = null;

        try {

            //http://clutchwin.com/api/v1/events/summary.json?
            //&access_token=abc&bat_id=aybae001&pit_id=parkj001&group=season
            final String baseUrl = Config.PlayerPlayerSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.BatterIdKey).append(playersContextViewModel.getBatterId())
                    .append(Config.PitcherIdKey).append(playersContextViewModel.getPitcherId())
                    .append(Config.GroupSeasonKeyValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            playersResults = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersResultsViewModel.PlayersResult[].class));

            try {
                if(playersResults != null && playersResults.size() > 0) {
                    Helpers.writeListToInternalStorage(playersResults, context, Config.PR_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersResultsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersResultsAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersResultsServiceFailure();
            }

            context = null;
        }
        return playersResults;
    }

    @Override
    protected void onPostExecute(List<PlayersResultsViewModel.PlayersResult> results) {

        if(onCompleteListener != null){
            onCompleteListener.onPlayersResultsServiceComplete(results);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersResultsServiceComplete(List<PlayersResultsViewModel.PlayersResult> results);
        public void onPlayersResultsServiceFailure();
    }
}
