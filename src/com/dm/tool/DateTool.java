package com.dm.tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期时间工具类
 * 
 * @author shuil
 *
 */
public class DateTool {

	private static Calendar cal = Calendar.getInstance();

	/**
	 * 返回date是星期几
	 * <li></li>
	 * 
	 * @param date
	 *            日期
	 * @return 星期几
	 */
	public static String getWeekDay(Date date) {
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case 1:
			return "周日";
		case 2:
			return "周一";
		case 3:
			return "周二";
		case 4:
			return "周三";
		case 5:
			return "周四";
		case 6:
			return "周五";
		case 7:
			return "周六";
		default:
			return dayOfWeek + "";
		}
	}

	/**
	 * 
	 * <li>根据日期获取本周日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getThisWeek(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, -dayOfWeek + 1);
		dateList.add(cal.getTime());
		cal.add(Calendar.DATE, 6);
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取本月日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getThisMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 1);//月份设置为下个月
		cal.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
		cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取近三月日期时间段</li>
	 * <li>这里我们近三月表示当前月 上个月和上上个月</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getThreeMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -2);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 3);//月份设置为下个月
		cal.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
		cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取近半年日期时间段</li>
	 * <li>这里我们近半年仅代表上半年或者下半年</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getHalfYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();

		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		if (month <= 5) {
			//上半年
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			dateList.add(cal.getTime());

			cal.add(Calendar.MONTH, 6);
			cal.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
			cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
			dateList.add(cal.getTime());
		} else {
			//下半年
			cal.set(Calendar.MONTH, 6);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			dateList.add(cal.getTime());

			cal.add(Calendar.YEAR, 1);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
			cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
			dateList.add(cal.getTime());
		}

		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取本年日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getThisYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.YEAR, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
		cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取对应上周日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getLastWeek(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, -dayOfWeek - 6);//这个获取的是上周日的日期

		dateList.add(cal.getTime());
		cal.add(Calendar.DATE, 6);
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取对应上个月日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getLastMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);//月份设置为上个月

		cal.set(Calendar.DAY_OF_MONTH, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
		cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取对应上一年日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getLastYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.YEAR, -1);
		dateList.add(cal.getTime());

		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取对应下一周日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getNextWeek(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, 7 - dayOfWeek + 1);

		dateList.add(cal.getTime());
		cal.add(Calendar.DATE, 6);
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取对应下个月日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getNextMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);//月份设置为下个月

		cal.set(Calendar.DAY_OF_MONTH, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
		cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>根据日期获取对应下一年日期时间段</li>
	 * 
	 * @param date
	 * @return list含有两个元素，间隔的开始和结束时间
	 */
	public static List<Date> getNextYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.YEAR, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>获取两个日期的天数</li>
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getDayNum(Date start, Date end) {
		cal.setTime(start);
		long startMills = cal.getTimeInMillis();
		cal.setTime(end);
		long endMills = cal.getTimeInMillis();
		try {
			long delta = (endMills - startMills) % (1000 * 3600 * 24);
			if (delta == 0) {
				return (int) ((endMills - startMills) / (1000 * 3600 * 24) + 1);
			} else {
				return (int) ((endMills - startMills) / (1000 * 3600 * 24) + 2);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

}
