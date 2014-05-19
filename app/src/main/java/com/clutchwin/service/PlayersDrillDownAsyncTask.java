package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersDrillDownAsyncTask extends AsyncTask<Void, Void, List<PlayersDrillDownViewModel.PlayersDrillDown>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;

    public PlayersDrillDownAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
    }

    @Override
    protected List<PlayersDrillDownViewModel.PlayersDrillDown> doInBackground(Void... params) {

        List<PlayersDrillDownViewModel.PlayersDrillDown> playersDrillDownResults = null;

        try {

            //http://clutchwin.com/api/v1/events/summary.json?
            //&access_token=abc&bat_id=aybae001&pit_id=parkj001&season=2013&group=game_date
            final String baseUrl = Config.PlayerPlayerYearSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.BatterIdKey).append(playersContextViewModel.getBatterId())
                    .append(Config.PitcherIdKey).append(playersContextViewModel.getPitcherId())
                    .append(Config.SeasonIdKey).append(playersContextViewModel.getResultYearId())
                    .append(Config.GroupGameDateKeyValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            playersDrillDownResults = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersDrillDownViewModel.PlayersDrillDown[].class));

            try {
                if(playersDrillDownResults != null && playersDrillDownResults.size() > 0) {
                    Helpers.writeListToInternalStorage(playersDrillDownResults, context, Config.PDD_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersDrillDownCacheAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersDrillDownCacheAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayerDrillDownServiceFailure();
            }

            context = null;
        }
        return playersDrillDownResults;
    }

    @Override
    protected void onPostExecute(List<PlayersDrillDownViewModel.PlayersDrillDown> results) {

        if(onCompleteListener != null){
            onCompleteListener.onPlayerDrillDownServiceComplete(results);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayerDrillDownServiceComplete(List<PlayersDrillDownViewModel.PlayersDrillDown> results);
        public void onPlayerDrillDownServiceFailure();
    }
}

