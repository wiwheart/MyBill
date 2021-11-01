package com.dm.model;

/**
 * 用于维护撤销操作
 * 
 * @author shuil
 *
 */
public class ReturnBill {

	//需要返回的操作类型
	//0 需要删除（当新增一个记录时，则代表返回需要删除该记录）
	//1 需要插入（当删除一个记录时，则代表返回需要插入该记录）
	//2 需要替换（当修改一个记录时，则代表返回需要修改回该记录）
	private int type;

	//记录下标
	private int index;

	private Bill bill;

	public ReturnBill(int type, int index, Bill bill) {
		super();
		this.type = type;
		this.index = index;
		this.bill = bill;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

}
