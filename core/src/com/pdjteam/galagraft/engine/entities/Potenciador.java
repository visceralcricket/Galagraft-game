package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import com.pdjteam.galagraft.config.GameConfig;

// strategies
import com.pdjteam.galagraft.strategies.EstrategiaPotenciador;

// interfaz
import com.pdjteam.galagraft.interfaces.Colisionable;

/**
 * Potenciador (power-up): objeto que cae desde donde murió un enemigo y que el Shulker recoge al tocarlo.
 * Es el contexto del Strategy (GM-7): lo que hace lo define la EstrategiaPotenciador que contiene,
 * así que un potenciador nuevo no necesita una subclase, solo una estrategia nueva.
 */
public class Potenciador extends EntidadDinamica {
    private final EstrategiaPotenciador estrategia;
    private Sound sonidoRecoger;  // puede ser null

    public Potenciador(float x, float y, EstrategiaPotenciador estrategia,
                       Texture textura, Sound sonidoRecoger) {
        super(x, y, 0f, -GameConfig.POTENCIADOR_VEL_CAIDA, 1, textura);
        this.estrategia = estrategia;
        this.sonidoRecoger = sonidoRecoger;

        if(getSprite() != null) {
            getSprite().setSize(GameConfig.POTENCIADOR_TAMANO, GameConfig.POTENCIADOR_TAMANO);
            getSprite().setPosition(x, y);
        }
    }

    /**
     * TEMPLATE METHOD (GM-8): Plantilla invariante de lo que ocurre al recoger un potenciador.
     * Es 'final' para que no se altere el orden: sonido -> efecto (estrategia) -> consumir.
     */
    public final void recoger(Shulker jugador) {
        if(!estaActiva() || jugador == null) return;

        reproducirSonido();
        jugador.activarPotenciador(this.estrategia);

        // se consume: deja de dibujarse y de colisionar
        setActiva(false);
    }

    // Gancho: las subclases pueden cambiar el sonido si lo necesitan
    protected void reproducirSonido() {
        if(this.sonidoRecoger != null) {
            this.sonidoRecoger.play();
        }
    }

    /** Cae en vertical y se desactiva al salir por el borde inferior sin ser recogido. */
    @Override
    public void actualizar(float delta) {
        if(!estaActiva()) return;

        setY(getY() + getVelocidadY() * delta);

        float alto = getSprite() != null ? getSprite().getHeight() : 32f;
        if(getY() < -alto) {
            setActiva(false);
        }
    }

    // Solo el Shulker puede recoger un potenciador
    @Override
    public void alColisionar(Colisionable otro) {
        if(otro instanceof Shulker) {
            recoger((Shulker) otro);
        }
    }

    // Getters y Setters
    public EstrategiaPotenciador getEstrategia() { return estrategia; }
    public Sound getSonidoRecoger() { return sonidoRecoger; }
    public void setSonidoRecoger(Sound sonidoRecoger) { this.sonidoRecoger = sonidoRecoger; }
}
