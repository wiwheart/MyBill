package com.dm.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dm.tool.HandleBill;

/**
 * 批量更改窗口
 * <li>根据tip批量更新</li>
 * 
 * @author shuil
 *
 */
public class MonthAddFrame extends JDialog {

	JComboBox yearSelect, daySelect;
	List<SalaryPanel> moneyList;
	JButton addBtn, closeBtn;
	JTextField tipJtf;

	HandleBill handleBill = HandleBill.getInstance();

	public MonthAddFrame(int xPos, int yPos) {
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(0, 0, 550, 250);

		addBtn = new JButton("√");
		addBtn.setBounds(310, 5, 45, 21);
		addBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		addBtn.setToolTipText("保存更新");
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(yearSelect.getSelectedItem());
				String[] salaryArr = new String[12];
				for (int i = 0; i < 12; i++) {
					salaryArr[i] = moneyList.get(i).jtf.getText();
				}
				handleBill.alterSalary(yearSelect.getSelectedItem().toString(), daySelect.getSelectedItem().toString(),
						tipJtf.getText(), salaryArr);
				MonthAddFrame.this.dispose();
			}
		});
		jp.add(addBtn);
		closeBtn = new JButton("×");
		closeBtn.setBounds(360, 5, 45, 21);
		closeBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		closeBtn.setToolTipText("关闭窗口");
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MonthAddFrame.this.dispose();
			}
		});
		jp.add(closeBtn);

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		JLabel yearJl = new JLabel("选择年份：", JLabel.LEFT);
		yearJl.setBounds(5, 5, 60, 22);
		jp.add(yearJl);
		Integer[] yearArr = new Integer[20];
		for (int i = 0; i < 20; i++) {
			yearArr[i] = i + 2021;
		}
		yearSelect = new JComboBox(yearArr);
		yearSelect.setBounds(60, 5, 50, 22);
		yearSelect.setSelectedItem(year);
		yearSelect.setToolTipText("选择批量录入的年份");
		jp.add(yearSelect);

		JLabel dayJl = new JLabel("选择日期：", JLabel.LEFT);
		dayJl.setBounds(118, 5, 70, 22);
		jp.add(dayJl);
		Integer[] dayArr = new Integer[31];
		for (int i = 1; i <= 31; i++) {
			dayArr[i - 1] = i;
		}
		daySelect = new JComboBox(dayArr);
		daySelect.setBounds(172, 5, 38, 22);
		daySelect.setToolTipText("选择每月添加的日期");
		jp.add(daySelect);

		tipJtf = new JTextField();
		tipJtf.setBounds(215, 5, 85, 22);
		tipJtf.setToolTipText("备注(相同备注的原有账单将被删除)");
		jp.add(tipJtf);

		moneyList = new ArrayList<MonthAddFrame.SalaryPanel>();
		for (int i = 0; i < 12; i++) {
			SalaryPanel salaryPanel = new SalaryPanel(i);
			if (i < 6) {
				salaryPanel.setBounds(i * 70 + 5, 30, 70, 50);
			} else {
				salaryPanel.setBounds((i - 6) * 70 + 5, 90, 70, 50);
			}
			moneyList.add(salaryPanel);
			jp.add(salaryPanel);
		}

		this.setTitle("批量录入");
		this.add(jp);
		this.setBounds(xPos, yPos, 430, 190);
		this.setVisible(true);

	}

	/**
	 * 自动填充后面的工资
	 * <li></li>
	 * 
	 * @param index
	 * @param money
	 */
	private void autoFill(int index, String money) {
		for (int i = index + 1; i < moneyList.size(); i++) {
			moneyList.get(i).jtf.setText(money);
		}
	}

	class SalaryPanel extends JPanel {
		JTextField jtf;
		JLabel monthJl, autoLabel;
		int index;

		public SalaryPanel(int index) {
			this.setLayout(null);
			this.index = index;
			jtf = new JTextField();
			jtf.setBounds(0, 8, 50, 20);
			this.add(jtf);

			monthJl = new JLabel(index + 1 + "月", JLabel.LEFT);
			monthJl.setBounds(5, 25, 30, 22);
			this.add(monthJl);

			if (index < 11) {
				autoLabel = new JLabel("▶", JLabel.CENTER);
				autoLabel.setBounds(35, 30, 15, 15);
				autoLabel.setBorder(BorderFactory.createLineBorder(Color.green));
				autoLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub
						autoFill(index, jtf.getText());
					}
				});
				this.add(autoLabel);
			}

			this.setSize(70, 50);
		}
	};

}
