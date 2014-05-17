package com.clutchwin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;

import java.util.List;

public class PlayersTeamsAdapter extends BaseAdapter {
    private final Context context;
    private int layoutResourceId;
    private List<PlayersTeamsViewModel.Team> values;

    public PlayersTeamsAdapter(Context context, int layoutResourceId, List<PlayersTeamsViewModel.Team> values) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PerformanceViewElementHolder holder;
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new PerformanceViewElementHolder();
            holder.elementOne = (TextView)convertView.findViewById(R.id.txtFranchise);
            holder.elementTwo = (TextView)convertView.findViewById(R.id.txtLeague);
            convertView.setTag(holder);
        }
        else
        {
            holder = (PerformanceViewElementHolder)convertView.getTag();
        }

        PlayersTeamsViewModel.Team resultRow = values.get(position);
        holder.elementOne.setText(resultRow.getLocation() + Config.Space + resultRow.getName());
        holder.elementTwo.setText(Config.Space + resultRow.getLeagueId());

        return convertView;
    }

    static class PerformanceViewElementHolder
    {
        public TextView elementOne;
        public TextView elementTwo;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

