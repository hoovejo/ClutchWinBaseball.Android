package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class PlayersPitchersAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;
    private PlayersPitchersViewModel playersPitchersViewModel;

    public PlayersPitchersAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel,
                                    PlayersPitchersViewModel inViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
        playersPitchersViewModel = inViewModel;
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

            playersPitchersViewModel.setIsBusy(true);
            //"http://versus.skeenshare.com/search/opponents_for_batter/aybae001/2013.json";
            final String baseUrl = Config.OpponentsForBatter;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(playersContextViewModel.getBatterId())
                    .append(Config.Slash).append(playersContextViewModel.getYearId())
                    .append(Config.JsonSuffix);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            PlayersPitchersViewModel.PitchersResult result = restTemplate.getForObject(finalUrl.toString(), PlayersPitchersViewModel.PitchersResult.class);

            try {
                Helpers.writeListToInternalStorage(result.rows, context, Config.PR_CacheFileKey);
            } catch (IOException e) {
                Log.e("PlayersPitchersAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

            playersPitchersViewModel.updateList(result.rows);

        } catch (Exception e) {
            Log.e("PlayersPitchersAsyncTask::doInBackground", e.getMessage(), e);
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

        playersPitchersViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}


