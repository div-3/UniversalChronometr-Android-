package com.d.ivan.universalchronometer.Timers;

import com.d.ivan.universalchronometer.Common.GlobalValues;

//Статический объект для работы в активности AddNew
public class TimerAddNew extends Timers {
    private static boolean isCreated = false;               //Флаг, что таймер-буфер уже создан
    private static Timers timerAddNew;                          //Объект таймера-буфера, доступный оз всего приложения
    private static GlobalValues.activityName lastWriter;        //Поле для сохранения информации о последней записавшей в буфер активности

    public static GlobalValues.activityName getLastWriter() {
        return lastWriter;
    }

    public static void createTimer(GlobalValues.activityName lastWriterValue){
        timerAddNew = new Timers();
        isCreated = true;
        lastWriter = lastWriterValue;
    }

    public static boolean loadTimer(Timers timer ,GlobalValues.activityName lastWriterValue) {
        timerAddNew.loadTimer(timer);
        lastWriter = lastWriterValue;
        return true;
    }

    //Метод для получения буфера
    public static Timers getTimer(){
        return timerAddNew;
    }

    //Метод для получения признака созданности буфера
    public static boolean isCreated(){
        return isCreated;
    }

    //Метод для очистки буфера
    public static boolean clearTimer(GlobalValues.activityName lastWriterValue){
        timerAddNew = new Timers();
        lastWriter = lastWriterValue;
        return true;
    }
}
