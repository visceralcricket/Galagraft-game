package com.pdjteam.galagraft.engine.entities;

import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

import com.pdjteam.galagraft.config.GameConfig;

// strategies
import com.pdjteam.galagraft.strategies.DisparoSimpleStrategy;
import com.pdjteam.galagraft.strategies.EstrategiaDisparo;

// Interfaz
import com.pdjteam.galagraft.interfaces.Colisionable;

public class Shulker extends EntidadDinamica {
    // GM-6 Singleton
    private static Shulker instancia;
    private static final float TAMANO_SHULKER = 64f;

    // atributos encapsulados: GM-5
    private int puntaje;
    private float tiempoDesdeUltimoDisparo;
    private float tiempoPowerUpRestante;
    private EstrategiaDisparo estrategiaDisparo; // referencia a Strategy (GM-7)
    private Texture texturaBala;
    private Sound sonidoDisparo;
    private Sound sonidoDisparoJ;
    private Sound sonidoHurt;
    private List<Proyectil> proyectilesJuego;

    private Shulker(float x, float y, Texture texturaJugador, Texture texturaBala, 
                    Sound sonidoDisparo, Sound sonidoDisparoJ, Sound sonidoHurt, List<Proyectil> proyectiles) {
        super(x, y, 500f, 0f, GameConfig.VIDAS_INICIALES, texturaJugador);
        this.puntaje = 0;
        this.tiempoDesdeUltimoDisparo = 0f;
        this.tiempoPowerUpRestante = 0f;
        this.estrategiaDisparo = new DisparoSimpleStrategy();
        this.texturaBala = texturaBala;
        this.sonidoDisparo = sonidoDisparo;
        this.sonidoDisparoJ = sonidoDisparoJ;
        this.sonidoHurt = sonidoHurt;
        this.proyectilesJuego = proyectiles;

        if(getSprite() != null) {
            getSprite().setSize(TAMANO_SHULKER, TAMANO_SHULKER);
            getSprite().setPosition(x, y);
        }
    }

    public static Shulker inicializarInstancia(float x, float y, Texture texJugador, Texture texBala,
                                               Sound sDisparo, Sound sDisparoJ, Sound sHurt, List<Proyectil> proyectiles) {
        if(instancia == null) {
            instancia = new Shulker(x, y, texJugador, texBala, sDisparo, sDisparoJ, sHurt, proyectiles);
        }
        else {
            instancia.reiniciarEstado(x, y, texJugador, texBala, sDisparo, sDisparoJ, sHurt, proyectiles);
        }
        return instancia;
    }

    public static Shulker getInstancia() {
        return instancia;
    }

    public void reiniciarEstado(float x, float y, Texture texJugador, Texture texBala,
                                Sound sDisparo, Sound sDisparoJ, Sound sHurt, List<Proyectil> proyectiles) {
        this.texturaBala = texBala;
        this.sonidoDisparo = sDisparo;
        this.sonidoDisparoJ = sDisparoJ;
        this.sonidoHurt = sHurt;
        this.proyectilesJuego = proyectiles;
        this.tiempoDesdeUltimoDisparo = 0f;
        this.tiempoPowerUpRestante = 0f;
        this.estrategiaDisparo = new DisparoSimpleStrategy();

        if(texJugador != null) {
            Sprite nuevoSprite = new Sprite(texJugador);
            nuevoSprite.setSize(TAMANO_SHULKER, TAMANO_SHULKER);
            nuevoSprite.setPosition(x, y);
            setSprite(nuevoSprite);
        }
        setX(x);
        setY(y);
        setActiva(true);
    }

    public void aplicarPowerUp(EstrategiaDisparo nuevaEstrategia, float duracionSegundos) {
        this.estrategiaDisparo = nuevaEstrategia;
        this.tiempoPowerUpRestante = duracionSegundos;
    }

    @Override
    public void actualizar(float delta) {
        if(!estaActiva()) return;

        this.tiempoDesdeUltimoDisparo += delta;

        if(this.tiempoPowerUpRestante > 0) {
            this.tiempoPowerUpRestante -= delta;
            if(this.tiempoPowerUpRestante <= 0) {
                this.estrategiaDisparo = new DisparoSimpleStrategy();
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            setX(getX() - getVelocidadX() * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            setX(getX() + getVelocidadX() * delta);
        }

        float ancho = getSprite() != null ? getSprite().getWidth() : TAMANO_SHULKER;
        
        if(getX() < 0) setX(0);
        if(getX() + ancho > GameConfig.ANCHO_MUNDO) {
            setX(GameConfig.ANCHO_MUNDO - ancho);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && 
            this.tiempoDesdeUltimoDisparo >= this.estrategiaDisparo.getTiempoEnfriamiento() &&
            this.proyectilesJuego.size() < GameConfig.MAX_BALAS) {

            this.estrategiaDisparo.ejecutarDisparo(this, this.proyectilesJuego, this.texturaBala);

            if(MathUtils.random() < GameConfig.PROB_DISPARO_J) {
                if(this.sonidoDisparoJ != null) this.sonidoDisparoJ.play();
            }
            else {
                if(this.sonidoDisparo != null) this.sonidoDisparo.play();
            }

            this.tiempoDesdeUltimoDisparo = 0f;
        }

        sincronizarSprite();
    }

    @Override
    public void alColisionar(Colisionable otro) {
        if(otro instanceof Proyectil) {
            Proyectil p = (Proyectil) otro;
            if(!p.isEsDelJugador()) {
                recibirDano(p.getDano());
                if(this.sonidoHurt != null) this.sonidoHurt.play();
            }
        }
        else if(otro instanceof Enemigo) {
            recibirDano(1);
            if(this.sonidoHurt != null) this.sonidoHurt.play();
        }
    }

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
    
    public Sound getSonidoDisparoJ() { return sonidoDisparoJ; }
    public void setSonidoDisparoJ(Sound sonidoDisparoJ) { this.sonidoDisparoJ = sonidoDisparoJ; }
    
    public Sound getSonidoHurt() { return sonidoHurt; }
    public void setSonidoHurt(Sound sonidoHurt) { this.sonidoHurt = sonidoHurt; }
    
    public List<Proyectil> getProyectilesJuego() { return proyectilesJuego; }
    public void setProyectilesJuego(List<Proyectil> proyectiles) { this.proyectilesJuego = proyectiles; }
}