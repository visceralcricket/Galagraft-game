package com.pdjteam.galagraft.interfaces;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.engine.waves.Wave;

//interfaz Builder
public interface WaveBuilder {
    public void reset();
    public void buildParametrosBase(int ronda, int cantEnemigos, int maxEnPantalla);
    public void buildComposicionEnemigos(int ronda);
    public void buildVelocidades(int velX, int velY);
    public void buildRecursos(Texture[] texEnemigos, Texture texFlecha, Texture texCargaFuego, List<Proyectil> balas);
    public Wave getResult();
}