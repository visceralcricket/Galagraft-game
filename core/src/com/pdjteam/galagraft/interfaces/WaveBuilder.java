package com.pdjteam.galagraft.interfaces;

import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.engine.waves.Wave;

//interfaz Builder
public interface WaveBuilder {
    void reset();
    void buildParametrosRonda(int numeroRonda, int cantEnemigos, int maxEnPantalla);
    void buildVelocidades(int velX, int velY);
    void buildCadenciaAparicion(int numeroRonda);
    void buildComposicionEnemigos(int numeroRonda);
    void buildRecursos(Texture[] texEnemigos, Texture texFlecha, Texture texCargaFuego);
    Wave getResultado();
}