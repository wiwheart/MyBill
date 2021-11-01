package com.dm.model;

/**
 * ����ά����������
 * 
 * @author shuil
 *
 */
public class ReturnBill {

	//��Ҫ���صĲ�������
	//0 ��Ҫɾ����������һ����¼ʱ�����������Ҫɾ���ü�¼��
	//1 ��Ҫ���루��ɾ��һ����¼ʱ�����������Ҫ����ü�¼��
	//2 ��Ҫ�滻�����޸�һ����¼ʱ�����������Ҫ�޸Ļظü�¼��
	private int type;

	//��¼�±�
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
