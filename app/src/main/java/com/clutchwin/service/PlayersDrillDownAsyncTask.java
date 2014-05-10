package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class PlayersDrillDownAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersDrillDownViewModel viewModel;
    private String batterId;
    private String pitcherId;
    private String yearId;
    private String gameType;

    public PlayersDrillDownAsyncTask(Context inContext, PlayersDrillDownViewModel inViewModel,
                                   String inBatterId, String inPitcherId, String inYearId, String inGameType){
        context = inContext;
        viewModel = inViewModel;
        batterId = inBatterId;
        pitcherId = inPitcherId;
        yearId = inYearId;
        gameType = inGameType;
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
            //"http://versus.skeenshare.com/search/player_vs_player_by_year/aybae001/parkj001/2013/regular.json";
            final String baseUrl = Config.PlayerPlayerYearSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(batterId)
                    .append(Config.Slash).append(pitcherId)
                    .append(Config.Slash).append(yearId)
                    .append(Config.Slash).append(gameType)
                    .append(Config.JsonSuffix);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            PlayersDrillDownViewModel.PlayersDrillDown result = restTemplate.getForObject(finalUrl.toString(), PlayersDrillDownViewModel.PlayersDrillDown.class);
            viewModel.updateList(result.rows);
        } catch (Exception e) {
            Log.e("PlayersDrillDownAsyncTask", e.getMessage(), e);
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
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}

