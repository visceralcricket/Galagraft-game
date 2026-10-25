package com.pdjteam.galagraft.strategies;

import com.pdjteam.galagraft.engine.entities.Shulker;

/**
 * STRATEGY (GM-7) de los potenciadores: cada potenciador del juego es una implementación de esta interfaz.
 * Para agregar uno nuevo basta con crear una clase que la implemente y registrarla en CatalogoPotenciadores.
 */
public interface EstrategiaPotenciador {

    // categoría compartida por los potenciadores que cambian el disparo (solo uno activo a la vez)
    String CATEGORIA_DISPARO = "disparo";

    // qué le hace al Shulker cuando lo recoge
    void aplicar(Shulker jugador);

    // deshace el efecto al expirar (o al reiniciar el estado del jugador)
    void revertir(Shulker jugador);

    // segundos que dura el efecto; 0 = efecto instantáneo (no se revierte)
    float getDuracion();

    // archivo del sprite con que se dibuja el potenciador mientras cae
    String getRutaTextura();

    // solo hay un efecto activo por categoría: al recoger otro de la misma categoría, reemplaza al anterior
    default String getCategoria() { return getClass().getName(); }
}
