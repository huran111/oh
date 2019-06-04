package com.tykj.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description TODO
 * @auther huran
 * @date
 **/
public class DateUtils {
   static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String addSeconds(){
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 6);
        System.out.println(sdf.format(nowTime.getTime()));
        return sdf.format(nowTime.getTime());
    }
}
