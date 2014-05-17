package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersResultsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersResultsAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;
    private PlayersResultsViewModel playersResultsViewModel;

    public PlayersResultsAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel,
                                   PlayersResultsViewModel inViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
        playersResultsViewModel = inViewModel;
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

            playersResultsViewModel.setIsBusy(true);
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
            List<PlayersResultsViewModel.PlayersResult> playersResults;
            playersResults = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersResultsViewModel.PlayersResult[].class));

            try {
                Helpers.writeListToInternalStorage(playersResults, context, playersResultsViewModel.CacheFileKey);
            } catch (IOException e) {
                Log.e("PlayersResultsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

            playersResultsViewModel.updateList(playersResults);

        } catch (Exception e) {
            Log.e("PlayersResultsAsyncTask::doInBackground", e.getMessage(), e);
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

        playersResultsViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}
