package com.DT.airPlaneGame;
import java.util.Random;


public class AirPlane extends FlyingObject implements Enemy {
	private int speed = 2;
	

	public AirPlane(){
		image = ShootGame.airplane;
		width = image.getWidth();
		height = image.getHeight();
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH-this.width);
		y = -this.height; //y:??????????
	}
	

	public int getScore(){
		return 5;
	}


	public void step(){
		y += speed;
	}

	@Override
	public boolean outOfBounds() {
		return this.y>ShootGame.HEIGHT;
	}
}









