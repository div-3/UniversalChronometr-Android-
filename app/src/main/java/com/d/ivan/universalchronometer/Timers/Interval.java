package com.d.ivan.universalchronometer.Timers;

import com.d.ivan.universalchronometer.Common.GlobalValues;

//Класс интервалов, состоящий из следующих признаков:
// - длительность
// - направление счёта
// - признак уведомления об окончании
// - признак автоматического перехода к следующему интервалу
public class Interval {
    private int duration = 0;
    private String title = "Interval";  //Название интервала
    private int maxDuration = 90000;    //Предельная длительность интервала в секундах (25 часов)
    private GlobalValues.timerDirection direction = GlobalValues.timerDirection.Backward;
    private GlobalValues.timerNotification notification = GlobalValues.timerNotification.Off;
    private GlobalValues.timerGoTOTheNext goTOTheNext = GlobalValues.timerGoTOTheNext.On;

    //Предельные значения времени для интервалов
    private final static int INTERVAL_MAX_HOUR = 23;
    private final static int INTERVAL_MAX_MINUTE = 59;
    private final static int INTERVAL_MAX_SECOND = 59;

    public static int getINTERVAL_MAX_HOUR() {
        return INTERVAL_MAX_HOUR;
    }

    public static int getINTERVAL_MAX_MINUTE() {
        return INTERVAL_MAX_MINUTE;
    }

    public static int getINTERVAL_MAX_SECOND() {
        return INTERVAL_MAX_SECOND;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public GlobalValues.timerDirection getDirection() {
        return direction;
    }

    public void setDirection(GlobalValues.timerDirection direction) {
        this.direction = direction;
    }

    public GlobalValues.timerNotification getNotification() {
        return notification;
    }

    public void setNotification(GlobalValues.timerNotification notification) {
        this.notification = notification;
    }

    public GlobalValues.timerGoTOTheNext getGoTOTheNext() {
        return goTOTheNext;
    }

    public void setGoTOTheNext(GlobalValues.timerGoTOTheNext goTOTheNext) {
        this.goTOTheNext = goTOTheNext;
    }

    //Изменение конкретного интервала
//!Внимание. Добавить проверку значений

    public boolean loadInterval(Interval intervalToLoad){
        if (intervalToLoad.duration > 0 && intervalToLoad.duration < maxDuration)this.duration = intervalToLoad.duration;
        this.title = intervalToLoad.title;
        this.direction = intervalToLoad.direction;
        this.notification = intervalToLoad.notification;
        this.goTOTheNext = intervalToLoad.goTOTheNext;
        return false;
    }

    //Вывод длительноти интервала в виде строки
    public String durationToString (){
        int hours = this.duration / 3600;
        int minutes = (this.duration % 3600) / 60;
        int secs = this.duration % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    //Вывод количества часов в интервале в виде строки
    public String getIntervalHoursToString (){
        int hours = this.duration / 3600;
        return Integer.toString(hours);
    }
    //Вывод количества минут в интервале в виде строки
    public String getIntervalMinutesToString (){
        int minutes = (this.duration % 3600) / 60;
        return Integer.toString(minutes);
    }
    //Вывод количества секунд в интервале в виде строки
    public String getIntervalSecondsToString (){
        int secs = this.duration % 60;
        return Integer.toString(secs);
    }

}
