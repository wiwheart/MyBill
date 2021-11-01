package com.dm.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.dm.tool.DateChooserJButton;
import com.dm.tool.DateTool;
import com.dm.tool.HandleBill;
import com.dm.view.CombineBillView;
import com.dm.view.SyncBillView;

public class BillPanel extends JPanel implements MouseListener, ActionListener {

	private JLabel dateLabel, moneyLabel, tipLabel;
	private JTextField moneyJtf, tipJtf;
	private DateChooserJButton dateBtn;
	private JButton deleteBtn, saveBtn, cancelBtn;
	private int index;
	private HandleBill handleBill = HandleBill.getInstance();

	public BillPanel(int xPos, int yPos, Bill bill, int index) {
		this.setLayout(null);
		this.index = index;
		this.setBounds(xPos, yPos, 375, 30);

		String date = bill.getDateStr();
		BigDecimal money = new BigDecimal(bill.getMoney());
		String tip = bill.getTip();

		String weekDay = DateTool.getWeekDay(bill.getDate());
		weekDay = weekDay.length() > 1 ? weekDay.substring(1) : weekDay;
		dateLabel = new JLabel(weekDay.concat("|").concat(date), JLabel.CENTER);
		dateLabel.setBounds(0, 0, 85, 30);
		this.add(dateLabel);

		moneyLabel = new JLabel(money.toString(), JLabel.CENTER);
		if (money.compareTo(new BigDecimal(0)) < 0) {
			moneyLabel.setForeground(Color.red);
		}
		moneyLabel.setBounds(70, 0, 100, 30);
		moneyLabel.setFont(new Font("仿宋", Font.BOLD, 14));
		moneyLabel.addMouseListener(this);
		this.add(moneyLabel);

		tipLabel = new JLabel(tip);
		tipLabel.setBounds(175, 0, 100, 30);
		tipLabel.addMouseListener(this);
		if (tip != null && tip.length() > 8) {
			tipLabel.setToolTipText(tip);
		}
		this.add(tipLabel);

		deleteBtn = new JButton("×");
		deleteBtn.setBounds(280, 4, 45, 22);
		deleteBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		deleteBtn.addActionListener(this);
		this.add(deleteBtn);

		dateBtn = new DateChooserJButton(date, "选择账单日期");
		dateBtn.setBounds(0, 4, 70, 22);
		dateBtn.setVisible(false);
		this.add(dateBtn);
		moneyJtf = new JTextField(money.toString());
		moneyJtf.setBounds(70, 4, 100, 22);
		moneyJtf.setVisible(false);
		moneyJtf.setToolTipText("输入赚取的金额，负数表示支出");
		this.add(moneyJtf);
		tipJtf = new JTextField(tip);
		tipJtf.setBounds(175, 4, 100, 22);
		tipJtf.setVisible(false);
		tipJtf.setToolTipText("输入此次交易备注");
		this.add(tipJtf);

		saveBtn = new JButton("√");
		saveBtn.setBounds(280, 4, 45, 22);
		saveBtn.setVisible(false);
		saveBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		saveBtn.addActionListener(this);
		this.add(saveBtn);
		cancelBtn = new JButton("×");
		cancelBtn.setBounds(328, 4, 45, 22);
		cancelBtn.setVisible(false);
		cancelBtn.setFont(new Font("仿宋", Font.BOLD, 12));
		cancelBtn.addActionListener(this);
		this.add(cancelBtn);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) {
					JPopupMenu popupMenu = new JPopupMenu();
					JMenuItem syncBillItem = new JMenuItem("同步账单");
					syncBillItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							new SyncBillView(
									(int) BillPanel.this.getLocationOnScreen().getX() + BillPanel.this.getWidth() + 10,
									(int) BillPanel.this.getLocationOnScreen().getY(), index);
						}
					});
					popupMenu.add(syncBillItem);

					if (handleBill.getTransIndexList().contains(index)) {
						syncBillItem.setToolTipText("将选择的记账批量添加到所选文件中去");

						if (handleBill.getTransIndexList().size() > 1) {
							JMenuItem combineBillItem = new JMenuItem("合并账单");
							combineBillItem.setToolTipText("将选择的记账合为一个账单");
							combineBillItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									new CombineBillView(
											(int) BillPanel.this.getLocationOnScreen().getX()
													+ BillPanel.this.getWidth() + 10,
											(int) BillPanel.this.getLocationOnScreen().getY());
								}
							});
							popupMenu.add(combineBillItem);
						}
					} else {
						syncBillItem.setToolTipText("将该笔记账添加到所选文件中去");
					}
					popupMenu.show(BillPanel.this, e.getX(), e.getY());
				} else {
					if (!handleBill.getTransIndexList().contains(index)) {
						dateLabel.setBorder(BorderFactory.createLineBorder(Color.red, 2));
					} else {
						dateLabel.setBorder(null);
					}
					handleBill.selectIndex(index);
				}
			}
		});
	}

	/**
	 * 从查找页面点击选中
	 * <li></li>
	 */
	public void selectThisPanel() {
		dateLabel.setBorder(BorderFactory.createLineBorder(Color.blue, 2));
	}

	/**
	 * 清空选中效果
	 * <li></li>
	 */
	public void defaultPanel() {
		dateLabel.setBorder(null);
	}

	private void returnNormal() {
		dateBtn.setVisible(false);
		moneyJtf.setVisible(false);
		tipJtf.setVisible(false);
		saveBtn.setVisible(false);
		cancelBtn.setVisible(false);

		dateLabel.setVisible(true);
		moneyLabel.setVisible(true);
		tipLabel.setVisible(true);
		deleteBtn.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2
				&& (e.getSource() == moneyLabel || e.getSource() == tipLabel)) {
			dateBtn.setVisible(true);
			moneyJtf.setVisible(true);
			tipJtf.setVisible(true);
			saveBtn.setVisible(true);
			cancelBtn.setVisible(true);

			dateLabel.setVisible(false);
			moneyLabel.setVisible(false);
			tipLabel.setVisible(false);
			deleteBtn.setVisible(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveBtn) {
			Bill alterBill = new Bill(dateBtn.getText(), moneyJtf.getText(), tipJtf.getText());
			handleBill.alterBillFromView(index, alterBill);
			returnNormal();
		} else if (e.getSource() == cancelBtn) {
			returnNormal();
		} else if (e.getSource() == deleteBtn) {
			handleBill.deleteBillFromView(index);
		}
	}

}
