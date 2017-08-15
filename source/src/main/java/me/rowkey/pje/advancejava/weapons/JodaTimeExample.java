package me.rowkey.pje.advancejava.weapons;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class JodaTimeExample {
    public static void main(String[] args) {
        DateTime dateTime = new DateTime(2017, 6, 21, 18, 00, 0); //2017.06.21 18:00:00
        System.out.println(dateTime.toString("yyyy-MM-dd"));

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        dateTime = DateTime.parse("2017-06-21", format);

        System.out.println(dateTime.toString());

        dateTime.plusDays(1) // 增加天
                .plusYears(1)// 增加年
                .plusMonths(1)// 增加月
                .plusWeeks(1)// 增加星期
                .minusMillis(1)// 减分钟
                .minusHours(1)// 减小时
                .minusSeconds(1);// 减秒数

        dateTime = new DateTime(new Date());
        Date date = dateTime.toDate();
    }
}
