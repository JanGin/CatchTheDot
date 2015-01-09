package org.progchen.catchthedot;

/**
 * @author cxj
 * @date 2015-1-5下午6:26:05
 */
public class Dot {
	//点的横坐标
	private int x;
	//点的纵坐标
	private int y;
	//点的状态
	private int status;
	//点的三种状态
	public static final int STATUS_OFF = 0;			//关闭状态
	public static final int STATUS_ON = 1;			//打开状态(即为路障)
	public static final int STATUS_IN = 2;			//进入状态
	
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
