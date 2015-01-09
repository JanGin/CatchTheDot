package org.progchen.catchthedot;

/**
 * @author cxj
 * @date 2015-1-5����6:26:05
 */
public class Dot {
	//��ĺ�����
	private int x;
	//���������
	private int y;
	//���״̬
	private int status;
	//�������״̬
	public static final int STATUS_OFF = 0;			//�ر�״̬
	public static final int STATUS_ON = 1;			//��״̬(��Ϊ·��)
	public static final int STATUS_IN = 2;			//����״̬
	
	public Dot(int x,int y){
		this.x = x;
		this.y = y;
		this.status = STATUS_OFF;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setXY(int x,int y){
		this.x = x;
		this.y = y;
	}
	
}
