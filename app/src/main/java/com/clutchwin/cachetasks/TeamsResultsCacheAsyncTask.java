package com.clutchwin.cachetasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsResultsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class TeamsResultsCacheAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsResultsViewModel teamsResultsViewModel;

    public TeamsResultsCacheAsyncTask(Context inContext, TeamsResultsViewModel inViewModel){
        context = inContext;
        teamsResultsViewModel = inViewModel;
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

            teamsResultsViewModel.setIsBusy(true);

            outObject = Helpers.readObjectFromInternalStorage(context, teamsResultsViewModel.CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<TeamsResultsViewModel.TeamsResult>>(){}.getType();
            List<TeamsResultsViewModel.TeamsResult> list = gson.fromJson(jsonArray.toString(), listType);
            teamsResultsViewModel.updateList(list);

        } catch (Exception e) {
            Log.e("TeamsResultsCacheAsyncTask::doInBackground", e.getMessage(), e);
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
        teamsResultsViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}
