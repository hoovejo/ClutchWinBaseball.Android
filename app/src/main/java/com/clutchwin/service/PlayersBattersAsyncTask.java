package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.clutchwin.viewmodels.PlayersContextViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersBattersAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;
    private PlayersBattersViewModel playersBattersViewModel;

    public PlayersBattersAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel,
                                   PlayersBattersViewModel inViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
        playersBattersViewModel = inViewModel;
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

            playersBattersViewModel.setIsBusy(true);
            //http://clutchwin.com/api/v1/players.json?
            final String baseUrl = Config.RosterSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.TeamIdKey).append(playersContextViewModel.getTeamId())
                    .append(Config.SeasonIdKey).append(playersContextViewModel.getYearId());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            List<PlayersBattersViewModel.Batter> batterList;
            batterList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersBattersViewModel.Batter[].class));

            try {
                Helpers.writeListToInternalStorage(batterList, context, Config.PB_CacheFileKey);
            } catch (IOException e) {
                Log.e("PlayersBattersAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

            playersBattersViewModel.updateList(batterList);

        } catch (Exception e) {
            Log.e("PlayersBattersAsyncTask::doInBackground", e.getMessage(), e);
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

        playersBattersViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}

