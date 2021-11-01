package com.dm.view;

import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dm.model.Bill;
import com.dm.model.ExtraBillPanel;

/**
 * 新开账单窗口
 * 
 * @author shuil
 *
 */
public class ExtraBillFrame extends JDialog {
	private JScrollPane jsp;
	JPanel labelJp, jp;
	JLabel calLabel;

	//控制每一个小账单的颜色
	Color[] labelColorArr = new Color[] { Color.green, Color.orange, Color.pink, Color.cyan };
	int colorSite = -1;

	public ExtraBillFrame(int xPos, int yPos, String title, ArrayList<Bill> billList) {
		jp = new JPanel();
		jp.setLayout(null);
		jp.setSize(new Dimension(375, 700));

		calLabel = new JLabel("", JLabel.LEFT);
		calLabel.setBounds(5, 2, 375, 22);
		jp.add(calLabel);

		labelJp = new JPanel();
		labelJp.setLayout(null);
		labelJp.setPreferredSize(new Dimension(375, 30 * billList.size()));
		jsp = new JScrollPane();
		jsp.setBounds(0, 30, 375, 630);
		jsp.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		jsp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		jsp.getVerticalScrollBar().setUnitIncrement(50);
		drawBillPanel(billList);
		jp.add(jsp);

		this.setBounds(xPos, yPos, 382, 690);
		this.setTitle("对比账单：" + title);
		this.add(jp);
		this.setVisible(true);
		this.setResizable(false);
	}

	/**
	 * 画账单
	 */
	private void drawBillPanel(ArrayList<Bill> billList) {
		colorSite = 0;
		labelJp.removeAll();
		labelJp.setPreferredSize(new Dimension(375, 30 * billList.size()));
		int index = 0;
		int month = -1;
		Color labelColor = getNextLabelColor();
		Calendar calendar = Calendar.getInstance();
		ArrayList<ShowBill> showBillList = makeShowList(billList);
		for (int i = showBillList.size() - 1; i >= 0; i--) {
			Bill bill = showBillList.get(i).bill;
			calendar.setTime(bill.getDate());
			if (calendar.get(Calendar.MONTH) != month) {
				month = calendar.get(Calendar.MONTH);
				labelColor = getNextLabelColor();
			}
			ExtraBillPanel billPanel = new ExtraBillPanel(0, index * 30, bill.getDateStr(),
					new BigDecimal(bill.getMoney()), bill.getTip(), showBillList.get(i).calMoney);
			billPanel.setBorder(BorderFactory.createLineBorder(labelColor));
			labelJp.add(billPanel);
			index++;
		}
		jsp.setViewportView(labelJp);
		jsp.repaint();
	}

	private ArrayList<ShowBill> makeShowList(ArrayList<Bill> billList) {
		ArrayList<ShowBill> showBillList = new ArrayList<ExtraBillFrame.ShowBill>();
		BigDecimal sum = new BigDecimal(0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < billList.size(); i++) {
			Bill bill = billList.get(i);
			if (i == 0) {
				sb.append(bill.getDateStr()).append(" —— ");
			}
			if (i == billList.size() - 1) {
				sb.append(bill.getDateStr()).append(" 共结余金额：");
			}
			sum = sum.add(new BigDecimal(bill.getMoney()));
			ShowBill showBill = new ShowBill(sum, bill);
			showBillList.add(showBill);
		}
		sb.append(sum);
		calLabel.setText(sb.toString());
		return showBillList;
	}

	/**
	 * 获取销账单的颜色
	 * <li></li>
	 * 
	 * @return
	 */
	private Color getNextLabelColor() {
		colorSite++;
		if (colorSite == labelColorArr.length) {
			colorSite = 0;
		}
		return labelColorArr[colorSite];
	}

	class ShowBill {
		BigDecimal calMoney;
		Bill bill;

		public ShowBill(BigDecimal calMoney, Bill bill) {
			this.calMoney = calMoney;
			this.bill = bill;
		}
	}
}
