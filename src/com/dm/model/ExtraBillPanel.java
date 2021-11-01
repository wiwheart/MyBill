package com.dm.model;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ExtraBillPanel extends JPanel {

	private JLabel dateLabel, moneyLabel, tipLabel, calLabel;

	public ExtraBillPanel(int xPos, int yPos, String date, BigDecimal money, String tip, BigDecimal cal) {
		this.setLayout(null);
		this.setBounds(xPos, yPos, 375, 30);

		dateLabel = new JLabel(date, JLabel.CENTER);
		dateLabel.setBounds(0, 0, 70, 30);
		this.add(dateLabel);

		moneyLabel = new JLabel(money.toString(), JLabel.CENTER);
		if (money.compareTo(new BigDecimal(0)) < 0) {
			moneyLabel.setForeground(Color.red);
		}
		moneyLabel.setBounds(70, 0, 100, 30);
		moneyLabel.setFont(new Font("∑¬ÀŒ", Font.BOLD, 14));
		this.add(moneyLabel);

		tipLabel = new JLabel(tip);
		tipLabel.setBounds(175, 0, 100, 30);
		if (tip != null && tip.length() > 8) {
			tipLabel.setToolTipText(tip);
		}
		this.add(tipLabel);

		calLabel = new JLabel("Ω·”‡£∫" + cal);
		if (cal.compareTo(new BigDecimal(0)) < 0) {
			calLabel.setForeground(Color.red);
		}
		calLabel.setBounds(275, 0, 100, 30);
		this.add(calLabel);
	}

}
