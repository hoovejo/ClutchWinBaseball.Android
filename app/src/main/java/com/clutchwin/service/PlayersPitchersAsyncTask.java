package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.ClutchWinApplication;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

public class PlayersPitchersAsyncTask extends AsyncTask<Void, Void, List<PlayersPitchersViewModel.Row>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersPitchersAsyncTask() {}

    @Override
    protected List<PlayersPitchersViewModel.Row> doInBackground(Void... params) {

        PlayersPitchersViewModel.PitchersResult result = null;

        try {

            //"http://versus.skeenshare.com/search/opponents_for_batter/aybae001/2013.json";
            final String baseUrl = Config.OpponentsForBatter;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(ClutchWinApplication.getPlayersContextViewModel().getBatterId())
                    .append(Config.Slash)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getYearId())
                    .append(Config.JsonSuffix);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            result = restTemplate.getForObject(finalUrl.toString(), PlayersPitchersViewModel.PitchersResult.class);

            try {
                if(result.rows != null && result.rows.size() > 0) {
                    Helpers.writeListToInternalStorage(result.rows, Config.PP_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersPitchersAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersPitchersAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersPitcherServiceFailure();
            }
        }
        return result.rows;
    }

    @Override
    protected void onPostExecute(List<PlayersPitchersViewModel.Row> result) {

        if(onCompleteListener != null){
            onCompleteListener.onPlayersPitcherServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersPitcherServiceComplete(List<PlayersPitchersViewModel.Row> result);
        public void onPlayersPitcherServiceFailure();
    }
}


