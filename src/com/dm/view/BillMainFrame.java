package com.dm.view;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.dm.model.Bill;
import com.dm.model.BillPanel;
import com.dm.model.CalBillPanel;
import com.dm.tool.DateChooserJButton;
import com.dm.tool.HandleBill;
import com.dm.tool.HandleThread;
import com.dm.tool.PropertiesTool;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

/**
 * 主页面
 * 
 * @author shuil
 *
 */
public class BillMainFrame extends JFrame implements ActionListener {

	private JScrollPane jsp;
	private DateChooserJButton dateBtn;
	JPanel labelJp, jp;
	JButton addBtn, addSalary, reCalBtn;
	JLabel calJl;
	JMenu billCheckMenu, extraBillMenu;

	HandleBill handleBill = HandleBill.getInstance();
	private JMenuItem selectItem;//记录当前选中的账单按钮

	//定义键盘全局响应热键
	static final int hotKeyZ = 0;
	static final int hotKeyY = 1;

	//控制每一个小账单的颜色
	Color[] labelColorArr = new Color[] { Color.green, Color.orange, Color.pink, Color.cyan };
	int colorSite = -1;
	public static ArrayList<BillPanel> billPanelList = new ArrayList<BillPanel>();//用于记录面板上的账单Panel列表，用于查找面板上的定位跳转

	public static CalBillPanel clickCalBillPanel;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());// 当前系统风格
		} catch (Exception e) {
		}
		BillMainFrame billMainFrame = new BillMainFrame();
		new HandleThread(billMainFrame).start();
	}

	public BillMainFrame() {
		if (!makeBillList(0)) {
			System.exit(1);
		}

		JMenuBar jmb = new JMenuBar();
		JMenu fileMenu = new JMenu("文件");
		billCheckMenu = new JMenu("账单切换");
		initBillCheckMenu();
		fileMenu.add(billCheckMenu);
		JMenuItem fileOpenMenu = new JMenuItem("浏览文件");
		fileOpenMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					Desktop.getDesktop().open(new File(handleBill.getDir()));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "路径错误!...");
				}
			}
		});
		fileMenu.add(fileOpenMenu);
		JMenuItem newBill = new JMenuItem("新建账单");
		newBill.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(BillMainFrame.this, "不准新建，去浏览文件里自己弄吧", "新建账单",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		fileMenu.add(newBill);
		jmb.add(fileMenu);

		JMenu calMenu = new JMenu("统计");
		JMenuItem searchBill = new JMenuItem("查找");
		searchBill.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				String fileName = handleBill.getBillFile().getName();
				new SearchBillFrame((int) BillMainFrame.this.getBounds().getX() + BillMainFrame.this.getWidth(),
						(int) BillMainFrame.this.getBounds().getY(), fileName.substring(0, fileName.length() - 4),
						handleBill.getBillList(), labelJp);
			}
		});
		calMenu.add(searchBill);
		JMenuItem calItem = new JMenuItem("统计");
		calItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				String fileName = handleBill.getBillFile().getName();
				new CalBillFrame((int) BillMainFrame.this.getBounds().getX() + BillMainFrame.this.getWidth(),
						(int) BillMainFrame.this.getBounds().getY(), fileName.substring(0, fileName.length() - 4),
						handleBill.getBillList());
			}
		});
		calMenu.add(calItem);
		jmb.add(calMenu);

		JMenu opMenu = new JMenu("操作");
		extraBillMenu = new JMenu("账单对比");
		initExtraBillMenu();
		opMenu.add(extraBillMenu);
		jmb.add(opMenu);
		JMenuItem defaultFile = new JMenuItem("设为默认");
		defaultFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				// TODO Auto-generated method stub
				handleBill.setDefaultFile();
			}
		});
		opMenu.add(defaultFile);

		jp = new JPanel();
		jp.setLayout(null);
		jp.setSize(new Dimension(375, 700));
		addBtn = new JButton("+");
		addBtn.setFont(new Font("仿宋", Font.BOLD, 20));
		addBtn.setBounds(0, 0, 45, 30);
		addBtn.setToolTipText("添加一个日期账单");
		addBtn.addActionListener(this);
		jp.add(addBtn);

		addSalary = new JButton("*");
		addSalary.setFont(new Font("仿宋", Font.BOLD, 20));
		addSalary.setBounds(45, 0, 45, 30);
		addSalary.setToolTipText("批量添加");
		addSalary.addActionListener(this);
		jp.add(addSalary);

		dateBtn = new DateChooserJButton(true, "选择统计日期");
		dateBtn.setBounds(92, 2, 80, 25);
		dateBtn.setToolTipText("选择统计截止的日期");
		jp.add(dateBtn);
		calJl = new JLabel("结余：999999999");
		calJl.setFont(new Font("仿宋", Font.BOLD, 14));
		calJl.setBounds(175, 2, 150, 25);
		calJl.setBorder(BorderFactory.createLineBorder(Color.black));
		showMoney();
		jp.add(calJl);

		reCalBtn = new JButton("●");
		reCalBtn.setBounds(330, 0, 45, 30);
		reCalBtn.setToolTipText("重绘");
		reCalBtn.addActionListener(this);
		jp.add(reCalBtn);

		labelJp = new JPanel();
		labelJp.setLayout(null);
		labelJp.setPreferredSize(new Dimension(375, 30 * HandleBill.billList.size()));
		jsp = new JScrollPane();
		jsp.setBounds(0, 30, 375, 630);
		jsp.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		jsp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		jsp.getVerticalScrollBar().setUnitIncrement(50);
		drawBillPanel();
		jp.add(jsp);

		if (PropertiesTool.keyListen) {
			try {
				// 注册热键，组合键（ctrl），主要热键+
				JIntellitype.getInstance().registerHotKey(hotKeyZ, JIntellitype.MOD_CONTROL, KeyEvent.VK_Z);
				JIntellitype.getInstance().registerHotKey(hotKeyY, JIntellitype.MOD_CONTROL, KeyEvent.VK_Y);

				// 添加热键监听器
				JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
					public void onHotKey(int markCode) {
						//当前用户焦点在该窗口时，才响应事件
						if (BillMainFrame.this.isFocused()) {
							if (markCode == hotKeyZ) {
								handleBill.returnBill();
							} else if (markCode == hotKeyY) {
								handleBill.forwardBill();
							}
						}
					}
				});
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "全局监听仅同时支持一个窗口，请关闭之前打开的程序，或者将配置文件中keyListen置为false");
			}
		}

		this.setJMenuBar(jmb);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		int w = 382;
		int h = 711;
		this.setBounds((width - w) / 2, (height - h) / 2, w, h);
		this.add(jp);

		Image mainIcon = null;// 设置主页图标
		try {
			File mainImgFile = new File("bill.png");//swing不支持ico文件
			System.out.println(mainImgFile.exists());
			mainIcon = ImageIO.read(mainImgFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setIconImage(mainIcon);

		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
	}

	/**
	 * 初始化账单切换菜单
	 */
	private void initBillCheckMenu() {
		billCheckMenu.removeAll();
		List<File> fileList = handleBill.getBillFileList();
		for (int i = 0; i < fileList.size(); i++) {
			JMenuItem fileItem = new JMenuItem(
					fileList.get(i).getName().substring(0, fileList.get(i).getName().length() - 4));
			int site = i;
			fileItem.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					selectItem.setForeground(null);
					selectItem.setArmed(false);
					changeBill(site);
					selectItem = fileItem;
					fileItem.setForeground(Color.orange);
					BillMainFrame.this.setTitle("当前账单： " + fileItem.getText());
				}
			});
			if (i == 0) {
				fileItem.setForeground(Color.orange);
				selectItem = fileItem;
				BillMainFrame.this.setTitle("当前账单： " + fileItem.getText());
			}
			billCheckMenu.add(fileItem);
		}
	}

	/**
	 * 初始化账单对比菜单
	 */
	private void initExtraBillMenu() {
		extraBillMenu.removeAll();
		List<File> fileList = handleBill.getBillFileList();
		for (int i = 0; i < fileList.size(); i++) {
			String billName = fileList.get(i).getName().substring(0, fileList.get(i).getName().length() - 4);
			JMenuItem fileItem = new JMenuItem(billName);
			int site = i;
			fileItem.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					ExtraBillFrame ebf = new ExtraBillFrame(
							(int) BillMainFrame.this.getBounds().getX() + BillMainFrame.this.getWidth(),
							(int) BillMainFrame.this.getBounds().getY(), billName,
							handleBill.getBillListFromFile(fileList.get(site)));
				}
			});
			extraBillMenu.add(fileItem);
		}
	}

	/**
	 * 根据账单文件下标重新加载页面
	 * 
	 * @param index
	 */
	private void changeBill(int index) {
		makeBillList(index);
		drawBillPanel();
		showMoney();
	}

	/**
	 * 创建账单链表
	 * 
	 * @param index
	 *            账单文件下标
	 */
	private boolean makeBillList(int index) {
		return handleBill.makeBillList(index);
	}

	/**
	 * 画账单
	 */
	public void drawBillPanel() {
		billPanelList = new ArrayList<BillPanel>();
		colorSite = 0;
		labelJp.removeAll();
		handleBill.initTransIndexList();
		labelJp.setPreferredSize(new Dimension(375, 30 * HandleBill.billList.size()));
		int index = 0;
		int month = -1;
		Color labelColor = getNextLabelColor();
		Calendar calendar = Calendar.getInstance();
		for (int i = HandleBill.billList.size() - 1; i >= 0; i--) {
			Bill bill = HandleBill.billList.get(i);
			calendar.setTime(bill.getDate());
			if (calendar.get(Calendar.MONTH) != month) {
				month = calendar.get(Calendar.MONTH);
				labelColor = getNextLabelColor();
			}
			BillPanel billPanel = new BillPanel(0, index * 30, bill, i);

			billPanel.setBorder(BorderFactory.createLineBorder(labelColor));
			billPanelList.add(billPanel);
			labelJp.add(billPanel);
			index++;
		}
		jsp.setViewportView(labelJp);
		jsp.repaint();
	}

	/**
	 * 显示金额结余
	 */
	public void showMoney() {
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date;
		try {
			date = format.parse(dateBtn.getText());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			date = new Date();
		}
		BigDecimal money = handleBill.calMoney(date);
		calJl.setText("结余：" + money.toString());
		calJl.setForeground(getMoneyColor(date, money));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == addBtn) {
			new AddBillView((int) this.getBounds().getX() + this.getWidth(), (int) this.getBounds().getY());
		} else if (e.getSource() == reCalBtn) {
			handleBill.initTransIndexList();//清空选择列表
			clearPanelBoard();//重绘回默认效果
		} else if (e.getSource() == addSalary) {
			new MonthAddFrame((int) this.getBounds().getX() + this.getWidth(), (int) this.getBounds().getY());
		}
	}

	/**
	 * 点击重绘按钮，将面板上的账单panel的选择效果去除
	 * <li>在此之前应该将选择列表清空</li>
	 */
	private void clearPanelBoard() {
		billPanelList.forEach(panel -> {
			panel.defaultPanel();
		});
	}

	/**
	 * 获取金额应该展示的颜色 预估平均每一个月应该有5k+的结余为一档
	 * <li>3k+的为blue</li>
	 * <li>3k以下的为red</li>
	 * 
	 * @param calDate
	 * @param money
	 * @return
	 */
	private Color getMoneyColor(Date calDate, BigDecimal money) {
		Calendar now = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.setTime(calDate);
		int monthNum = (cal.get(Calendar.YEAR) - now.get(Calendar.YEAR)) * 12 + cal.get(Calendar.MONTH)
				- now.get(Calendar.MONTH) + 1;
		if (money.compareTo(new BigDecimal(monthNum * 5000)) > 0) {
			return new Color(46, 139, 87);
		} else if (money.compareTo(new BigDecimal(monthNum * 3000)) > 0) {
			return Color.blue;
		} else {
			return Color.red;
		}
	}

	/**
	 * 获取销账单的颜色
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
