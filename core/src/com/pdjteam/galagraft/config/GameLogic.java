package com.pdjteam.galagraft.config;

import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pdjteam.galagraft.engine.entities.*;

public class GameLogic {
	private final Shulker jugador;
	private final ArrayList<Esqueleto> mobs1 = new ArrayList<>();
	private final ArrayList<Bullet> balas = new ArrayList<>();
	private int puntuacion;
	private int ronda;
	private int velX, velY, cantEnemigos;
	private int direccion = 1; // sentido de la formación: 1 = derecha, -1 = izquierda
	
	public GameLogic(Shulker jugador, int ronda, int puntuacion, int velX, int velY, int cantEnemigos, Texture[] texEnemigos) {
		this.jugador = jugador;
		this.ronda = ronda;
		this.puntuacion = puntuacion;
		this.velX = velX;
		this.velY = velY;
		this.cantEnemigos = cantEnemigos;
		crearEnemigos(texEnemigos);
	}
	
	// Crea los enemigos en formación de grilla: fila por fila, partiendo arriba a la izquierda
	private void crearEnemigos(Texture[] texEnemigos) {
		Random r = new Random();
		int mayorLado = 0;
		for (int lado : GameConfig.ENEMIGO_LADOS) mayorLado = Math.max(mayorLado, lado);
		int celda = mayorLado + GameConfig.ENEMIGO_DISTANCIA_MIN;
		
		for (int i = 0; i < cantEnemigos; i++) {
			int fila = i / GameConfig.ENEMIGOS_POR_FILA;
			int columna = i % GameConfig.ENEMIGOS_POR_FILA;
			int tipo = r.nextInt(texEnemigos.length);
			int lado = GameConfig.ENEMIGO_LADOS[tipo];
			// cada enemigo queda centrado dentro de su celda
			int x = GameConfig.FORMACION_MARGEN + columna * celda + (celda - lado) / 2;
			int y = GameConfig.ALTO_MUNDO - GameConfig.FORMACION_MARGEN - (fila + 1) * celda + (celda - lado) / 2;
			// la velocidad la maneja la formación (moverFormacion), no cada enemigo
			mobs1.add(new Esqueleto(x, y, lado, 0, 0, texEnemigos[tipo]));
		}
	}
	
	// Mueve toda la formación como un bloque: avanza en horizontal y, al llegar a un borde,
	// baja un escalón y cambia de sentido. Los bordes se miden con los enemigos que siguen vivos.
	private void moverFormacion() {
		if (mobs1.isEmpty()) return;
		
		float izq = Float.MAX_VALUE, der = -Float.MAX_VALUE, abajo = Float.MAX_VALUE;
		for (Esqueleto e : mobs1) {
			Rectangle a = e.getArea();
			izq = Math.min(izq, a.x);
			der = Math.max(der, a.x + a.width);
			abajo = Math.min(abajo, a.y);
		}
		
		float limiteIzq = GameConfig.FORMACION_MARGEN;
		float limiteDer = GameConfig.ANCHO_MUNDO - GameConfig.FORMACION_MARGEN;
		
		int dx = direccion * velX;
		int dy = 0;
		boolean tocaBorde = false;
		if (dx > 0 && der + dx >= limiteDer) {
			dx = Math.max(0, (int) (limiteDer - der)); // avanza solo hasta quedar justo en el borde
			tocaBorde = true;
		} else if (dx < 0 && izq + dx <= limiteIzq) {
			dx = Math.min(0, (int) (limiteIzq - izq));
			tocaBorde = true;
		}
		
		if (tocaBorde) {
			// baja un escalón sin salirse por abajo y vuelve en sentido contrario
			dy = -Math.min(GameConfig.ENEMIGO_ESCALON, (int) abajo);
			direccion = -direccion;
		}
		
		for (Esqueleto e : mobs1) e.mover(dx, dy);
	}

	// Avanza fotograma y devuelve cuantos enemigos fueron destruidos por las balas
	public int actualizar() {
		int destruidos = 0;
		if (jugador.estaHerido()) return 0;
		
		for (int i = 0; i < balas.size(); i++) {
			Bullet b = balas.get(i);
			b.update();
			for (int j = 0; j < mobs1.size(); j++) {
				if (b.checkCollision(mobs1.get(j))) {
					mobs1.remove(j);
					j--;
					puntuacion += GameConfig.PUNTOS_POR_ENEMIGO;
					destruidos++;
					break;
				}
			}
			if (b.isDestroyed()) {
				balas.remove(i);
				i--;
			}
		}
		
		moverFormacion();
		
		// Sin rebote entre enemigos: un enemigo más rápido atraviesa al más lento y sigue su ruta
		
		return destruidos;
	}
	
	public boolean agregarBala(Bullet b) { return balas.add(b); }
	public boolean rondaCompletada( ) { return mobs1.isEmpty() && jugador.getVidas() > 0; }
	public boolean juegoTerminado() { return jugador.estaDestruido(); }
	public void eliminarEnemigo(int i) { mobs1.remove(i); }
	
	public int getPuntuacion() { return puntuacion; }
	public int getRonda() { return ronda; }
	public int getVelX() { return velX; }
	public int getVelY() { return velY; }
	public int getCantEnemigos() { return cantEnemigos; }
	public Shulker getJugador() {return jugador;}
	public ArrayList<Esqueleto> getEnemigos() { return mobs1; }
	public ArrayList<Bullet> getBalas() { return balas;}
}