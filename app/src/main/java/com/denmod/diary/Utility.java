package com.denmod.diary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    static String getDate() {
        Date date = Calendar.getInstance().getTime();
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }
}
