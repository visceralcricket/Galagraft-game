package com.pdjteam.galagraft.strategies;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.engine.entities.Shulker;

public interface EstrategiaDisparo {
    void ejecutarDisparo(Shulker jugador, List<Proyectil> proyectiles, Texture texturaBala);
    float getTiempoEnfriamiento();
    String getNombreEstrategia();
}