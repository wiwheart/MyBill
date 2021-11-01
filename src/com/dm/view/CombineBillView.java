package com.dm.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
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

/**
 * 合并多个账单
 * 
 * @author shuil
 *
 */
public class CombineBillView extends JDialog implements ActionListener {

	private DateChooserJButton dateBtn;
	private JTextField moneyJtf, tipJtf;
	private JButton addBtn, cancelBtn;
	HandleBill handleBill = HandleBill.getInstance();

	public CombineBillView(int x, int y) {
		JPanel jp = new JPanel();
		jp.setLayout(null);

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
		addBtn.setBounds(288, 2, 45, 21);
		addBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		addBtn.addActionListener(this);
		jp.add(addBtn);
		cancelBtn = new JButton("×");
		cancelBtn.setBounds(335, 2, 45, 21);
		cancelBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		cancelBtn.addActionListener(this);
		jp.add(cancelBtn);

		List<Bill> billList = handleBill.getBillList();
		List<Integer> transList = handleBill.getTransIndexList();
		BigDecimal sum = new BigDecimal(0);
		for (int i = 0; i < transList.size(); i++) {
			Bill bill = billList.get(transList.get(i));

			TipLabel tipLabel = new TipLabel(bill.getDateStr(), 0);
			tipLabel.setLocation(0, 30 + 22 * i);
			jp.add(tipLabel);

			BigDecimal one = null;
			try {
				one = new BigDecimal(bill.getMoney());
			} catch (Exception e) {
				one = new BigDecimal(0);
			}
			sum = sum.add(one);
			tipLabel = new TipLabel(bill.getMoney(), 1);
			tipLabel.setLocation(92, 30 + 22 * i);
			jp.add(tipLabel);

			tipLabel = new TipLabel(bill.getTip(), 2);
			tipLabel.setLocation(184, 30 + 22 * i);
			jp.add(tipLabel);
		}

		//添加组合价格标签（自动将所选的账单金额差值算出来）
		TipLabel tipLabel = new TipLabel(sum.toString(), 1);
		tipLabel.setLocation(280, 30 + (transList.size() / 2) * 22);
		jp.add(tipLabel);

		jp.setBounds(0, 0, 380, 25 * (transList.size() + 1));

		this.setUndecorated(true);
		this.setBounds(x, y, 380, 25 * (transList.size() + 1));
		this.setVisible(true);
		this.add(jp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == cancelBtn) {
			// AddBillView.this.dispose();
			this.dispose();
		} else if (e.getSource() == addBtn) {
			if ("".equals(moneyJtf.getText().trim())) {
				moneyJtf.setText("0");
			}
			Bill bill = new Bill(dateBtn.getText(), moneyJtf.getText(), tipJtf.getText());
			handleBill.combineBill(bill);
			this.dispose();
		}
	}

	class TipLabel extends JLabel {

		public TipLabel(String str, int index) {
			super(str, JLabel.CENTER);
			this.setBounds(0, 0, 90, 22);
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					switch (index) {
					case 0:
						dateBtn.setText(str);
						break;
					case 1:
						moneyJtf.setText(str);
						break;
					case 2:
						tipJtf.setText(str);
						break;
					}
				}
			});
			this.setBorder(BorderFactory.createLineBorder(Color.green));
		}
	}

}
