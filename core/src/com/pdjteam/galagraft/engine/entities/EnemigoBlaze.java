package com.pdjteam.galagraft.engine.entities;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import com.pdjteam.galagraft.config.GameConfig;

public class EnemigoBlaze extends Enemigo {

    public EnemigoBlaze(float x, float y, Texture texBlaze, Texture texCargaFuego, List<Proyectil> proyectiles) {
        super(x, y, 160f, -25f, 3, GameConfig.INTERVALO_DISPARO_BLAZE, 250, texBlaze, texCargaFuego, proyectiles);
    }

    @Override
    protected Proyectil crearProyectilAtaque() {
        float centroX = getX() + (getSprite().getWidth() / 2f);
        float direccionX = 0f;
        if(Shulker.getInstancia() != null) {
            float diffX = Shulker.getInstancia().getX() - centroX;
            direccionX = MathUtils.clamp(diffX, -120f, 120f);
        }
        // carga de fuego que sale justo bajo el blaze, apuntada hacia el jugador
        float lado = GameConfig.CARGA_FUEGO_LADO;
        return new Proyectil(centroX - lado / 2f, getY() - lado, direccionX, -320f, 2, false, getTexturaProyectil(), lado, lado);
    }
}