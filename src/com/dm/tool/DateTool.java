package com.dm.tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ����ʱ�乤����
 * 
 * @author shuil
 *
 */
public class DateTool {

	private static Calendar cal = Calendar.getInstance();

	/**
	 * ����date�����ڼ�
	 * <li></li>
	 * 
	 * @param date
	 *            ����
	 * @return ���ڼ�
	 */
	public static String getWeekDay(Date date) {
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case 1:
			return "����";
		case 2:
			return "��һ";
		case 3:
			return "�ܶ�";
		case 4:
			return "����";
		case 5:
			return "����";
		case 6:
			return "����";
		case 7:
			return "����";
		default:
			return dayOfWeek + "";
		}
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ��������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
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
	 * <li>�������ڻ�ȡ��������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getThisMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 1);//�·�����Ϊ�¸���
		cal.set(Calendar.DAY_OF_MONTH, 1);//��������Ϊ1��
		cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ����������ʱ���</li>
	 * <li>�������ǽ����±�ʾ��ǰ�� �ϸ��º����ϸ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getThreeMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -2);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 3);//�·�����Ϊ�¸���
		cal.set(Calendar.DAY_OF_MONTH, 1);//��������Ϊ1��
		cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ����������ʱ���</li>
	 * <li>�������ǽ�����������ϰ�������°���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getHalfYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();

		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		if (month <= 5) {
			//�ϰ���
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			dateList.add(cal.getTime());

			cal.add(Calendar.MONTH, 6);
			cal.set(Calendar.DAY_OF_MONTH, 1);//��������Ϊ1��
			cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
			dateList.add(cal.getTime());
		} else {
			//�°���
			cal.set(Calendar.MONTH, 6);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			dateList.add(cal.getTime());

			cal.add(Calendar.YEAR, 1);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);//��������Ϊ1��
			cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
			dateList.add(cal.getTime());
		}

		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ��������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getThisYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.YEAR, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);//��������Ϊ1��
		cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ��Ӧ��������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getLastWeek(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, -dayOfWeek - 6);//�����ȡ���������յ�����

		dateList.add(cal.getTime());
		cal.add(Calendar.DATE, 6);
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ��Ӧ�ϸ�������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getLastMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);//�·�����Ϊ�ϸ���

		cal.set(Calendar.DAY_OF_MONTH, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);//��������Ϊ1��
		cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ��Ӧ��һ������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getLastYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.YEAR, -1);
		dateList.add(cal.getTime());

		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ��Ӧ��һ������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
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
	 * <li>�������ڻ�ȡ��Ӧ�¸�������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getNextMonth(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);//�·�����Ϊ�¸���

		cal.set(Calendar.DAY_OF_MONTH, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);//��������Ϊ1��
		cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>�������ڻ�ȡ��Ӧ��һ������ʱ���</li>
	 * 
	 * @param date
	 * @return list��������Ԫ�أ�����Ŀ�ʼ�ͽ���ʱ��
	 */
	public static List<Date> getNextYear(Date date) {
		List<Date> dateList = new ArrayList<Date>();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.YEAR, 1);
		dateList.add(cal.getTime());

		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);//���ص�ǰһ��
		dateList.add(cal.getTime());
		return dateList;
	}

	/**
	 * 
	 * <li>��ȡ�������ڵ�����</li>
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
