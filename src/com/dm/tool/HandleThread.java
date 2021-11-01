package com.dm.tool;

import com.dm.view.BillMainFrame;

public class HandleThread extends Thread {

	private BillMainFrame billMainFrame;
	HandleBill handleBill = HandleBill.getInstance();

	public HandleThread(BillMainFrame billMainFrame) {
		this.billMainFrame = billMainFrame;
	}

	public void run() {
		while (true) {
			if (handleBill.getRepaintFlag()) {
				//System.out.println("重绘图像...");
				billMainFrame.drawBillPanel();
				handleBill.resetRepaint();
			}
			if (handleBill.getCalFlag()) {
				//System.out.println("重算结余...");
				billMainFrame.showMoney();
				handleBill.resetCal();
			}
			if (handleBill.getSaveBill()) {
				//System.out.println("保存文件...");
				handleBill.saveBillFile();
				handleBill.resetSaveBill();
			}
		}
	}

}
