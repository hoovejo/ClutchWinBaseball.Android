package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class PlayersPitchersAsyncTask extends AsyncTask<Void, Void, PlayersPitchersViewModel.PitchersResult> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;

    public PlayersPitchersAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
    }

    @Override
    protected PlayersPitchersViewModel.PitchersResult doInBackground(Void... params) {

        PlayersPitchersViewModel.PitchersResult result = null;

        try {

            //"http://versus.skeenshare.com/search/opponents_for_batter/aybae001/2013.json";
            final String baseUrl = Config.OpponentsForBatter;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(playersContextViewModel.getBatterId())
                    .append(Config.Slash).append(playersContextViewModel.getYearId())
                    .append(Config.JsonSuffix);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            result = restTemplate.getForObject(finalUrl.toString(), PlayersPitchersViewModel.PitchersResult.class);

            try {
                if(result.rows != null && result.rows.size() > 0) {
                    Helpers.writeListToInternalStorage(result.rows, context, Config.PR_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersPitchersAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersPitchersAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersPitcherServiceFailure();
            }

            context = null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(PlayersPitchersViewModel.PitchersResult result) {

        if(onCompleteListener != null){
            onCompleteListener.onPlayersPitcherServiceComplete(result);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersPitcherServiceComplete(PlayersPitchersViewModel.PitchersResult result);
        public void onPlayersPitcherServiceFailure();
    }
}


