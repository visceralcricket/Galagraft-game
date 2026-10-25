package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.pdjteam.galagraft.config.GameConfig;
import com.pdjteam.galagraft.interfaces.Colisionable;

public class Bullet implements Colisionable {

    private int xSpeed;
    private int ySpeed;
    private boolean destroyed = false;
    private Sprite spr;

    public Bullet(float x, float y, int xSpeed, int ySpeed, Texture tx) {
        spr = new Sprite(tx);
        spr.setSize(GameConfig.BALA_ANCHO, GameConfig.BALA_ALTO);
        spr.setPosition(x, y);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void update() {
        spr.setPosition(spr.getX() + xSpeed, spr.getY() + ySpeed);
        if (spr.getX() < 0 || spr.getX() + spr.getWidth() > GameConfig.ANCHO_MUNDO) {
            destroyed = true;
        }
        if (spr.getY() < 0 || spr.getY() + spr.getHeight() > GameConfig.ALTO_MUNDO) {
            destroyed = true;
        }
    }

    public void draw(SpriteBatch batch) {
        spr.draw(batch);
    }

    public boolean checkCollision(Enemigo b2) {
        if (spr.getBoundingRectangle().overlaps(b2.getAreaColision())) {
            this.destroyed = true;
            return true;
        }
        return false;
    }

    @Override
    public Rectangle getAreaColision() {
        return spr.getBoundingRectangle();
    }

    @Override
    public boolean verificarColision(Colisionable otro) {
        if (this.destroyed || !otro.estaActiva()) return false;
        return this.getAreaColision().overlaps(otro.getAreaColision());
    }

    @Override
    public void alColisionar(Colisionable otro) {
        this.destroyed = true;
    }

    @Override
    public boolean estaActiva() {
        return !this.destroyed;
    }

    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }
    public int getXSpeed() { return xSpeed; }
    public void setXSpeed(int xSpeed) { this.xSpeed = xSpeed; }
    public int getYSpeed() { return ySpeed; }
    public void setYSpeed(int ySpeed) { this.ySpeed = ySpeed; }
    public Sprite getSpr() { return spr; }
    public void setSpr(Sprite spr) { this.spr = spr; }
}