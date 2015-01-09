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
 * @date 2015-1-5下午6:06:12
 */

/*画布类,实现画面设计*/
public class Ground extends SurfaceView implements OnTouchListener{
	
	private static int WIDTH = 40;				//每个点的宽度
	private static final int ROW = 10;
	private static final int COL = 10;
	//障碍物个数
	private static final int BOLCKS = 20;
	//点击次数
	private static int step = 1;
	//要捕获的那个点
	private Dot cat;
	//二维数组
	private Dot[][] matrix;
	//构造函数
	public Ground(Context context) {
		super(context);
		getHolder().addCallback(callback);
		//初始化地板
		matrix = new Dot[ROW][COL];
		for (int i = 0; i < ROW; i++ ){
			for(int j = 0; j < COL; j++){
				matrix[i][j] = new Dot(j,i);
			}
		}
		setOnTouchListener(this);
		//初始化
		initGame();
	}
	//初始化游戏
	public void initGame(){
		for(int x = 0; x < ROW; x++){
			for(int y = 0; y < COL; y++){
				matrix[x][y].setStatus(Dot.STATUS_OFF);
			}
		}
		cat = new Dot(4,5);
		getDot(4,5).setStatus(Dot.STATUS_IN);
		for (int i = 0; i < BOLCKS;){
			//设置障碍物随机生成点的横纵坐标
			int x = (int)((Math.random() * 1000) % COL);
			int y = (int)((Math.random() * 1000) % ROW);
			if (getDot(x,y).getStatus() == Dot.STATUS_OFF){
				getDot(x,y).setStatus(Dot.STATUS_ON);
				i++;
			}
		}
		Ground.step = 1;
	}

	//绘制地板方法
	public void drawGround(){
		//创建画布
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.LTGRAY);
	
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		for (int i = 0; i < ROW; i++){
			int offset = 0;			//设置缩进
			if ( i % 2 != 0){
				offset = WIDTH / 2;
			}
			for (int j = 0; j < COL; j++){
				Dot dot = getDot(j,i);
				//给不同状态的点上色
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
				//绘制到画布上
				canvas.drawOval(new RectF(dot.getX() * WIDTH + offset, dot.getY() * WIDTH, 	//
						(dot.getX()+1) * WIDTH + offset, (dot.getY()+1) * WIDTH), paint);
			}
		}
		getHolder().unlockCanvasAndPost(canvas);
	}
	
	//回调接口
	private Callback callback = new Callback(){

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			WIDTH = arg2/(COL + 1);
			drawGround();
		}
		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			//界面初始化时调用绘制画布函数
			drawGround();
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {	
		}
	};

	/*
	 * 边界判断,判断点是否处于边界状态
	 * 判断条件为点的任何一方坐标有一个等于0或者等于行列值
	 */
	private boolean isAtEdge(Dot dot){
		if (dot.getX() * dot.getY() == 0 || dot.getX() + 1 == COL || dot.getY() + 1 == ROW){
			return true;
		}
		return false;
	}
	//根据坐标值获得具体的点
	private Dot getDot(int x,int y){
		return matrix[y][x];
	}
	//
	/*
	 * 获得点的相邻点,dir为方向值
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
	
	//要捕获的点的移动方式
	private void moveTo(Dot dot){
		dot.setStatus(Dot.STATUS_IN);
		getDot(cat.getX(),cat.getY()).setStatus(Dot.STATUS_OFF);;
		cat.setXY(dot.getX(), dot.getY());
	}
	
	/*
	 * 获取当前点到边界的距离,根据返回值判断是否有路障
	 * 返回正数说明当前点到边界之间的路径上没有路障
	 * 返回负数说明有路障
	 * 并且正负数的数值表示点到边界或路障之间的距离
	 */
	private int getDistance(Dot dot, int dir){
		int distance = 0;
		if (isAtEdge(dot)){
			return 1;
		}
		//当前点所在的初始位置
		Dot orignal = dot;
		//下一个点
		Dot next;
		while(true){
			next = getNeighour(orignal, dir);
			//如果遇到路障直接返回
			if (next.getStatus() == Dot.STATUS_ON){
				return -distance;
			}
			//如果下个点在边界上距离+1后返回
			else if (isAtEdge(next)){
				return ++distance;
			}
			distance++;
			orignal = next;	
		}
	}
	
	/*
	 *相应点击事件,点击后红点的下一步移动 (关键算法)
	 */
	private void move(){
		//存放所有可用的点
		List<Dot> avaliableDot = new ArrayList<Dot>();
		//存放到边缘距离最短的点
		List<Dot> leastToEdge = new ArrayList<Dot>();
		//存放可用点和点的方向
		Map<Dot,Integer> map = new HashMap<Dot,Integer>();
		if (isAtEdge(cat)){
			lose();
			return;
		}
		//遍历猫的四周的点,寻找下个可用的点作为逃跑的点
		for (int i = 1; i <= 6; i++){
			Dot neighbor = getNeighour(cat, i);
			if (neighbor.getStatus() == Dot.STATUS_OFF){
				avaliableDot.add(neighbor);
				map.put(neighbor, i);
				//如果可用的点中有到边缘最短距离的点则将该点存起来
				if (getDistance(neighbor,i) > 0){
					leastToEdge.add(neighbor);
				}
			} 
		}
		if (avaliableDot.size() == 0){
			win();
		} else if (avaliableDot.size() == 1){		//如果只有一个可行的点只能朝这个点走
			moveTo(avaliableDot.get(0));
		}
		else {
			//寻找最优的下一个点
			Dot best = null;
			//如果存在无路障到达边缘最短的点
			if (leastToEdge.size() > 0){
				//min一定要确保足够大,至少大于界面中距离最远的两点之间的距离,否则会报空指针异常
				int min = 99;
				for (int i = 0; i < leastToEdge.size(); i++){
					//获得当前为位置到所有可以到达边缘的位置之间的最小距离
					int minDistance = getDistance(leastToEdge.get(i),	//
							map.get(leastToEdge.get(i)));
					if (minDistance < min){
						min = minDistance;
						best = leastToEdge.get(i);
					}
				}
				moveTo(best);
			}
			//当所有方向上都有路障时选择到达路障距离最长的点
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
				//移动到最优点
				moveTo(best);
			}
		}
	}
	
	
	/**
	 * 游戏结束
	 */
	private void lose() {
		Toast.makeText(getContext(), "(；′⌒`)You fail to catch the dot", Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * 游戏胜利 
	 */
	private void win(){
		Toast.makeText(getContext(), "(*^__^*)You Win by using "+ Ground.step +" steps to catch the dot", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_UP){
			int x,y = (int)(motionEvent.getY() / WIDTH);
			if (y%2 == 0){
				//奇数行
				x = (int)(motionEvent.getX() / WIDTH);
			} else{
				//偶数行
				x = (int)((motionEvent.getX() - WIDTH/2 ) / WIDTH);
			}
			if (x + 1 > COL || y + 1 > ROW){
				initGame();
				/*for(int i = 1; i <= 6; i++){
					System.out.println("@"+getDistance(cat, i));
				}*/
			}else if (getDot(x,y).getStatus() == Dot.STATUS_OFF){
				getDot(x,y).setStatus(Dot.STATUS_ON);
				//点的移动
				move();
				Ground.step = Ground.step + 1;
			}
			drawGround();
		}
		return true;
	}
}
