package com.pdjteam.galagraft.engine.waves;

import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.config.GameConfig;
import com.pdjteam.galagraft.interfaces.WaveBuilder;

public class StandardWaveBuilder implements WaveBuilder {

    private Wave oleada;

    public StandardWaveBuilder() {
        this.reset();
    }

    @Override
    public void reset() {
        this.oleada = new Wave();
    }

    @Override
    public void buildParametrosRonda(int numeroRonda, int cantEnemigos, int maxEnPantalla) {
        oleada.setNumeroRonda(numeroRonda);
        oleada.setCantEnemigos(cantEnemigos);
        oleada.setMaxEnPantalla(maxEnPantalla);
    }

    @Override
    public void buildVelocidades(int velX, int velY) {
        oleada.setVelocidades(velX, velY);
    }

    @Override
    public void buildCadenciaAparicion(int numeroRonda) {
        float reduccion = Math.max(0, numeroRonda - 1) * 150f;
        float intervalo = Math.max(1200f, GameConfig.TIEMPO_ENTRE_LINEAS_MS - reduccion);
        oleada.setIntervaloSpawnMs(intervalo);
    }

    @Override
    public void buildComposicionEnemigos(int numeroRonda) {
        if (numeroRonda == 1) {
            oleada.setTiposPermitidos(new int[]{
                    GameConfig.TIPO_ESQUELETO,
                    GameConfig.TIPO_ESQUELETO,
                    GameConfig.TIPO_BLAZE
            });
        } else if (numeroRonda == 2) {
            oleada.setTiposPermitidos(new int[]{
                    GameConfig.TIPO_ESQUELETO,
                    GameConfig.TIPO_BLAZE,
                    GameConfig.TIPO_GHAST
            });
        } else {
            oleada.setTiposPermitidos(new int[]{
                    GameConfig.TIPO_ESQUELETO,
                    GameConfig.TIPO_BLAZE,
                    GameConfig.TIPO_BLAZE,
                    GameConfig.TIPO_GHAST,
                    GameConfig.TIPO_GHAST
            });
        }
    }

    @Override
    public void buildRecursos(Texture[] texEnemigos, Texture texFlecha, Texture texCargaFuego) {
        oleada.setRecursos(texEnemigos, texFlecha, texCargaFuego);
    }

    @Override
    public Wave getResultado() {
        Wave producto = this.oleada;
        this.reset();
        return producto;
    }
}