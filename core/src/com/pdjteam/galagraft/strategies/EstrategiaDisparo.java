package com.pdjteam.galagraft.strategies;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.engine.entities.Proyectil;

public interface EstrategiaDisparo {
    List<Proyectil> ejecutarDisparo(Texture texturaBala);
    float getTiempoEnfriamiento();
    String getNombreEstrategia();
}