package com.pdjteam.galagraft.engine.entities;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;

public class EnemigoEsqueleto extends Enemigo {

    public EnemigoEsqueleto(float x, float y, Texture texEsqueleto, Texture texFlecha, List<Proyectil> proyectiles) {
        super(x, y, 120f, -15f, 2, 2.0f, 100, texEsqueleto, texFlecha, proyectiles);
    }

    @Override
    protected void ejecutarMovimiento(float delta) {
        setX(getX() + getVelocidadX() * delta);
        setY(getY() + getVelocidadY() * delta);
    }

    @Override
    protected Proyectil crearProyectilAtaque() {
        // dispara una flecha hacia abajo (recto)
        float centroX = getX() + (getSprite().getWidth() / 2f) - 4f;
        return new Proyectil(centroX, getY(), 0f, -280f, 1, false, getTexturaProyectil());
    }
}