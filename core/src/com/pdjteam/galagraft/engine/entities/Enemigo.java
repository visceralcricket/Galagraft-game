package com.pdjteam.galagraft.engine.entities;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

// interfaz
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
        // desfase al azar: así los enemigos de una misma fila no disparan todos en el mismo instante
        this.tiempoDesdeUltimoDisparo = MathUtils.random(0f, intervaloDisparo);
        this.intervaloDisparo = intervaloDisparo;
        this.puntajeOtorgado = puntaje;
        this.texturaProyectil = texturaProyectil;
        this.listaProyectilesJuego = proyectiles;
    }

    /**
     * TEMPLATE METHOD (GM 8): Plantilla invariante del ciclo de vida de un enemigo en cada frame.
     * Es 'final' para evitar que las subclases modifiquen el orden de ejecución.
     * GameLogic la invoca en cada fotograma para cada enemigo. El desplazamiento en formación y
     * los límites de la pantalla los controla GameLogic, así que aquí solo vive el ciclo propio del enemigo.
     */
    @Override
    public final void actualizar(float delta) {
        if(!estaActiva()) return;

        // actualizar cooldowns
        actualizarTemporizadores(delta);

        // gancho: movimiento propio (por defecto ninguno, la formación mueve al enemigo)
        ejecutarMovimiento(delta);

        // gancho + operación abstracta de ataque
        if(puedeAtacar()) {
            Proyectil nuevoDisparo = crearProyectilAtaque();
            if(nuevoDisparo != null && this.listaProyectilesJuego != null) {
                this.listaProyectilesJuego.add(nuevoDisparo);
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

    // Gancho: movimiento propio de cada enemigo. Por defecto no hace nada porque la formación de GameLogic
    // es quien lo desplaza; un enemigo que necesite moverse distinto puede sobrescribirlo.
    protected void ejecutarMovimiento(float delta) { }

    // Operación abstracta que deben implementar Esqueleto, Blaze y cualquier enemigo nuevo
    protected abstract Proyectil crearProyectilAtaque();

    @Override
    public void alColisionar(Colisionable otro) {
        if(otro instanceof Proyectil) {
            Proyectil p = (Proyectil) otro;
            if(p.isEsDelJugador()) {
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