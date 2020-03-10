package com.DT.airPlaneGame;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;

/** 主程序 */
public class ShootGame extends JPanel {
	public static final int WIDTH = 400;  //窗口宽
	public static final int HEIGHT = 654; //窗口高
	
	public static BufferedImage background; //背景图
	public static BufferedImage start; //启动图
	public static BufferedImage pause; //暂停图
	public static BufferedImage gameover; //游戏结束图
	public static BufferedImage airplane; //敌机图
	public static BufferedImage bee; //小蜜蜂图
	public static BufferedImage bullet; //子弹图
	public static BufferedImage hero0; //英雄机图1
	public static BufferedImage hero1; //英雄机图2

	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	private int state = 0;
	
	private Hero hero = new Hero();  //英雄机对象
	private FlyingObject[] flyings = {}; //敌人(敌机+小蜜蜂)数组
	private Bullet[] bullets = {}; //子弹数组
	
	
	private Timer timer;  //定时器
	private int intervel = 10; //时间间隔(以毫秒为单位)

	static{  //初始化静态资源
		try{
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** 随机生成敌人(敌机+小蜜蜂)对象 */
	//方法工厂模式，创建指定的对象
	public static FlyingObject nextOne(){
		Random rand = new Random();  //随机数对象
		int type = rand.nextInt(20); //生成0到19之间的随机数
		if(type <= 5){
			return new Bee();  //为0时返回蜜蜂对象
		}else{
			return new AirPlane(); //1到19时返回敌机对象
		}
	}
	
	int flyEnteredIndex = 0; //敌人入场计数
	/** 敌人入场 */
	public void enterAction(){ //10毫秒走一次
		flyEnteredIndex++; //10毫秒增1
		if(flyEnteredIndex%40==0){ //每400(10*40)毫秒走一次
			FlyingObject one = nextOne(); //获取随机敌人对象
			flyings = Arrays.copyOf(flyings,flyings.length+1); //扩容
			flyings[flyings.length-1] = one; //将敌人存储到flyings数组中的最后一个元素
		}
	}
	
	/** 飞行物走一步 */
	public void stepAction(){ //10毫秒走一次
		hero.step();  //英雄机走一步
		for (FlyingObject  f : flyings) {
			f.step();
		}
		for(int i=0;i<bullets.length;i++){ //遍历所有子弹
			bullets[i].step(); //子弹走一步
		}
	}
	
	int shootIndex = 0; //英雄机发射子弹计数器
	/** 子弹入场(英雄机发射子了弹) */
	public void shootAction(){ //10毫秒走一次
		shootIndex++; //10毫秒增1
		if(shootIndex%30==0){ //每300(10*30)毫秒走一次
			Bullet[] bs = hero.shoot(); //获取英雄机发射的子弹数组
			bullets = Arrays.copyOf(bullets,bullets.length+bs.length); //扩容(bs有几个扩充几个)
			System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length); //追加数组
		}
	}
	
	/** 启动执行 */
	public void action(){
		MouseAdapter listener = new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				//英雄机移动
				if (state == RUNNING) {
					int x = e.getX();
					int y = e.getY();
					hero.moveTo( x, y );
				}

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				switch (state){
					case START:
						state = RUNNING;
						break;
	 				case GAME_OVER:
	 					hero = new Hero();
	 					flyings = new FlyingObject[0];
	 					bullets = new Bullet[0];
	 					score = 0;
						state = START;
						break;
				}
			}
			/** 鼠标移开事件处理 */
			public void mouseExited(MouseEvent e){
				if(state == RUNNING){ //运行状态时
					state = PAUSE;    //改为暂停状态
				}
			}
			/** 鼠标移入事件处理 */
			public void mouseEntered(MouseEvent e){
				if(state == PAUSE){  //暂停状态时
					state = RUNNING; //改为运行状态
				}
			}
		};

		this.addMouseListener( listener );//处理鼠标操作事件
		this.addMouseMotionListener( listener );//处理鼠标的滑动事件




		timer = new Timer(); //创建定时器对象
		timer.schedule(new TimerTask(){
			public void run(){ //定时干的事，10毫秒走一次
				if(state == RUNNING) {
					enterAction(); //敌人(敌机+小蜜蜂)入场
					stepAction();  //飞行物走一步
					shootAction(); //子弹入场(英雄机发射子弹)
					outOfBoundAction();
					bangAction();//子弹与敌人撞
					checkGameOverAciton();
				}
				repaint();     //重绘(调用paint()方法)
			}
		},intervel,intervel);
	}

	public void checkGameOverAciton() {
		if (isGameOver()){
			state = GAME_OVER;
		}

	 }

	public boolean isGameOver(){
		 for (int i = 0;i<flyings.length;i++){
			if (hero.hit( flyings[i] )){
				hero.setDoubleFire(0);
				hero.subtractLife();
				FlyingObject temp = flyings[i];
				flyings[i] = flyings[flyings.length-1];
				flyings[flyings.length-1] = temp;
				//缩容：缩掉最后一个对象，即被撞敌人对象
				flyings = Arrays.copyOf( flyings,flyings.length-1 );
			}
		 }
		return hero.getLife() <= 0;
	 }


	/**
	 * 所有子弹和所有敌人撞
	 */
	public void bangAction() {
		for (Bullet b:bullets) {
		bang(b);
		}
	}
	int score = 0;
	/**
	 * 一个子弹和所有敌人撞
	 * @param bullet
	 */
	public void bang(Bullet bullet){
		int index = -1;//记录被撞敌人的索引 很好的习惯，工作中常用方法，自制跳转表
		for(int i = 0;i<flyings.length;i++){
			FlyingObject flyingObject = flyings[i];
			if(flyingObject.shootBy( bullet )){
				index = i;//记录被撞敌人的索引
				break;//该子弹不再与其他敌人相撞
			}
		}
		if(index != -1) {//撞上了
			FlyingObject one = flyings[index];
			if(one instanceof Enemy){
				Enemy enemy = (Enemy) one;
				score += enemy.getScore();
			}if (one instanceof Award){
				Award award = (Award) one;
				  int type =award.getType();

				  switch (type){
					  case Award.DOUBLE_FIRE:
					  	 hero.addDoubleFire();
					  	 break;
					  case Award.LIFE:
					  	hero.addLife();
					  	break;
				  }
			}
			//将被撞敌人和最后一个元素交换
			FlyingObject temp = flyings[index];
			flyings[index] = flyings[flyings.length-1];
			flyings[flyings.length-1] = temp;
			//缩容：缩掉最后一个对象，即被撞敌人对象
			flyings = Arrays.copyOf( flyings,flyings.length-1 );

		}
 	}

	/**
	 * 删除越界的飞行物
	 * 小算法
	 */
	public void outOfBoundAction() {
		 int index = 0;
		 FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for (FlyingObject f:flyings) {
			if (!f.outOfBounds()){
				flyingLives[index] = f;
				index++;
			}
		}
		flyings = Arrays.copyOf( flyingLives,index );
		//越界子弹的删除
		 index = 0;
		Bullet[] bulletLives = new Bullet[bullets.length];
		for (Bullet b:bullets) {
			if (!b.outOfBounds()){
				bulletLives[index] = b;
				index++;
			}
		}
		bullets = Arrays.copyOf( bulletLives,index );

	}


	/** 重写paint() g:画笔*/
 	public void paint(Graphics g){
 		g.drawImage(background,0,0,null); //画背景图
 		paintHero(g); //画英雄机对象
 		paintFlyingObjects(g); //画敌人(敌机+小蜜蜂)对象
 		paintBullets(g); //画子弹对象
		paintScore(g);//画分和命
		paintState(g);

  	}

	public void paintState(Graphics g) {
 		switch (state){
			case START:
				g.drawImage( start,0,0,null );
				break;
			case PAUSE:
				g.drawImage( pause,0,0,null );
				break;
			case GAME_OVER:
				g.drawImage( gameover,0,0,null );
				break;

		}
	}

	public void paintScore(Graphics g) {
 		g.setColor(new Color(0xFF0000));
 		g.setFont( new Font( Font.DIALOG ,Font.BOLD,20) );

 		g.drawString( "SCORE:"+score,10,25 );
 		g.drawString( "LIFE:"+hero.getLife(),10,40 );
	}

	/** 画英雄机 */
 	public void paintHero(Graphics g){
 		g.drawImage(hero.image,hero.x,hero.y,null); //画英雄机对象
 	}
 	/** 画敌人 */
 	public void paintFlyingObjects(Graphics g){
 		for(int i=0;i<flyings.length;i++){ //遍历所有敌人
 			FlyingObject f = flyings[i]; //获取每一个敌人
 			g.drawImage(f.image,f.x,f.y,null); //画敌人对象
 		}
 	}
 	/** 画子弹 */
 	public void paintBullets(Graphics g){
 		for(int i=0;i<bullets.length;i++){ //遍历所有子弹
 			Bullet b = bullets[i]; //获取每一个子弹
 			g.drawImage(b.image,b.x,b.y,null); //画子弹对象
 		}
 	}

 	/** 主方法 */
	public static void main(String[] args) {
 		JFrame frame = new JFrame("Fly"); //窗口
 		ShootGame game = new ShootGame(); //面板
 		frame.add(game); //将面板添加到窗口中
 		frame.setSize(WIDTH, HEIGHT); //设置窗口大小
 		frame.setAlwaysOnTop(true); //设置一直居上
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置默认关闭操作--关闭窗口时退出程序
 		frame.setLocationRelativeTo(null); //设置相对位置为null，即:居中
 		frame.setVisible(true); //1.设置窗口可见  2.尽快调用paint()方法
 		
 		game.action();  //启动执行
 		
	}
}

















