package com.d.ivan.universalchronometer.Common;

//Класс с глобалиными константами, перечислениями и т.д.
public class GlobalValues {
    public enum activityName {MainActivity, AddNewTimerActivity, ShowAllTimersActivity}     //Названия всех активностей в проекте
    public enum timerStatus {Run, Paused, Stopped, Ended}                                    //Возможные статусы таймера
    public enum timerDirection {Forward, Backward}                                           //Возможные направления счёта таймера
    public enum timerNotification {Off, Sound, Screen, Flashlight}                           //Способы оповещения об окончании таймера
    public enum timerGoTOTheNext {Off, On}                                                   //Признак перехода к следующему таймеру по окончании текущего

}
