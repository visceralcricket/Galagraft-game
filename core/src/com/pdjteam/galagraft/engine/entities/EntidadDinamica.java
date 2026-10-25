package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// Interfaz
import com.pdjteam.galagraft.interfaces.Colisionable;

public abstract class EntidadDinamica implements Colisionable {
	
    private float x;
    private float y;
    private float velocidadX;
    private float velocidadY;
    
    private int vida;
    private int vidaMaxima;
    
    private boolean activa;
    private Sprite sprite;

    public EntidadDinamica(float x, float y, float velocidadX, float velocidadY, int vidaMaxima, Texture textura) {
        this.x = x;
        this.y = y;
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.activa = true;
        if (textura != null) {
            this.sprite = new Sprite(textura);
            this.sprite.setPosition(x, y);
        }
    }

    // método abstracto: obliga a las subclases a definir su comportamiento por frame
    public abstract void actualizar(float delta);

    public void dibujar(SpriteBatch batch) {
        if (this.activa && this.sprite != null) {
            this.sprite.draw(batch);
        }
    }

    public void recibirDano(int cantidad) {
        this.vida -= cantidad;
        if (this.vida <= 0) {
            this.vida = 0;
            this.activa = false;
        }
    }

    protected void sincronizarSprite() {
        if (this.sprite != null) {
            this.sprite.setPosition(this.x, this.y);
        }
    }

    @Override
    public Rectangle getAreaColision() {
        if (this.sprite != null) {
            return this.sprite.getBoundingRectangle();
        }
        return new Rectangle(this.x, this.y, 32, 32);
    }

    @Override
    public boolean verificarColision(Colisionable otro) {
        if (!this.activa || !otro.estaActiva()) {
            return false;
        }
        return this.getAreaColision().overlaps(otro.getAreaColision());
    }

    @Override
    public boolean estaActiva() {
        return this.activa;
    }

    // getters y setters obligatorios por encapsulamiento (GM-5)
    public float getX() { return x; }
    public void setX(float x) { this.x = x; sincronizarSprite(); }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; sincronizarSprite(); }

    public float getVelocidadX() { return velocidadX; }
    public void setVelocidadX(float velocidadX) { this.velocidadX = velocidadX; }

    public float getVelocidadY() { return velocidadY; }
    public void setVelocidadY(float velocidadY) { this.velocidadY = velocidadY; }

    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = Math.min(vida, vidaMaxima); }

    public int getVidaMaxima() { return vidaMaxima; }
    public void setVidaMaxima(int vidaMaxima) { this.vidaMaxima = vidaMaxima; }

    public void setActiva(boolean activa) { this.activa = activa; }

    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }
}