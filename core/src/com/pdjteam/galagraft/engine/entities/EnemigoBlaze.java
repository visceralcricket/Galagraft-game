package com.pdjteam.galagraft.engine.entities;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class EnemigoBlaze extends Enemigo {
    private float tiempoVida;

    public EnemigoBlaze(float x, float y, Texture texBlaze, Texture texCargaFuego, List<Proyectil> proyectiles) {
        super(x, y, 160f, -25f, 3, 1.4f, 250, texBlaze, texCargaFuego, proyectiles);
        this.tiempoVida = 0f;
    }

    @Override
    protected void ejecutarMovimiento(float delta) {
        this.tiempoVida += delta;
        float desplazamientoOndulatorio = MathUtils.sin(this.tiempoVida * 5f) * getVelocidadX();
        setX(getX() + desplazamientoOndulatorio * delta);
        setY(getY() + getVelocidadY() * delta);
    }

    @Override
    protected Proyectil crearProyectilAtaque() {
        float centroX = getX() + (getSprite().getWidth() / 2f);
        float direccionX = 0f;
        if(Shulker.getInstancia() != null) {
            float diffX = Shulker.getInstancia().getX() - centroX;
            direccionX = MathUtils.clamp(diffX, -120f, 120f);
        }
        return new Proyectil(centroX, getY(), direccionX, -320f, 2, false, getTexturaProyectil());
    }

    public float getTiempoVida() { return tiempoVida; }
    public void setTiempoVida(float tiempoVida) { this.tiempoVida = tiempoVida; }
}