package com.pdjteam.galagraft.engine.waves;

import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.interfaces.WaveBuilder;

//Define el orden de construcción utilizando la interfaz Builder
public class WaveDirector {

    private WaveBuilder builder;

    public WaveDirector(WaveBuilder builder) { this.builder = builder; }

    public void setBuilder(WaveBuilder builder) { this.builder = builder; }

    public Wave construirOleada(
            int numeroRonda,
            int cantEnemigos,
            int maxEnPantalla,
            int velX,
            int velY,
            Texture[] texEnemigos,
            Texture texFlecha,
            Texture texCargaFuego) {

        builder.reset();
        builder.buildParametrosRonda(numeroRonda, cantEnemigos, maxEnPantalla);
        builder.buildVelocidades(velX, velY);
        builder.buildCadenciaAparicion(numeroRonda);
        builder.buildComposicionEnemigos(numeroRonda);
        builder.buildRecursos(texEnemigos, texFlecha, texCargaFuego);
        return builder.getResultado();
    }
}