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
	
	private boolean destruido = false;
    private int vidas = GameConfig.VIDAS_INICIALES;
    private float xVel = 0;
    private final float VELOCIDAD_NAVE = 8f; // velocidad fija manejable
    
    private Sprite spr;
    private Sound soundHerido;
    private Sound soundBala;
    private Sound soundBalaJ;
    private Texture txBala;
    
    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;
    
    public Shulker(int x, int y, Texture tx, Sound soundChoque, Texture txBala, Sound soundBala, Sound soundBalaJ) {
    	this.soundHerido = soundChoque;
    	this.soundBala = soundBala;
    	this.soundBalaJ = soundBalaJ;
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
        	
        	if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
        		xVel = -VELOCIDAD_NAVE;
        	}
	        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
	        	xVel = VELOCIDAD_NAVE;
	        }
	        
        	float newX = x + xVel;
        	float newY = y; // la nave solo se mueve en horizontal
        	
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && juego.puedeDisparar()) {
        	Bullet  bala = new Bullet(spr.getX() + spr.getWidth()/2 - GameConfig.BALA_ANCHO/2, spr.getY() + spr.getHeight()-5, 0, 12, txBala);
        	juego.agregarBala(bala);
        	// con probabilidad PROB_DISPARO_J suena disparoJ en lugar del sonido normal
        	if (MathUtils.random() < GameConfig.PROB_DISPARO_J) {
        		soundBalaJ.play();
        	} else {
        		soundBala.play();
        	}
        }
    }
      
    public boolean checkCollision(Esqueleto b) {
        if(!herido && b.getArea().overlaps(spr.getBoundingRectangle())) {
            vidas--;
            herido = true;
  		    tiempoHerido = tiempoHeridoMax;
  		    soundHerido.play();
  		    
            if (vidas <= 0) destruido = true;
            return true;
        }
        return false;
    }
    
    public boolean estaDestruido() { return !herido && destruido; }
    public boolean estaHerido() { return herido; }
    public int getVidas() { return vidas; }
    public int getX() { return (int) spr.getX(); }
    public int getY() { return (int) spr.getY(); }
	public void setVidas(int vidas) { this.vidas = vidas; }
	
}