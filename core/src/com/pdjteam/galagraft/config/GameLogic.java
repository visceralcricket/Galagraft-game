package com.pdjteam.galagraft.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

// entidades
import com.pdjteam.galagraft.engine.entities.Enemigo;
import com.pdjteam.galagraft.engine.entities.EnemigoBlaze;
import com.pdjteam.galagraft.engine.entities.EnemigoEsqueleto;
import com.pdjteam.galagraft.engine.entities.Proyectil;
import com.pdjteam.galagraft.engine.entities.Shulker;

public class GameLogic {

    private final Shulker jugador;
    private final ArrayList<Enemigo> mobs1 = new ArrayList<>();
    private final List<Proyectil> balas;

    private final Texture[] texEnemigos;
    private final Random azar = new Random();

    private int puntuacion;
    private int ronda;
    private int velX;
    private int velY;
    private int cantEnemigos;
    private int maxEnPantalla;

    private int pendientes;
    private float msDesdeLinea = 0;

    private final int celda;
    private int siguienteFila = 0;

    private final Map<Integer, Integer> direccionFila =
            new HashMap<>();

    public GameLogic(
            Shulker jugador,
            int ronda,
            int puntuacion,
            int velX,
            int velY,
            int cantEnemigos,
            int maxEnPantalla,
            Texture[] texEnemigos) {

        this.jugador = jugador;
        this.ronda = ronda;
        this.puntuacion = puntuacion;
        this.velX = velX;
        this.velY = velY;
        this.cantEnemigos = cantEnemigos;
        this.maxEnPantalla = maxEnPantalla;
        this.texEnemigos = texEnemigos;

        // Shulker administra la lista de proyectiles del juego.
        this.balas = jugador.getProyectilesJuego();

        this.pendientes = cantEnemigos;

        int mayorLado = 0;

        for(int lado : GameConfig.ENEMIGO_LADOS) {
            mayorLado = Math.max(mayorLado, lado);
        }

        this.celda = mayorLado + GameConfig.ENEMIGO_DISTANCIA_MIN;

        generarLineas(0);
    }

    private void generarLineas(float delta) {

        if(pendientes <= 0) return;

        msDesdeLinea += delta * 1000;

        if(!mobs1.isEmpty() && msDesdeLinea < GameConfig.INTERVALO_LINEA_MS) return;

        int cantidad = Math.min(pendientes, Math.min(GameConfig.ENEMIGOS_POR_FILA, maxEnPantalla));

        if(mobs1.size() + cantidad > maxEnPantalla) return;

        crearLinea(cantidad);
        pendientes -= cantidad;
        msDesdeLinea = 0;
    }

    private void crearLinea(int cantidad) {

        int yAparicion = GameConfig.ALTO_MUNDO - GameConfig.FORMACION_MARGEN - celda;

        if(!mobs1.isEmpty()) {
            float techo = 0;

            for(Enemigo e : mobs1) {
                Rectangle area = e.getAreaColision();
                techo = Math.max(techo, area.y + area.height);
            }

            int empuje = (int) Math.ceil(techo - (yAparicion - GameConfig.ENEMIGO_DISTANCIA_MIN / 2));
            
            if(empuje > 0) desplazar(0, -empuje);
        }

        int fila = siguienteFila++;
        direccionFila.put(fila, 1);

        for(int c = 0; c < cantidad; c++) {
            int tipo = azar.nextInt(texEnemigos.length);
            int lado = GameConfig.ENEMIGO_LADOS[tipo];
            
            int x = GameConfig.FORMACION_MARGEN
                    + c * celda
                    + (celda - lado) / 2;
            
            int y = yAparicion + (celda - lado) / 2;
            Enemigo e;
            if(azar.nextBoolean()) {
                e = new EnemigoEsqueleto(x, y, texEnemigos[tipo], jugador.getTexturaBala(), balas);
            }
            else {
                e = new EnemigoBlaze(x, y, texEnemigos[tipo], jugador.getTexturaBala(), balas);
            }
            
            if(e.getSprite() != null) e.getSprite().setSize(lado, lado);

            e.setFila(fila);
            mobs1.add(e);
        }
    }

    private void moverFormacion() {

        Map<Integer, ArrayList<Enemigo>> filas =
                new TreeMap<>();

        for(Enemigo e : mobs1) {
            ArrayList<Enemigo> grupo =
                    filas.get(e.getFila());
            if(grupo == null) {
                grupo = new ArrayList<>();
                filas.put(e.getFila(), grupo);
            }
            grupo.add(e);
        }
        direccionFila.keySet().retainAll(filas.keySet());
        for(Map.Entry<Integer, ArrayList<Enemigo>> par: filas.entrySet()) {
            moverFila(par.getKey(), par.getValue());
        }
    }

    private void moverFila(int fila, ArrayList<Enemigo> enemigos) {

        float izq = Float.MAX_VALUE;
        float der = -Float.MAX_VALUE;

        for(Enemigo e : enemigos) {
            Rectangle area = e.getAreaColision();
            izq = Math.min(izq, area.x);
            der = Math.max(der, area.x + area.width);
        }

        float limiteIzq = GameConfig.FORMACION_MARGEN;
        float limiteDer = GameConfig.ANCHO_MUNDO - GameConfig.FORMACION_MARGEN;

        int direccion = direccionFila.getOrDefault(fila, 1);
        int dx = direccion * velX;

        boolean tocaBorde = false;
        if(dx > 0 && der + dx >= limiteDer) {
            dx = Math.max( 0, (int) (limiteDer - der));

            direccion = -1;
            tocaBorde = true;
        }
        
        else if(dx < 0 && izq + dx <= limiteIzq) {
            dx = Math.min( 0, (int) (limiteIzq - izq));
            direccion = 1;
            tocaBorde = true;
        }
        
        direccionFila.put(fila, direccion);
        int dy = 0;
        if(tocaBorde && puedeBajar( fila, enemigos, GameConfig.ENEMIGO_ESCALON)) {
            dy = -GameConfig.ENEMIGO_ESCALON;
        }
        for(Enemigo e : enemigos) {
            e.mover(dx, dy);
        }
    }

    private boolean puedeBajar( int fila, ArrayList<Enemigo> enemigos, int paso) {
        int holgura = GameConfig.ENEMIGO_DISTANCIA_MIN / 2;

        for(Enemigo e : enemigos) {

            Rectangle a = e.getAreaColision();
            float desde = a.y - paso - holgura;
            float hasta = a.y - paso + a.height + holgura;

            for(Enemigo otro : mobs1) {
                Rectangle o = otro.getAreaColision();
                
                if(otro.getFila() != fila
                        && desde < o.y + o.height
                        && hasta > o.y) {
                    return false;
                }
            }
        }
        return true;
    }

    private void desplazar(int dx, int dy) {
        for(Enemigo e : mobs1) {
            e.mover(dx, dy);
        }
    }

    private void eliminarEscapados() {
        for(int i = mobs1.size() - 1; i >= 0; i--) {
            if(mobs1.get(i).getAreaColision().y <= 0) {
                mobs1.remove(i);
                pendientes += GameConfig.ENEMIGOS_POR_ESCAPE;
            }
        }
    }

    public int actualizar(float delta) {
        int destruidos = 0;
        if(!jugador.estaActiva()) {
            return 0;
        }
        // updatear proyectiles y comprobar sus colisiones
        for(int i = balas.size() - 1; i >= 0; i--) {

            Proyectil p = balas.get(i);
            p.actualizar(delta);
            if(!p.estaActiva()) {
                balas.remove(i);
                continue;
            }
            if(!p.isEsDelJugador()) {
                continue;
            }
            for(int j = mobs1.size() - 1; j >= 0; j--) {

                Enemigo enemigo = mobs1.get(j);
                if(p.verificarColision(enemigo)) {
                	
                	
                    p.setActiva(false);
                    balas.remove(i);
                    mobs1.remove(j);
                    puntuacion += GameConfig.PUNTOS_POR_ENEMIGO;
                    jugador.agregarPuntaje(
                            GameConfig.PUNTOS_POR_ENEMIGO
                    );
                    destruidos++;
                    break;
                }
            }
        }
        generarLineas(delta);
        moverFormacion();
        eliminarEscapados();
        return destruidos;
    }

    public boolean puedeDisparar() {
        return balas.size() < GameConfig.MAX_BALAS;
    }

    public boolean agregarBala(Proyectil p) {
        return puedeDisparar()
                && !balas.contains(p)
                && balas.add(p);
    }

    public boolean rondaCompletada() {
        return (mobs1.isEmpty() && pendientes <= 0 && jugador.estaActiva());
    }

    public boolean juegoTerminado() { return !jugador.estaActiva(); }
    public void eliminarEnemigo(int i) { mobs1.remove(i); }
    public int getPuntuacion() { return puntuacion; }
    public int getRonda() { return ronda; }
    public int getVelX() { return velX; }
    public int getVelY() { return velY; }
    public int getCantEnemigos() { return cantEnemigos; }
    public int getMaxEnPantalla() { return maxEnPantalla; }
    public Shulker getJugador() { return jugador; }
    public ArrayList<Enemigo> getEnemigos() { return mobs1; }
    public List<Proyectil> getBalas() { return balas; }
}