package com.dm.model;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dm.tool.PropertiesTool;

public class Bill {

	private Date date;
	private String dateStr;
	private String money;
	private String tip;

	private DateFormat format = new SimpleDateFormat("yyyy/MM/dd");

	public Bill(String dateStr, String money, String tip) {
		super();
		try {
			this.date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			this.date = new Date();
		}
		this.dateStr = dateStr;
		String moneyStr = money.trim();
		if ("".equals(moneyStr)) {
			moneyStr = "0";
		} else {
			try {
				if (ifNeedCalculate(moneyStr)) {
					moneyStr = String.valueOf(PropertiesTool.se.eval(moneyStr));
				}
				//处理四舍五入，防止金额小数过长（一般正常输入不会出问题，但是这个表达式计算的引擎有点坑会出现精度问题）
				moneyStr = new BigDecimal(moneyStr).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			} catch (Exception e) {
				moneyStr = "0";
			}
		}
		this.money = moneyStr;
		this.tip = tip;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	@Override
	public String toString() {
		return "[date=" + dateStr + ", money=" + money + ", tip=" + tip + "]";
	}

	/**
	 * 判断金额是否是表达式
	 * <li></li>
	 * 
	 * @param moneyStr
	 * @return
	 */
	private boolean ifNeedCalculate(String moneyStr) {
		int subCount = 0;//记录-号的数量
		for (int i = 0; i < moneyStr.length(); i++) {
			if (moneyStr.charAt(i) == '-') {
				subCount++;
				if (subCount == 2) {
					return true;
				}
			}
			if (moneyStr.charAt(i) != '-' && moneyStr.charAt(i) != '.'
					&& (moneyStr.charAt(i) < '0' || moneyStr.charAt(i) > '9')) {
				//含有特殊符号，表示需要计算
				return true;
			}
		}
		return false;
	}
}
