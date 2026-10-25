package com.pdjteam.galagraft.engine.powerups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.pdjteam.galagraft.engine.entities.Potenciador;

// strategies
import com.pdjteam.galagraft.strategies.DisparoTriplePowerUpStrategy;
import com.pdjteam.galagraft.strategies.EstrategiaPotenciador;

/**
 * Catálogo de los potenciadores disponibles: cada tipo es una EstrategiaPotenciador.
 * Carga el sprite de cada tipo (que la estrategia indica) y fabrica potenciadores al azar.
 */
public class CatalogoPotenciadores {
    private final List<Supplier<EstrategiaPotenciador>> tipos = new ArrayList<>();
    private final Map<String, Texture> texturas = new HashMap<>();
    private final Random azar = new Random();

    public CatalogoPotenciadores() {
        // PARA AGREGAR UN POTENCIADOR NUEVO: crear su estrategia y registrarla aquí (única línea a modificar)
        registrar(DisparoTriplePowerUpStrategy::new);
    }

    // Cada drop crea una estrategia nueva, para que ningún potenciador comparta estado con otro
    public void registrar(Supplier<EstrategiaPotenciador> tipo) {
        String ruta = tipo.get().getRutaTextura();
        if(!texturas.containsKey(ruta)) {
            texturas.put(ruta, new Texture(Gdx.files.internal(ruta)));
        }
        tipos.add(tipo);
    }

    public Potenciador crearAleatorio(float x, float y) {
        EstrategiaPotenciador estrategia = tipos.get(azar.nextInt(tipos.size())).get();
        return new Potenciador(x, y, estrategia, texturas.get(estrategia.getRutaTextura()), null);
    }

    public void dispose() {
        for(Texture t : texturas.values()) {
            t.dispose();
        }
        texturas.clear();
    }
}
