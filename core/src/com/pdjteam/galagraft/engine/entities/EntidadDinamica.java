package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class EntidadDinamica {
	protected float x;
	protected float y;
	protected float xVel;
	protected float yVel;
	protected float width;
	protected float height;
	
	// entidades tienen un método de actualización lógica independiente
	// public abstract void update(float deltaTime);
	
	// public abstract void draw(SpriteBatch batch);
	
	public Rectangle getHitBox() {
		return new Rectangle(x, y, width, height);
	}
}
