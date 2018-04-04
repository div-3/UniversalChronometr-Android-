package com.d.ivan.universalchronometer.Timers;

//Статический таймер. Считает в фоне.

import com.d.ivan.universalchronometer.Common.GlobalValues;

public class TimerCounting extends Timers {
    private static boolean isTimerCounting = false;               //Флаг, что таймер уже создан
    private static Timers timerCounting;                          //Объект таймера, доступный оз всего приложения
    private static GlobalValues.activityName lastWriter;          //Поле для сохранения информации о последней активности, изменившей таймер
    private static boolean isLoad = false;                          //Признак, что таймер загружен



    public static void createTimerCounting(){
        timerCounting = new Timers();
        isTimerCounting = true;
    }

    //Загрузка в текущий таймер указанного объекта
    public static boolean loadTimer(Timers timerToLoad, GlobalValues.activityName lastWriterValue) {
        if (timerToLoad != null) {
            timerCounting.loadTimer(timerToLoad);

            isLoad = true;
            lastWriter = lastWriterValue;
            timerCounting.setCurrentIntervalStatus(GlobalValues.timerStatus.Stopped);
        }
        return true;
    }

    //Метод для получения таймера
    public static Timers getTimerCounting(){
        return timerCounting;
    }

    //Метод для получения признака созданности буфера
    public static boolean isTimerCounting(){
        return isTimerCounting;
    }

    //Метод для очистки буфера
    public static boolean clearTimerCounting(){
        timerCounting = new Timers();
        return true;
    }


    public static boolean isLoad() {
        return isLoad;
    }

    public static void setIsLoad(boolean isLoad) {
        TimerCounting.isLoad = isLoad;
    }
}
