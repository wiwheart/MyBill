package com.dm.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.dm.model.Bill;
import com.dm.model.SearchBillPanel;

/**
 * 查找账单窗口
 * 
 * @author shuil
 *
 */
public class SearchBillFrame extends JDialog {
	private JScrollPane jsp;
	JPanel labelJp, jp;
	JTextField jtf;//查找字符串
	JButton jb;//查找按钮
	ArrayList<Bill> billList;
	ArrayList<Integer> panelIndexList;
	JPanel sourceJp;

	//控制每一个小账单的颜色
	Color[] labelColorArr = new Color[] { Color.green, Color.orange, Color.pink, Color.cyan };
	int colorSite = -1;

	public SearchBillFrame(int xPos, int yPos, String title, ArrayList<Bill> billList, JPanel sourceJp) {
		this.sourceJp = sourceJp;
		this.billList = billList;
		panelIndexList = new ArrayList<Integer>();

		jp = new JPanel();
		jp.setLayout(null);
		jp.setSize(new Dimension(375, 700));

		jtf = new JTextField();
		jtf.setBounds(2, 5, 300, 22);
		jtf.setToolTipText("<html>普通搜索字符串将会匹配日期/金额/备注。也可以使用特殊的字符串查找，例如：<br>" + "&lt;100 金额小于100<br>"
				+ "&lt;-100 消费金额小于100<br>" + "&lt;+100 赚取金额小于100<br>" + "&lt;=100 金额小于等于100<br>"
				+ "&lt;=-100 消费金额小于等于100<br>" + "&lt;=+100 赚取金额小于等于100&nbsp;&nbsp;&nbsp;&nbsp;以上操作对于&gt;同样适用<br>"
				+ "50-100 金额在[50,100]区间 也可以用+-来指定金额类型：-50--100 +50-+100<br>" + "</html>");
		jtf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					search();
				}
			}
		});
		jp.add(jtf);

		jb = new JButton("查找");
		jb.setToolTipText("针对日期是匹配查找，金额和备注是包含查找");
		jb.setBounds(305, 5, 70, 22);
		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		jp.add(jb);

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
		this.setTitle("查找账单：" + title);
		this.add(jp);
		this.setVisible(true);
		this.setResizable(false);
	}

	/**
	 * 画账单
	 */
	private void drawBillPanel(List<Bill> billList) {
		colorSite = 0;
		labelJp.removeAll();
		labelJp.setPreferredSize(new Dimension(375, 30 * billList.size()));
		int index = 0;
		int month = -1;
		Color labelColor = getNextLabelColor();
		Calendar calendar = Calendar.getInstance();
		for (int i = billList.size() - 1; i >= 0; i--) {
			Bill bill = billList.get(i);
			calendar.setTime(bill.getDate());
			if (calendar.get(Calendar.MONTH) != month) {
				month = calendar.get(Calendar.MONTH);
				labelColor = getNextLabelColor();
			}
			int sourceIndex = index;
			if (!panelIndexList.isEmpty()) {
				sourceIndex = panelIndexList.get(i);
			}
			SearchBillPanel billPanel = new SearchBillPanel(0, index * 30, bill.getDateStr(),
					new BigDecimal(bill.getMoney()), bill.getTip(), sourceIndex);
			//为查找panel添加点击效果，点击可以跳转到真正的账单位置，方便进一步的操作
			//由于初始化出的sourceIndex有可能和最新的链表数据不符，所以需要考虑下标越界的情况（即账单发生了变动但查找页面中的显示内容还未更新）
			billPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					sourceJp.scrollRectToVisible(new Rectangle(0, billPanel.getSourceIndex() * 30, 375, 30));
					if (billPanel.getSourceIndex() < BillMainFrame.billPanelList.size()) {
						BillMainFrame.billPanelList.get(billPanel.getSourceIndex()).selectThisPanel();
					}
				}
			});
			billPanel.setBorder(BorderFactory.createLineBorder(labelColor));
			labelJp.add(billPanel);
			index++;
		}
		jsp.setViewportView(labelJp);
		jsp.repaint();
	}

	/**
	 * 查找绘制
	 * <li></li>
	 */
	private void search() {
		panelIndexList = new ArrayList<Integer>();
		String searchStr = jtf.getText().trim();
		if (!"".equals(searchStr)) {
			List<Bill> drawList = new ArrayList<Bill>();
			if (searchStr.startsWith("<") || searchStr.startsWith(">")) {
				String symbol = getSymbol(searchStr);
				if (symbol.equals("<")) {
					drawList = findBillLessThan(searchStr.substring(1), false, 0);
				} else if (symbol.equals("<+")) {
					drawList = findBillLessThan(searchStr.substring(2), false, 1);
				} else if (symbol.equals("<-")) {
					drawList = findBillLessThan(searchStr.substring(2), false, -1);
				} else if (symbol.equals("<=")) {
					drawList = findBillLessThan(searchStr.substring(2), true, 0);
				} else if (symbol.equals("<=+")) {
					drawList = findBillLessThan(searchStr.substring(3), true, 1);
				} else if (symbol.equals("<=-")) {
					drawList = findBillLessThan(searchStr.substring(3), true, -1);
				} else if (symbol.equals(">")) {
					drawList = findBillMoreThan(searchStr.substring(1), false, 0);
				} else if (symbol.equals(">+")) {
					drawList = findBillMoreThan(searchStr.substring(2), false, 1);
				} else if (symbol.equals(">-")) {
					drawList = findBillMoreThan(searchStr.substring(2), false, -1);
				} else if (symbol.equals(">=")) {
					drawList = findBillMoreThan(searchStr.substring(2), true, 0);
				} else if (symbol.equals(">=+")) {
					drawList = findBillMoreThan(searchStr.substring(3), true, 1);
				} else if (symbol.equals(">=-")) {
					drawList = findBillMoreThan(searchStr.substring(3), true, -1);
				} else {
					drawList = findBillByStr(searchStr);
				}
			} else if (searchStr.substring(1).contains("-") && !searchStr.endsWith("-")) {
				String[] arr = null;
				if (!searchStr.startsWith("-")) {
					arr = searchStr.split("-", 2);
				} else {
					int index = searchStr.substring(1).indexOf("-");
					arr = new String[] { searchStr.substring(0, index + 1), searchStr.substring(index + 2) };
				}
				if (arr.length > 1) {
					if (arr[0].startsWith("+")) {
						drawList = findBillBetween(arr[0].substring(1), arr[1].substring(1), true, 1);
					} else if (arr[0].startsWith("-")) {
						drawList = findBillBetween(arr[0].substring(1), arr[1].substring(1), true, -1);
					} else {
						drawList = findBillBetween(arr[0], arr[1], true, 0);
					}
				} else {
					drawList = findBillByStr(searchStr);
				}
			} else if (searchStr.startsWith("-")) {
				drawList = findBillByStr(searchStr.substring(1), -1);
			} else if (searchStr.startsWith("+")) {
				drawList = findBillByStr(searchStr.substring(1), 1);
			} else {
				drawList = findBillByStr(searchStr);
			}
			drawBillPanel(drawList);
		} else {
			drawBillPanel(billList);
		}
	}

	/**
	 * 通过字符串查找
	 * 
	 * @param searchStr
	 * @return
	 */
	private List<Bill> findBillByStr(String searchStr) {
		ArrayList<Bill> drawList = new ArrayList<Bill>();
		for (int i = 0; i < billList.size(); i++) {
			Bill bill = billList.get(i);
			if (bill.getTip().contains(searchStr) || bill.getMoney().toString().contains(searchStr)
					|| bill.getDateStr().equals(searchStr)) {
				drawList.add(bill);
				panelIndexList.add(billList.size() - i - 1);
			}
		}
		return drawList;
	}

	/**
	 * 通过字符串查找并指定花费还是赚取金额
	 * 
	 * @param searchStr
	 * @param abFlag
	 *            -1 花费 1 赚取
	 * @return
	 */
	private List<Bill> findBillByStr(String searchStr, int abFlag) {
		BigDecimal comDecimal = BigDecimal.ZERO;
		try {
			comDecimal = new BigDecimal(searchStr);
		} catch (Exception e) {
			return findBillByStr(searchStr);
		}
		ArrayList<Bill> drawList = new ArrayList<Bill>();
		for (int i = 0; i < billList.size(); i++) {
			Bill bill = billList.get(i);
			BigDecimal billDecimal = new BigDecimal(bill.getMoney()).abs();
			if (abFlag < 0) {

				if (bill.getMoney().startsWith("-") && (bill.getTip().contains(searchStr)
						|| bill.getMoney().toString().contains(searchStr) || bill.getDateStr().equals(searchStr))) {
					drawList.add(bill);
					panelIndexList.add(billList.size() - i - 1);
				}
			} else {
				if (!bill.getMoney().startsWith("-") && (bill.getTip().contains(searchStr)
						|| bill.getMoney().toString().contains(searchStr) || bill.getDateStr().equals(searchStr))) {
					drawList.add(bill);
					panelIndexList.add(billList.size() - i - 1);
				}
			}
		}
		return drawList;
	}

	/**
	 * 查找金额>=money
	 * <li></li>
	 * 
	 * @param searchMoney
	 *            金额
	 * @param equalFlag
	 *            是否包括=
	 * @param abFlag
	 *            -1 表示查找花费账单 0 表示查找所有账单 1 表示查找赚取账单
	 * @return
	 */
	private List<Bill> findBillMoreThan(String searchMoney, boolean equalFlag, int abFlag) {
		BigDecimal comDecimal = BigDecimal.ZERO;
		try {
			comDecimal = new BigDecimal(searchMoney);
		} catch (Exception e) {
			return findBillByStr(searchMoney);
		}
		ArrayList<Bill> drawList = new ArrayList<Bill>();
		for (int i = 0; i < billList.size(); i++) {
			Bill bill = billList.get(i);
			BigDecimal billDecimal = new BigDecimal(bill.getMoney()).abs();
			//包含边界=
			if (equalFlag) {
				if (abFlag == -1) {
					//花费>=money
					if (bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) >= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else if (abFlag == 0) {
					//金额>=money
					if (billDecimal.compareTo(comDecimal) >= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else {
					//赚取>=money
					if (!bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) >= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				}

			} else {
				if (abFlag == -1) {
					//花费>money
					if (bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) > 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else if (abFlag == 0) {
					//金额>money
					if (billDecimal.compareTo(comDecimal) > 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else {
					//赚取>money
					if (!bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) > 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				}
			}
		}
		return drawList;
	}

	/**
	 * 查找金额<=money
	 * <li></li>
	 * 
	 * @param searchMoney
	 *            金额
	 * @param equalFlag
	 *            是否包括=
	 * @param abFlag
	 *            -1 表示查找花费账单 0 表示查找所有账单 1 表示查找赚取账单
	 * @return
	 */
	private List<Bill> findBillLessThan(String searchMoney, boolean equalFlag, int abFlag) {
		BigDecimal comDecimal = BigDecimal.ZERO;
		try {
			comDecimal = new BigDecimal(searchMoney);
		} catch (Exception e) {
			return findBillByStr(searchMoney);
		}
		ArrayList<Bill> drawList = new ArrayList<Bill>();
		for (int i = 0; i < billList.size(); i++) {
			Bill bill = billList.get(i);
			BigDecimal billDecimal = new BigDecimal(bill.getMoney()).abs();
			//包含边界=
			if (equalFlag) {
				if (abFlag == -1) {
					//花费<=money
					if (bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) <= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else if (abFlag == 0) {
					//金额<=money
					if (billDecimal.compareTo(comDecimal) <= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else {
					//赚取<=money
					if (!bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) <= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				}

			} else {
				if (abFlag == -1) {
					//花费<money
					if (bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) < 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else if (abFlag == 0) {
					//金额<money
					if (billDecimal.compareTo(comDecimal) < 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else {
					//赚取<money
					if (!bill.getMoney().startsWith("-") && billDecimal.compareTo(comDecimal) < 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				}
			}
		}
		return drawList;
	}

	/**
	 * 查找金额在两个金额之间
	 * 
	 * @param money1
	 *            金额1
	 * @param money2
	 *            金额2
	 * @param equalFlag
	 *            是否包括=
	 * @param abFlag
	 *            -1 表示查找花费账单 0 表示查找所有账单 1 表示查找赚取账单
	 * @return
	 */
	private List<Bill> findBillBetween(String money1, String money2, boolean equalFlag, int abFlag) {
		BigDecimal srartDecimal = BigDecimal.ZERO;
		BigDecimal endDecimal = BigDecimal.ZERO;
		try {
			srartDecimal = new BigDecimal(money1);
			endDecimal = new BigDecimal(money2);
		} catch (Exception e) {
			return findBillByStr(money1);
		}
		ArrayList<Bill> drawList = new ArrayList<Bill>();
		for (int i = 0; i < billList.size(); i++) {
			Bill bill = billList.get(i);
			BigDecimal billDecimal = new BigDecimal(bill.getMoney()).abs();
			//包含边界=
			if (equalFlag) {
				if (abFlag == -1) {
					//花费money1<=X<=money2
					if (bill.getMoney().startsWith("-") && billDecimal.compareTo(srartDecimal) >= 0
							&& billDecimal.compareTo(endDecimal) <= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else if (abFlag == 0) {
					//金额money1<=X<=money2
					if (billDecimal.compareTo(srartDecimal) >= 0 && billDecimal.compareTo(endDecimal) <= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else {
					//赚取money1<=X<=money2
					if (!bill.getMoney().startsWith("-") && billDecimal.compareTo(srartDecimal) >= 0
							&& billDecimal.compareTo(endDecimal) <= 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				}

			} else {
				if (abFlag == -1) {
					//花费money1<X<money2
					if (bill.getMoney().startsWith("-") && billDecimal.compareTo(srartDecimal) > 0
							&& billDecimal.compareTo(endDecimal) < 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else if (abFlag == 0) {
					//金额money1<X<money2
					if (billDecimal.compareTo(srartDecimal) > 0 && billDecimal.compareTo(endDecimal) < 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				} else {
					//赚取money1<X<money2
					if (!bill.getMoney().startsWith("-") && billDecimal.compareTo(srartDecimal) > 0
							&& billDecimal.compareTo(endDecimal) < 0) {
						drawList.add(bill);
						panelIndexList.add(billList.size() - i - 1);
					}
				}
			}
		}
		return drawList;
	}

	/**
	 * 获取查找金额的符号
	 * 
	 * @param searchStr
	 * @return
	 */
	private String getSymbol(String searchStr) {
		String symbol = "";
		if (searchStr.startsWith(">") || searchStr.startsWith("<")) {
			while (searchStr.charAt(0) < '0' || searchStr.charAt(0) > '9') {
				symbol = symbol.concat(searchStr.substring(0, 1));
				searchStr = searchStr.substring(1);
			}
		}
		return symbol;
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
}
