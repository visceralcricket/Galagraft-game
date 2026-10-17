package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import com.pdjteam.galagraft.view.PantallaJuego;
import com.pdjteam.galagraft.config.*;
import com.pdjteam.galagraft.engine.entities.Bullet;



public class Shulker extends EntidadDinamica {
	
	private boolean destruida = false;
    private int vidas = 3;
    private float xVel = 0;
    private float yVel = 0;
    private final float VELOCIDAD_NAVE = 6f; // velocidad fija manejable
    
    private Sprite spr;
    private Sound sonidoHerido;
    private Sound soundBala;
    private Texture txBala;
    
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;
    
    public Shulker(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala) {
    	this.sonidoHerido = soundChoque;
    	this.soundBala = soundBala;
    	this.txBala = txBala;
    	this.spr = new Sprite(tx);
    	this.spr.setBounds(x, y, 45, 45);
    }
    
    public void draw(SpriteBatch batch, PantallaJuego juego){
        float x =  spr.getX();
        float y =  spr.getY();
        
        if (!herido) {
        	
        	// resetear vector velocidad en cada frame para evitar inercia
        	xVel = 0;
        	yVel = 0;
        	
        	if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
        		xVel = -VELOCIDAD_NAVE;
        	}
	        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
	        	xVel = VELOCIDAD_NAVE;
	        }
	        
        	if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
        		yVel = -VELOCIDAD_NAVE;
        	}
        	if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
        		yVel = VELOCIDAD_NAVE;
        	}
        	
        	float newX = x + xVel;
        	float newY = y + yVel;
        	
        	// físicas de borde (colisiones) en vez de rebote
        	if(newX < 0) newX = 0;
        	else if(newX + spr.getWidth() > GameConfig.ANCHO_MUNDO) {
        		newX = GameConfig.ANCHO_MUNDO - spr.getWidth();
        	}
        	
        	if(newY < 0) newY = 0;
        	else if(newY + spr.getHeight() > GameConfig.ALTO_MUNDO) {
        		newY = GameConfig.ALTO_MUNDO - spr.getHeight();
        	}
         
        	spr.setPosition(newX, newY);
        	spr.draw(batch);
        }
        
        // Lógica de temblor al recibir daño
        else {
        	spr.setX(spr.getX() + MathUtils.random(-2,2));
        	spr.draw(batch); 
        	spr.setX(x); // restaurar posición original
        	tiempoHerido--;
        	if (tiempoHerido <= 0) herido = false;
 		 }
        // mecánica de disparo
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        	Bullet  bala = new Bullet(spr.getX() + spr.getWidth()/2 - GameConfig.BALA_ANCHO/2, spr.getY() + spr.getHeight()-5, 0, 3, txBala);
        	juego.agregarBala(bala);
        	soundBala.play();
        }
    }
      
    public boolean checkCollision(Esqueleto b) {
        if(!herido && b.getArea().overlaps(spr.getBoundingRectangle())) {
            vidas--;
            herido = true;
  		    tiempoHerido = tiempoHeridoMax;
  		    sonidoHerido.play();
  		    
            if (vidas <= 0) destruida = true;
            return true;
        }
        return false;
    }
    
    public boolean estaDestruido() { return !herido && destruida; }
    public boolean estaHerido() { return herido; }
    public int getVidas() { return vidas; }
    public int getX() { return (int) spr.getX(); }
    public int getY() { return (int) spr.getY(); }
	public void setVidas(int vidas2) { vidas = vidas2; }
	
}