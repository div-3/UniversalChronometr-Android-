package com.d.ivan.universalchronometer.AddNewActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.d.ivan.universalchronometer.Common.CommonMethods;
import com.d.ivan.universalchronometer.Common.GlobalValues;
import com.d.ivan.universalchronometer.R;
import com.d.ivan.universalchronometer.Timers.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class IntervalAdapterForAddNew extends RecyclerView.Adapter<IntervalViewHolderForAddNew> {

    private List<Interval> intervalList;
    private Context context;
    private ArrayList<IntervalViewHolderForAddNew> recyclerViewArrayList = new ArrayList<>();     //Список активных (видимых и несколько ещё) элементов RecyclerView
    private ArrayList<Integer> selectedItems = new ArrayList<>();       //Список элементов для выделения, получается от внешнего источника
//    private int tempDuration = 0;
//    private ArrayList<ArrayList> textWatchers = new ArrayList<>();    //Массив TextWatcher'ов для элементов
    private HashMap<Integer, TextWatcher> textWatchersArray = new HashMap<>();
    private static boolean readyToWriteToIntervalList = false;             //Признак, что можно писать в массив интервалов (заблокировано при создании эдементов RecyclerView)

    private String TAG = "AddNewRecyclerIntervalAdapter";


    public IntervalAdapterForAddNew(List<Interval> intervalList, Context context) {
        this.intervalList = intervalList;
        this.context = context;
    }

    @Override
    public IntervalViewHolderForAddNew onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (parent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_new_recycler_list_item_land, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_new_recycler_list_item_port, parent, false);
        }
        readyToWriteToIntervalList = false;

        return new IntervalViewHolderForAddNew(itemView);
    }

    @Override
    public void onBindViewHolder(final IntervalViewHolderForAddNew holder, int position) {

        readyToWriteToIntervalList = false;

        int holderPosition = holder.getAdapterPosition();

        //Заполнение контроллов надутого элемента RecyclerView данными из массива

        //Заполнение EditText для названия
        if (!intervalList.get(holderPosition).getTitle().equals("")) {
            holder.intervalTitle.setText(intervalList.get(holderPosition).getTitle());
        } else {
            holder.intervalTitle.setText("");
        }

        //Заполнение элемента intervalMenu
//!Добавить меню

        //Обработка EditText для ввода часов
        if (!intervalList.get(holderPosition).getIntervalHoursToString().equals("0")) {
            holder.intervalDurationHours.setText(intervalList.get(holderPosition).getIntervalHoursToString());
        } else {
            holder.intervalDurationHours.setText("");
        }
        holder.intervalDurationHours.setFilters(new InputFilter[]{CommonMethods.inputFilter(0, Interval.getINTERVAL_MAX_HOUR())});

        //Обработка EditText для ввода минут
        if (!intervalList.get(holderPosition).getIntervalMinutesToString().equals("0")) {
            holder.intervalDurationMinutes.setText(intervalList.get(holderPosition).getIntervalMinutesToString());
        } else {
            holder.intervalDurationMinutes.setText("");
        }
        holder.intervalDurationMinutes.setFilters(new InputFilter[]{CommonMethods.inputFilter(0, Interval.getINTERVAL_MAX_MINUTE())});

        //Обработка EditText для ввода секунд
        if (!intervalList.get(holderPosition).getIntervalSecondsToString().equals("0")) {
            holder.intervalDurationSeconds.setText(intervalList.get(holderPosition).getIntervalSecondsToString());
        } else {
            holder.intervalDurationSeconds.setText("");
        }
        holder.intervalDurationSeconds.setFilters(new InputFilter[]{CommonMethods.inputFilter(0, Interval.getINTERVAL_MAX_SECOND())});

        //Определение обработчиков ввода в текстовые поля ввода длительности интервала
        if (!textWatchersArray.containsKey(holderPosition)) {

            TextWatcher textWatcherForDurationElement = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (!readyToWriteToIntervalList) return;
                    intervalList.get(holder.getAdapterPosition()).setDuration(calculateIntervalDuration(holder));
                }
            };

            TextWatcher textWatcherForTitleElement = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (!readyToWriteToIntervalList) return;
                    intervalList.get(holder.getAdapterPosition()).setTitle(holder.intervalTitle.getText().toString());
                }
            };
//            textWatchersArray.put(holderPosition, textWatcherForDurationElement);

            holder.intervalDurationHours.addTextChangedListener(textWatcherForDurationElement);
            holder.intervalDurationMinutes.addTextChangedListener(textWatcherForDurationElement);
            holder.intervalDurationSeconds.addTextChangedListener(textWatcherForDurationElement);

            holder.intervalTitle.addTextChangedListener(textWatcherForTitleElement);
        }

        //Задание картинки на кнопке Направления и добавление обработчика на клик
        if (intervalList.get(holderPosition).getDirection().equals(GlobalValues.timerDirection.Backward)) {
            holder.intervalDirection.setImageResource(R.drawable.ic_backward_count);
        } else {
            holder.intervalDirection.setImageResource(R.drawable.ic_forward_count);
        }

        holder.intervalDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDirection(holder.intervalDirection, holder);
            }
        });

        //Задание картинки на кнопке Уведомлений и добавление обработчика на клик
        GlobalValues.timerNotification tn = intervalList.get(holderPosition).getNotification();
        switch (tn) {
            case Sound:
                holder.intervalAlert.setImageResource(R.drawable.ic_alert_sound);
                break;
            case Screen:
                holder.intervalAlert.setImageResource(R.drawable.ic_alert_screen);
                break;
            case Flashlight:
                holder.intervalAlert.setImageResource(R.drawable.ic_alert_flashlight);
                break;
            case Off:
                holder.intervalAlert.setImageResource(R.drawable.ic_alert_off);
                break;
        }

        holder.intervalAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAlert(holder.intervalAlert, holder);
            }
        });

        //Задание картинки на кнопке Автоперехода и добавление обработчика на клик
        if (intervalList.get(holderPosition).getGoTOTheNext().equals(GlobalValues.timerGoTOTheNext.Off)) {
            holder.intervalGoToTheNext.setImageResource(R.drawable.ic_go_to_the_next_off);
        } else {
            holder.intervalGoToTheNext.setImageResource(R.drawable.ic_go_to_the_next_on);
        }

        holder.intervalGoToTheNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGoToTheNext(holder.intervalGoToTheNext, holder);
            }
        });

        recyclerViewArrayList.add(holder);

        //Выделение элементов списка на основе массива selectedItems
        updateRecyclerListColor(holder);

        readyToWriteToIntervalList = true;
    }

    @Override
    public void onViewRecycled(IntervalViewHolderForAddNew holder) {
        readyToWriteToIntervalList = false;
        super.onViewRecycled(holder);
        recyclerViewArrayList.remove(holder);
    }

    //Обновление цветового выделения всех активных элементов списка на основе массива выделенных элементов (выделенный элемент - серый цвет, все остальные - белый)
    private void updateRecyclerListColor(IntervalViewHolderForAddNew holderForAddNew) {
        if (selectedItems.size() > 0) {
            boolean isSelected = false;
            for (int i = 0; i < selectedItems.size(); i++) {
                if (holderForAddNew.getAdapterPosition() == selectedItems.get(i)) {
                    isSelected = true;
                }
            }

            if (isSelected) {
                holderForAddNew.linearLayout.setBackgroundColor
                        (ContextCompat.getColor(context, R.color.main_recycler_selected_interval_layout_color));   //Выделение цветом выбранного элемента списка
            } else {
                holderForAddNew.linearLayout.setBackgroundColor
                        (ContextCompat.getColor(context, R.color.transparent));
            }
        }
    }

    //Задание списка выделенных элементов
    public void setSelectedItems(ArrayList<Integer> selectedItemsInput) {
        if (selectedItems != null) {
            this.selectedItems = selectedItemsInput;
        }
    }

    @Override
    public int getItemCount() {
        if (intervalList != null) {
            return intervalList.size();
        }
        return 0;
    }

    //Обработчик клика на кнопку Направления
    private void onClickDirection(ImageButton ib, IntervalViewHolderForAddNew holderForAddNew) {
        if (intervalList.get(holderForAddNew.getAdapterPosition()).getDirection().equals(GlobalValues.timerDirection.Backward)) {
            ib.setImageResource(R.drawable.ic_forward_count);
            intervalList.get(holderForAddNew.getAdapterPosition()).setDirection(GlobalValues.timerDirection.Forward);
        } else {
            ib.setImageResource(R.drawable.ic_backward_count);
            intervalList.get(holderForAddNew.getAdapterPosition()).setDirection(GlobalValues.timerDirection.Backward);
        }
    }

    //Обработчик клика на кнопку Уведомления
    private void onClickAlert(ImageButton ib, IntervalViewHolderForAddNew holderForAddNew) {
        GlobalValues.timerNotification tn = intervalList.get(holderForAddNew.getAdapterPosition()).getNotification();
        switch (tn) {
            case Off:
                ib.setImageResource(R.drawable.ic_alert_sound);
                intervalList.get(holderForAddNew.getAdapterPosition()).setNotification(GlobalValues.timerNotification.Sound);
                break;
            case Sound:
                ib.setImageResource(R.drawable.ic_alert_screen);
                intervalList.get(holderForAddNew.getAdapterPosition()).setNotification(GlobalValues.timerNotification.Screen);
                break;
            case Screen:
                ib.setImageResource(R.drawable.ic_alert_flashlight);
                intervalList.get(holderForAddNew.getAdapterPosition()).setNotification(GlobalValues.timerNotification.Flashlight);
                break;
            case Flashlight:
                ib.setImageResource(R.drawable.ic_alert_off);
                intervalList.get(holderForAddNew.getAdapterPosition()).setNotification(GlobalValues.timerNotification.Off);
                break;
        }
    }

    //Обработчик клика на кнопку Автоперехода
    private void onClickGoToTheNext(ImageButton ib, IntervalViewHolderForAddNew holderForAddNew) {
        if (intervalList.get(holderForAddNew.getAdapterPosition()).getGoTOTheNext().equals(GlobalValues.timerGoTOTheNext.Off)) {
            ib.setImageResource(R.drawable.ic_go_to_the_next_on);
            intervalList.get(holderForAddNew.getAdapterPosition()).setGoTOTheNext(GlobalValues.timerGoTOTheNext.On);
        } else {
            ib.setImageResource(R.drawable.ic_go_to_the_next_off);
            intervalList.get(holderForAddNew.getAdapterPosition()).setGoTOTheNext(GlobalValues.timerGoTOTheNext.Off);
        }
    }

    //Подсчёт длительности интервала из контроллов
    private int calculateIntervalDuration(IntervalViewHolderForAddNew holderForAddNew) {
        int hours;
        int minutes;
        int seconds;
        int tmp;

        try {
            hours = Integer.parseInt(holderForAddNew.intervalDurationHours.getText().toString());
        } catch (NumberFormatException ne) {
            hours = 0;
        }

        try {
            minutes = Integer.parseInt(holderForAddNew.intervalDurationMinutes.getText().toString());
        } catch (NumberFormatException ne) {
            minutes = 0;
        }

        try {
            seconds = Integer.parseInt(holderForAddNew.intervalDurationSeconds.getText().toString());
        } catch (NumberFormatException ne) {
            seconds = 0;
        }

        tmp = hours * 3600 + minutes * 60 + seconds;


        return tmp;
    }

    //TextWatcher для полей EditText для ввода значений интервалов
//    private void addTextWatcherToArray(final IntervalViewHolderForAddNew holderForAddNew) {
//    private TextWatcher addTextWatcherToArray(final IntervalViewHolderForAddNew holderForAddNew) {
//
//        TextWatcher textWatcher = new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                Log.d(TAG, "afterTextChanged: 222222222222 + operaton + index: " + holderForAddNew.getAdapterPosition());
//                intervalList.get(holderForAddNew.getAdapterPosition()).setDuration(calculateIntervalDuration(holderForAddNew));
//            }
//        };
//        Log.d(TAG, "addTextWatcherToArray: 3333333333333333333 + holderIndex " + holderForAddNew.getAdapterPosition());
////        textWatchers.add(holderForAddNew.getAdapterPosition(), textWatcher);
////        textWatchers.add(new ArrayList());
////        textWatchers.get(textWatchers.size() - 1).add(0, holderForAddNew.getAdapterPosition());
////        textWatchers.get(textWatchers.size() - 1).add(1, textWatcher);
//
//        return textWatcher;
//    }

//    private TextWatcher getTextWatcherFromArray(int recyclerListIndex){
//        for (int i = 0; i < textWatchers.size(); i++) {
//            if ((int) textWatchers.get(i).get(0) == recyclerListIndex){
//                return (TextWatcher) textWatchers.get(i).get(1);
//            }
//        }
//        return null;
//    }


    //Очистка всего списка интервалов
    public boolean clearAll(){
        intervalList.clear();
        this.notifyDataSetChanged();
        return true;
    }

    //Удаление элемента списка по Swipe'у
    public void removeElementBySwipe(int position){
        if(position >= 0 && position <this.getItemCount()) {
            this.intervalList.remove(position);
            this.notifyDataSetChanged();
            Log.i(TAG, "removeElementBySwipe: Remove element number: " + position);
        }
    }
}