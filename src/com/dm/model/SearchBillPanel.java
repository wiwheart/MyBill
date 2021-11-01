package com.dm.model;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SearchBillPanel extends JPanel {

	private JLabel dateLabel, moneyLabel, tipLabel;
	private JTextField dateJtf;
	private int sourceIndex;

	/**
	 * 
	 * @param xPos
	 * @param yPos
	 * @param date
	 * @param money
	 * @param tip
	 * @param sourceIndex
	 *            ���ڼ�¼��panel��Ӧ�����������˵�panel���±꣬���ڵ����ת
	 */
	public SearchBillPanel(int xPos, int yPos, String date, BigDecimal money, String tip, int sourceIndex) {
		this.sourceIndex = sourceIndex;
		this.setLayout(null);
		this.setBounds(xPos, yPos, 375, 30);

		dateJtf = new JTextField(date, JTextField.CENTER);
		dateJtf.setBounds(0, 2, 70, 26);
		dateJtf.setEditable(false);
		dateJtf.setBorder(null);
		this.add(dateJtf);

		moneyLabel = new JLabel(money.toString(), JLabel.CENTER);
		if (money.compareTo(new BigDecimal(0)) < 0) {
			moneyLabel.setForeground(Color.red);
		}
		moneyLabel.setBounds(70, 0, 100, 30);
		moneyLabel.setFont(new Font("����", Font.BOLD, 14));
		this.add(moneyLabel);

		tipLabel = new JLabel(tip);
		tipLabel.setBounds(175, 0, 200, 30);
		if (tip != null && tip.length() > 8) {
			tipLabel.setToolTipText(tip);
		}
		this.add(tipLabel);
	}

	public int getSourceIndex() {
		return sourceIndex;
	}

}
