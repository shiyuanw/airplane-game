package com.DT.airPlaneGame;

import java.util.Random;

/**
 * this class is a subclass of FlyingObject and Award
 */
public class Bee extends FlyingObject implements Award {
    private int xSpeed = 2;
    private  int ySpeed = 3;
    private  int awardType;

    public Bee(){
        image = ShootGame.bee;
        width = image.getWidth();
        height = image.getHeight();
        Random random = new Random();
        x = random.nextInt(ShootGame.WIDTH-this.width);
        y =-this.height;
        awardType =random.nextInt(2);
    }

    /**
     * this method is to get the award type
     * @return the award type
     */
    @Override
    public int getType() {
        return awardType;
    }

	@Override
	public void step() {
		if (x>=ShootGame.WIDTH-this.width) {
			xSpeed = -1;
		} else if (x<0) {
			xSpeed = 1;
		}
		
		x += xSpeed;
		y += ySpeed;
		
		
	}

    @Override
    public boolean outOfBounds() {
        return this.y>ShootGame.HEIGHT;
    }
}
