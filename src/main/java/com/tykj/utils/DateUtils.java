package com.tykj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @auther huran
 * @date
 **/
public class DateUtils {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String addSeconds() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 6);
        System.out.println(sdf.format(nowTime.getTime()));
        return sdf.format(nowTime.getTime());
    }

    /**
     * 时间戳
     *
     * @return
     */
    public static Long CreateDate() {
        return System.currentTimeMillis();
    }

    /**
     * 失效时间 当前时间+10min
     *
     * @return
     */
    public static Long expriredDate(Long time) {
        return time += 5 * 1000 * 60;
    }

    public static void main(String[] args) {
        System.out.println("获取当前系统时间为："
                +new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒").format(CreateDate()));//转换成标准年月日的形式
        System.out.println("获取当前系统时间为："
                +new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒").format(expriredDate(CreateDate())));//转换成标准年月日的形式
    }
}
