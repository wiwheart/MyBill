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
				//System.out.println("�ػ�ͼ��...");
				billMainFrame.drawBillPanel();
				handleBill.resetRepaint();
			}
			if (handleBill.getCalFlag()) {
				//System.out.println("�������...");
				billMainFrame.showMoney();
				handleBill.resetCal();
			}
			if (handleBill.getSaveBill()) {
				//System.out.println("�����ļ�...");
				handleBill.saveBillFile();
				handleBill.resetSaveBill();
			}
		}
	}

}
