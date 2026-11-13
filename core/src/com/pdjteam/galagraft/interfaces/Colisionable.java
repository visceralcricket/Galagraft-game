package com.pdjteam.galagraft.interfaces;

import com.badlogic.gdx.math.Rectangle;
import com.pdjteam.galagraft.engine.entities.Enemigo;
import com.pdjteam.galagraft.engine.entities.Potenciador;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.engine.entities.Shulker;

public interface Colisionable {
    Rectangle getAreaColision();
    boolean verificarColision(Colisionable otro);
    void alColisionar(Colisionable otro);
    boolean estaActiva();

    // Double-dispatch con implementación por defecto vacía (no-op)
    default void recibirColisionDeJugador(Shulker jugador) {}
    default void recibirColisionDeEnemigo(Enemigo enemigo) {}
    default void recibirColisionDeProyectil(Proyectil proyectil) {}
    default void recibirColisionDePotenciador(Potenciador potenciador) {}
}