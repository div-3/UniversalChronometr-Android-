package com.d.ivan.universalchronometer.Common;

//Разные полезные методы
public class UsefulInformation {

    //----------------------------------------------------------------------------------------------
    //Получение текущей ориентации экрана
    //----------------------------------------------------------------------------------------------
    //.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE

    //----------------------------------------------------------------------------------------------
    //Перевод DP в PX   http://qaru.site/questions/10697/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp
    //----------------------------------------------------------------------------------------------
//    public int convertDpToPixels(int dp) {
//        float px = dp * getResources().getDisplayMetrics().density;
//        return (int) px;
//    }


    //----------------------------------------------------------------------------------------------
    //Всплывающая подсказка.
    //----------------------------------------------------------------------------------------------
    /*
    * Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.wrong_intervals), Toast.LENGTH_SHORT);
                toast.show();*/


    //----------------------------------------------------------------------------------------------
    //Диалоговое окно https://developer.android.com/guide/topics/ui/dialogs.html?hl=ru
    //----------------------------------------------------------------------------------------------
    /*
    * // 1. Instantiate an AlertDialog.Builder with its constructor
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    // 2. Chain together various setter methods to set the dialog characteristics
    builder.setMessage(R.string.dialog_message)
           .setTitle(R.string.dialog_title);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    //3. Add the buttons
    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // User clicked OK button
               }
           });
    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // User cancelled the dialog
               }
           });
    //4. Set other dialog properties
    ...

    //5. Create the AlertDialog
    AlertDialog dialog = builder.create();
    dialog.show();
    * */


    //----------------------------------------------------------------------------------------------
    /*Получение цвета из ресурсов
    //----------------------------------------------------------------------------------------------
    * Для получения цвета применяет метод ContextCompat.getColor(), который в качестве первого
    * параметра принимает текущий объект Activity, а второй парамет - идентификатор цветового ресурса.*/



    //----------------------------------------------------------------------------------------------
    /*Работа с RecyclerView
    //----------------------------------------------------------------------------------------------
    * intervalAdapter.notifyDataSetChanged();     //Перерисовать RecyclerView при изменении данных
    * recyclerView.scrollToPosition(currentTimer.getCurrentIntervalIndex());      //Прокрутить RecyclerView до указанного элемента
    * */



    /*//----------------------------------------------------------------------------------------------
    /*InputFilter на поле для ввода информации
    //----------------------------------------------------------------------------------------------
    *
    * private InputFilter inputFilter(final int min, final int max){

        InputFilter inputFilter60 = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal;

                StringBuilder sb = new StringBuilder();     //Надо объединять переменные, чтобы получить всю введённую в поле строку
                sb.append(dest);        //Первые введённые символы без последнего, находятся в переменной dest
                sb.append(source);      //Последний введённый символ находится в переменной source

                //Обработка исключения, если введены не числа
                try {
                    int temp = Integer.parseInt(sb.toString());

                    //Проверка условия невалидности введённых данных
                    if (temp < min || temp > max || sb.charAt(0) == '0'){       //Проверка, что первый символ введённого числа не '0', и число находится в диапазоне от min до max
                        keepOriginal = true;
                    } else {
                        keepOriginal = false;
                    }
                } catch (NumberFormatException no){
                    keepOriginal = true;
                }

                //Если новый символ невалиден или получившееся значение невалидно, то возврщаем пустую строку, иначе введённый символ
                if (keepOriginal) {
                    return "";
                }
                else {
                    return source;
                }
            }
        };

        return inputFilter60;
    }
    * */


    /*Реализация Swipe для элементов RecyclerView
    https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28*/


}
