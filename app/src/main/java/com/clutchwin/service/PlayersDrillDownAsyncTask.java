package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersDrillDownAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;
    private PlayersDrillDownViewModel playersDrillDownViewModel;

    public PlayersDrillDownAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel,
                                     PlayersDrillDownViewModel inViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
        playersDrillDownViewModel = inViewModel;
    }

    @Override
    protected void onPreExecute(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            playersDrillDownViewModel.setIsBusy(true);
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
            List<PlayersDrillDownViewModel.PlayersDrillDown> playersDrillDownResults;
            playersDrillDownResults = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersDrillDownViewModel.PlayersDrillDown[].class));

            try {
                Helpers.writeListToInternalStorage(playersDrillDownResults, context, playersDrillDownViewModel.CacheFileKey);
            } catch (IOException e) {
                Log.e("PlayersDrillDownCacheAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

            playersDrillDownViewModel.updateList(playersDrillDownResults);

        } catch (Exception e) {
            Log.e("PlayersDrillDownCacheAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onFailure();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        if(onCompleteListener != null){
            onCompleteListener.onComplete();
        }

        playersDrillDownViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}

