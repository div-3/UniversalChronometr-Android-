package com.d.ivan.universalchronometer.AddNewActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.d.ivan.universalchronometer.Common.GlobalValues;
import com.d.ivan.universalchronometer.R;
import com.d.ivan.universalchronometer.Timers.Interval;
import com.d.ivan.universalchronometer.Timers.TimerAddNew;
import com.d.ivan.universalchronometer.Timers.Timers;
import com.d.ivan.universalchronometer.Timers.TimerBuffer;

import java.util.ArrayList;

//Импорт для реализации Swipe
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import static android.support.v7.widget.helper.ItemTouchHelper.*;

//В конце добавлен класс с реализацией Swipe на элементах RecycleView (https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28)

public class AddNewTimerActivity extends AppCompatActivity{
    private enum intervalButtonType {Direction, Alert, GoToTheNext}
    private enum controlsType {CHECK_BOX, EDIT_TEXT, TEXT_VIEW, BUTTON}     //Типы контроллов на странице

    private Button btnOk;
    private Button btnCancel;
    private ImageButton ibtnAddInterval;
    private ImageButton ibtnDeleteIntervals;
    private EditText title;
    private EditText comment;

    private Context context = this;

    private Timers tempTimer;

    //Для RecyclerView
    private RecyclerView recyclerView;
    private IntervalAdapterForAddNew adapter;
    private ArrayList<Interval> tempIntervalsForRecylerView = new ArrayList<>();    //Временные интервалы для RecyclerView. При нажатии кнопки Ok буду сохранены в TimerBuffer
    private ArrayList<Integer> wrongIntervals = new ArrayList<>();      //Перечень неправильно заполненных интервалов

    //Создание объекта ItemTouchHelper для реализации Swipe на элементах RecycleView
    SwipeController swipeController = new SwipeController();
    ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);



    private final String TAG = "AddNewTimer";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_timer);

        tempTimer = new Timers();

        recyclerView = (RecyclerView) findViewById(R.id.add_new_recycler_view);

        title = (EditText) findViewById(R.id.add_new_Title);
        comment = (EditText) findViewById(R.id.add_new_comment);

        //Обработка нажатия кнопки "Добавить интервал"
        ibtnAddInterval = (ImageButton) findViewById(R.id.add_new_add_interval);
        ibtnAddInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewIntervalRow();
            }
        });

        //Обработка нажатия кнопки "Удалить всё"
        ibtnDeleteIntervals = (ImageButton) findViewById(R.id.add_new_delete_interval);
        ibtnDeleteIntervals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearAll();
            }
        });

        //Обработка нажатия на кнопку "OK"
        btnOk = (Button) findViewById(R.id.add_new_Ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOk();
            }
        });

        //Обработка нажатия на кнопку "Cancel"
        btnCancel = (Button) findViewById(R.id.add_new_Cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCancel();
            }
        });

        //Запуск recyclerView
        adapter = new IntervalAdapterForAddNew(tempIntervalsForRecylerView, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Привязка Swipe к RecyclerView
        swipeController.setAdapter(adapter);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                touchListener(view, motionEvent);
                return false;
            }
        });

        //Автоматическое создание полей для первого интервала
        createNewIntervalRow();
    }

    //Добавление меню в Action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new, menu);
        return true;
    }

    //Обработка нажатия на элемент меню в Action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // обработка нажатий
        switch (item.getItemId()) {
            case R.id.menu_add_new_add:
                createNewIntervalRow();
                return true;
            case R.id.menu_add_new_clear:
                adapter.clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Если вдруг буфер не создан, то создаём
        if (!TimerBuffer.isTimerBuffer()) {
            TimerBuffer.createTimerBuffer(GlobalValues.activityName.AddNewTimerActivity);
        }

        //Если вдруг таймер TimerAddNew не создан, то создаём
        if (!TimerAddNew.isCreated()) {
            TimerAddNew.createTimer(GlobalValues.activityName.AddNewTimerActivity);
        }

        //Если в буфер писал кто-то ещё, кроме активности AddNew, то очищаем буфер
        if (!TimerBuffer.getLastWriter().equals(GlobalValues.activityName.AddNewTimerActivity)){
            TimerBuffer.clearTimerBuffer(GlobalValues.activityName.AddNewTimerActivity);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Добавление нового элемента в Recycler
    private void createNewIntervalRow() {
        tempIntervalsForRecylerView.add(new Interval());
        adapter.notifyDataSetChanged();
    }

    //Обработка нажатия на кнопку Ok
    private void onClickOk(){
        Timers tmpTmr = new Timers();
        wrongIntervals.clear();

        //Заполнение полей объекта Timers:
        boolean everythingIsOK = true;                  //Флаг, что всё распарсилось правильно


        //Заполнение названия
        if(!title.getText().toString().isEmpty() && !title.getText().toString().equals(getText(R.string.wrong_title))) {
            tmpTmr.setTitle(title.getText().toString());
            title.setTextColor(ContextCompat.getColor(context,R.color.colorTextGray));
        } else {
            title.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
            title.setText(getText(R.string.wrong_title));
            everythingIsOK = false;
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.wrong_title), Toast.LENGTH_SHORT);
            toast.show();
        }

        //Заполнение комментария
        if(!comment.getText().toString().isEmpty()) {
            tmpTmr.setComment(comment.getText().toString());
        }

        if (tempIntervalsForRecylerView.size() == 0) {
            everythingIsOK = false;
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.empty_intervals), Toast.LENGTH_SHORT);
            toast.show();
        }

        //Проверка, что все интервалы заполнены корректно (длительности не равны 0 и имена не пустые)
        for (int i = 0; i < tempIntervalsForRecylerView.size(); i++) {
            if (tempIntervalsForRecylerView.get(i).getDuration() == 0 || tempIntervalsForRecylerView.get(i).getTitle().equals("")) {
                wrongIntervals.add(i);
                everythingIsOK = false;
                Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.wrong_intervals), Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        if (everythingIsOK) {
            //Если всё в порядке, то сохраняем интервалы во временный таймер и сохраняем таймер в буфер
            tmpTmr.setIntervals(tempIntervalsForRecylerView);

            TimerBuffer.loadBuffer(tmpTmr, GlobalValues.activityName.AddNewTimerActivity);        //Сохранение созданного таймера в буфер
            setResult(RESULT_OK);   //Возврат результата ОК на вызвавшую активность
            finish();               //Закрытие текущей активности
        } else {
            if (wrongIntervals.size() != 0) {
                adapter.setSelectedItems(wrongIntervals);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void onClickCancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.add_new_cancel_message)
                .setTitle(R.string.add_new_cancel_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                setResult(RESULT_CANCELED);   //Возврат результата CANCEL на вызвавшую активность
                finish();               //Закрытие текущей активности
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                //Ничего не делаем
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void touchListener(View view, MotionEvent motionEvent){
        float downX = 0f;
        float upX = 0f;
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = motionEvent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                upX = motionEvent.getX();
                break;
            }
        }

        float deltaX = downX - upX;

        if(Math.abs(deltaX)>0){
            if(deltaX>=0){

//                        swipeToRight();
            }else{
//                        swipeToLeft();
            }
        }
    }
}





/*****************************************************************************
* Реализация Swipe на элементах RecyclerView (https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28)
 * При свайпе влево элемент удаляется, при свайпе вправо появляется кнопка Edit
* ****************************************************************************/

enum ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

class SwipeController extends Callback {

    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private RectF buttonInstance = null;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private static final float buttonWidth = 300;
    private IntervalAdapterForAddNew adapter;

    private String TAG = "Swipe Controller";


    public void setAdapter(IntervalAdapterForAddNew adapter) {
        if(adapter != null) {
            this.adapter = adapter;
        }
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT | RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth);
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;
        drawButtons(c, currentItemViewHolder);
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    if (dX < -buttonWidth) {
                        buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                        adapter.removeElementBySwipe(viewHolder.getAdapterPosition());  //Удаление элемента на котором свайпнули влево.

                    }
                    else if (dX > buttonWidth) {
                        buttonShowedState  = ButtonsState.LEFT_VISIBLE;                 //Пока ничего не делаем, только включаем кнопку.
                    }

                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;
                    buttonShowedState = ButtonsState.GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float buttonWidthWithoutPadding = buttonWidth - 20;
        float corners = 16;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
        p.setColor(Color.BLUE);
        c.drawRoundRect(leftButton, corners, corners, p);
        drawText("EDIT", c, leftButton, p);

        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("DELETE", c, rightButton, p);

        buttonInstance = null;
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        }
        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }
}

