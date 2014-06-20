package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.ClutchWinApplication;
import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class PlayersDrillDownAsyncTask extends AsyncTask<Void, Void, List<PlayersDrillDownViewModel.PlayersDrillDown>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersDrillDownAsyncTask() {}

    @Override
    protected List<PlayersDrillDownViewModel.PlayersDrillDown> doInBackground(Void... params) {

        List<PlayersDrillDownViewModel.PlayersDrillDown> list = null;

        try {

            //http://clutchwin.com/api/v1/events/summary.json?
            //&access_token=abc&bat_id=aybae001&pit_id=parkj001&season=2013&group=game_date
            final String baseUrl = Config.PlayerPlayerYearSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.BatterIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getBatterId())
                    .append(Config.PitcherIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getPitcherId())
                    .append(Config.SeasonIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getResultYearId())
                    .append(Config.GroupGameDateKeyValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            list = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersDrillDownViewModel.PlayersDrillDown[].class));

            /*
            try {
                if(list != null && list.size() > 0) {
                    Helpers.writeListToInternalStorage(list, Config.PDD_CacheFileKey);
                } else {
                    Helpers.deleteCacheFile(Config.PDD_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersDrillDownCacheAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }
            */

        } catch (Exception e) {
            Log.e("PlayersDrillDownCacheAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayerDrillDownServiceFailure();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersDrillDownViewModel.PlayersDrillDown> result) {

        if(onCompleteListener != null){
            onCompleteListener.onPlayerDrillDownServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayerDrillDownServiceComplete(List<PlayersDrillDownViewModel.PlayersDrillDown> result);
        public void onPlayerDrillDownServiceFailure();
    }
}

