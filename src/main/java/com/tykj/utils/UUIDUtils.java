package com.tykj.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @Description TODO
 * @auther huran
 * @date
 **/
public class UUIDUtils {
    /**
     * 临时二维码UUID参数
     * @return
     */
    public static String getQrTmpUUID(){
        int first = new Random(10).nextInt(8) + 1;
        System.out.println(first);
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return first + String.format("%s%015d","tmp", hashCodeV);
    }
    public static String getUUID(){

        return String.format("%s%s","tmp",UUID.randomUUID().toString().replace("-", ""));
    }

    public static void main(String[] args) {
        System.out.println(getQrTmpUUID());
    }
}
