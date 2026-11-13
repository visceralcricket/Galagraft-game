package com.pdjteam.galagraft.engine.entities;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;

import com.pdjteam.galagraft.config.GameConfig;

public class EnemigoGhast extends Enemigo {

	public EnemigoGhast(float x, float y, Texture texGhast, Texture texCargaFuego) {
        super(x, y, 100f, -10f, 3, GameConfig.INTERVALO_DISPARO_GHAST, 400, texGhast, texCargaFuego);
    }

    @Override
    protected Proyectil crearProyectilAtaque() {
        // carga de fuego grande y lenta, que cae en linea recta justo bajo el ghast
        float lado = GameConfig.CARGA_FUEGO_GHAST_LADO;
        float centroX = getX() + (getSprite().getWidth() / 2f);
        return new Proyectil(centroX - lado / 2f, getY() - lado, 0f, -260f, 2, false, getTexturaProyectil(), lado, lado);
    }
}
