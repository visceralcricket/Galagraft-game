package com.pdjteam.galagraft.engine.waves;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.interfaces.WaveBuilder;

// Define el orden de construcción utilizando la interfaz Builder
public class WaveDirector {
    private WaveBuilder builder;

    public WaveDirector(WaveBuilder builder) { this.builder = builder; }

    public void setBuilder(WaveBuilder builder) { this.builder = builder; }

    public Wave construirOleada(int ronda, int cantEnemigos, int maxEnPantalla, int velX, int velY,
            Texture[] texEnemigos, Texture texFlecha, Texture texCargaFuego, List<Proyectil> balas) {
    	
        builder.reset();
        builder.buildParametrosBase(ronda, cantEnemigos, maxEnPantalla);
        builder.buildComposicionEnemigos(ronda);
        builder.buildVelocidades(velX, velY);
        builder.buildRecursos(texEnemigos, texFlecha, texCargaFuego, balas);
        
        return builder.getResult();
    }
}