package com.pdjteam.galagraft.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import com.pdjteam.galagraft.engine.entities.Enemigo;
import com.pdjteam.galagraft.engine.entities.Potenciador;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.engine.entities.Shulker;
import com.pdjteam.galagraft.engine.powerups.CatalogoPotenciadores;
import com.pdjteam.galagraft.engine.waves.StandardWaveBuilder;
import com.pdjteam.galagraft.engine.waves.Wave;
import com.pdjteam.galagraft.engine.waves.WaveDirector;
import com.pdjteam.galagraft.interfaces.WaveBuilder;

public class GameLogic {

    private final Shulker jugador;
    private final Wave oleadaActual;
    private final List<Proyectil> balas = new ArrayList<>();
    private final List<Potenciador> potenciadores = new ArrayList<>();
    private final CatalogoPotenciadores catalogoPotenciadores;
    private final Random azar = new Random();

    private int puntuacion;

    public GameLogic(
            int ronda,
            int puntuacion,
            int velX,
            int velY,
            int cantEnemigos,
            int maxEnPantalla,
            Texture[] texEnemigos,
            Texture texFlecha,
            Texture texCargaFuego,
            CatalogoPotenciadores catalogoPotenciadores) {

        // GM-6: obtener instancia de jugador Shulker
        this.jugador = Shulker.getInstancia();
        this.puntuacion = puntuacion;
        this.catalogoPotenciadores = catalogoPotenciadores;

        // GM-9: construcción de la oleada mediante el patrón Builder
        WaveBuilder builder = new StandardWaveBuilder();
        WaveDirector director = new WaveDirector(builder);
        this.oleadaActual = director.construirOleada(
                ronda,
                cantEnemigos,
                maxEnPantalla,
                velX,
                velY,
                texEnemigos,
                texFlecha,
                texCargaFuego
        );
        this.oleadaActual.iniciarOleada();
    }

    private void soltarPotenciador(Enemigo muerto) {
        if(azar.nextFloat() >= GameConfig.PROB_DROP_POTENCIADOR) return;

        Rectangle area = muerto.getAreaColision();
        float lado = GameConfig.POTENCIADOR_TAMANO;
        float x = area.x + (area.width - lado) / 2f;
        float y = area.y + (area.height - lado) / 2f;

        potenciadores.add(catalogoPotenciadores.crearAleatorio(x, y));
    }

    private void actualizarPotenciadores(float delta) {
        for (int i = potenciadores.size() - 1; i >= 0; i--) {
            Potenciador pw = potenciadores.get(i);
            pw.actualizar(delta);
            if(pw.verificarColision(jugador)) {
                pw.alColisionar(jugador);
            }
            if(!pw.estaActiva()) {
                potenciadores.remove(i);
            }
        }
    }

    private void sincronizarCupoBalasJugador() {
        int activasJugador = 0;
        for (Proyectil p : balas) {
            if (p.estaActiva() && p.isEsDelJugador()) {
                activasJugador++;
            }
        }
        jugador.setBalasActivasEnMundo(activasJugador);
    }

    public int actualizar(float delta) {
        int destruidos = 0;
        if(!jugador.estaActiva()) {
            return 0;
        }

        // sincronizar conteo de balas para hayCupoDeBalas() y recolectar nuevos disparos del Shulker
        sincronizarCupoBalasJugador();
        balas.addAll(jugador.recogerProyectilesDisparados());
        
        oleadaActual.actualizar(delta);
        balas.addAll(oleadaActual.recogerProyectilesDisparados());

        List<Enemigo> enemigos = oleadaActual.getEnemigosActivos();

        // colisiones de proyectiles (GM-4 y GM-5: polimorfismo sin instanceof)
        for (int i = balas.size() - 1; i >= 0; i--) {
            Proyectil p = balas.get(i);
            p.actualizar(delta);
            if(!p.estaActiva()) {
                balas.remove(i);
                continue;
            }
            if(!p.isEsDelJugador()) {
                if(p.verificarColision(jugador)) {
                    p.alColisionar(jugador);
                    if (!p.estaActiva()) {
                        balas.remove(i);
                    }
                }
                continue;
            }
            for (int j = enemigos.size() - 1; j >= 0; j--) {
                Enemigo enemigo = enemigos.get(j);
                if(enemigo.estaActiva() && p.verificarColision(enemigo)) {
                    p.alColisionar(enemigo);
                    if(!enemigo.estaActiva()) {
                        oleadaActual.eliminarEnemigo(enemigo);
                        soltarPotenciador(enemigo);
                        puntuacion += GameConfig.PUNTOS_POR_ENEMIGO;
                        jugador.agregarPuntaje(GameConfig.PUNTOS_POR_ENEMIGO);
                        destruidos++;
                    }
                    if (!p.estaActiva()) {
                        balas.remove(i);
                    }
                    break;
                }
            }
        }

        List<Enemigo> enemigosRestantes = oleadaActual.getEnemigosActivos();
        for (int i = enemigosRestantes.size() - 1; i >= 0; i--) {
            Enemigo e = enemigosRestantes.get(i);
            if (e.estaActiva() && jugador.verificarColision(e)) {
                jugador.alColisionar(e);
                if (!e.estaActiva()) {
                    oleadaActual.eliminarEnemigo(e);
                }
            }
        }

        actualizarPotenciadores(delta);
        sincronizarCupoBalasJugador();
        jugador.actualizar(delta);
        balas.addAll(jugador.recogerProyectilesDisparados());
        
        return destruidos;
    }

    public GameLogic crearSiguienteRonda(
            Texture[] texEnemigos,
            Texture texFlecha,
            Texture texCargaFuego) {

        int nuevaRonda = getRonda() + 1;
        int nuevaVel = Math.min(getVelX() + GameConfig.VEL_POR_RONDA, GameConfig.VEL_MAX);
        int nuevosEnemigos = Math.round(getCantEnemigos() * (1 + GameConfig.CRECIMIENTO_ENEMIGOS));
        int nuevoMaxPantalla = Math.round(getMaxEnPantalla() * (1 + GameConfig.CRECIMIENTO_MAX_PANTALLA));

        return new GameLogic(
                nuevaRonda,
                this.puntuacion,
                nuevaVel,
                nuevaVel,
                nuevosEnemigos,
                nuevoMaxPantalla,
                texEnemigos,
                texFlecha,
                texCargaFuego,
                this.catalogoPotenciadores
        );
    }

    public boolean puedeDisparar() {
        return jugador.hayCupoDeBalas();
    }

    public boolean agregarBala(Proyectil p) {
        return puedeDisparar() && !balas.contains(p) && balas.add(p);
    }

    public boolean rondaCompletada() {
        return oleadaActual.estaCompletada() && jugador.estaActiva();
    }

    public boolean juegoTerminado() { return !jugador.estaActiva(); }
    public void eliminarEnemigo(int i) { oleadaActual.eliminarEnemigo(i); }
    public int getPuntuacion() { return puntuacion; }
    public int getRonda() { return oleadaActual.getNumeroRonda(); }
    public int getVelX() { return oleadaActual.getVelX(); }
    public int getVelY() { return oleadaActual.getVelY(); }
    public int getCantEnemigos() { return oleadaActual.getCantEnemigos(); }
    public int getMaxEnPantalla() { return oleadaActual.getMaxEnPantalla(); }
    public Shulker getJugador() { return jugador; }

    // GM-5: uso de interfaz List - retorno de copias de las colecciones originales
    public List<Enemigo> getEnemigos() { return oleadaActual.getEnemigosActivos(); }
    public List<Proyectil> getBalas() { return new ArrayList<>(balas); }
    public List<Potenciador> getPotenciadores() { return new ArrayList<>(potenciadores); }
}