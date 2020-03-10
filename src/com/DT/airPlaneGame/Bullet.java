package com.DT.airPlaneGame;

import java.awt.image.BufferedImage;

/**
 * this class is a subclass of FlyingObject
 */
public class Bullet extends FlyingObject {
     private int speed = 3;
     
     
     public Bullet(int x,int y){
         image = ShootGame.bullet;
         width = image.getWidth();
         height = image.getHeight();
         this.x = x;
         this.y = y;
     }


	@Override
	public void step() {
		y -= speed;
		
	}

    @Override
    public boolean outOfBounds() {
        return this.y<-this.height;
    }
}
