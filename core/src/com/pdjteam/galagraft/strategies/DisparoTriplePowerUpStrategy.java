package com.pdjteam.galagraft.strategies;

import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.engine.entities.Shulker;
import com.pdjteam.galagraft.config.GameConfig;

public class DisparoTriplePowerUpStrategy implements EstrategiaDisparo, EstrategiaPotenciador {
    private float enfriamiento = 0.22f; // Power-up: mayor cadencia + triple proyectil en abanico

    @Override
    public List<Proyectil> ejecutarDisparo(Texture texturaBala) {
        List<Proyectil> nuevos = new ArrayList<>();
        Shulker jugador = Shulker.getInstancia();
        float xCentral = jugador.getX() + (jugador.getSprite().getWidth() / 2f) - 4f;
        float ySalida = jugador.getY() + jugador.getSprite().getHeight();

        nuevos.add(new Proyectil(xCentral, ySalida, 0f, 500f, 1, true, texturaBala));
        nuevos.add(new Proyectil(xCentral - 10f, ySalida, -110f, 480f, 1, true, texturaBala));
        nuevos.add(new Proyectil(xCentral + 10f, ySalida, 110f, 480f, 1, true, texturaBala));
        return nuevos;
    }

    @Override
    public float getTiempoEnfriamiento() { return enfriamiento; }
    public void setTiempoEnfriamiento(float enfriamiento) { this.enfriamiento = enfriamiento; }

    @Override
    public String getNombreEstrategia() { return "Power-Up Triple"; }

    @Override
    public void aplicar() { Shulker.getInstancia().setEstrategiaDisparo(this); }

    @Override
    public void revertir() { Shulker.getInstancia().setEstrategiaDisparo(new DisparoSimpleStrategy()); }

    @Override
    public float getDuracion() { return GameConfig.DURACION_DISPARO_TRIPLE; }

    @Override
    public String getRutaTextura() { return GameConfig.TEX_POTENCIADOR_TRIPLE; }

    @Override
    public String getCategoria() { return CATEGORIA_DISPARO; }
}