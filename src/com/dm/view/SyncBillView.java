package com.dm.view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.dm.tool.HandleBill;

/**
 * ÕËµ¥Í¬²½
 * 
 * @author shuil
 *
 */
public class SyncBillView extends JFrame implements ActionListener {

	private JCheckBox fileCheckBox;
	private JButton syncBtn, cancelBtn;
	HandleBill handleBill = HandleBill.getInstance();

	private List<Integer> selectList;
	private int index;

	public SyncBillView(int x, int y, int index) {
		this.index = index;
		JPanel jp = new JPanel();
		jp.setLayout(null);
		selectList = new ArrayList<Integer>();
		List<File> selectFileList = handleBill.getBillFileList();
		int i = 0;
		for (File file : selectFileList) {
			int site = i;
			JCheckBox jcb = new JCheckBox(file.getName().substring(0, file.getName().length() - 4));
			jcb.setBounds(5, 7 + i * 22, 150, 22);
			jcb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (jcb.isSelected()) {
						selectList.add(new Integer(site));
						//System.out.println(selectList);
					} else {
						selectList.remove(new Integer(site));
						//System.out.println(selectList);
					}
				}
			});
			jp.add(jcb);
			i++;
		}
		jp.setBounds(0, 0, 100, 7 + selectFileList.size() * 22);

		syncBtn = new JButton("¡Ì");
		syncBtn.setBounds(5, 17 + selectFileList.size() * 22, 45, 21);
		syncBtn.setFont(new Font("·ÂËÎ", Font.BOLD, 12));
		syncBtn.addActionListener(this);
		jp.add(syncBtn);
		cancelBtn = new JButton("¡Á");
		cancelBtn.setBounds(55, 17 + selectFileList.size() * 22, 45, 21);
		cancelBtn.setFont(new Font("·ÂËÎ", Font.BOLD, 12));
		cancelBtn.addActionListener(this);
		jp.add(cancelBtn);

		this.setUndecorated(true);
		this.setBounds(x, y, 100, 38 + selectFileList.size() * 22);
		this.setVisible(true);
		this.add(jp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == cancelBtn) {
			// AddBillView.this.dispose();
			this.dispose();
		} else if (e.getSource() == syncBtn) {
			if (!selectList.isEmpty()) {
				handleBill.syncBillToSelectFile(selectList, index);
			}
			this.dispose();
		}
	}

}
