package com.pdjteam.galagraft.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.config.GameConfig;

public class EnemigoEsqueleto extends Enemigo {

	public EnemigoEsqueleto(float x, float y, Texture texEsqueleto, Texture texFlecha) {
        super(x, y, 120f, -15f, 1, GameConfig.INTERVALO_DISPARO_ESQUELETO, 100, texEsqueleto, texFlecha);
    }

    @Override
    protected Proyectil crearProyectilAtaque() {
        // dispara una flecha hacia abajo en linea recta, que sale justo bajo el esqueleto
        float centroX = getX() + (getSprite().getWidth() / 2f);
        return new Proyectil(centroX - GameConfig.FLECHA_ANCHO / 2f, getY() - GameConfig.FLECHA_ALTO,
                0f, -280f, 1, false, getTexturaProyectil(), GameConfig.FLECHA_ANCHO, GameConfig.FLECHA_ALTO);
    }
}