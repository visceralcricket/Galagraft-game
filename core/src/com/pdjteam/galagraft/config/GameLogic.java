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
	
	public GameLogic(Shulker jugador, int ronda, int puntuacion, int velX, int velY, int cantEnemigos, Texture[] texEnemigos) {
		this.jugador = jugador;
		this.ronda = ronda;
		this.puntuacion = puntuacion;
		this.velX = velX;
		this.velY = velY;
		this.cantEnemigos = cantEnemigos;
		crearEnemigos(texEnemigos);
	}
	
	private void crearEnemigos(Texture[] texEnemigos) {
		Random r = new Random();
		for (int i = 0; i < cantEnemigos; i++) {
			int tipo = r.nextInt(texEnemigos.length);
			Texture tex = texEnemigos[tipo];
			Esqueleto e;
			int intentos = 0;
			// Reintenta con otra posición hasta encontrar un lugar libre (o agotar los intentos)
			do {
				e = new Esqueleto(
					r.nextInt(GameConfig.ANCHO_MUNDO),
					50 + r.nextInt(GameConfig.ALTO_MUNDO - 50),
					GameConfig.ENEMIGO_LADOS[tipo], velX + r.nextInt(4), velY + r.nextInt(4), tex);
				intentos++;
			} while (muyCerca(e) && intentos < GameConfig.ENEMIGO_INTENTOS_SPAWN);
			mobs1.add(e);
		}
	}
	
	// Indica si el enemigo quedó a menos de la distancia mínima de otro ya creado
	private boolean muyCerca(Esqueleto nuevo) {
		float m = GameConfig.ENEMIGO_DISTANCIA_MIN;
		Rectangle area = nuevo.getArea();
		Rectangle zona = new Rectangle(area.x - m, area.y - m, area.width + 2 * m, area.height + 2 * m);
		for (Esqueleto otro : mobs1)
			if (zona.overlaps(otro.getArea())) return true;
		return false;
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
		
		for(Esqueleto e : mobs1) e.update();
		
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