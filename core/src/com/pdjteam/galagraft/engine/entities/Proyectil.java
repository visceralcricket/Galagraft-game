package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.config.GameConfig;
import com.pdjteam.galagraft.interfaces.Colisionable;

public class Proyectil extends EntidadDinamica {
    private int dano;
    private boolean esDelJugador;

    public Proyectil(float x, float y, float velocidadX, float velocidadY, int dano, boolean esDelJugador, Texture textura) {
        this(x, y, velocidadX, velocidadY, dano, esDelJugador, textura, GameConfig.BALA_ANCHO, GameConfig.BALA_ALTO);
    }

    // Variante con tamaño propio, para los proyectiles que no miden lo mismo que la bala del jugador
    public Proyectil(float x, float y, float velocidadX, float velocidadY, int dano, boolean esDelJugador, Texture textura,
                     float ancho, float alto) {
        super(x, y, velocidadX, velocidadY, textura);
        this.dano = dano;
        this.esDelJugador = esDelJugador;
        if (getSprite() != null) {
            getSprite().setSize(ancho, alto);
            getSprite().setPosition(x, y);
        }
    }

    @Override
    public void actualizar(float delta) {
        if(!estaActiva()) return;
        setX(getX() + getVelocidadX() * delta);
        setY(getY() + getVelocidadY() * delta);

        float alto = getSprite() != null ? getSprite().getHeight() : GameConfig.BALA_ALTO;
        float ancho = getSprite() != null ? getSprite().getWidth() : GameConfig.BALA_ANCHO;

        if(getY() > GameConfig.ALTO_MUNDO || getY() < -alto ||
            getX() < -ancho || getX() > GameConfig.ANCHO_MUNDO) {
            setActiva(false);
        }
    }

    @Override
    public void alColisionar(Colisionable otro) {
        if (!estaActiva() || otro == null || !otro.estaActiva()) return;
        otro.recibirColisionDeProyectil(this);
    }

    @Override
    public void recibirColisionDeEnemigo(Enemigo enemigo) {
        if (this.esDelJugador) {
            enemigo.recibirDano(this.dano);
            this.setActiva(false);
        }
    }

    @Override
    public void recibirColisionDeJugador(Shulker jugador) {
        if (!this.esDelJugador) {
            jugador.recibirColisionDeProyectil(this);
        }
    }

    public int getDano() { return dano; }
    public void setDano(int dano) { this.dano = dano; }
    
    public boolean isEsDelJugador() { return esDelJugador; }
    public void setEsDelJugador(boolean esDelJugador) { this.esDelJugador = esDelJugador; }
}