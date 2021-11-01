package com.dm.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.dm.model.Bill;
import com.dm.model.ReturnBill;

/**
 * 账单处理类
 * 
 * @author shuil
 *
 */
public class HandleBill {

	// 账单文件
	private static String fileDir;
	private static File billFile;
	private static ArrayList<File> billFileList;
	//用于记录要被批量添加到其他账单文件的账单下标
	private static ArrayList<Integer> transIndexList;
	// 账单bean列表
	public static volatile ArrayList<Bill> billList;
	//回退列表 用于响应ctrl+Z
	private static LinkedList<ReturnBill> returnList;
	//回退列表 用于响应ctrl+Y
	private static LinkedList<ReturnBill> forwardList;
	//配合回退操作，回退一次时，记录当前时间startTime；线程运行如果时间超过了saveDelay的秒数，则触发一次文件保存操作，任务结束
	private Thread saveThread;
	private volatile long startTime;
	private static int saveDelay = 5;

	private static HandleBill handleBill = new HandleBill();

	// 控制画面重绘（添加账单信息时/切换统计日期时触发）
	private volatile boolean repaintView = false;
	// 控制金额结余重新计算（添加账单信息时/切换统计日期时触发）
	private volatile boolean recalMoney = false;

	// 控制账单文件更新
	private volatile boolean saveBill = false;

	public boolean repaint;

	static {
		billFileList = new ArrayList<File>();
		returnList = new LinkedList<ReturnBill>();
		forwardList = new LinkedList<ReturnBill>();
		billList = new ArrayList<Bill>();
		transIndexList = new ArrayList<Integer>();
		File dir = new File("");
		try {
			fileDir = dir.getCanonicalPath().concat(File.separator).concat("bill");
			dir = new File(fileDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!dir.exists()) {
			dir.mkdir();
		}
		File defaultFile = null;
		if (dir.isDirectory()) {
			File[] fileArr = dir.listFiles();
			for (File file : fileArr) {
				if (file.isFile() && file.getName().endsWith(".txt")) {
					billFileList.add(file);
					if (file.getName().equals(PropertiesTool.defaultFileName)) {
						defaultFile = file;
					}
				}
			}
		}
		if (defaultFile != null) {
			billFileList.remove(defaultFile);
			billFileList.add(0, defaultFile);
		}
	}

	private HandleBill() {
	}

	public static HandleBill getInstance() {
		return handleBill;
	}

	/**
	 * 创建账单链表
	 * 
	 * <li>针对于页面上的操作</li>
	 * 
	 * @param index
	 *            账单文件下标
	 */
	public boolean makeBillList(int index) {
		if (billFileList.isEmpty()) {
			JOptionPane.showMessageDialog(null, "没有可用的账单文件，请在bill目录下创建");
			return false;
		}
		initReturnList();
		billFile = billFileList.get(index);
		billList = getBillListFromFile(billFile);
		return true;
	}

	/**
	 * 从文件中获得账单链表
	 * <li></li>
	 * 
	 * @param file
	 * @return
	 */
	public ArrayList<Bill> getBillListFromFile(File file) {
		ArrayList<Bill> fileBillList = new ArrayList<Bill>();
		try (BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			String tempString;
			while ((tempString = buff.readLine()) != null) {
				if (!tempString.trim().equals("")) {
					String[] arr = tempString.trim().split("# #");
					if (arr.length >= 3) {
						Bill bill = new Bill(arr[0], arr[1], arr[2]);
						fileBillList.add(bill);
					} else if (arr.length >= 2) {
						Bill bill = new Bill(arr[0], arr[1], "");
						fileBillList.add(bill);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileBillList;
	}

	/**
	 * 通过页面主动添加一个账单
	 * 
	 * @param bill
	 */
	public void addBeanFromView(Bill bill) {
		//添加返回记录
		returnList.add(new ReturnBill(0, addBean(bill), bill));

		reHandle();
		clearForwardList();
	}

	/**
	 * 通过页面主动修改账单
	 * <li>之前的修改不支持修改时间，现在可以修改了，但是直接替换会导致账单顺序出问题</li>
	 * <li>所以现在认为一次修改=删除+添加。对应的回退记录里将不存在修改标志的账单了(2021/05/15)</li>
	 * 
	 * @param index
	 * @param bill
	 */
	public void alterBillFromView(int index, Bill bill) {
		//先删除旧的
		returnList.add(new ReturnBill(1, index, billList.get(index)));
		deleteBill(index);
		//再添加修改后的
		returnList.add(new ReturnBill(0, addBean(bill), bill));

		reHandle();
		clearForwardList();
	}

	/**
	 * 通过页面主动删除账单
	 * 
	 * @param index
	 */
	public void deleteBillFromView(int index) {
		//添加返回记录
		returnList.add(new ReturnBill(1, index, billList.get(index)));
		deleteBill(index);
		reHandle();
		clearForwardList();
	}

	/**
	 * 向当前链表中添加一个账单
	 * 
	 * @param bill
	 * @return
	 */
	private int addBean(Bill bill) {
		return addBean(bill, billList);
	}

	/**
	 * 向给定的链表中添加一个账单
	 * 
	 * @param bill
	 * @return
	 */
	private int addBean(Bill bill, ArrayList<Bill> currentBillList) {
		int index = 0;
		if (currentBillList.isEmpty() || currentBillList.get(0).getDate().after(bill.getDate())) {
			currentBillList.add(0, bill);
		} else if (currentBillList.get(currentBillList.size() - 1).getDate().before(bill.getDate())
				|| currentBillList.get(currentBillList.size() - 1).getDate().compareTo(bill.getDate()) == 0) {
			index = currentBillList.size();
			currentBillList.add(bill);
		} else {
			for (int i = 0; i < currentBillList.size(); i++) {
				if (currentBillList.get(i).getDate().after(bill.getDate())) {
					index = i;
					currentBillList.add(i, bill);
					break;
				}
			}
		}
		return index;
	}

	/**
	 * 修改账单
	 * <li></li>
	 */
	private void alterBill(int index, Bill bill) {
		billList.set(index, bill);/////TODO 
	}

	/**
	 * 删除账单
	 * 
	 * @param index
	 */
	private void deleteBill(int index) {
		billList.remove(index);
	}

	/**
	 * 撤销一次账单操作
	 */
	public void returnBill() {
		if (returnList.isEmpty()) {
			return;
		}
		ReturnBill returnBill = returnList.removeLast();
		switch (returnBill.getType()) {
		case 0:
			deleteBill(returnBill.getIndex());
			returnBill.setType(1);
			forwardList.add(returnBill);
			break;
		case 1:
			addBean(returnBill.getBill());
			returnBill.setType(0);
			forwardList.add(returnBill);
			break;
		case 2:
			forwardList.add(new ReturnBill(2, returnBill.getIndex(), billList.get(returnBill.getIndex())));
			alterBill(returnBill.getIndex(), returnBill.getBill());
			break;
		default:
			return;
		}
		repaintView = true;
		recalMoney = true;
		prepareSaveFile();
	}

	/**
	 * 前进一次账单操作
	 */
	public void forwardBill() {
		if (forwardList.isEmpty()) {
			return;
		}
		ReturnBill forwardBill = forwardList.removeLast();
		switch (forwardBill.getType()) {
		case 0:
			forwardBill.setType(1);
			returnList.add(forwardBill);
			deleteBill(forwardBill.getIndex());
			break;
		case 1:
			addBean(forwardBill.getBill());
			forwardBill.setType(0);
			returnList.add(forwardBill);
			break;
		case 2:
			returnList.add(new ReturnBill(2, forwardBill.getIndex(), billList.get(forwardBill.getIndex())));
			alterBill(forwardBill.getIndex(), forwardBill.getBill());
			break;
		default:
			return;
		}
		repaintView = true;
		recalMoney = true;
		prepareSaveFile();
	}

	/**
	 * 回退操作后，线程开始计算文件保存的时机
	 * <li></li>
	 */
	private void prepareSaveFile() {
		startTime = System.currentTimeMillis();
		if (saveThread == null || !saveThread.isAlive()) {
			saveThread = new Thread(() -> {
				while (true) {
					if ((System.currentTimeMillis() - startTime) >= saveDelay * 1000) {
						saveBill = true;
						break;
					}
				}
			});
			saveThread.start();
		}
	}

	/**
	 * 重绘 重新统计 保存文件 三个标志位全部置为true
	 */
	private void reHandle() {
		repaintView = true;
		recalMoney = true;
		saveBill = true;
	}

	/**
	 * 根据日期统计余额
	 * 
	 * @param date
	 */
	public BigDecimal calMoney(Date date) {
		BigDecimal money = new BigDecimal(0);
		for (Bill bill : billList) {
			if (bill.getDate().compareTo(date) <= 0) {
				money = money.add(new BigDecimal(bill.getMoney()));
			}
		}
		return money;
	}

	/**
	 * 将最新的账单链表存入文件
	 */
	public void saveBillFile() {
		saveBillFile(billList, billFile);
	}

	/**
	 * 将指定账单链表存入指定文件
	 */
	private void saveBillFile(ArrayList<Bill> saveList, File saveFile) {
		StringBuilder sb = new StringBuilder();
		for (Bill bill : saveList) {
			sb.append(bill.getDateStr()).append("# #").append(bill.getMoney().toString()).append("# #")
					.append(bill.getTip()).append("\n");
		}
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), "UTF-8"));
			writer.write(sb.toString());
			writer.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 用户在页面上左键点击label则触发
	 * <li>如果对应下标已经存在则移除，否则添加</li>
	 * 
	 * @param index
	 */
	public void selectIndex(Integer index) {
		if (transIndexList.contains(index)) {
			transIndexList.remove(index);
		} else {
			transIndexList.add(index);
		}
	}

	/**
	 * 将当前链表中的指定账单同步到所有文件中去
	 * 
	 * @param index
	 */
	public void syncBillToSelectFile(List<Integer> selectFileIndexList, int index) {
		//如果用户右键的下标包含在之前选择的账单里则批量添加
		if (transIndexList.contains(index)) {
			new Thread(() -> {
				selectFileIndexList.forEach(i -> {
					File file = billFileList.get(i);
					ArrayList<Bill> nowBillList = getBillListFromFile(file);
					if (!file.getName().equals(billFile.getName())) {
						for (Integer billIndex : transIndexList) {
							Bill bill = billList.get(billIndex);
							addBean(bill, nowBillList);
						}
					}
					saveBillFile(nowBillList, file);
				});
				transIndexList = new ArrayList<Integer>();
				repaintView = true;
			}).start();
		} else {
			//否则单独添加
			Bill bill = billList.get(index);
			new Thread(() -> {
				selectFileIndexList.forEach(i -> {
					File file = billFileList.get(i);
					if (!file.getName().equals(billFile.getName())) {
						ArrayList<Bill> nowBillList = getBillListFromFile(file);
						addBean(bill, nowBillList);
						saveBillFile(nowBillList, file);
					}
				});
			}).start();
		}
	}

	/**
	 * 合并多个链表到新的账单中
	 * 
	 * @param bill
	 *            合并生成的新账单
	 */
	public void combineBill(Bill bill) {
		//下标倒序排序，这样遍历删除的时候不会导致下标错位
		Comparator<Integer> reverseComparator = Collections.reverseOrder();
		Collections.sort(transIndexList, reverseComparator);
		//System.out.println(transIndexList);

		//删除选择的账单
		for (Integer index : transIndexList) {
			returnList.add(new ReturnBill(1, index, billList.get(index)));
			deleteBill(index);
		}

		//添加合并后的账单
		returnList.add(new ReturnBill(0, addBean(bill), bill));
		transIndexList = new ArrayList<Integer>();
		reHandle();
		clearForwardList();
	}

	/**
	 * 处理工资添加
	 * <li></li>
	 * 
	 * @param year
	 * @param day
	 * @param salaryArr
	 */
	public void alterSalary(String yearStr, String dayStr, String tipStr, String[] salaryArr) {
		//先删除已有工资账单
		String trimTip = tipStr.trim();
		if (!"".equals(tipStr)) {
			billList.removeIf(
					bill -> bill.getDateStr().substring(0, 4).equals(yearStr) && trimTip.equals(bill.getTip()));
		}
		int year = Integer.parseInt(yearStr);
		int day = Integer.parseInt(dayStr);
		for (int i = 0; i < 12; i++) {
			Bill bill = new Bill(getRealTime(year, i + 1, day), salaryArr[i], trimTip);
			addBean(bill, billList);
		}
		reHandle();
	}

	/**
	 * 获取实际应该构造的日期
	 * <li></li>
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private String getRealTime(int year, int month, int day) {
		if (day <= 28) {
			return year + (month < 10 ? "/0" : "/") + month + (day < 10 ? "/0" : "/") + day;
		}
		int max = 0;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			max = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			max = 30;
			break;
		case 2:
			if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
				max = 29;
			} else {
				max = 28;
			}
			break;
		default:
			break;

		}
		return year + (month < 10 ? "/0" : "/") + month + "/" + (day <= max ? day : max);
	}

	/**
	 * 设置当前账单为默认账单
	 */
	public void setDefaultFile() {
		File proFile = new File("setting.properties");
		if (!proFile.exists()) {
			try {
				proFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String fileName = billFile.getName();
		Properties pro = new Properties();
		try {
			pro.load(new InputStreamReader(new FileInputStream(proFile), "UTF-8"));
			pro.setProperty("default", fileName);
			pro.store(new OutputStreamWriter(new FileOutputStream(proFile), "UTF-8"), "default bill file");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<File> getBillFileList() {
		return billFileList;
	}

	/**
	 * 获取当前账单
	 * 
	 * @return
	 */
	public ArrayList<Bill> getBillList() {
		return billList;
	}

	/**
	 * 获取当前账单文件
	 * 
	 * @return
	 */
	public File getBillFile() {
		return billFile;
	}

	public boolean getRepaintFlag() {
		return repaintView;
	}

	public boolean getCalFlag() {
		return recalMoney;
	}

	public boolean getSaveBill() {
		return saveBill;
	}

	public void resetRepaint() {
		repaintView = false;
	}

	public void resetCal() {
		recalMoney = false;
	}

	public void resetSaveBill() {
		saveBill = false;
	}

	public void reCal() {
		recalMoney = true;
	}

	/**
	 * 初始化还原列表
	 */
	public void initReturnList() {
		if (!returnList.isEmpty()) {
			returnList = new LinkedList<ReturnBill>();
		}
		clearForwardList();
	}

	/**
	 * 清空forward列表
	 */
	public void clearForwardList() {
		if (!forwardList.isEmpty()) {
			forwardList = new LinkedList<ReturnBill>();
		}
	}

	public String getDir() {
		return fileDir;
	}

	public void initTransIndexList() {
		transIndexList = new ArrayList<Integer>();
	}

	public List<Integer> getTransIndexList() {
		return transIndexList;
	}
}
