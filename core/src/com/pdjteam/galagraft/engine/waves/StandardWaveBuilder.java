package com.pdjteam.galagraft.engine.waves;

import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.config.GameConfig;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.interfaces.WaveBuilder;

public class StandardWaveBuilder implements WaveBuilder {
    private Wave wave;

    public StandardWaveBuilder() { this.reset(); }
    @Override
    public void reset() { this.wave = new Wave(); }

    @Override
    public void buildParametrosBase(int ronda, int cantEnemigos, int maxEnPantalla) {
        this.wave.setNumeroRonda(ronda);
        this.wave.setCantEnemigos(cantEnemigos);
        this.wave.setMaxEnPantalla(maxEnPantalla);
        this.wave.setIntervaloSpawnMs(GameConfig.INTERVALO_LINEA_MS);
    }

    @Override
    public void buildComposicionEnemigos(int ronda) {
        if(ronda == 1) {
            // en la ronda 1 aparecen esqueletos y blazes -> a partir de la 2 se incluyen ghasts
            this.wave.setTiposPermitidos(new int[] {
                GameConfig.TIPO_ESQUELETO,
                GameConfig.TIPO_BLAZE
            });
        }
        else {
            this.wave.setTiposPermitidos(new int[] {
                GameConfig.TIPO_GHAST,
                GameConfig.TIPO_ESQUELETO,
                GameConfig.TIPO_BLAZE
            });
        }
    }

    @Override
    public void buildVelocidades(int velX, int velY) {
        int vx = Math.min(GameConfig.VEL_MAX, velX);
        int vy = Math.min(GameConfig.VEL_MAX, velY);
        this.wave.setVelocidades(vx, vy);
    }

    @Override
    public void buildRecursos(Texture[] texEnemigos, Texture texFlecha, Texture texCargaFuego, List<Proyectil> balas) {
        this.wave.setRecursos(texEnemigos, texFlecha, texCargaFuego, balas);
    }

    @Override
    public Wave getResult() {
        Wave resultado = this.wave;
        this.reset();
        return resultado;
    }
}