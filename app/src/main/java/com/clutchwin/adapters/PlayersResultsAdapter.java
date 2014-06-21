package com.clutchwin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clutchwin.R;
import com.clutchwin.viewmodels.PlayersResultsViewModel;

import java.text.NumberFormat;
import java.util.List;

public class PlayersResultsAdapter extends BaseAdapter {

    private final Context context;
    private int layoutResourceId;
    private List<PlayersResultsViewModel.PlayersResult> values;

    public PlayersResultsAdapter(Context context, int layoutResourceId, List<PlayersResultsViewModel.PlayersResult> values) {
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
            if (convertView != null) {
                holder.elementOne = (TextView)convertView.findViewById(R.id.textYear);
                holder.elementTwo = (TextView)convertView.findViewById(R.id.textGames);
                holder.elementThree = (TextView)convertView.findViewById(R.id.textAtBat);
                holder.elementFour = (TextView)convertView.findViewById(R.id.textHit);
                holder.elementFive = (TextView)convertView.findViewById(R.id.textWalk);
                holder.elementSix = (TextView)convertView.findViewById(R.id.textStrikeOut);
                holder.elementSeven = (TextView)convertView.findViewById(R.id.textSecondBase);
                holder.elementEight = (TextView)convertView.findViewById(R.id.textThirdBase);
                holder.elementNine = (TextView)convertView.findViewById(R.id.textHomeRun);
                holder.elementTen = (TextView)convertView.findViewById(R.id.textRunBattedIn);
                holder.elementEleven = (TextView)convertView.findViewById(R.id.textAverage);
                convertView.setTag(holder);
            }
        }
        else
        {
            holder = (PerformanceViewElementHolder)convertView.getTag();
        }

        PlayersResultsViewModel.PlayersResult resultRow = values.get(position);
        holder.elementOne.setText(resultRow.getYear());
        holder.elementTwo.setText(resultRow.getGames().toString());
        holder.elementThree.setText(resultRow.getAtBat().toString());
        holder.elementFour.setText(resultRow.getHit().toString());
        holder.elementFive.setText(resultRow.getWalks().toString());
        holder.elementSix.setText(resultRow.getStrikeOut().toString());
        holder.elementSeven.setText(resultRow.getSecondBase().toString());
        holder.elementEight.setText(resultRow.getThirdBase().toString());
        holder.elementNine.setText(resultRow.getHomeRun().toString());
        holder.elementTen.setText(resultRow.getRunBattedIn().toString());

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(3);
        numberFormat.setMinimumFractionDigits(3);
        String averageAsString = numberFormat.format(resultRow.getAverage().doubleValue());

        holder.elementEleven.setText(averageAsString);

        return convertView;
    }

    static class PerformanceViewElementHolder
    {
        public TextView elementOne;
        public TextView elementTwo;
        public TextView elementThree;
        public TextView elementFour;
        public TextView elementFive;
        public TextView elementSix;
        public TextView elementSeven;
        public TextView elementEight;
        public TextView elementNine;
        public TextView elementTen;
        public TextView elementEleven;
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
