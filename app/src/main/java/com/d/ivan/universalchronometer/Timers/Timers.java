package com.d.ivan.universalchronometer.Timers;


import com.d.ivan.universalchronometer.Common.GlobalValues;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Timers {
    private String title = "";                                  //Название таймера
    private String comment = "";                                //Комментарий
    private GlobalValues.timerStatus currentIntervalStatus = GlobalValues.timerStatus.Stopped;              //Текущий статус таймера
    private GlobalValues.timerDirection currentIntervalDirection = GlobalValues.timerDirection.Backward;    //Текущее направление счёта таймера
    private GlobalValues.timerGoTOTheNext currentIntervalGoToTheNext = GlobalValues.timerGoTOTheNext.On;    //Текущее направление счёта таймера
    private int currentIntervalValue = 0;                       //Текущее значение таймера
    private int currentIntervalIndex = 0;                       //Индекс выбранного интервала в массиве интервалов
    private int currentIntervalChangeValue = 1;                 //Квант изменения таймера в секундах
    private int currentIntervalLimit;                      //Текущий предел таймера
    private ArrayList <Interval> intervals = new ArrayList<>(1);                     //Массив интервалов данного таймера

    //Переменные для фоновой задачи отсчёта таймера
    private Timer tmr;
    private TimerTask tsk;

    private final String TAG = "Timers";

    public int getCurrentIntervalIndex() {
        return currentIntervalIndex;
    }

    public void setCurrentIntervalIndex(int currentIntervalIndex) {
        this.currentIntervalIndex = currentIntervalIndex;
    }

    public GlobalValues.timerStatus getCurrentIntervalStatus() {
        return currentIntervalStatus;
    }

    public void setCurrentIntervalStatus(GlobalValues.timerStatus currentIntervalStatus) {
        this.currentIntervalStatus = currentIntervalStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(ArrayList<Interval> intervalsToLoad) {
        intervals.clear();
        for (int i = 0; i < intervalsToLoad.size(); i++) {
            intervals.add(new Interval());
            intervals.get(i).loadInterval(intervalsToLoad.get(i));
        }
    }

    public int addInterval(){
        this.intervals.add(new Interval());
        return intervals.size();
    }

    //Запуск таймера
    public void timerRun(){
        if (currentIntervalStatus.equals(GlobalValues.timerStatus.Run)) return;       //Проверка, что таймер уже не запущен

        if (tmr != null) {
            tmr.cancel();
        }

        //Если стоим не на паузе
        if (!currentIntervalStatus.equals(GlobalValues.timerStatus.Paused)) {
            currentIntervalDirection = intervals.get(currentIntervalIndex).getDirection();  //Берём текущее значение направления для интервала
            currentIntervalLimit = intervals.get(currentIntervalIndex).getDuration();       //Устанавливаем предел для данного интервала
            currentIntervalGoToTheNext = intervals.get(currentIntervalIndex).getGoTOTheNext();       //Получаем признак перехода на следующий интервал
        }

        //Формирование начального значения таймера
        if (currentIntervalDirection.equals(GlobalValues.timerDirection.Backward)) {
            //Если считаем вниз, то в таймер грузится предельное значение
            if (!currentIntervalStatus.equals(GlobalValues.timerStatus.Paused) || currentIntervalValue <= 0) {
                currentIntervalValue = currentIntervalLimit;  //Если таймер стоит на паузе , то отсчёт должен продолжиться с места остановки, иначе с указанного значения.
            }
        } else {
            //Если считаем вверх, то в таймер грузится 0
            if (!currentIntervalStatus.equals(GlobalValues.timerStatus.Paused) || currentIntervalValue >= currentIntervalLimit) {
                currentIntervalValue = 0;  //Если таймер стоит на паузе, то отсчёт должен продолжиться с места остановки, иначе с указанного значения.
            }
        }

        //Запуск задачи отсчёта таймера
        tmr = new Timer();                  //Создание таймера запуска для задачи
        tsk = new MyTimerTask();            //создание экземпляра задачи
        currentIntervalStatus = GlobalValues.timerStatus.Run;    //Установка признака работы таймера
        tmr.schedule(tsk,1000, 1000);       //Запуск задачи
    }

    //Основная задача отсчёта в таймере
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            //Отсчёт таймера
            if (currentIntervalDirection.equals(GlobalValues.timerDirection.Backward)) {
                currentIntervalValue -= currentIntervalChangeValue;
            } else {
                currentIntervalValue += currentIntervalChangeValue;
            }

            //Остановка, если таймер досчитал до 0 или до текущего предела.
            if (isTimeOut()) {
                setCurrentIntervalStatus(GlobalValues.timerStatus.Ended);

                //Если у текущего интервала стоит признак автоматического перехода на следующий интервал, то осуществить переход
                if (currentIntervalGoToTheNext.equals(GlobalValues.timerGoTOTheNext.On)){
                    if (nextInterval()){
                        timerRun();
                    } else {
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }
        }
    }


    //Постановка таймера на паузу и возврат текущего значения таймера
    public void timerPause(){
        if (tsk != null) tsk.cancel();
        currentIntervalStatus = GlobalValues.timerStatus.Paused;
    }

    //Остановка таймера и возврат текущего значения таймера
    public void timerStop(){
        if (tsk != null) tsk.cancel();
        currentIntervalStatus = GlobalValues.timerStatus.Stopped;
        currentIntervalValue = currentIntervalLimit;
    }

    //Переход к следующему интервалу
    public boolean nextInterval(){
        if (currentIntervalIndex < intervals.size() - 1) {          //Если впереди есть интервал, то сдвигаемся, иначе останавливаемся на последнем и выдаём ошибку (false)
            currentIntervalIndex++;
            return true;
        }
        return false;
    }

    //Переход к предыдущему интервалу
    public boolean previousInterval(){
        if (currentIntervalIndex > 0) {                 //Если вначале есть интервал, то сдвигаемся, иначе останавливаемся на первом и выдаём ошибку (false)
            currentIntervalIndex--;
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        if (currentIntervalStatus.equals(GlobalValues.timerStatus.Run) || currentIntervalStatus.equals(GlobalValues.timerStatus.Paused) ||
                currentIntervalStatus.equals(GlobalValues.timerStatus.Ended) || currentIntervalStatus.equals(GlobalValues.timerStatus.Stopped)) {

            //Перевод значения таймера из int в строку с форматом HH:MM:SS
            int hours = currentIntervalValue / 3600;
            int minutes = (currentIntervalValue % 3600) / 60;
            int secs = currentIntervalValue % 60;
            if (currentIntervalDirection.equals(GlobalValues.timerDirection.Backward)){
                return String.format("%02d:%02d:%02d", hours, minutes, secs);
            } else {
                //Перевод значения таймера из int в строку с форматом HH:MM:SS/HHL:MML:SSL
                int hoursLimit = currentIntervalLimit / 3600;
                int minutesLimit = (currentIntervalLimit % 3600) / 60;
                int secsLimit = currentIntervalLimit % 60;
                return String.format("%02d:%02d:%02d/%02d:%02d:%02d", hours, minutes, secs, hoursLimit, minutesLimit, secsLimit);
            }
        }
        return "Null";
    }

    //Загрузка в текущий таймер значений из переданного таймера
    public boolean loadTimer(Timers timerToLoad) {
        this.title = timerToLoad.title;                                             //Название таймера
        this.comment = timerToLoad.comment;                                         //Комментарий
        this.currentIntervalStatus = timerToLoad.currentIntervalStatus;             //Текущий статус таймера
        this.currentIntervalChangeValue = timerToLoad.currentIntervalChangeValue;   //Квант изменения таймера в секундах
        this.currentIntervalIndex = 0;
        this.setIntervals(timerToLoad.getIntervals());
        return false;
    }

    //Метод проверки, закончилось ли время
    public boolean isTimeOut(){
        return currentIntervalValue <= 0 || currentIntervalValue >= currentIntervalLimit;
    }
}
