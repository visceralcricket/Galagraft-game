package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.pdjteam.galagraft.config.*;


public class Esqueleto {
	private int x;
    private int y;
    private int xSpeed;
    private int ySpeed;
    private Sprite spr;

    public Esqueleto(int x, int y, int size, int xSpeed, int ySpeed, Texture tx) {
    	spr = new Sprite(tx);
    	this.x = Math.max(0,  Math.min(x, (int)(GameConfig.ANCHO_MUNDO - spr.getWidth())));
    	this.y = Math.max(0,  Math.min(y, (int)(GameConfig.ALTO_MUNDO - spr.getWidth())));
    	
        spr.setPosition(this.x, this.y);
        this.setXSpeed(xSpeed);
        this.setYSpeed(ySpeed);
    }
    
    public void update() {
        x += getXSpeed();
        y += getYSpeed();
        
        if (x < 0) {
        	x = 0;
        	setXSpeed(Math.abs(getXSpeed()));
        } else if (x + spr.getWidth() > GameConfig.ANCHO_MUNDO) {
        	x = (int)(GameConfig.ANCHO_MUNDO - spr.getWidth());
        	setXSpeed(-Math.abs(getXSpeed()));
        }
        
        if (y < 0) {
        	y = 0;
        	setXSpeed(Math.abs(getXSpeed()));
        } else if (y + spr.getWidth() > GameConfig.ALTO_MUNDO) {
        	y = (int)(GameConfig.ALTO_MUNDO - spr.getWidth());
        	setYSpeed(-Math.abs(getYSpeed()));
        }
        
        if (x+getXSpeed() < 0 || x+getXSpeed()+spr.getWidth() > GameConfig.ANCHO_MUNDO)
        	setXSpeed(getXSpeed() * -1);
        if (y+getYSpeed() < 0 || y+getYSpeed()+spr.getHeight() > GameConfig.ALTO_MUNDO)
        	setYSpeed(getYSpeed() * -1);
        spr.setPosition(x, y);
    }
    
    public Rectangle getArea() {
    	return spr.getBoundingRectangle();
    }
    public void draw(SpriteBatch batch) {
    	spr.draw(batch);
    }
    
    public void checkCollision(Esqueleto b2) {
        if(spr.getBoundingRectangle().overlaps(b2.spr.getBoundingRectangle())){
        	// rebote
            if (getXSpeed() ==0) setXSpeed(getXSpeed() + b2.getXSpeed()/2);
            if (b2.getXSpeed() ==0) b2.setXSpeed(b2.getXSpeed() + getXSpeed()/2);
        	setXSpeed(- getXSpeed());
            b2.setXSpeed(-b2.getXSpeed());
            
            if (getYSpeed() ==0) setYSpeed(getYSpeed() + b2.getYSpeed()/2);
            if (b2.getYSpeed() ==0) b2.setYSpeed(b2.getYSpeed() + getYSpeed()/2);
            setYSpeed(- getYSpeed());
            b2.setYSpeed(- b2.getYSpeed()); 
        }
    }
	public int getXSpeed() {
		return xSpeed;
	}
	public void setXSpeed(int xSpeed) {
		this.xSpeed = xSpeed;
	}
	public int getYSpeed() {
		return ySpeed;
	}
	public void setYSpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}
	
    
}
