package com.d.ivan.universalchronometer.MainActivity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.d.ivan.universalchronometer.R;

//-----------------------------------------------------------------------------------------------------
//RecycleView
//ViewHolder - кэширует контролы (findViewById) элементов RecycleView
//-----------------------------------------------------------------------------------------------------

public class IntervalViewHolderForMainActivity extends RecyclerView.ViewHolder {
    public LinearLayout linearLayout;
    public RelativeLayout relativeLayout;
    public TextView counterTextView;
    public TextView titleTextView;
    public TextView durationTextView;
    public ImageView directionImageButton;
    public ImageView alarmImageButton;
    public ImageView goToNextImageButton;
    public ImageButton startSelectedInterval;

    public IntervalViewHolderForMainActivity(View itemView) {
        super(itemView);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.main_list_item);
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.main_relative_layout);
        counterTextView = (TextView) itemView.findViewById(R.id.main_counter_tv);
        titleTextView = (TextView) itemView.findViewById(R.id.main_title_tv);
        durationTextView = (TextView) itemView.findViewById(R.id.main_duration_tv);
        directionImageButton = (ImageView) itemView.findViewById(R.id.main_direction_iv);
        alarmImageButton = (ImageView) itemView.findViewById(R.id.main_alarm_iv);
        goToNextImageButton = (ImageView) itemView.findViewById(R.id.main_go_to_next_iv);
        startSelectedInterval = (ImageButton) itemView.findViewById(R.id.main_start_selected_interval);
    }
}
