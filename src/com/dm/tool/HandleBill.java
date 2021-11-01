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
 * �˵�������
 * 
 * @author shuil
 *
 */
public class HandleBill {

	// �˵��ļ�
	private static String fileDir;
	private static File billFile;
	private static ArrayList<File> billFileList;
	//���ڼ�¼Ҫ��������ӵ������˵��ļ����˵��±�
	private static ArrayList<Integer> transIndexList;
	// �˵�bean�б�
	public static volatile ArrayList<Bill> billList;
	//�����б� ������Ӧctrl+Z
	private static LinkedList<ReturnBill> returnList;
	//�����б� ������Ӧctrl+Y
	private static LinkedList<ReturnBill> forwardList;
	//��ϻ��˲���������һ��ʱ����¼��ǰʱ��startTime���߳��������ʱ�䳬����saveDelay���������򴥷�һ���ļ�����������������
	private Thread saveThread;
	private volatile long startTime;
	private static int saveDelay = 5;

	private static HandleBill handleBill = new HandleBill();

	// ���ƻ����ػ棨����˵���Ϣʱ/�л�ͳ������ʱ������
	private volatile boolean repaintView = false;
	// ���ƽ��������¼��㣨����˵���Ϣʱ/�л�ͳ������ʱ������
	private volatile boolean recalMoney = false;

	// �����˵��ļ�����
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
	 * �����˵�����
	 * 
	 * <li>�����ҳ���ϵĲ���</li>
	 * 
	 * @param index
	 *            �˵��ļ��±�
	 */
	public boolean makeBillList(int index) {
		if (billFileList.isEmpty()) {
			JOptionPane.showMessageDialog(null, "û�п��õ��˵��ļ�������billĿ¼�´���");
			return false;
		}
		initReturnList();
		billFile = billFileList.get(index);
		billList = getBillListFromFile(billFile);
		return true;
	}

	/**
	 * ���ļ��л���˵�����
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
	 * ͨ��ҳ���������һ���˵�
	 * 
	 * @param bill
	 */
	public void addBeanFromView(Bill bill) {
		//��ӷ��ؼ�¼
		returnList.add(new ReturnBill(0, addBean(bill), bill));

		reHandle();
		clearForwardList();
	}

	/**
	 * ͨ��ҳ�������޸��˵�
	 * <li>֮ǰ���޸Ĳ�֧���޸�ʱ�䣬���ڿ����޸��ˣ�����ֱ���滻�ᵼ���˵�˳�������</li>
	 * <li>����������Ϊһ���޸�=ɾ��+��ӡ���Ӧ�Ļ��˼�¼�ｫ�������޸ı�־���˵���(2021/05/15)</li>
	 * 
	 * @param index
	 * @param bill
	 */
	public void alterBillFromView(int index, Bill bill) {
		//��ɾ���ɵ�
		returnList.add(new ReturnBill(1, index, billList.get(index)));
		deleteBill(index);
		//������޸ĺ��
		returnList.add(new ReturnBill(0, addBean(bill), bill));

		reHandle();
		clearForwardList();
	}

	/**
	 * ͨ��ҳ������ɾ���˵�
	 * 
	 * @param index
	 */
	public void deleteBillFromView(int index) {
		//��ӷ��ؼ�¼
		returnList.add(new ReturnBill(1, index, billList.get(index)));
		deleteBill(index);
		reHandle();
		clearForwardList();
	}

	/**
	 * ��ǰ���������һ���˵�
	 * 
	 * @param bill
	 * @return
	 */
	private int addBean(Bill bill) {
		return addBean(bill, billList);
	}

	/**
	 * ����������������һ���˵�
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
	 * �޸��˵�
	 * <li></li>
	 */
	private void alterBill(int index, Bill bill) {
		billList.set(index, bill);/////TODO 
	}

	/**
	 * ɾ���˵�
	 * 
	 * @param index
	 */
	private void deleteBill(int index) {
		billList.remove(index);
	}

	/**
	 * ����һ���˵�����
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
	 * ǰ��һ���˵�����
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
	 * ���˲������߳̿�ʼ�����ļ������ʱ��
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
	 * �ػ� ����ͳ�� �����ļ� ������־λȫ����Ϊtrue
	 */
	private void reHandle() {
		repaintView = true;
		recalMoney = true;
		saveBill = true;
	}

	/**
	 * ��������ͳ�����
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
	 * �����µ��˵���������ļ�
	 */
	public void saveBillFile() {
		saveBillFile(billList, billFile);
	}

	/**
	 * ��ָ���˵��������ָ���ļ�
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
	 * �û���ҳ����������label�򴥷�
	 * <li>�����Ӧ�±��Ѿ��������Ƴ����������</li>
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
	 * ����ǰ�����е�ָ���˵�ͬ���������ļ���ȥ
	 * 
	 * @param index
	 */
	public void syncBillToSelectFile(List<Integer> selectFileIndexList, int index) {
		//����û��Ҽ����±������֮ǰѡ����˵������������
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
			//���򵥶����
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
	 * �ϲ���������µ��˵���
	 * 
	 * @param bill
	 *            �ϲ����ɵ����˵�
	 */
	public void combineBill(Bill bill) {
		//�±굹��������������ɾ����ʱ�򲻻ᵼ���±��λ
		Comparator<Integer> reverseComparator = Collections.reverseOrder();
		Collections.sort(transIndexList, reverseComparator);
		//System.out.println(transIndexList);

		//ɾ��ѡ����˵�
		for (Integer index : transIndexList) {
			returnList.add(new ReturnBill(1, index, billList.get(index)));
			deleteBill(index);
		}

		//��Ӻϲ�����˵�
		returnList.add(new ReturnBill(0, addBean(bill), bill));
		transIndexList = new ArrayList<Integer>();
		reHandle();
		clearForwardList();
	}

	/**
	 * ���������
	 * <li></li>
	 * 
	 * @param year
	 * @param day
	 * @param salaryArr
	 */
	public void alterSalary(String yearStr, String dayStr, String tipStr, String[] salaryArr) {
		//��ɾ�����й����˵�
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
	 * ��ȡʵ��Ӧ�ù��������
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
	 * ���õ�ǰ�˵�ΪĬ���˵�
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
	 * ��ȡ��ǰ�˵�
	 * 
	 * @return
	 */
	public ArrayList<Bill> getBillList() {
		return billList;
	}

	/**
	 * ��ȡ��ǰ�˵��ļ�
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
	 * ��ʼ����ԭ�б�
	 */
	public void initReturnList() {
		if (!returnList.isEmpty()) {
			returnList = new LinkedList<ReturnBill>();
		}
		clearForwardList();
	}

	/**
	 * ���forward�б�
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
