package com.clutchwin.cachetasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersTeamsCacheAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersTeamsViewModel playersTeamsViewModel;

    public PlayersTeamsCacheAsyncTask(Context inContext, PlayersTeamsViewModel inViewModel){
        context = inContext;
        playersTeamsViewModel = inViewModel;
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
        Object outObject;
        try {

            playersTeamsViewModel.setIsBusy(true);

            outObject = Helpers.readObjectFromInternalStorage(context, playersTeamsViewModel.CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersTeamsViewModel.Team>>(){}.getType();
            List<PlayersTeamsViewModel.Team> list = gson.fromJson(jsonArray.toString(), listType);
            playersTeamsViewModel.updateList(list);

        } catch (Exception e) {
            Log.e("PlayersTeamsCacheAsyncTask::doInBackground", e.getMessage(), e);
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
        playersTeamsViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}

