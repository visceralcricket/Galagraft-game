package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.pdjteam.galagraft.config.GameConfig;
import com.pdjteam.galagraft.interfaces.Colisionable;

public class Proyectil implements Colisionable {
    private float x;
    private float y;
    private float velocidadX;
    private float velocidadY;
    private int dano;
    private boolean esDelJugador;
    private boolean activa;
    private Sprite sprite;

    public Proyectil(float x, float y, float velocidadX, float velocidadY, int dano, boolean esDelJugador, Texture textura) {
        this.x = x;
        this.y = y;
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        this.dano = dano;
        this.esDelJugador = esDelJugador;
        this.activa = true;
        this.sprite = new Sprite(textura);
        this.sprite.setSize(GameConfig.BALA_ANCHO, GameConfig.BALA_ALTO);
        this.sprite.setPosition(x, y);
    }

    public void actualizar(float delta) {
        if (!this.activa) return;
        this.x += this.velocidadX * delta;
        this.y += this.velocidadY * delta;
        this.sprite.setPosition(this.x, this.y);

        if (this.y > GameConfig.ALTO_MUNDO || this.y < -this.sprite.getHeight() ||
            this.x < -this.sprite.getWidth() || this.x > GameConfig.ANCHO_MUNDO) {
            this.activa = false;
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (this.activa && this.sprite != null && this.sprite.getTexture() != null) {
            this.sprite.draw(batch);
        }
    }

    @Override
    public Rectangle getAreaColision() {
        return this.sprite.getBoundingRectangle();
    }

    @Override
    public boolean verificarColision(Colisionable otro) {
        if (!this.activa || !otro.estaActiva()) return false;
        return this.getAreaColision().overlaps(otro.getAreaColision());
    }

    @Override
    public void alColisionar(Colisionable otro) {
        this.activa = false;
    }

    @Override
    public boolean estaActiva() { return activa; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    
    public float getVelocidadX() { return velocidadX; }
    public void setVelocidadX(float velocidadX) { this.velocidadX = velocidadX; }
    
    public float getVelocidadY() { return velocidadY; }
    public void setVelocidadY(float velocidadY) { this.velocidadY = velocidadY; }
    
    public int getDano() { return dano; }
    public void setDano(int dano) { this.dano = dano; }
    
    public boolean isEsDelJugador() { return esDelJugador; }
    public void setEsDelJugador(boolean esDelJugador) { this.esDelJugador = esDelJugador; }
    
    public void setActiva(boolean activa) { this.activa = activa; }
    
    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }
}