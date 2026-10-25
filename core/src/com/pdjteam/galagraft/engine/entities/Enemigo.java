package com.pdjteam.galagraft.engine.entities;

import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.interfaces.Colisionable;

public abstract class Enemigo extends EntidadDinamica {
    private float tiempoDesdeUltimoDisparo;
    private float intervaloDisparo;
    private int puntajeOtorgado;
    private Texture texturaProyectil;
    private List<Proyectil> listaProyectilesJuego;

    public Enemigo(float x, float y, float vx, float vy, int vidaMaxima, float intervaloDisparo, 
                   int puntaje, Texture texturaEnemigo, Texture texturaProyectil, List<Proyectil> proyectiles) {
        super(x, y, vx, vy, vidaMaxima, texturaEnemigo);
        this.tiempoDesdeUltimoDisparo = 0f;
        this.intervaloDisparo = intervaloDisparo;
        this.puntajeOtorgado = puntaje;
        this.texturaProyectil = texturaProyectil;
        this.listaProyectilesJuego = proyectiles;
    }

    /**
     * TEMPLATE METHOD (GM 8): Plantilla invariante del ciclo de vida de un enemigo en cada frame.
     * Es 'final' para evitar que las subclases modifiquen el orden de ejecución.
     */
    @Override
    public final void actualizar(float delta) {
        if (!estaActiva()) return;

        // actualizar cooldowns
        actualizarTemporizadores(delta);

        // cada mob se mueve distinto
        ejecutarMovimiento(delta);

        // rebotar en bordes laterales
        verificarLimitesPantalla();

        // gancho + operación abstracta de ataque
        if (puedeAtacar()) {
            Proyectil nuevoDisparo = crearProyectilAtaque();
            if (nuevoDisparo != null && this.listaProyectilesJuego != null) {
                this.listaProyectilesJuego.add(nuevoDisparo);
            }
            reiniciarTemporizadorDisparo();
        }

        sincronizarSprite();
    }

    protected void actualizarTemporizadores(float delta) {
        this.tiempoDesdeUltimoDisparo += delta;
    }

    protected void verificarLimitesPantalla() {
        float anchoSprite = getSprite() != null ? getSprite().getWidth() : 32f;
        if (getX() <= 0) {
            setX(0);
            setVelocidadX(Math.abs(getVelocidadX()));
        } else if (getX() + anchoSprite >= Gdx.graphics.getWidth()) {
            setX(Gdx.graphics.getWidth() - anchoSprite);
            setVelocidadX(-Math.abs(getVelocidadX()));
        }

        if (getY() < -anchoSprite) {
            setActiva(false);
        }
    }

    protected boolean puedeAtacar() {
        return this.tiempoDesdeUltimoDisparo >= this.intervaloDisparo;
    }

    protected void reiniciarTemporizadorDisparo() {
        this.tiempoDesdeUltimoDisparo = 0f;
    }

    // Operaciones  abstractas que deben implementar Esqueleto, Blaze y Ghast
    protected abstract void ejecutarMovimiento(float delta);
    protected abstract Proyectil crearProyectilAtaque();

    @Override
    public void alColisionar(Colisionable otro) {
        if (otro instanceof Proyectil) {
            Proyectil p = (Proyectil) otro;
            if (p.isEsDelJugador()) {
                recibirDano(p.getDano());
            }
        }
    }
    
    private int fila;

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public void mover(float dx, float dy) {
        setX(getX() + dx);
        setY(getY() + dy);
    }

    public void mover(int dx, int dy) {
        setX(getX() + dx);
        setY(getY() + dy);
    }

    // Getters y Setters
    public float getTiempoDesdeUltimoDisparo() { return tiempoDesdeUltimoDisparo; }
    public void setTiempoDesdeUltimoDisparo(float t) { this.tiempoDesdeUltimoDisparo = t; }
    public float getIntervaloDisparo() { return intervaloDisparo; }
    public void setIntervaloDisparo(float intervalo) { this.intervaloDisparo = intervalo; }
    public int getPuntajeOtorgado() { return puntajeOtorgado; }
    public void setPuntajeOtorgado(int puntaje) { this.puntajeOtorgado = puntaje; }
    public Texture getTexturaProyectil() { return texturaProyectil; }
    public void setTexturaProyectil(Texture texturaProyectil) { this.texturaProyectil = texturaProyectil; }
    public List<Proyectil> getListaProyectilesJuego() { return listaProyectilesJuego; }
    public void setListaProyectilesJuego(List<Proyectil> lista) { this.listaProyectilesJuego = lista; }
}