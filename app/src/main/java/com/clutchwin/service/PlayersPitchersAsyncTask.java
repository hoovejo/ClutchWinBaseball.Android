package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.ClutchWinApplication;
import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;
import com.crittercism.app.Crittercism;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class PlayersPitchersAsyncTask extends AsyncTask<Void, Void, List<PlayersPitchersViewModel.Pitcher>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersPitchersAsyncTask() {}

    @Override
    protected List<PlayersPitchersViewModel.Pitcher> doInBackground(Void... params) {

        List<PlayersPitchersViewModel.Pitcher> list = null;

        try {
            //http://clutchwin.com/api/v1/opponents/pitchers.json?
            final String baseUrl = Config.OpponentsForBatter;
            StringBuilder finalUrl;
            finalUrl = new StringBuilder(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.BatterIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getBatterId())
                    .append(Config.SeasonIdKey)
                    .append(ClutchWinApplication.getPlayersContextViewModel().getYearId())
                    .append(Config.FieldSetBasicKeyValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            list = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersPitchersViewModel.Pitcher[].class));

            /*
            try {
                if(list != null && list.size() > 0) {
                    Helpers.writeListToInternalStorage(list, Config.PP_CacheFileKey);
                } else {
                    Helpers.deleteCacheFile(Config.PP_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersPitchersAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }
            */

        } catch (Exception e) {
            Log.e("PlayersPitchersCache", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersPitcherServiceFailure(e);
            }

            Crittercism.logHandledException(e);
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersPitchersViewModel.Pitcher> result) {

        if(onCompleteListener != null){
            onCompleteListener.onPlayersPitcherServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersPitcherServiceComplete(List<PlayersPitchersViewModel.Pitcher> result);
        public void onPlayersPitcherServiceFailure(Exception e);
    }
}


