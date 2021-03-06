package com.clutchwin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clutchwin.R;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;

import java.text.NumberFormat;
import java.util.List;

public class PlayersDrillDownAdapter extends BaseAdapter {

    private final Context context;
    private int layoutResourceId;
    private List<PlayersDrillDownViewModel.PlayersDrillDown> values;

    public PlayersDrillDownAdapter(Context context, int layoutResourceId, List<PlayersDrillDownViewModel.PlayersDrillDown> values) {
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
                holder.elementOne = (TextView)convertView.findViewById(R.id.textGameDate);
                holder.elementTwo = (TextView)convertView.findViewById(R.id.textAtBat);
                holder.elementThree = (TextView)convertView.findViewById(R.id.textHit);
                holder.elementFour = (TextView)convertView.findViewById(R.id.textWalk);
                holder.elementFive = (TextView)convertView.findViewById(R.id.textStrikeOut);
                holder.elementSix = (TextView)convertView.findViewById(R.id.textSecondBase);
                holder.elementSeven = (TextView)convertView.findViewById(R.id.textThirdBase);
                holder.elementEight = (TextView)convertView.findViewById(R.id.textHomeRun);
                holder.elementNine = (TextView)convertView.findViewById(R.id.textRunBattedIn);
                holder.elementTen = (TextView)convertView.findViewById(R.id.textAverage);

                convertView.setTag(holder);
            }
        }
        else
        {
            holder = (PerformanceViewElementHolder)convertView.getTag();
        }

        PlayersDrillDownViewModel.PlayersDrillDown resultRow = values.get(position);
        holder.elementOne.setText(resultRow.getGameDate());
        holder.elementTwo.setText(resultRow.getAtBat().toString());
        holder.elementThree.setText(resultRow.getHit().toString());
        holder.elementFour.setText(resultRow.getWalks().toString());
        holder.elementFive.setText(resultRow.getStrikeOut().toString());
        holder.elementSix.setText(resultRow.getSecondBase().toString());
        holder.elementSeven.setText(resultRow.getThirdBase().toString());
        holder.elementEight.setText(resultRow.getHomeRun().toString());
        holder.elementNine.setText(resultRow.getRunBattedIn().toString());

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(3);
        numberFormat.setMinimumFractionDigits(3);
        String averageAsString = numberFormat.format(resultRow.getAverage().doubleValue());

        holder.elementTen.setText(averageAsString.replaceFirst("^0+(?!$)", ""));

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
