package com.clutchwin.cachetasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class PlayersBattersCacheAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersBattersViewModel playersBattersViewModel;

    public PlayersBattersCacheAsyncTask(Context inContext, PlayersBattersViewModel inViewModel){
        context = inContext;
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
        Object outObject;
        try {

            playersBattersViewModel.setIsBusy(true);

            outObject = Helpers.readObjectFromInternalStorage(context, playersBattersViewModel.CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<PlayersBattersViewModel.Batter>>(){}.getType();
            List<PlayersBattersViewModel.Batter> list = gson.fromJson(jsonArray.toString(), listType);
            playersBattersViewModel.updateList(list);

        } catch (Exception e) {
            Log.e("PlayersBattersCacheAsyncTask::doInBackground", e.getMessage(), e);
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

