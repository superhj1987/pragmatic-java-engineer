package me.rowkey.pje.advancejava.weapons;

import com.google.common.collect.Lists;
import me.rowkey.pje.common.meta.TestUser;
import me.rowkey.pje.common.meta.User;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Apache Commons实例
 */
public class CommonsExample {
    public static void collections() {
        List<String> list = Lists.newArrayList();
        List<String> list2 = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(list)) {
            CollectionUtils.subtract(list, list2);
            CollectionUtils.subtract(list, list2);
            CollectionUtils.retainAll(list, list2);
        }

        OrderedMap map = new LinkedMap();
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        map.firstKey();
        map.nextKey("1");
    }

    public static void io() throws IOException {
        File file = new File("/data/data.txt");
        List lines = FileUtils.readLines(file, "UTF-8"); //读取成字符串结合
        System.out.println(lines);

        byte[] fileBytes = FileUtils.readFileToByteArray(file); //读取成字节数组
        FileUtils.writeByteArrayToFile(file, fileBytes); //字节写入文件
        FileUtils.writeStringToFile(file, "test"); //字符串写入文件

        InputStream is = new URL("http://baidu.cim").openStream();
        try {
            System.out.println(IOUtils.toString(is, "utf-8"));
            //IOUtils.readLines(is, "utf-8");
        } finally {
            IOUtils.closeQuietly(is);
        }

        FileSystemUtils.freeSpaceKb();
    }

    public static void lang() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String[] strs = new String[]{"1", "4", "2"};
        ArrayUtils.addAll(strs, "3");

        RandomUtils.nextInt(0, 10);
        RandomStringUtils.random(3);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        stopWatch.split();
        stopWatch.getSplitTime();
        stopWatch.suspend();
        stopWatch.resume();
        stopWatch.stop();
        stopWatch.getTime();

        long max = NumberUtils.max(new long[]{1, 5, 10}); //计算数组最大值

        MethodUtils.invokeStaticMethod(StringUtils.class, "isNotBlank", "test"); //调用静态方法
        MethodUtils.invokeMethod(StringUtils.class, "isNotBlank", "test"); //调用静态方法

        DateUtils.truncate(new Date(), Calendar.HOUR);
        DateFormatUtils.format(new Date(), "yyyyMMdd");
    }

    public static void beanUtils() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        BeanUtilsBean beanUtilsBean = new BeanUtilsBean2();
        beanUtilsBean.getConvertUtils().register(false, false, 0);//错误不抛出异常、不使用Null做默认值，数组的默认大小为0

        User user = new User();
        user.setName("test");
        TestUser testUser = new TestUser();

        beanUtilsBean.copyProperties(testUser, user);
        System.out.println(testUser);
    }
}
