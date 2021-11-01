package com.dm.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.dm.model.Bill;
import com.dm.model.CalBillPanel;
import com.dm.tool.DateChooserJButton;
import com.dm.tool.DateTool;
import com.dm.tool.PropertiesTool;

/**
 * 账单统计窗口
 * 
 * @author shuil
 *
 */
public class CalBillFrame extends JDialog {
	private DateChooserJButton fromBtn, toBtn;
	JComboBox typeSelect;

	JScrollPane jsp;
	JPanel labelJp, jp;
	JLabel indexJl, countJl, costJl, earnJl, profitJl;
	public static JTextPane jtp;
	public static String allStr = "";//全面分析数据

	JButton weekJb, monthJb, triMonthJb, halfYearJb, yearJb, lastMonthJb, lastWeekJb, lastYearJb, nextWeekJb,
			nextMonthJb, nextYearJb;

	ArrayList<Bill> billList;//原始统计数据
	BigDecimal costAll;//统计所有支出
	BigDecimal earnAll;//统计所有收入

	public CalBillFrame(int xPos, int yPos, String title, ArrayList<Bill> billList) {
		Date start = new Date();
		Date end = new Date();
		if (billList.size() > 0) {
			start = billList.get(0).getDate();
			end = billList.get(billList.size() - 1).getDate();
		}

		//默认按天统计，现在默认统计当前月份。如果当前月份与账单不存在交集，则默认选取账单的首尾时间统计
		//获取当前月份第一天
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date tempDate = cal.getTime();
		if (start.before(tempDate) && end.after(tempDate)) {
			start = tempDate;
			//获取当前月份最后一天
			cal.set(Calendar.DATE, 1); //主要就是这个roll方法   
			cal.roll(Calendar.DATE, -1);
			end = cal.getTime();
		}

		this.billList = billList;
		jp = new JPanel();
		jp.setLayout(null);
		jp.setSize(new Dimension(680, 700));

		JLabel jl1 = new JLabel("From:");
		jl1.setBounds(5, 5, 70, 22);
		jp.add(jl1);
		fromBtn = new DateChooserJButton(start, "选择统计开始日期");
		fromBtn.setBounds(2, 27, 70, 22);
		fromBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				showCal();
			}
		});
		jp.add(fromBtn);
		JLabel jl2 = new JLabel("To:");
		jl2.setBounds(5, 50, 70, 22);
		jp.add(jl2);
		toBtn = new DateChooserJButton(end, "选择统计结束日期");
		toBtn.setBounds(2, 72, 70, 22);
		toBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				showCal();
			}
		});
		jp.add(toBtn);
		JLabel jl3 = new JLabel("统计方式:");
		jl3.setBounds(5, 100, 70, 22);
		jp.add(jl3);
		typeSelect = new JComboBox<String>(new String[] { "日", "周", "月", "年" });
		typeSelect.setBounds(2, 122, 70, 22);
		typeSelect.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				showCal();
			}
		});
		jp.add(typeSelect);

		int jbStart = 200;
		int jbHeight = 22;
		int jbInterval = 2;
		int jbY = 10;
		int jbWidth = 80;
		int jbX = 0;
		weekJb = new JButton("本周");
		weekJb.setBounds(jbX, jbStart, jbWidth, jbHeight);
		weekJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getThisWeek(new Date()));
			}
		});
		jp.add(weekJb);

		monthJb = new JButton("本月");
		monthJb.setBounds(jbX, jbStart + jbHeight + jbInterval, jbWidth, jbHeight);
		monthJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getThisMonth(new Date()));
			}
		});
		jp.add(monthJb);

		triMonthJb = new JButton("近三月");
		triMonthJb.setBounds(jbX, jbStart + jbHeight * 2 + jbInterval * 2, jbWidth, jbHeight);
		triMonthJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getThreeMonth(new Date()));
			}
		});
		jp.add(triMonthJb);

		halfYearJb = new JButton("近半年");
		halfYearJb.setBounds(jbX, jbStart + jbHeight * 3 + jbInterval * 3, jbWidth, jbHeight);
		halfYearJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getHalfYear(new Date()));
			}
		});
		jp.add(halfYearJb);

		yearJb = new JButton("本年");
		yearJb.setBounds(jbX, jbStart + jbHeight * 4 + jbInterval * 4, jbWidth, jbHeight);
		yearJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getThisYear(new Date()));
			}
		});
		jp.add(yearJb);

		lastWeekJb = new JButton("▲上一周");
		lastWeekJb.setBounds(jbX, jbStart + jbHeight * 5 + jbInterval * 5 + jbY, jbWidth, jbHeight);
		lastWeekJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getLastWeek(fromBtn.getDate()));
			}
		});
		jp.add(lastWeekJb);

		nextWeekJb = new JButton("▼下一周");
		nextWeekJb.setBounds(jbX, jbStart + jbHeight * 6 + jbInterval * 6 + jbY, jbWidth, jbHeight);
		nextWeekJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getNextWeek(toBtn.getDate()));
			}
		});
		jp.add(nextWeekJb);

		lastMonthJb = new JButton("▲上一月");
		lastMonthJb.setBounds(jbX, jbStart + jbHeight * 7 + jbInterval * 7 + jbY * 2, jbWidth, jbHeight);
		lastMonthJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getLastMonth(fromBtn.getDate()));
			}
		});
		jp.add(lastMonthJb);

		nextMonthJb = new JButton("▼下一月");
		nextMonthJb.setBounds(jbX, jbStart + jbHeight * 8 + jbInterval * 8 + jbY * 2, jbWidth, jbHeight);
		nextMonthJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getNextMonth(toBtn.getDate()));
			}
		});
		jp.add(nextMonthJb);

		lastYearJb = new JButton("▲上一年");
		lastYearJb.setBounds(jbX, jbStart + jbHeight * 9 + jbInterval * 9 + jbY * 3, jbWidth, jbHeight);
		lastYearJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getLastYear(fromBtn.getDate()));
			}
		});
		jp.add(lastYearJb);

		nextYearJb = new JButton("▼下一年");
		nextYearJb.setBounds(jbX, jbStart + jbHeight * 10 + jbInterval * 10 + jbY * 3, jbWidth, jbHeight);
		nextYearJb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeRangeByBtn(DateTool.getNextYear(toBtn.getDate()));
			}
		});
		jp.add(nextYearJb);

		indexJl = new JLabel("月份");
		indexJl.setBounds(100, 5, 70, 22);
		indexJl.setForeground(Color.blue);
		indexJl.setFont(new Font("仿宋", Font.BOLD, 14));
		jp.add(indexJl);
		JLabel jl4 = new JLabel("支出");
		jl4.setBounds(190, 5, 70, 22);
		jl4.setForeground(Color.blue);
		jl4.setFont(new Font("仿宋", Font.BOLD, 14));
		jp.add(jl4);
		JLabel jl5 = new JLabel("收入");
		jl5.setBounds(285, 5, 70, 22);
		jl5.setForeground(Color.blue);
		jl5.setFont(new Font("仿宋", Font.BOLD, 14));
		jp.add(jl5);

		labelJp = new JPanel();
		labelJp.setLayout(null);
		jsp = new JScrollPane();
		jsp.setBounds(80, 30, 290, 600);
		jsp.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		jsp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		jsp.getVerticalScrollBar().setUnitIncrement(50);
		jp.add(jsp);

		JLabel jl6 = new JLabel("总计");
		jl6.setBounds(30, 635, 70, 22);
		jl6.setForeground(Color.blue);
		jl6.setFont(new Font("仿宋", Font.BOLD, 14));
		jp.add(jl6);
		countJl = new JLabel("12");
		countJl.setBounds(110, 635, 70, 22);
		countJl.setFont(new Font("仿宋", Font.BOLD, 14));
		jp.add(countJl);
		costJl = new JLabel("0");
		costJl.setBounds(170, 635, 150, 22);
		costJl.setFont(new Font("仿宋", Font.BOLD, 14));
		costJl.setForeground(Color.red);
		jp.add(costJl);
		earnJl = new JLabel("0");
		earnJl.setBounds(280, 635, 150, 22);
		earnJl.setFont(new Font("仿宋", Font.BOLD, 14));
		earnJl.setForeground(new Color(46, 139, 87));
		jp.add(earnJl);
		profitJl = new JLabel("0");
		profitJl.setBounds(390, 635, 150, 22);
		profitJl.setFont(new Font("仿宋", Font.BOLD, 14));
		profitJl.setForeground(new Color(46, 139, 87));
		jp.add(profitJl);

		JPanel jp2 = new JPanel();
		jp2.setBounds(372, 30, 420, 600);
		jp2.setBorder(BorderFactory.createLineBorder(Color.black));
		jp2.setLayout(null);
		jtp = new JTextPane();
		jtp.setContentType("text/html");
		jtp.setEditable(false);
		jtp.setText("");
		JScrollPane jsp2 = new JScrollPane(jtp);
		jsp2.setBounds(0, 0, 420, 600);
		jp2.add(jsp2);
		jp.add(jp2);

		showCal();

		this.setBounds(xPos, yPos, 800, 690);
		this.setTitle("统计账单：" + title);
		this.add(jp);
		this.setVisible(true);
		this.setResizable(false);
	}

	/**
	 * 处理分析数据
	 * 
	 * @param billList
	 * @return
	 */
	private void showCal() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date start = new Date();
		Date end = new Date();
		try {
			start = dateFormat.parse(fromBtn.getText());
			end = dateFormat.parse(toBtn.getText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (start.after(end)) {
			labelJp.removeAll();
			labelJp.setPreferredSize(new Dimension(0, 0));
			jsp.setViewportView(labelJp);
			jsp.repaint();
			return;
		}
		ArrayList<CalBillPanel> showBillList = new ArrayList<>();
		switch (typeSelect.getSelectedIndex()) {
		case 0:
			showBillList = calByDay(start, end);
			break;
		case 1:
			showBillList = calByWeek(start, end);
			break;
		case 2:
			showBillList = calByMonth(start, end);
			break;
		case 3:
			showBillList = calByYear(start, end);
			break;
		default:
			break;
		}
		drawBillPanel(showBillList);

		showCalSum(start, end);
	}

	/**
	 * 按周统计
	 * <li></li>
	 * 
	 * @return
	 */
	private ArrayList<CalBillPanel> calByWeek(Date start, Date end) {
		indexJl.setText("周");
		ArrayList<CalBillPanel> showList = new ArrayList<CalBillPanel>();
		DateRangeWeek dateRangeWeek = new DateRangeWeek(start, end);

		DateCom dateCom = null;
		int index = 0;
		int count = 0;
		Calendar calendar = Calendar.getInstance();
		String tempTip = "";
		ArrayList<Bill> tempBillList = new ArrayList<Bill>();

		BigDecimal cost = new BigDecimal(0);
		BigDecimal earn = new BigDecimal(0);

		costAll = new BigDecimal(0);
		earnAll = new BigDecimal(0);

		while ((dateCom = dateRangeWeek.getNext()) != null) {
			calendar.setTime(dateCom.start);
			tempTip = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "-";
			calendar.setTime(dateCom.end);
			tempTip += (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
			tempBillList = new ArrayList<Bill>();

			cost = new BigDecimal(0);
			earn = new BigDecimal(0);

			for (; index < billList.size(); index++) {
				Bill bill = billList.get(index);
				if (!bill.getDate().before(dateCom.start) && !bill.getDate().after(dateCom.end)) {
					if (bill.getMoney().startsWith("-")) {
						BigDecimal temp = new BigDecimal(bill.getMoney());
						cost = cost.add(temp);
						costAll = costAll.add(temp);
					} else {
						BigDecimal temp = new BigDecimal(bill.getMoney());
						earn = earn.add(temp);
						earnAll = earnAll.add(temp);
					}
					tempBillList.add(bill);
				}
				if (bill.getDate().after(dateCom.end)) {
					break;
				}
			}
			if (!tempBillList.isEmpty()) {
				CalBillPanel calBillPanel = new CalBillPanel(0, count * 30, tempTip, cost.negate().toString(),
						earn.toString(), tempBillList);
				showList.add(calBillPanel);
				count++;
			}
		}
		return showList;
	}

	/**
	 * 按月统计
	 * 
	 * @return
	 */
	private ArrayList<CalBillPanel> calByMonth(Date start, Date end) {
		indexJl.setText("月份");
		ArrayList<CalBillPanel> showList = new ArrayList<CalBillPanel>();
		Calendar calendar = Calendar.getInstance();
		int year = 1900;
		int month = -1;
		BigDecimal cost = new BigDecimal(0);
		BigDecimal earn = new BigDecimal(0);

		costAll = new BigDecimal(0);
		earnAll = new BigDecimal(0);
		ArrayList<Bill> tempBillList = new ArrayList<Bill>();
		int count = 0;
		for (Bill bill : billList) {
			if (bill.getDate().compareTo(start) >= 0 && bill.getDate().compareTo(end) <= 0) {
				calendar.setTime(bill.getDate());
				if (month == -1) {
					year = calendar.get(Calendar.YEAR);
					month = calendar.get(Calendar.MONTH) + 1;
					tempBillList.add(bill);
					if (bill.getMoney().startsWith("-")) {
						cost = new BigDecimal(bill.getMoney());
						costAll = costAll.add(cost);
					} else {
						earn = new BigDecimal(bill.getMoney());
						earnAll = earnAll.add(earn);
					}
				} else {
					int yearNow = calendar.get(Calendar.YEAR);
					int monthNow = calendar.get(Calendar.MONTH) + 1;
					if (yearNow == year && monthNow == month) {
						tempBillList.add(bill);
						if (bill.getMoney().startsWith("-")) {
							BigDecimal temp = new BigDecimal(bill.getMoney());
							cost = cost.add(temp);
							costAll = costAll.add(temp);
						} else {
							BigDecimal temp = new BigDecimal(bill.getMoney());
							earn = earn.add(temp);
							earnAll = earnAll.add(temp);
						}
					} else {
						String tip = year + "/" + (month > 9 ? month : "0" + month);
						CalBillPanel calBillPanel = new CalBillPanel(0, count * 30, tip, cost.negate().toString(),
								earn.toString(), tempBillList);
						showList.add(calBillPanel);
						tempBillList = new ArrayList<Bill>();
						tempBillList.add(bill);
						count++;
						year = yearNow;
						month = monthNow;
						if (bill.getMoney().startsWith("-")) {
							cost = new BigDecimal(bill.getMoney());
							earn = new BigDecimal(0);
							costAll = costAll.add(cost);
						} else {
							cost = new BigDecimal(0);
							earn = new BigDecimal(bill.getMoney());
							earnAll = earnAll.add(earn);
						}
					}
				}
			}
		}
		if (month != -1) {
			String tip = year + "/" + (month > 9 ? month : "0" + month);
			CalBillPanel calBillPanel = new CalBillPanel(0, count * 30, tip, cost.negate().toString(), earn.toString(),
					tempBillList);
			showList.add(calBillPanel);
		}
		return showList;
	}

	/**
	 * 按日统计
	 * 
	 * @return
	 */
	private ArrayList<CalBillPanel> calByDay(Date start, Date end) {
		indexJl.setText("日期");
		ArrayList<CalBillPanel> showList = new ArrayList<CalBillPanel>();
		Calendar calendar = Calendar.getInstance();
		String weekDay = "";
		int year = 1900;
		int month = -1;
		int day = -1;
		BigDecimal cost = new BigDecimal(0);
		BigDecimal earn = new BigDecimal(0);

		costAll = new BigDecimal(0);
		earnAll = new BigDecimal(0);
		ArrayList<Bill> tempBillList = new ArrayList<Bill>();
		int count = 0;
		for (Bill bill : billList) {
			if (bill.getDate().compareTo(start) >= 0 && bill.getDate().compareTo(end) <= 0) {
				calendar.setTime(bill.getDate());
				if (month == -1) {
					year = calendar.get(Calendar.YEAR);
					month = calendar.get(Calendar.MONTH) + 1;
					day = calendar.get(Calendar.DAY_OF_MONTH);
					weekDay = DateTool.getWeekDay(bill.getDate());
					weekDay = weekDay.length() > 1 ? weekDay.substring(1) : weekDay;
					tempBillList.add(bill);
					if (bill.getMoney().startsWith("-")) {
						cost = new BigDecimal(bill.getMoney());
						costAll = costAll.add(cost);
					} else {
						earn = new BigDecimal(bill.getMoney());
						earnAll = earnAll.add(earn);
					}
				} else {
					int yearNow = calendar.get(Calendar.YEAR);
					int monthNow = calendar.get(Calendar.MONTH) + 1;
					int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
					if (yearNow == year && monthNow == month && dayNow == day) {
						tempBillList.add(bill);
						if (bill.getMoney().startsWith("-")) {
							BigDecimal temp = new BigDecimal(bill.getMoney());
							cost = cost.add(temp);
							costAll = costAll.add(temp);
						} else {
							BigDecimal temp = new BigDecimal(bill.getMoney());
							earn = earn.add(temp);
							earnAll = earnAll.add(temp);
						}
					} else {
						String tip = weekDay + " " + year + "/" + (month > 9 ? month : "0" + month) + "/"
								+ (day > 9 ? day : "0" + day);
						CalBillPanel calBillPanel = new CalBillPanel(0, count * 30, tip, cost.negate().toString(),
								earn.toString(), tempBillList);
						showList.add(calBillPanel);
						tempBillList = new ArrayList<Bill>();
						tempBillList.add(bill);
						count++;
						year = yearNow;
						month = monthNow;
						day = dayNow;
						weekDay = DateTool.getWeekDay(bill.getDate());
						weekDay = weekDay.length() > 1 ? weekDay.substring(1) : weekDay;
						if (bill.getMoney().startsWith("-")) {
							cost = new BigDecimal(bill.getMoney());
							earn = new BigDecimal(0);
							costAll = costAll.add(cost);
						} else {
							cost = new BigDecimal(0);
							earn = new BigDecimal(bill.getMoney());
							earnAll = earnAll.add(earn);
						}
					}
				}
			}
		}
		if (month != -1) {
			String tip = weekDay + " " + year + "/" + (month > 9 ? month : "0" + month) + "/"
					+ (day > 9 ? day : "0" + day);
			CalBillPanel calBillPanel = new CalBillPanel(0, count * 30, tip, cost.negate().toString(), earn.toString(),
					tempBillList);
			showList.add(calBillPanel);
		}
		return showList;
	}

	/**
	 * 按年统计
	 * 
	 * @return
	 */
	private ArrayList<CalBillPanel> calByYear(Date start, Date end) {
		indexJl.setText("年份");
		ArrayList<CalBillPanel> showList = new ArrayList<CalBillPanel>();
		Calendar calendar = Calendar.getInstance();
		int year = 1900;
		BigDecimal cost = new BigDecimal(0);
		BigDecimal earn = new BigDecimal(0);

		costAll = new BigDecimal(0);
		earnAll = new BigDecimal(0);
		ArrayList<Bill> tempBillList = new ArrayList<Bill>();
		int count = 0;
		for (Bill bill : billList) {
			if (bill.getDate().compareTo(start) >= 0 && bill.getDate().compareTo(end) <= 0) {
				calendar.setTime(bill.getDate());
				if (year == 1900) {
					year = calendar.get(Calendar.YEAR);
					tempBillList.add(bill);
					if (bill.getMoney().startsWith("-")) {
						cost = new BigDecimal(bill.getMoney());
						costAll = costAll.add(cost);
					} else {
						earn = new BigDecimal(bill.getMoney());
						earnAll = earnAll.add(earn);
					}
				} else {
					int yearNow = calendar.get(Calendar.YEAR);
					int monthNow = calendar.get(Calendar.MONTH) + 1;
					if (yearNow == year) {
						tempBillList.add(bill);
						if (bill.getMoney().startsWith("-")) {
							BigDecimal temp = new BigDecimal(bill.getMoney());
							cost = cost.add(temp);
							costAll = costAll.add(temp);
						} else {
							BigDecimal temp = new BigDecimal(bill.getMoney());
							earn = earn.add(temp);
							earnAll = earnAll.add(temp);
						}
					} else {
						String tip = year + "";
						CalBillPanel calBillPanel = new CalBillPanel(0, count * 30, tip, cost.negate().toString(),
								earn.toString(), tempBillList);
						showList.add(calBillPanel);
						tempBillList = new ArrayList<Bill>();
						tempBillList.add(bill);
						count++;
						year = yearNow;
						if (bill.getMoney().startsWith("-")) {
							cost = new BigDecimal(bill.getMoney());
							earn = new BigDecimal(0);
							costAll = costAll.add(cost);
						} else {
							cost = new BigDecimal(0);
							earn = new BigDecimal(bill.getMoney());
							earnAll = earnAll.add(earn);
						}
					}
				}
			}
		}
		if (year != 1900) {
			String tip = year + "";
			CalBillPanel calBillPanel = new CalBillPanel(0, count * 30, tip, cost.negate().toString(), earn.toString(),
					tempBillList);
			showList.add(calBillPanel);
		}
		return showList;
	}

	/**
	 * 显示数据
	 */
	private void drawBillPanel(ArrayList<CalBillPanel> showList) {
		labelJp.removeAll();
		labelJp.setPreferredSize(new Dimension(280, 30 * showList.size()));
		for (CalBillPanel calBillPanel : showList) {
			labelJp.add(calBillPanel);
		}
		jsp.setViewportView(labelJp);
		jsp.repaint();

		countJl.setText(showList.size() + "");
		costJl.setText(costAll.negate().toString());
		earnJl.setText(earnAll.toString());

		BigDecimal profitDecimal = earnAll.add(costAll);
		if (profitDecimal.compareTo(new BigDecimal(0)) > 0) {
			profitJl.setForeground(new Color(46, 139, 87));
		} else {
			profitJl.setForeground(Color.red);
		}
		profitJl.setText(profitDecimal.toString());

	}

	/**
	 * <li>通过按钮重新统计日期账单，按钮包括本周,本月...</li>
	 * 
	 * @param dateList
	 */
	private void changeRangeByBtn(List<Date> dateList) {
		fromBtn.setDate(dateList.get(0));
		toBtn.setDate(dateList.get(1));
		showCal();
	}

	/**
	 * 计算显示起止日期的账单分析数据
	 * 
	 * @param start
	 * @param end
	 */
	private void showCalSum(Date start, Date end) {
		List<Bill> calList = billList.stream()
				.filter(bill -> bill.getDate().compareTo(start) >= 0 && bill.getDate().compareTo(end) <= 0)
				.collect(Collectors.toList());
		allStr = "<div style='background-color:green'>" + calAvg(start, end, calList) + calSum(calList) + "</div>";
		jtp.setText(allStr);
	}

	/**
	 * 计算所给账单的平均数信息
	 * 
	 * @param calList
	 * @return
	 */
	public String calAvg(Date start, Date end, List<Bill> calList) {
		int days = DateTool.getDayNum(start, end);
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		StringBuilder costSb = new StringBuilder(
				"<span style='color:orange'>从" + format.format(start) + "到" + format.format(end) + "统计如下：</span><br>");
		StringBuilder earnSb = new StringBuilder();
		BigDecimal cost = new BigDecimal(0);
		BigDecimal earn = new BigDecimal(0);
		for (Bill bill : calList) {
			if (bill.getMoney().startsWith("-")) {
				cost = cost.add(new BigDecimal(bill.getMoney()));
			} else {
				earn = earn.add(new BigDecimal(bill.getMoney()));
			}
		}

		if (days <= 0) {
			return "";
		}

		BigDecimal costAvg = cost.divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_UP);
		costSb.append("平均每天花费" + costAvg.negate() + "元");
		BigDecimal earnAvg = earn.divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_UP);
		earnSb.append("平均每天赚取" + earnAvg + "元");

		if (days >= 7) {
			int divider = days / 7;
			costAvg = cost.divide(new BigDecimal(divider), 2, BigDecimal.ROUND_HALF_UP);
			costSb.append(";平均每周花费" + costAvg.negate() + "元");
			earnAvg = earn.divide(new BigDecimal(divider), 2, BigDecimal.ROUND_HALF_UP);
			earnSb.append(";平均每周赚取" + earnAvg + "元");
		}

		if (days >= 30) {
			int divider = days / 30;
			costAvg = cost.divide(new BigDecimal(divider), 2, BigDecimal.ROUND_HALF_UP);
			costSb.append(";平均每月花费" + costAvg.negate() + "元");
			earnAvg = earn.divide(new BigDecimal(divider), 2, BigDecimal.ROUND_HALF_UP);
			earnSb.append(";平均每月赚取" + earnAvg + "元");
		}

		if (days >= 365) {
			int divider = days / 365;
			costAvg = cost.divide(new BigDecimal(divider), 2, BigDecimal.ROUND_HALF_UP);
			costSb.append(";平均每年花费" + costAvg.negate() + "元");
			earnAvg = earn.divide(new BigDecimal(divider), 2, BigDecimal.ROUND_HALF_UP);
			earnSb.append(";平均每年赚取" + earnAvg + "元");
		}

		return costSb.append("<br>").append(earnSb).append("<br><br>").toString();
	}

	/**
	 * 计算所给账单的综合分析信息
	 * 
	 * @param calList
	 * @return
	 */
	public static String calSum(List<Bill> calList) {
		Map<String, BigDecimal> costNumMap = new LinkedHashMap<String, BigDecimal>();
		PropertiesTool.costProMap.forEach((keyWord, value) -> {
			costNumMap.put(keyWord, new BigDecimal(0));
		});
		Map<String, BigDecimal> earnNumMap = new LinkedHashMap<String, BigDecimal>();
		PropertiesTool.earnProMap.forEach((keyWord, value) -> {
			earnNumMap.put(keyWord, new BigDecimal(0));
		});

		BigDecimal otherCost = new BigDecimal(0);
		BigDecimal otherEarn = new BigDecimal(0);

		BigDecimal costSum = new BigDecimal(0);
		BigDecimal earnSum = new BigDecimal(0);

		//遍历要计算的账单
		for (Bill bill : calList) {
			boolean addFlag = false;
			if (bill.getMoney().startsWith("-")) {
				costSum = costSum.add(new BigDecimal(bill.getMoney()));
				LOOP1: for (String key : PropertiesTool.costProMap.keySet()) {
					List<String> valueList = PropertiesTool.costProMap.get(key);
					for (String keyWord : valueList) {
						if (bill.getTip().contains(keyWord)) {
							BigDecimal num = costNumMap.get(key);
							num = num.add(new BigDecimal(bill.getMoney()));
							costNumMap.put(key, num);
							addFlag = true;
							break LOOP1;
						}
					}
				}
				if (!addFlag) {
					otherCost = otherCost.add(new BigDecimal(bill.getMoney()));
				}
			} else {
				earnSum = earnSum.add(new BigDecimal(bill.getMoney()));
				LOOP2: for (String key : PropertiesTool.earnProMap.keySet()) {
					List<String> valueList = PropertiesTool.earnProMap.get(key);
					for (String keyWord : valueList) {
						if (bill.getTip().contains(keyWord)) {
							BigDecimal num = earnNumMap.get(key);
							num = num.add(new BigDecimal(bill.getMoney()));
							earnNumMap.put(key, num);
							addFlag = true;
							break LOOP2;
						}
					}
				}
				if (!addFlag) {
					otherEarn = otherEarn.add(new BigDecimal(bill.getMoney()));
				}
			}
		}

		NumberFormat percent = NumberFormat.getPercentInstance();
		percent.setMaximumFractionDigits(2);
		BigDecimal floatVar = new BigDecimal(0);
		BigDecimal compareZero = new BigDecimal(0);
		StringBuilder sb = new StringBuilder(
				"<span style='color:orange'>收支归类如下：</span><br><table><tr><td><table><tr><th colspan='3'>花费</th></tr>");
		for (String key : costNumMap.keySet()) {
			sb.append("<tr><td>").append(key).append("</td>");
			BigDecimal temp = costNumMap.get(key);
			sb.append("<td>").append(temp.negate()).append("</td>");
			if (temp.compareTo(compareZero) == 0) {
				sb.append("<td>0</td></tr>");
			} else {
				floatVar = temp.divide(costSum, 2, BigDecimal.ROUND_HALF_UP);
				sb.append("<td>").append(percent.format(floatVar.doubleValue())).append("</td></tr>");
			}
		}
		sb.append("<tr><td>").append("其他").append("</td>");
		sb.append("<td>").append(otherCost.negate()).append("</td>");
		if (otherCost.compareTo(compareZero) == 0) {
			sb.append("<td>0</td></tr></table></td>");
		} else {
			floatVar = otherCost.divide(costSum, 2, BigDecimal.ROUND_HALF_UP);
			sb.append("<td>").append(percent.format(floatVar.doubleValue())).append("</td></tr></table></td>");
		}

		sb.append("<td>===(*^▽^*)===</td>");

		sb.append("<td><table><tr><th colspan='3'>收入</th></tr>");
		for (String key : earnNumMap.keySet()) {
			sb.append("<tr><td>").append(key).append("</td>");
			BigDecimal temp = earnNumMap.get(key);
			sb.append("<td>").append(temp).append("</td>");
			if (temp.compareTo(compareZero) == 0) {
				sb.append("<td>0</td></tr>");
			} else {
				floatVar = temp.divide(earnSum, 2, BigDecimal.ROUND_HALF_UP);
				sb.append("<td>").append(percent.format(floatVar.doubleValue())).append("</td></tr>");
			}
		}
		sb.append("<tr><td>").append("其他").append("</td>");
		sb.append("<td>").append(otherEarn).append("</td>");

		if (otherEarn.compareTo(compareZero) == 0) {
			sb.append("<td>0</td></tr></table></td></tr></table>");
		} else {
			floatVar = otherEarn.divide(earnSum, 2, BigDecimal.ROUND_HALF_UP);
			sb.append("<td>").append(percent.format(floatVar.doubleValue()))
					.append("</td></tr></table></td></tr></table>");
		}

		return sb.toString();
	}

	/**
	 * <li>用于将日期时间段划分为周返回</li>
	 * <li>不精确到时分秒</li>
	 * 
	 * @author shuil
	 *
	 */
	class DateRangeWeek {
		DateCom datecom;
		int index = 0;
		List<DateCom> weekList;

		public DateRangeWeek(Date start, Date end) {
			weekList = new ArrayList<CalBillFrame.DateCom>();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(start);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == 7) {
				//开始日期刚好是周六
				weekList.add(new DateCom(start, start));
			} else {
				calendar.add(Calendar.DATE, 7 - dayOfWeek);
				if (!calendar.getTime().before(end)) {
					//开始日期和结束日期刚好在一周中。否则开始循环计算周
					weekList.add(new DateCom(start, end));
				} else {
					//先把前面的部分周给加进去
					weekList.add(new DateCom(start, calendar.getTime()));
				}
			}
			//新的一周的第一天
			calendar.add(Calendar.DATE, 1);
			Date tempStart = calendar.getTime();
			calendar.add(Calendar.DATE, 6);
			Date tempEnd = calendar.getTime();
			while (!tempEnd.after(end)) {
				weekList.add(new DateCom(tempStart, tempEnd));
				calendar.add(Calendar.DATE, 1);
				tempStart = calendar.getTime();
				if (tempStart.after(end)) {
					break;
				}
				calendar.add(Calendar.DATE, 6);
				tempEnd = calendar.getTime();
			}
			//最后一周不满，也要补进去
			if (!tempStart.after(end)) {
				weekList.add(new DateCom(tempStart, end));
			}
			//System.out.println(weekList);
		}

		public DateCom getNext() {
			if (index < weekList.size()) {
				return weekList.get(index++);
			}
			return null;
		}

	}

	/**
	 * 日期组合类
	 * 
	 * @author shuil
	 *
	 */
	class DateCom {
		Date start, end;

		public DateCom(Date start, Date end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			return format.format(start) + "-" + format.format(end);
		}
	}

}
