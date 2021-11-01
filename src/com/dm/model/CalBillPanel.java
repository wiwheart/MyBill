package com.dm.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dm.tool.DateTool;
import com.dm.view.BillMainFrame;
import com.dm.view.CalBillFrame;

public class CalBillPanel extends JPanel {

	private JLabel costLabel, earnLabel, tipLabel;

	//��ǰ��Ƭ��Ӧ���˵�
	private ArrayList<Bill> billList;

	public CalBillPanel(int xPos, int yPos, String dateRange, String cost, String earn, ArrayList<Bill> billList) {
		this.billList = billList;
		this.setLayout(null);
		this.setBounds(xPos, yPos, 375, 30);

		tipLabel = new JLabel(dateRange, JLabel.CENTER);
		tipLabel.setBounds(-5, 0, 100, 30);
		if (dateRange.length() < 10) {
			tipLabel.setFont(new Font("����", Font.BOLD, 14));
		} else {
			tipLabel.setFont(new Font("����", Font.BOLD, 12));
		}
		this.add(tipLabel);

		costLabel = new JLabel(cost, JLabel.CENTER);
		costLabel.setForeground(Color.red);
		costLabel.setBounds(80, 0, 100, 30);
		costLabel.setFont(new Font("����", Font.BOLD, 14));
		this.add(costLabel);

		earnLabel = new JLabel(earn, JLabel.CENTER);
		earnLabel.setForeground(new Color(46, 139, 87));
		earnLabel.setBounds(170, 0, 100, 30);
		earnLabel.setFont(new Font("����", Font.BOLD, 14));
		this.add(earnLabel);

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				clickLabel();
			}
		});
	}

	/**
	 * �������
	 * <li></li>
	 */
	private void clickLabel() {
		if (BillMainFrame.clickCalBillPanel != null) {
			BillMainFrame.clickCalBillPanel.setBorder(null);
		}
		calEachBill();
		CalBillPanel.this.setBorder(BorderFactory.createLineBorder(Color.red));
		BillMainFrame.clickCalBillPanel = CalBillPanel.this;
	}

	/**
	 * ͳ��
	 * <li></li>
	 */
	private void calEachBill() {
		StringBuilder sb = new StringBuilder("<span style='color:orange'>" + tipLabel.getText() + "��֧��¼���£�</span><br>");
		billList.forEach(bill -> {
			sb.append(bill.getDateStr()).append("(").append(DateTool.getWeekDay(bill.getDate())).append(") ");
			if (new BigDecimal(bill.getMoney()).compareTo(new BigDecimal(0)) < 0) {
				sb.append("����<span style='color:red'> ").append(bill.getMoney().substring(1))
						.append(" </span>Ԫ��<span style='color:orange'>").append(bill.getTip()).append("</span>��<br>");
			} else {
				sb.append("��<span style='color:blue'>").append(bill.getTip())
						.append("</span>׬ȡ<span style='color:green'> ").append(bill.getMoney()).append("</span>Ԫ<br>");
			}
		});
		String singleStr = CalBillFrame.calSum(billList);
		CalBillFrame.jtp.setText("<div>" + sb.toString() + "<br>" + singleStr
				+ "</div><div style='background-color:green'>" + CalBillFrame.allStr + "</div>");
		CalBillFrame.jtp.setCaretPosition(0);
	}

}
