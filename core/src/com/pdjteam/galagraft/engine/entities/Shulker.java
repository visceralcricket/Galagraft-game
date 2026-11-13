package com.pdjteam.galagraft.engine.entities;

import java.util.ArrayList;
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
import com.pdjteam.galagraft.strategies.EstrategiaPotenciador;

// Interfaz
import com.pdjteam.galagraft.interfaces.Colisionable;

public class Shulker extends EntidadDinamica {
    // GM-6 Singleton
    private static Shulker instancia;
    private static final float TAMANO_SHULKER = 64f;

    // atributos encapsulados: GM-5
    private int puntaje;
    private float tiempoDesdeUltimoDisparo;
    private final List<EfectoActivo> efectosActivos = new ArrayList<>(); // potenciadores en curso (Strategy GM-7)
    private EstrategiaDisparo estrategiaDisparo; // referencia a Strategy (GM-7)
    private Texture texturaBala;
    private Sound sonidoDisparo;
    private Sound sonidoDisparoJ;
    private Sound sonidoHurt;
    private final List<Proyectil> proyectilesRecienDisparados = new ArrayList<>();
    private int balasActivasEnMundo = 0;

    // Un potenciador temporal en curso: su estrategia y los segundos que le quedan
    private static class EfectoActivo {
        private final EstrategiaPotenciador estrategia;
        private float restante;

        EfectoActivo(EstrategiaPotenciador estrategia, float restante) {
            this.estrategia = estrategia;
            this.restante = restante;
        }
    }

    private Shulker(float x, float y, Texture texturaJugador, Texture texturaBala, 
                    Sound sonidoDisparo, Sound sonidoDisparoJ, Sound sonidoHurt) {
        super(x, y, 500f, 0f, GameConfig.VIDAS_INICIALES, texturaJugador);
        this.puntaje = 0;
        this.tiempoDesdeUltimoDisparo = 0f;
        this.estrategiaDisparo = new DisparoSimpleStrategy();
        this.texturaBala = texturaBala;
        this.sonidoDisparo = sonidoDisparo;
        this.sonidoDisparoJ = sonidoDisparoJ;
        this.sonidoHurt = sonidoHurt;

        if(getSprite() != null) {
            getSprite().setSize(TAMANO_SHULKER, TAMANO_SHULKER);
            getSprite().setPosition(x, y);
        }
    }

    public static Shulker inicializarInstancia(float x, float y, Texture texJugador, Texture texBala,
                                               Sound sDisparo, Sound sDisparoJ, Sound sHurt) {
        if(instancia == null) {
            instancia = new Shulker(x, y, texJugador, texBala, sDisparo, sDisparoJ, sHurt);
        }
        else {
            instancia.reiniciarEstado(x, y, texJugador, texBala, sDisparo, sDisparoJ, sHurt);
        }
        return instancia;
    }

    public static Shulker getInstancia() {
        return instancia;
    }

    public void reiniciarEstado(float x, float y, Texture texJugador, Texture texBala,
                                Sound sDisparo, Sound sDisparoJ, Sound sHurt) {
        this.texturaBala = texBala;
        this.sonidoDisparo = sDisparo;
        this.sonidoDisparoJ = sDisparoJ;
        this.sonidoHurt = sHurt;
        this.tiempoDesdeUltimoDisparo = 0f;
        // los potenciadores no pasan de una ronda a otra: se revierten sus efectos
        revertirEfectosActivos();
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

    /**
     * Activa un potenciador (Strategy GM-7): aplica su efecto y, si es temporal, lo revierte al expirar.
     * Solo hay un efecto activo por categoría: el nuevo reemplaza al anterior.
     */
    public void activarPotenciador(EstrategiaPotenciador nuevo) {
        if(nuevo == null) return;

        for(int i = efectosActivos.size() - 1; i >= 0; i--) {
            EfectoActivo activo = efectosActivos.get(i);
            if(activo.estrategia.getCategoria().equals(nuevo.getCategoria())) {
                activo.estrategia.revertir();
                efectosActivos.remove(i);
            }
        }

        nuevo.aplicar();

        if(nuevo.getDuracion() > 0) {
            efectosActivos.add(new EfectoActivo(nuevo, nuevo.getDuracion()));
        }
    }

    private void actualizarEfectosActivos(float delta) {
        for(int i = efectosActivos.size() - 1; i >= 0; i--) {
            EfectoActivo activo = efectosActivos.get(i);
            activo.restante -= delta;
            if(activo.restante <= 0) {
                activo.estrategia.revertir();
                efectosActivos.remove(i);
            }
        }
    }

    private void revertirEfectosActivos() {
        for(EfectoActivo activo : efectosActivos) {
            activo.estrategia.revertir();
        }
        efectosActivos.clear();
    }

    /**
     * Regla de munición: MAX_BALAS cuenta solo las balas del jugador (las enemigas no)
     * y se ignora mientras hay un potenciador de disparo activo.
     */
    public boolean hayCupoDeBalas() {
        for (EfectoActivo activo : efectosActivos) {
            if (EstrategiaPotenciador.CATEGORIA_DISPARO.equals(activo.estrategia.getCategoria())) {
                return true;
            }
        }
        return this.balasActivasEnMundo < GameConfig.MAX_BALAS;
    }

    public void setBalasActivasEnMundo(int cantidad) {
        this.balasActivasEnMundo = Math.max(0, cantidad);
    }

    @Override
    public void actualizar(float delta) {
        if(!estaActiva()) return;

        this.tiempoDesdeUltimoDisparo += delta;

        actualizarEfectosActivos(delta);

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
            hayCupoDeBalas()) {
        	
        	List<Proyectil> generados = this.estrategiaDisparo.ejecutarDisparo(this.texturaBala);
        	this.proyectilesRecienDisparados.addAll(generados);

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
        if (!estaActiva() || otro == null || !otro.estaActiva()) return;
        otro.recibirColisionDeJugador(this);
    }

    @Override
    public void recibirColisionDeEnemigo(Enemigo enemigo) {
        this.recibirDano(1);
        if (this.sonidoHurt != null) {
            this.sonidoHurt.play();
        }
        enemigo.setActiva(false);
    }

    @Override
    public void recibirColisionDeProyectil(Proyectil proyectil) {
        if (!proyectil.isEsDelJugador()) {
            this.recibirDano(1);
            if (this.sonidoHurt != null) {
                this.sonidoHurt.play();
            }
            proyectil.setActiva(false);
        }
    }

    @Override
    public void recibirColisionDePotenciador(Potenciador potenciador) {
        if (potenciador.estaActiva()) {
            potenciador.recoger();
        }
    }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
    public void agregarPuntaje(int puntos) { this.puntaje += puntos; }
    
    public float getTiempoDesdeUltimoDisparo() { return tiempoDesdeUltimoDisparo; }
    public void setTiempoDesdeUltimoDisparo(float t) { this.tiempoDesdeUltimoDisparo = t; }
    
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
    
    public List<Proyectil> recogerProyectilesDisparados() {
        List<Proyectil> copia = new ArrayList<>(this.proyectilesRecienDisparados);
        this.proyectilesRecienDisparados.clear();
        return copia;
    }

    public List<Proyectil> getProyectilesJuego() {
        return new ArrayList<>(this.proyectilesRecienDisparados);
    }
}