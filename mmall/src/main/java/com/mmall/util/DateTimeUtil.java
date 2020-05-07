package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Description:
 *
 * @author Huangfeiteng
 * @date Created on 2020/5/7
 */
public class DateTimeUtil {


    private static  final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 字符串转date
     * @param dateTimeStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * date转字符串
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date , String formatStr){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }


    /**
     * 字符串转date
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * date转字符串
     * @param date
     * @return
     */
    public static String dateToStr(Date date ){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
