package com.pdjteam.galagraft.strategies;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;

import com.pdjteam.galagraft.engine.entities.Proyectil;

import com.pdjteam.galagraft.engine.entities.Shulker;

public class DisparoSimpleStrategy implements EstrategiaDisparo {
    private float enfriamiento = 0.35f;

    @Override
    public void ejecutarDisparo(Shulker jugador, List<Proyectil> proyectiles, Texture texturaBala) {
        float xCentral = jugador.getX() + (jugador.getSprite().getWidth() / 2f) - 4f;
        float ySalida = jugador.getY() + jugador.getSprite().getHeight();
        proyectiles.add(new Proyectil(xCentral, ySalida, 0f, 450f, 1, true, texturaBala));
    }

    @Override
    public float getTiempoEnfriamiento() { return enfriamiento; }
    public void setTiempoEnfriamiento(float enfriamiento) { this.enfriamiento = enfriamiento; }

    @Override
    public String getNombreEstrategia() { return "Normal"; }
}