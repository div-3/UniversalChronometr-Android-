package com.d.ivan.universalchronometer.Timers;


import com.d.ivan.universalchronometer.Common.GlobalValues;

//Статический объект Таймер-буффер для обмена таймерами
public class TimerBuffer extends Timers {
    private static boolean isBufferTimer = false;               //Флаг, что таймер-буфер уже создан
    private static Timers bufferTimer;                          //Объект таймера-буфера, доступный оз всего приложения
    private static GlobalValues.activityName lastWriter;        //Поле для сохранения информации о последней записавшей в буфер активности

    public static GlobalValues.activityName getLastWriter() {
        return lastWriter;
    }

    public static void setLastWriter(GlobalValues.activityName lastWriter) {
        TimerBuffer.lastWriter = lastWriter;
    }

    public static void createTimerBuffer(GlobalValues.activityName lastWriterValue){
        bufferTimer = new Timers();
        isBufferTimer = true;
        lastWriter = lastWriterValue;
    }

    public static boolean loadBuffer(Timers timer ,GlobalValues.activityName lastWriterValue) {
        bufferTimer.loadTimer(timer);
        lastWriter = lastWriterValue;
        return true;
    }

    //Метод для получения буфера
    public static Timers getTimerBuffer(){
        return bufferTimer;
    }

    //Метод для получения признака созданности буфера
    public static boolean isTimerBuffer(){
        return isBufferTimer;
    }

    //Метод для очистки буфера
    public static boolean clearTimerBuffer(GlobalValues.activityName lastWriterValue){
        bufferTimer = new Timers();
        lastWriter = lastWriterValue;
        return true;
    }
}
