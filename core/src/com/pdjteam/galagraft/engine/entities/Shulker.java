package com.pdjteam.galagraft.engine.entities;

import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.strategies.DisparoSimpleStrategy;
import com.pdjteam.galagraft.strategies.EstrategiaDisparo;
import com.pdjteam.galagraft.interfaces.Colisionable;

public class Shulker extends EntidadDinamica {
    // Instancia única, estática y privada (GM-6: Singleton)
    private static Shulker instancia;

    // Atributos encapsulados (GM-5)
    private int puntaje;
    private float tiempoDesdeUltimoDisparo;
    private float tiempoPowerUpRestante;
    private EstrategiaDisparo estrategiaDisparo; // referencia a Strategy (GM-7)
    private Texture texturaBala;
    private Sound sonidoDisparo;
    private Sound sonidoHurt;
    private List<Proyectil> proyectilesJuego;

    // Patrón Singleton con constructor private (GM-6)
    private Shulker(float x, float y, Texture texturaJugador, Texture texturaBala, 
                    Sound sonidoDisparo, Sound sonidoHurt, List<Proyectil> proyectiles) {
        super(x, y, 300f, 0f, 5, texturaJugador);
        this.puntaje = 0;
        this.tiempoDesdeUltimoDisparo = 0f;
        this.tiempoPowerUpRestante = 0f;
        this.estrategiaDisparo = new DisparoSimpleStrategy();
        this.texturaBala = texturaBala;
        this.sonidoDisparo = sonidoDisparo;
        this.sonidoHurt = sonidoHurt;
        this.proyectilesJuego = proyectiles;
    }

    // acceso global para inicializar o recuperar el Singleton
    public static Shulker inicializarInstancia(float x, float y, Texture texJugador, Texture texBala,
                                               Sound sDisparo, Sound sHurt, List<Proyectil> proyectiles) {
        if (instancia == null) {
            instancia = new Shulker(x, y, texJugador, texBala, sDisparo, sHurt, proyectiles);
        } else {
            instancia.reiniciarEstado(x, y, proyectiles);
        }
        return instancia;
    }

    public static Shulker getInstancia() {
        return instancia;
    }

    public void reiniciarEstado(float x, float y, List<Proyectil> proyectiles) {
        setX(x);
        setY(y);
        setVida(getVidaMaxima());
        setActiva(true);
        this.puntaje = 0;
        this.tiempoDesdeUltimoDisparo = 0f;
        this.tiempoPowerUpRestante = 0f;
        this.estrategiaDisparo = new DisparoSimpleStrategy();
        this.proyectilesJuego = proyectiles;
    }

    // alternar comportamiento de disparo al recoger un PowerUp (GM-7)
    public void aplicarPowerUp(EstrategiaDisparo nuevaEstrategia, float duracionSegundos) {
        this.estrategiaDisparo = nuevaEstrategia;
        this.tiempoPowerUpRestante = duracionSegundos;
    }

    @Override
    public void actualizar(float delta) {
        if (!estaActiva()) return;

        this.tiempoDesdeUltimoDisparo += delta;

        // control de duración del Power-Up -> vuelve a estrategia base al expirar
        if (this.tiempoPowerUpRestante > 0) {
            this.tiempoPowerUpRestante -= delta;
            if (this.tiempoPowerUpRestante <= 0) {
                this.estrategiaDisparo = new DisparoSimpleStrategy();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            setX(getX() - getVelocidadX() * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            setX(getX() + getVelocidadX() * delta);
        }

        float ancho = getSprite() != null ? getSprite().getWidth() : 32f;
        if (getX() < 0) setX(0);
        if (getX() + ancho > Gdx.graphics.getWidth()) setX(Gdx.graphics.getWidth() - ancho);

        // Disparo delegando al patrón Strategy (GM-7)
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && 
            this.tiempoDesdeUltimoDisparo >= this.estrategiaDisparo.getTiempoEnfriamiento()) {
            this.estrategiaDisparo.ejecutarDisparo(this, this.proyectilesJuego, this.texturaBala);
            if (this.sonidoDisparo != null) {
                this.sonidoDisparo.play(0.5f);
            }
            this.tiempoDesdeUltimoDisparo = 0f;
        }

        sincronizarSprite();
    }

    @Override
    public void alColisionar(Colisionable otro) {
        if (otro instanceof Proyectil) {
            Proyectil p = (Proyectil) otro;
            if (!p.isEsDelJugador()) {
                recibirDano(p.getDano());
                if (this.sonidoHurt != null) this.sonidoHurt.play();
            }
        } else if (otro instanceof Enemigo) {
            recibirDano(1);
            if (this.sonidoHurt != null) this.sonidoHurt.play();
        }
    }

    // Getters y Setters (GM-5)
    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
    public void agregarPuntaje(int puntos) { this.puntaje += puntos; }
    public float getTiempoDesdeUltimoDisparo() { return tiempoDesdeUltimoDisparo; }
    public void setTiempoDesdeUltimoDisparo(float t) { this.tiempoDesdeUltimoDisparo = t; }
    public float getTiempoPowerUpRestante() { return tiempoPowerUpRestante; }
    public void setTiempoPowerUpRestante(float t) { this.tiempoPowerUpRestante = t; }
    public EstrategiaDisparo getEstrategiaDisparo() { return estrategiaDisparo; }
    public void setEstrategiaDisparo(EstrategiaDisparo estrategia) { this.estrategiaDisparo = estrategia; }
    public Texture getTexturaBala() { return texturaBala; }
    public void setTexturaBala(Texture texturaBala) { this.texturaBala = texturaBala; }
    public Sound getSonidoDisparo() { return sonidoDisparo; }
    public void setSonidoDisparo(Sound sonidoDisparo) { this.sonidoDisparo = sonidoDisparo; }
    public Sound getSonidoHurt() { return sonidoHurt; }
    public void setSonidoHurt(Sound sonidoHurt) { this.sonidoHurt = sonidoHurt; }
    public List<Proyectil> getProyectilesJuego() { return proyectilesJuego; }
    public void setProyectilesJuego(List<Proyectil> proyectiles) { this.proyectilesJuego = proyectiles; }
}