package com.pdjteam.galagraft.engine.entities;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.pdjteam.galagraft.interfaces.Colisionable;

public abstract class Enemigo extends EntidadDinamica {
    private float tiempoDesdeUltimoDisparo;
    private float intervaloDisparo;
    private int puntajeOtorgado;
    private Texture texturaProyectil;
    private final List<Proyectil> proyectilesRecienDisparados = new ArrayList<>();
    private int fila;

    public Enemigo(float x, float y, float vx, float vy, int vidaMaxima, float intervaloDisparo, 
                   int puntaje, Texture texturaEnemigo, Texture texturaProyectil) {
        super(x, y, vx, vy, vidaMaxima, texturaEnemigo);
        this.tiempoDesdeUltimoDisparo = MathUtils.random(0f, intervaloDisparo);
        this.intervaloDisparo = intervaloDisparo;
        this.puntajeOtorgado = puntaje;
        this.texturaProyectil = texturaProyectil;
    }

    /**
     * TEMPLATE METHOD (GM 8): Plantilla invariante del ciclo de vida de un enemigo en cada frame
     */
    @Override
    public final void actualizar(float delta) {
        if(!estaActiva()) return;

        actualizarTemporizadores(delta);
        ejecutarMovimiento(delta);

        if(puedeAtacar()) {
            Proyectil nuevoDisparo = crearProyectilAtaque();
            if(nuevoDisparo != null) {
                this.proyectilesRecienDisparados.add(nuevoDisparo);
            }
            reiniciarTemporizadorDisparo();
        }

        sincronizarSprite();
    }

    protected void actualizarTemporizadores(float delta) {
        this.tiempoDesdeUltimoDisparo += delta;
    }

    protected boolean puedeAtacar() {
        return this.tiempoDesdeUltimoDisparo >= this.intervaloDisparo;
    }

    protected void reiniciarTemporizadorDisparo() {
        this.tiempoDesdeUltimoDisparo = 0f;
    }

    protected void ejecutarMovimiento(float delta) { }

    protected abstract Proyectil crearProyectilAtaque();

    @Override
    public void alColisionar(Colisionable otro) {
        if (!estaActiva() || otro == null || !otro.estaActiva()) return;
        otro.recibirColisionDeEnemigo(this);
    }

    @Override
    public void recibirColisionDeProyectil(Proyectil proyectil) {
        if (proyectil.isEsDelJugador()) {
            this.recibirDano(1);
            proyectil.setActiva(false);
        }
    }

    @Override
    public void recibirColisionDeJugador(Shulker jugador) {
        jugador.recibirColisionDeEnemigo(this);
    }

    public List<Proyectil> recogerProyectilesDisparados() {
        List<Proyectil> copia = new ArrayList<>(this.proyectilesRecienDisparados);
        this.proyectilesRecienDisparados.clear();
        return copia;
    }

    public List<Proyectil> getListaProyectilesJuego() {
        return new ArrayList<>(this.proyectilesRecienDisparados);
    }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public void mover(float dx, float dy) {
        setX(getX() + dx);
        setY(getY() + dy);
    }

    public void mover(int dx, int dy) {
        setX(getX() + dx);
        setY(getY() + dy);
    }

    public float getTiempoDesdeUltimoDisparo() { return tiempoDesdeUltimoDisparo; }
    public void setTiempoDesdeUltimoDisparo(float t) { this.tiempoDesdeUltimoDisparo = t; }
    public float getIntervaloDisparo() { return intervaloDisparo; }
    public void setIntervaloDisparo(float intervalo) { this.intervaloDisparo = intervalo; }
    public int getPuntajeOtorgado() { return puntajeOtorgado; }
    public void setPuntajeOtorgado(int puntaje) { this.puntajeOtorgado = puntaje; }
    public Texture getTexturaProyectil() { return texturaProyectil; }
    public void setTexturaProyectil(Texture texturaProyectil) { this.texturaProyectil = texturaProyectil; }
}