package com.pdjteam.galagraft.strategies;

/**
 * STRATEGY (GM-7) de los potenciadores: cada potenciador del juego es una implementación de esta interfaz.
 * Para agregar uno nuevo basta con crear una clase que la implemente y registrarla en CatalogoPotenciadores.
 */
public interface EstrategiaPotenciador {

    String CATEGORIA_DISPARO = "disparo";
    
    void aplicar();
    void revertir();
    float getDuracion();
    String getRutaTextura();
    String getCategoria();
}
