package com.d.ivan.universalchronometer.Common;

import android.text.InputFilter;
import android.text.Spanned;

//Общие методы
public class CommonMethods {

    /*InputFilter на поле для ввода информации*/
    public static InputFilter inputFilter(final int min, final int max){

        InputFilter inputFilter = new InputFilter() {

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

        return inputFilter;
    }
}
