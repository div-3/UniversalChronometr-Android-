package com.d.ivan.universalchronometer.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d.ivan.universalchronometer.AddNewActivity.AddNewTimerActivity;
import com.d.ivan.universalchronometer.Common.GlobalValues;
import com.d.ivan.universalchronometer.R;
import com.d.ivan.universalchronometer.Timers.TimerBuffer;
import com.d.ivan.universalchronometer.Timers.TimerCounting;
import com.d.ivan.universalchronometer.Timers.Timers;

import java.util.Timer;
import java.util.TimerTask;

// Вопросы и комментарии в public class QUESTIONS
public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    //Элементы управления:
    private TextView mainTitle;             //TV с названием таймера
    private TextView mainTimerTextView;     //TV со значением текущего таймера

    //Кнопки управления интервалом:
    private ImageButton startInterval;      //Кнопка Start
    private ImageButton pauseInterval;      //Кнопка Pause
    private ImageButton stopInterval;       //Кнопка Stop
    private ImageButton nextInterval;       //Кнопка Next
    private ImageButton previousInterval;   //Кнопка Previous

    //Дополнительные элементы интерфейса
    private MenuView.ItemView addNewMenuItem;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    //Переменные для фонового потока обновления информации в TV со значением таймера
    private Timer tmr;
    private MyTimerTask tsk;
    private Timers currentTimer;
    private int previousIntervalIndex;

    //Реквест-коды для запуска активностей
    static final int ADD_NEW_TIMER = 100;  //Реквест-код для активити "Добавить новый таймер"

    //Перечень интервалов в RecyclerView
    private RecyclerView recyclerView;
    private IntervalAdapter intervalAdapter;

    //Дополнительные переменные
    private static Context context;
    private String TAG = "MAIN_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализация контроллов
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.add_new_timer_floating_button);
        startInterval = (ImageButton) findViewById(R.id.main_activity_ib_start);
        pauseInterval = (ImageButton) findViewById(R.id.main_activity_ib_pause);
        stopInterval = (ImageButton) findViewById(R.id.main_activity_ib_stop);
        nextInterval = (ImageButton) findViewById(R.id.main_activity_ib_next);
        previousInterval = (ImageButton) findViewById(R.id.main_activity_ib_previous);
        mainTimerTextView = (TextView) findViewById(R.id.main_text_view);
        mainTitle = (TextView) findViewById(R.id.main_title);
        addNewMenuItem = (MenuView.ItemView) findViewById(R.id.main_add_new_timer);

        //Определение бокового меню
        setSupportActionBar(toolbar);
        context = this;
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Специальный класс для размещения пунктов меню в drawer'е
        navigationView =  (NavigationView) findViewById(R.id.nav_view);
        //Добавляем слушателя нажатий на пункт списка
        navigationView.setNavigationItemSelectedListener(this);
        initHeader();


        //Создание внешнего статического объекта Таймера-Буффера
        if (!TimerBuffer.isTimerBuffer()) {
            TimerBuffer.createTimerBuffer(GlobalValues.activityName.MainActivity);
        }

        //Запуск основного фонового процесса обновления информации в TV таймера
        if (tmr != null) {
            tmr.cancel();
        }
        tmr = new Timer();      //Создание таймера
        tsk = new MyTimerTask();    //создание экземпляра задачи
        tmr.scheduleAtFixedRate(tsk,0, 200);


        //------------------------------------------------------------------------------------------
        //Обработчики событий:
        //------------------------------------------------------------------------------------------

        //Нажатие кнопки "Старт":
        startInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Если таймер уже запущен, то ничего не делаем
                if (currentTimer.getCurrentIntervalStatus().equals(GlobalValues.timerStatus.Run)) {return;}

                if (TimerCounting.isLoad()) {
                    startTimer();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.chose_timer), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        });

        //Нажатие кнопки "Пауза":
        pauseInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        //Нажатие кнопки "Стоп":
        stopInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer(false);
            }
        });

        //Нажатие кнопки "Следующий интервал":
        nextInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextIntervalActon();
            }
        });

        //Нажатие кнопки "Предыдущий интервал":
        previousInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousIntervalAction();
            }
        });

        //Нажатие плавающей кнопки "Добавить новый таймер":
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Запуск активити для создания нового таймера
                Intent addNewTimer = new Intent(MainActivity.this, AddNewTimerActivity.class);
                startActivityForResult(addNewTimer, ADD_NEW_TIMER);
            }
        });
    }

    //Пример работы с элементом бокового меню - Navigation Header, где отображается иконка приложения, мыло и др.
    private void initHeader(){
        ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderLogo);
        TextView nameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderName);
        TextView emailView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEMail);
        imageView.setImageResource(R.drawable.ic_logo);
        nameView.setText("Ivan D");
        emailView.setText("div-3@mail.ru");
    }

    // Отрабатываем нажатие на кнопку Назад:
    // если drawer открыт, то закрываем его, если закрыт - закрываем приложение
    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // Отрабатываем нажатие на элемент drawer'а
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        switch (id){
//            case R.id.nav_camera:
//                setNewScreen(EmptyFragment.getInstats(Color.GREEN));
//                break;
//            case R.id.nav_gallery:
//                setNewScreen(EmptyFragment.getInstats(Color.BLUE));
//                break;
//            case  R.id.nav_slideshow:
//                setNewScreen(EmptyFragment.getInstats(Color.MAGENTA));
//                break;
//            case  R.id.nav_manage:
//                setNewScreen(EmptyFragment.getInstats(Color.RED));
//                break;
//            case  R.id.nav_share:
//                BottomTabNavigation.launch(this, HomeScreen.THIRD);
//                break;
//            case  R.id.nav_send:
//                FragmentNavigationActivity.launch(this);
//                break;
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //закрываем NavigationView
        //параметр определяет анимацию закрытия
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void setNewScreen(EmptyFragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content, fragment);
//        fragmentTransaction.commit();
//    }

    @Override
    protected void onResume() {
        super.onResume();

        //Создание внешнего статического объекта Таймера
        if (TimerCounting.isTimerCounting()) {
            currentTimer = TimerCounting.getTimerCounting();
        } else {
            TimerCounting.createTimerCounting();
            currentTimer = TimerCounting.getTimerCounting();
        }

        //Если таймер тикает, то при рестарте формы надо продолжить отсчёт
        if (currentTimer.getCurrentIntervalStatus().equals(GlobalValues.timerStatus.Run)) {
            pauseTimer();
            startTimer();
        }

        //Загрузка списка итервалов в RecyclerView на mainActivity
        if (currentTimer.getIntervals() != null) {
            intervalAdapter = new IntervalAdapter(currentTimer.getIntervals(), this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(intervalAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW_TIMER && resultCode == RESULT_OK){
            stopTimer(true);

            //Если в буфере лежит таймер от активности AddNewTimer, то загружаем его в TimerCounting
            if (TimerBuffer.getLastWriter().equals(GlobalValues.activityName.AddNewTimerActivity)) {     //Если последняя активность, загрузившая в буффер данные была AddNewActivity, то создаём таймер из данных в буфере
                TimerCounting.loadTimer(TimerBuffer.getTimerBuffer(), GlobalValues.activityName.MainActivity);
                mainTitle.setText(currentTimer.getTitle());
            }
        }
        TimerBuffer.clearTimerBuffer(GlobalValues.activityName.MainActivity);
    }


    //Основная задача вывода информации о таймере
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            if (currentTimer != null) {
                final String strDate = currentTimer.toString();

                //Вывод текущего таймера в текстовое поле
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Если номер интервала изменился, то надо обновить RecyclerView
                        if (previousIntervalIndex != currentTimer.getCurrentIntervalIndex()){
                            intervalAdapter.notifyDataSetChanged();                                     //Перерисовать RecyclerView при изменении данных
                            recyclerView.scrollToPosition(currentTimer.getCurrentIntervalIndex());      //Прокрутить RecyclerView до указанного элемента
                            previousIntervalIndex = currentTimer.getCurrentIntervalIndex();
                        }

                        //Если текущий интервал закончился, то текст красим в красный
                        if (currentTimer.getCurrentIntervalStatus().equals(GlobalValues.timerStatus.Ended)) {
                            mainTimerTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        } else {
                            mainTimerTextView.setTextColor(ContextCompat.getColor(context, R.color.colorTextGray));
                        }

                        mainTimerTextView.setText(strDate);     //Вывод строки таймера
                    }
                });
            }
        }
    }

    //Запуск таймера
    public void startTimer() {
        currentTimer.timerRun();
        if (intervalAdapter != null) {
            intervalAdapter.notifyDataSetChanged();
        }
    }

    //Обработка нажатия на кнопку "Пауза"
    public void pauseTimer(){
        if (currentTimer.getCurrentIntervalStatus().equals(GlobalValues.timerStatus.Run)){
            currentTimer.timerPause();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.timer_does_not_start), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Обработка нажатия на кнопку "Стоп"
    public void stopTimer(boolean hidden){
        if (!currentTimer.getCurrentIntervalStatus().equals(GlobalValues.timerStatus.Stopped)){
            currentTimer.timerStop();           //остановка таймера
        } else {
            if (!hidden) {
                Toast toast = Toast.makeText(context, getText(R.string.timer_does_not_start), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //Обработка нажатия на кнопку "Следующий интервал"
    public void nextIntervalActon(){
        if (currentTimer.nextInterval()) {
            stopTimer(true);
            startTimer();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.main_next_missing), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Обработка нажатия на кнопку "Предыдущий интервал"
    public void previousIntervalAction(){
        if (currentTimer.previousInterval()) {
            stopTimer(true);
            startTimer();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.main_previous_missing), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}