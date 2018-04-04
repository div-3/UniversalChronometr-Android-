package com.d.ivan.universalchronometer.MainActivity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.d.ivan.universalchronometer.Common.GlobalValues;
import com.d.ivan.universalchronometer.R;
import com.d.ivan.universalchronometer.Timers.Interval;
import com.d.ivan.universalchronometer.Timers.TimerCounting;

import java.util.ArrayList;
import java.util.List;

//-----------------------------------------------------------------------------------------------------
//RecycleView  Хорошая статья - https://ziginsider.github.io/RecyclerView/
//Adapter
//-----------------------------------------------------------------------------------------------------

public class IntervalAdapter extends RecyclerView.Adapter<IntervalViewHolderForMainActivity>{
    private List<Interval> intervalList;
    private Context context;
    private ArrayList<IntervalViewHolderForMainActivity> recyclerViewArrayList = new ArrayList<>();     //Список активных (видимых и несколько ещё) элементов RecyclerView

    private final String TAG = "RecycleViewAdapter";

    public IntervalAdapter(List<Interval> intervalList, Context context) {
        this.intervalList = intervalList;
        this.context = context;
    }



    //Определяем макет и надуваем одного элемента RecyclerView
    @Override
    public IntervalViewHolderForMainActivity onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_recycler_list_item, parent, false);
        return new IntervalViewHolderForMainActivity(itemView);
    }

    //Метод для заполнения данными надутого макета одного элемента RecycleView
    @Override
    public void onBindViewHolder(final IntervalViewHolderForMainActivity holder, int position) {
        recyclerViewArrayList.add(holder);  //Добавляем создаваемые видимые холдеры в список

        //Задаём значения контроллов на создаваемом элементе стиска RecyclerView надутом макете
        holder.counterTextView.setText(String.valueOf(holder.getAdapterPosition() + 1));
        holder.titleTextView.setText(intervalList.get(holder.getAdapterPosition()).getTitle());
        holder.durationTextView.setText(intervalList.get(holder.getAdapterPosition()).durationToString());

        //Выбор иконки для отображжения Направления отсчёта
        GlobalValues.timerDirection td = intervalList.get(holder.getAdapterPosition()).getDirection();
        if (td.equals(GlobalValues.timerDirection.Backward)){
            holder.directionImageButton.setImageResource(R.drawable.ic_backward_count);
        } else {
            holder.directionImageButton.setImageResource(R.drawable.ic_forward_count);
        }

        //Выбор иконки для отображжения типа Уведомления об окончании
        GlobalValues.timerNotification tn = intervalList.get(holder.getAdapterPosition()).getNotification();
        switch (tn) {
            case Off:
                holder.alarmImageButton.setImageResource(R.drawable.ic_alert_sound);
                break;
            case Sound:
                holder.alarmImageButton.setImageResource(R.drawable.ic_alert_screen);
                break;
            case Screen:
                holder.alarmImageButton.setImageResource(R.drawable.ic_alert_flashlight);
                break;
            case Flashlight:
                holder.alarmImageButton.setImageResource(R.drawable.ic_alert_off);
                break;
        }

        //Выбор иконки для отображжения Автоматического перехода
        GlobalValues.timerGoTOTheNext tg = intervalList.get(holder.getAdapterPosition()).getGoTOTheNext();
        if (tg.equals(GlobalValues.timerGoTOTheNext.Off)){
            holder.goToNextImageButton.setImageResource(R.drawable.ic_go_to_the_next_off);
        } else {
            holder.goToNextImageButton.setImageResource(R.drawable.ic_go_to_the_next_on);
        }

        //Если интервал был запущен ранее, то его надо выделить цветом и очистить все остальные видимые элементы на RecyclerView
        if (holder.getAdapterPosition() == TimerCounting.getTimerCounting().getCurrentIntervalIndex()) {
            updateRecyclerListColor(TimerCounting.getTimerCounting().getCurrentIntervalIndex());
        }

        //Добавление на кнопку Старт обработчика для запуска выбранного интервала из списка
        holder.startSelectedInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerCounting.getTimerCounting().timerStop();
                TimerCounting.getTimerCounting().setCurrentIntervalIndex(holder.getAdapterPosition());
                TimerCounting.getTimerCounting().timerRun();
                updateRecyclerListColor(TimerCounting.getTimerCounting().getCurrentIntervalIndex());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (intervalList != null) {
            return intervalList.size();
        }
        return 0;
    }

    //Метод вызывается при очистке (удалении) элемента RecycleView вышедшего за пределы видимости
    @Override
    public void onViewRecycled(IntervalViewHolderForMainActivity holder) {
        recyclerViewArrayList.remove(holder);
        holder.relativeLayout.setBackgroundColor
                (ContextCompat.getColor(context, R.color.main_recycler_base_layout_color));
        super.onViewRecycled(holder);
    }

    //Обновление цветового выделения всех активных элементов списка (выделенный элемент - серый цвет, все остальные - белый)
    private void updateRecyclerListColor(int selectedItem) {
        for (int i = 0; i < recyclerViewArrayList.size(); i++) {
            if (recyclerViewArrayList.get(i).getAdapterPosition() == selectedItem) {

                recyclerViewArrayList.get(i).relativeLayout.setBackgroundColor
                        (ContextCompat.getColor(context, R.color.main_recycler_selected_interval_layout_color));   //Выделение цветом выбранного элемента списка
            } else {
                recyclerViewArrayList.get(i).relativeLayout.setBackgroundColor
                        (ContextCompat.getColor(context, R.color.transparent));
            }
        }
    }

}
