package com.pdjteam.galagraft.engine.waves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pdjteam.galagraft.config.GameConfig;
import com.pdjteam.galagraft.engine.entities.Enemigo;
import com.pdjteam.galagraft.engine.entities.EnemigoBlaze;
import com.pdjteam.galagraft.engine.entities.EnemigoEsqueleto;
import com.pdjteam.galagraft.engine.entities.EnemigoGhast;
import com.pdjteam.galagraft.engine.entities.Proyectil;

public class Wave {

    // partes configuradas por el Builder
    private int numeroRonda;
    private int cantEnemigos;
    private int maxEnPantalla;
    private int pendientes;
    private int velX;
    private int velY;
    private float intervaloSpawnMs;
    private int[] tiposPermitidos;

    private Texture[] texEnemigos;
    private Texture texFlecha;
    private Texture texCargaFuego;
    private List<Proyectil> balas;

    // estado interno y lógica de la formación de la oleada
    private final ArrayList<Enemigo> enemigosActivos = new ArrayList<>();
    private final Map<Integer, Integer> direccionFila = new HashMap<>();
    private final Random azar = new Random();
    private final int celda;
    private float msDesdeLinea = 0f;
    private int siguienteFila = 0;

    public Wave() {
        int mayorLado = 0;
        for (int lado : GameConfig.ENEMIGO_LADOS) {
            mayorLado = Math.max(mayorLado, lado);
        }
        this.celda = mayorLado + GameConfig.ENEMIGO_DISTANCIA_MIN;
    }

    public void setNumeroRonda(int numeroRonda) {
        this.numeroRonda = numeroRonda;
    }

    public void setCantEnemigos(int cantEnemigos) {
        this.cantEnemigos = cantEnemigos;
        this.pendientes = cantEnemigos;
    }

    public void setMaxEnPantalla(int maxEnPantalla) {
        this.maxEnPantalla = maxEnPantalla;
    }

    public void setVelocidades(int velX, int velY) {
        this.velX = velX;
        this.velY = velY;
    }

    public void setIntervaloSpawnMs(float intervaloSpawnMs) {
        this.intervaloSpawnMs = intervaloSpawnMs;
    }

    public void setTiposPermitidos(int[] tiposPermitidos) {
        this.tiposPermitidos = tiposPermitidos;
    }

    public void setRecursos(Texture[] texEnemigos, Texture texFlecha, Texture texCargaFuego, List<Proyectil> balas) {
        this.texEnemigos = texEnemigos;
        this.texFlecha = texFlecha;
        this.texCargaFuego = texCargaFuego;
        this.balas = balas;
    }

    public void iniciarOleada() {
        generarLineas(0f);
    }

    public void actualizar(float delta) {
        generarLineas(delta);
        moverFormacion();
        eliminarEscapados();
        for (Enemigo e : enemigosActivos) {
            e.actualizar(delta);
        }
    }

    private void generarLineas(float delta) {
        if(pendientes <= 0) return;

        msDesdeLinea += delta * 1000f;
        if(!enemigosActivos.isEmpty() && msDesdeLinea < intervaloSpawnMs) return;

        int cantidad = Math.min(pendientes, Math.min(GameConfig.ENEMIGOS_POR_FILA, maxEnPantalla));
        if(enemigosActivos.size() + cantidad > maxEnPantalla) return;

        crearLinea(cantidad);
        pendientes -= cantidad;
        msDesdeLinea = 0f;
    }

    private void crearLinea(int cantidad) {
        int yAparicion = GameConfig.ALTO_MUNDO - GameConfig.FORMACION_MARGEN - celda;

        if(!enemigosActivos.isEmpty()) {
            float techo = 0;
            for (Enemigo e : enemigosActivos) {
                Rectangle area = e.getAreaColision();
                techo = Math.max(techo, area.y + area.height);
            }
            
            int empuje = (int) Math.ceil(techo - (yAparicion - GameConfig.ENEMIGO_DISTANCIA_MIN / 2));
            if(empuje > 0) desplazar(0, -empuje);
        }

        int fila = siguienteFila++;
        direccionFila.put(fila, 1);

        for (int c = 0; c < cantidad; c++) {
            int tipo = (tiposPermitidos != null && tiposPermitidos.length > 0)
                    ? tiposPermitidos[azar.nextInt(tiposPermitidos.length)]
                    : azar.nextInt(texEnemigos.length);

            int lado = GameConfig.ENEMIGO_LADOS[tipo];
            int x = GameConfig.FORMACION_MARGEN + c * celda + (celda - lado) / 2;
            int y = yAparicion + (celda - lado) / 2;

            Enemigo e;
            switch (tipo) {
                case GameConfig.TIPO_GHAST:
                    e = new EnemigoGhast(x, y, texEnemigos[tipo], texCargaFuego, balas);
                    break;
                case GameConfig.TIPO_ESQUELETO:
                    e = new EnemigoEsqueleto(x, y, texEnemigos[tipo], texFlecha, balas);
                    break;
                default:
                    e = new EnemigoBlaze(x, y, texEnemigos[tipo], texCargaFuego, balas);
                    break;
            }

            if(e.getSprite() != null) e.getSprite().setSize(lado, lado);
            e.setFila(fila);
            enemigosActivos.add(e);
        }
    }

    private void moverFormacion() {
        Map<Integer, ArrayList<Enemigo>> filas = new TreeMap<>();
        
        for (Enemigo e : enemigosActivos) {
            ArrayList<Enemigo> grupo = filas.get(e.getFila());
            if(grupo == null) {
                grupo = new ArrayList<>();
                filas.put(e.getFila(), grupo);
            }
            grupo.add(e);
        }
        
        direccionFila.keySet().retainAll(filas.keySet());
        
        for (Map.Entry<Integer, ArrayList<Enemigo>> par : filas.entrySet()) {
            moverFila(par.getKey(), par.getValue());
        }
    }

    private void moverFila(int fila, ArrayList<Enemigo> enemigos) {
        float izq = Float.MAX_VALUE;
        float der = -Float.MAX_VALUE;

        for (Enemigo e : enemigos) {
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
            dx = Math.max(0, (int) (limiteDer - der));
            direccion = -1;
            tocaBorde = true;
        }
        else if(dx < 0 && izq + dx <= limiteIzq) {
            dx = Math.min(0, (int) (limiteIzq - izq));
            direccion = 1;
            tocaBorde = true;
        }

        direccionFila.put(fila, direccion);
        int dy = 0;
        
        if(tocaBorde && puedeBajar(fila, enemigos, GameConfig.ENEMIGO_ESCALON)) {
            dy = -GameConfig.ENEMIGO_ESCALON;
        }
        
        for (Enemigo e : enemigos) {
            e.mover(dx, dy);
        }
    }

    private boolean puedeBajar(int fila, ArrayList<Enemigo> enemigos, int paso) {
        int holgura = GameConfig.ENEMIGO_DISTANCIA_MIN / 2;
        for (Enemigo e : enemigos) {
            Rectangle a = e.getAreaColision();
            float desde = a.y - paso - holgura;
            float hasta = a.y - paso + a.height + holgura;

            for (Enemigo otro : enemigosActivos) {
                Rectangle o = otro.getAreaColision();
                if(otro.getFila() != fila && desde < o.y + o.height && hasta > o.y) {
                    return false;
                }
            }
        }
        return true;
    }

    private void desplazar(int dx, int dy) {
        for (Enemigo e : enemigosActivos) {
            e.mover(dx, dy);
        }
    }

    private void eliminarEscapados() {
        for (int i = enemigosActivos.size() - 1; i >= 0; i--) {
            if(enemigosActivos.get(i).getAreaColision().y <= 0) {
                enemigosActivos.remove(i);
                pendientes += GameConfig.ENEMIGOS_POR_ESCAPE;
            }
        }
    }

    public void eliminarEnemigo(int index) {
        enemigosActivos.remove(index);
    }

    public boolean estaCompletada() {
        return enemigosActivos.isEmpty() && pendientes <= 0;
    }

    // --- Getters ---
    public ArrayList<Enemigo> getEnemigosActivos() { return enemigosActivos; }
    public int getNumeroRonda() { return numeroRonda; }
    public int getCantEnemigos() { return cantEnemigos; }
    public int getPendientes() { return pendientes; }
    public int getMaxEnPantalla() { return maxEnPantalla; }
    public int getVelX() { return velX; }
    public int getVelY() { return velY; }
    public float getIntervaloSpawnMs() { return intervaloSpawnMs; }
}