package org.progchen.catchthedot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

/**
 * @author cxj
 * @date 2015-1-5����6:06:12
 */

/*������,ʵ�ֻ������*/
public class Ground extends SurfaceView implements OnTouchListener{
	
	private static int WIDTH = 40;				//ÿ����Ŀ��
	private static final int ROW = 10;
	private static final int COL = 10;
	//�ϰ������
	private static final int BOLCKS = 20;
	//�������
	private static int step = 1;
	//Ҫ������Ǹ���
	private Dot cat;
	//��ά����
	private Dot[][] matrix;
	//���캯��
	public Ground(Context context) {
		super(context);
		getHolder().addCallback(callback);
		//��ʼ���ذ�
		matrix = new Dot[ROW][COL];
		for (int i = 0; i < ROW; i++ ){
			for(int j = 0; j < COL; j++){
				matrix[i][j] = new Dot(j,i);
			}
		}
		setOnTouchListener(this);
		//��ʼ��
		initGame();
	}
	//��ʼ����Ϸ
	public void initGame(){
		for(int x = 0; x < ROW; x++){
			for(int y = 0; y < COL; y++){
				matrix[x][y].setStatus(Dot.STATUS_OFF);
			}
		}
		cat = new Dot(4,5);
		getDot(4,5).setStatus(Dot.STATUS_IN);
		for (int i = 0; i < BOLCKS;){
			//�����ϰ���������ɵ�ĺ�������
			int x = (int)((Math.random() * 1000) % COL);
			int y = (int)((Math.random() * 1000) % ROW);
			if (getDot(x,y).getStatus() == Dot.STATUS_OFF){
				getDot(x,y).setStatus(Dot.STATUS_ON);
				i++;
			}
		}
		Ground.step = 1;
	}

	//���Ƶذ巽��
	public void drawGround(){
		//��������
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.LTGRAY);
	
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		for (int i = 0; i < ROW; i++){
			int offset = 0;			//��������
			if ( i % 2 != 0){
				offset = WIDTH / 2;
			}
			for (int j = 0; j < COL; j++){
				Dot dot = getDot(j,i);
				//����ͬ״̬�ĵ���ɫ
				switch(dot.getStatus()){
				case Dot.STATUS_IN:
					paint.setColor(Color.RED);
					break;
				case Dot.STATUS_OFF:
					paint.setColor(0xFFEEEEEE);
					break;
				case Dot.STATUS_ON:
					paint.setColor(0xFFFFAA00);
					break;
				default:
					break;
				
				}
				//���Ƶ�������
				canvas.drawOval(new RectF(dot.getX() * WIDTH + offset, dot.getY() * WIDTH, 	//
						(dot.getX()+1) * WIDTH + offset, (dot.getY()+1) * WIDTH), paint);
			}
		}
		getHolder().unlockCanvasAndPost(canvas);
	}
	
	//�ص��ӿ�
	private Callback callback = new Callback(){

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			WIDTH = arg2/(COL + 1);
			drawGround();
		}
		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			//�����ʼ��ʱ���û��ƻ�������
			drawGround();
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {	
		}
	};

	/*
	 * �߽��ж�,�жϵ��Ƿ��ڱ߽�״̬
	 * �ж�����Ϊ����κ�һ��������һ������0���ߵ�������ֵ
	 */
	private boolean isAtEdge(Dot dot){
		if (dot.getX() * dot.getY() == 0 || dot.getX() + 1 == COL || dot.getY() + 1 == ROW){
			return true;
		}
		return false;
	}
	//��������ֵ��þ���ĵ�
	private Dot getDot(int x,int y){
		return matrix[y][x];
	}
	//
	/*
	 * ��õ�����ڵ�,dirΪ����ֵ
	 */
	private Dot getNeighour(Dot dot,int dir){
		switch(dir){
		case 1:
			return getDot(dot.getX() - 1,dot.getY());
		case 2:
			if (dot.getY()%2 == 0) {
				return getDot(dot.getX()-1, dot.getY()-1);
			}else {
				return getDot(dot.getX(), dot.getY()-1);
			}
		case 3:
			if (dot.getY()%2 == 0) {
				return getDot(dot.getX(), dot.getY()-1);
			}else {
				return getDot(dot.getX()+1, dot.getY()-1);
			}
		case 4:
			return getDot(dot.getX()+1, dot.getY());
		case 5:
			if (dot.getY()%2 == 0) {
				return getDot(dot.getX(), dot.getY()+1);
			}else {
				return getDot(dot.getX()+1, dot.getY()+1);
			}
		case 6:
			if (dot.getY()%2 == 0) {
				return getDot(dot.getX()-1, dot.getY()+1);
			}else {
				return getDot(dot.getX(), dot.getY()+1);
			}
		}
	
		return null;
	}
	
	//Ҫ����ĵ���ƶ���ʽ
	private void moveTo(Dot dot){
		dot.setStatus(Dot.STATUS_IN);
		getDot(cat.getX(),cat.getY()).setStatus(Dot.STATUS_OFF);;
		cat.setXY(dot.getX(), dot.getY());
	}
	
	/*
	 * ��ȡ��ǰ�㵽�߽�ľ���,���ݷ���ֵ�ж��Ƿ���·��
	 * ��������˵����ǰ�㵽�߽�֮���·����û��·��
	 * ���ظ���˵����·��
	 * ��������������ֵ��ʾ�㵽�߽��·��֮��ľ���
	 */
	private int getDistance(Dot dot, int dir){
		int distance = 0;
		if (isAtEdge(dot)){
			return 1;
		}
		//��ǰ�����ڵĳ�ʼλ��
		Dot orignal = dot;
		//��һ����
		Dot next;
		while(true){
			next = getNeighour(orignal, dir);
			//�������·��ֱ�ӷ���
			if (next.getStatus() == Dot.STATUS_ON){
				return -distance;
			}
			//����¸����ڱ߽��Ͼ���+1�󷵻�
			else if (isAtEdge(next)){
				return ++distance;
			}
			distance++;
			orignal = next;	
		}
	}
	
	/*
	 *��Ӧ����¼�,����������һ���ƶ� (�ؼ��㷨)
	 */
	private void move(){
		//������п��õĵ�
		List<Dot> avaliableDot = new ArrayList<Dot>();
		//��ŵ���Ե������̵ĵ�
		List<Dot> leastToEdge = new ArrayList<Dot>();
		//��ſ��õ�͵�ķ���
		Map<Dot,Integer> map = new HashMap<Dot,Integer>();
		if (isAtEdge(cat)){
			lose();
			return;
		}
		//����è�����ܵĵ�,Ѱ���¸����õĵ���Ϊ���ܵĵ�
		for (int i = 1; i <= 6; i++){
			Dot neighbor = getNeighour(cat, i);
			if (neighbor.getStatus() == Dot.STATUS_OFF){
				avaliableDot.add(neighbor);
				map.put(neighbor, i);
				//������õĵ����е���Ե��̾���ĵ��򽫸õ������
				if (getDistance(neighbor,i) > 0){
					leastToEdge.add(neighbor);
				}
			} 
		}
		if (avaliableDot.size() == 0){
			win();
		} else if (avaliableDot.size() == 1){		//���ֻ��һ�����еĵ�ֻ�ܳ��������
			moveTo(avaliableDot.get(0));
		}
		else {
			//Ѱ�����ŵ���һ����
			Dot best = null;
			//���������·�ϵ����Ե��̵ĵ�
			if (leastToEdge.size() > 0){
				//minһ��Ҫȷ���㹻��,���ٴ��ڽ����о�����Զ������֮��ľ���,����ᱨ��ָ���쳣
				int min = 99;
				for (int i = 0; i < leastToEdge.size(); i++){
					//��õ�ǰΪλ�õ����п��Ե����Ե��λ��֮�����С����
					int minDistance = getDistance(leastToEdge.get(i),	//
							map.get(leastToEdge.get(i)));
					if (minDistance < min){
						min = minDistance;
						best = leastToEdge.get(i);
					}
				}
				moveTo(best);
			}
			//�����з����϶���·��ʱѡ�񵽴�·�Ͼ�����ĵ�
			else{
				int max = 0;
				for (int i = 0; i < avaliableDot.size(); i++){
					int maxDistance = getDistance(avaliableDot.get(i),	//
							map.get(avaliableDot.get(i)));
					if (maxDistance <= max){
						max = maxDistance;
						best = avaliableDot.get(i);
					}
				}
				//�ƶ������ŵ�
				moveTo(best);
			}
		}
	}
	
	
	/**
	 * ��Ϸ����
	 */
	private void lose() {
		Toast.makeText(getContext(), "(�����`)You fail to catch the dot", Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * ��Ϸʤ�� 
	 */
	private void win(){
		Toast.makeText(getContext(), "(*^__^*)You Win by using "+ Ground.step +" steps to catch the dot", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_UP){
			int x,y = (int)(motionEvent.getY() / WIDTH);
			if (y%2 == 0){
				//������
				x = (int)(motionEvent.getX() / WIDTH);
			} else{
				//ż����
				x = (int)((motionEvent.getX() - WIDTH/2 ) / WIDTH);
			}
			if (x + 1 > COL || y + 1 > ROW){
				initGame();
				/*for(int i = 1; i <= 6; i++){
					System.out.println("@"+getDistance(cat, i));
				}*/
			}else if (getDot(x,y).getStatus() == Dot.STATUS_OFF){
				getDot(x,y).setStatus(Dot.STATUS_ON);
				//����ƶ�
				move();
				Ground.step = Ground.step + 1;
			}
			drawGround();
		}
		return true;
	}
}
