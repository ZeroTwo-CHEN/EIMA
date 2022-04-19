package top.remake.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author gzz
 */
public class RandomUtil {

    private RandomUtil() {
    }

    /**
     * 返回一个精确的计算机时间
     */
    public static String randomName() {
        String name;
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        Random random = new Random();
        name = simpleDateFormat.format(date) + random.nextInt(100, 999);
        return name;
    }
}
