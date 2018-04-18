package book.util;

import book.domain.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public class DateUtils {

    /**
     * 兼顾性能和simpleDateFormat的安全性,所以使用一个线程使用一个simpleDateFormat实例的方式
     */
    private static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>();

    private static String lock = "local";

    /**
     * 一分钟的长度
     */
    private static final long MINUTE_LENGTH = 1000 * 60;

    /**
     * 一天的长度
     */
    private static final long DAY_LENGTH = 1000 * 60 * 60 * 24;

    /**
     * 一周的长度
     */
    private static final long WEEK_length = 100 * 60 * 60 * 24 * 7;

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    /**
     * 将字符串表达式转换为日期
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static Date parse(String str) {
        try {
            ValidateUtils.checkNotEmpty(str, "待转换日期为空");
            SimpleDateFormat simpleDateFormat = local.get();
            if (simpleDateFormat == null) {
                synchronized (lock) {
                    if (simpleDateFormat == null) {
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        local.set(simpleDateFormat);
                        return simpleDateFormat.parse(str);
                    }
                }
            }
            return simpleDateFormat.parse(str);
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "日期转换失败,str:{0}", str);
            throw new BusinessException("日期格式有误");
        }
    }

    /**
     * 将日期转换为字符串表达式
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        try {
            ValidateUtils.checkNotNull(date, "待转换的日期为空");
            SimpleDateFormat simpleDateFormat = local.get();
            if (simpleDateFormat == null) {
                synchronized (lock) {
                    if (simpleDateFormat == null) {
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        local.set(simpleDateFormat);
                        return simpleDateFormat.format(date);
                    }
                }
            }
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "日期转换失败,str:{0}", date);
            throw new BusinessException("日期格式有误");
        }
    }

    /**
     * 获取今天起始
     *
     * @return
     */
    public static String getToday(Date date) {
        try {
            ValidateUtils.checkNotNull(date, "[getBeforeDay]传入对象为空");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date dayEnd = calendar.getTime();
            return format(dayEnd);
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "获取今日起始,date:{0}", date);
            throw new BusinessException("日期格式有误");
        }
    }

    public static String nextMonth(Date date) {
        try {
            ValidateUtils.checkNotNull(date, "[nextMonth]传入的对象为空");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            return format(calendar.getTime());
        } catch (Exception e) {
            LoggerUtil.error(e, LOGGER, "获取下一个月书籍失败,date:{0}", date);
            throw new BusinessException("日期格式有误");
        }
    }
}
