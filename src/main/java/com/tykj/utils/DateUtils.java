package com.tykj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
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
    /**
     * 时间戳
     * @return
     */
    public static String CreateDate(){
        String dataStr=null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        dataStr = sdf.format(new Date());
        return dataStr;
    }
    /**
     * 失效时间 当前时间+10min
     * @return
     */
    public static String expriredDate(String dataStr){
        String aDataStr=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            Date date = sdf.parse(dataStr);
            date.setTime(date.getTime()+5*60*1000);
            aDataStr=sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return aDataStr;
    }
}
