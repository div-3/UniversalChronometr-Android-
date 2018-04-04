package com.d.ivan.universalchronometer.AddNewActivity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.ivan.universalchronometer.R;

//RecyclerView Holder

public class IntervalViewHolderForAddNew extends RecyclerView.ViewHolder {
    public LinearLayout linearLayout;
    public ImageButton intervalMenu;
    public TextView counter;
    public EditText intervalTitle;
    public EditText intervalDurationHours;
    public EditText intervalDurationMinutes;
    public EditText intervalDurationSeconds;
    public ImageButton intervalDirection;
    public ImageButton intervalAlert;
    public ImageButton intervalGoToTheNext;


    public IntervalViewHolderForAddNew(View itemView) {
        super(itemView);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.add_new_recycler_linear_layout);
        intervalMenu = (ImageButton) itemView.findViewById(R.id.add_new_item_menu);
        counter = (TextView) itemView.findViewById(R.id.add_new_tv_number);
        intervalTitle = (EditText) itemView.findViewById(R.id.add_new_interval_title);
        intervalDurationHours = (EditText) itemView.findViewById(R.id.add_new_interval_hour);
        intervalDurationMinutes = (EditText) itemView.findViewById(R.id.add_new_interval_minute);
        intervalDurationSeconds = (EditText) itemView.findViewById(R.id.add_new_interval_second);
        intervalDirection = (ImageButton) itemView.findViewById(R.id.add_new_ib_direction);
        intervalAlert = (ImageButton) itemView.findViewById(R.id.add_new_ib_alarm);
        intervalGoToTheNext = (ImageButton) itemView.findViewById(R.id.add_new_ib_go_to_the_next);
    }
}
