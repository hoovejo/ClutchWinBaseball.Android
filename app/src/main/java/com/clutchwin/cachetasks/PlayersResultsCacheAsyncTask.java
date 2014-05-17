package com.clutchwin.cachetasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersResultsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersResultsCacheAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersResultsViewModel playersResultsViewModel;

    public PlayersResultsCacheAsyncTask(Context inContext, PlayersResultsViewModel inViewModel){
        context = inContext;
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
        Object outObject;
        try {

            playersResultsViewModel.setIsBusy(true);

            outObject = Helpers.readObjectFromInternalStorage(context, playersResultsViewModel.CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersResultsViewModel.PlayersResult>>(){}.getType();
            List<PlayersResultsViewModel.PlayersResult> list = gson.fromJson(jsonArray.toString(), listType);
            playersResultsViewModel.updateList(list);

        } catch (Exception e) {
            Log.e("PlayersResultsCacheAsyncTask::doInBackground", e.getMessage(), e);
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
