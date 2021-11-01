package com.dm.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dm.model.Bill;
import com.dm.tool.DateChooserJButton;
import com.dm.tool.HandleBill;
import com.dm.tool.PropertiesTool;

/**
 * 账单添加页面
 * 
 * @author shuil
 *
 */
public class AddBillView extends JDialog implements ActionListener {

	private DateChooserJButton dateBtn;
	private JTextField moneyJtf, tipJtf;
	private JButton addBtn, cancelBtn;
	HandleBill handleBill = HandleBill.getInstance();
	private List<String> tipList = PropertiesTool.tipProList;

	JLabel jl1, jl2, jl3, jl4, jl5;

	public AddBillView(int x, int y) {
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(0, 0, 450, 100);

		dateBtn = new DateChooserJButton("", "选择添加日期");
		dateBtn.setBounds(0, 2, 80, 21);
		jp.add(dateBtn);

		moneyJtf = new JTextField();
		moneyJtf.setBounds(80, 2, 100, 21);
		moneyJtf.setToolTipText("输入赚取的金额，负数表示支出");
		jp.add(moneyJtf);
		tipJtf = new JTextField();
		tipJtf.setBounds(185, 2, 100, 21);
		tipJtf.setToolTipText("输入此次交易备注");
		jp.add(tipJtf);

		addBtn = new JButton("√");
		addBtn.setBounds(310, 2, 45, 21);
		addBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		addBtn.addActionListener(this);
		jp.add(addBtn);
		cancelBtn = new JButton("×");
		cancelBtn.setBounds(365, 2, 45, 21);
		cancelBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		cancelBtn.addActionListener(this);
		jp.add(cancelBtn);

		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String dayStr = format.format(new Date());
		jl4 = new TipLabel("今天" + dayStr, 0);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		dayStr = format.format(calendar.getTime());
		jl5 = new TipLabel("明天" + dayStr, 0);
		calendar.add(Calendar.DATE, -2);
		dayStr = format.format(calendar.getTime());
		jl3 = new TipLabel("昨天" + dayStr, 0);
		calendar.add(Calendar.DATE, -1);
		dayStr = format.format(calendar.getTime());
		jl2 = new TipLabel("前天" + dayStr, 0);
		calendar.add(Calendar.DATE, -1);
		dayStr = format.format(calendar.getTime());
		jl1 = new TipLabel("大前天" + dayStr, 0);

		jl1.setLocation(0, 25);
		jp.add(jl1);
		jl2.setLocation(102, 25);
		jp.add(jl2);
		jl3.setLocation(204, 25);
		jp.add(jl3);
		jl4.setLocation(306, 25);
		jp.add(jl4);

		jl5.setLocation(0, 47);
		jp.add(jl5);

		int index = 0;
		JLabel tipTip = null;
		if (!tipList.isEmpty()) {
			tipTip = new TipLabel(tipList.get(index), 1);
			tipTip.setLocation(102, 47);
			jp.add(tipTip);
			tipJtf.setText(tipList.get(index));
		}

		index++;
		if (tipList.size() > 1) {
			tipTip = new TipLabel(tipList.get(index), 1);
			tipTip.setLocation(204, 47);
			jp.add(tipTip);
		}

		index++;
		if (tipList.size() > 2) {
			tipTip = new TipLabel(tipList.get(index), 1);
			tipTip.setLocation(306, 47);
			jp.add(tipTip);
		}

		index++;
		if (tipList.size() > 3) {
			tipTip = new TipLabel(tipList.get(index), 1);
			tipTip.setLocation(0, 69);
			jp.add(tipTip);
		}

		index++;
		if (tipList.size() > 4) {
			tipTip = new TipLabel(tipList.get(index), 1);
			tipTip.setLocation(102, 69);
			jp.add(tipTip);
		}

		index++;
		if (tipList.size() > 5) {
			tipTip = new TipLabel(tipList.get(index), 1);
			tipTip.setLocation(204, 69);
			jp.add(tipTip);
		}

		index++;
		if (tipList.size() > 6) {
			tipTip = new TipLabel(tipList.get(index), 1);
			tipTip.setLocation(306, 69);
			jp.add(tipTip);
		}

		this.setUndecorated(true);
		this.setBounds(x, y, 410, 90);
		this.setVisible(true);
		this.add(jp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == cancelBtn) {
			this.dispose();
		} else if (e.getSource() == addBtn) {
			if ("".equals(moneyJtf.getText().trim())) {
				moneyJtf.setText("0");
			}
			Bill bill = new Bill(dateBtn.getText(), moneyJtf.getText(), tipJtf.getText());
			handleBill.addBeanFromView(bill);
			this.dispose();
		}
	}

	class TipLabel extends JLabel {
		public TipLabel(String str, int index) {
			super(str, JLabel.CENTER);
			this.setBounds(0, 0, 100, 20);
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					switch (index) {
					case 0:
						dateBtn.setText(str.substring(str.length() - 10));
						break;
					case 1:
						tipJtf.setText(str);
						break;
					}
				}
			});
			this.setBorder(BorderFactory.createLineBorder(Color.green));
		}
	}

}
