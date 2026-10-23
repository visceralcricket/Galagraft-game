package com.pdjteam.galagraft.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pdjteam.galagraft.engine.entities.*;

public class GameLogic {
	private final Shulker jugador;
	private final ArrayList<Enemigo> mobs1 = new ArrayList<>();
	private final ArrayList<Bullet> balas = new ArrayList<>();
	private final Texture[] texEnemigos;
	private final Random azar = new Random();
	private int puntuacion;
	private int ronda;
	private int velX, velY, cantEnemigos, maxEnPantalla;

	// Oleada: enemigos que faltan por aparecer y milisegundos desde la última línea creada
	private int pendientes;
	private float msDesdeLinea = 0;

	// Formación: tamaño de cada celda de la grilla. Cada fila de enemigos se mueve por su cuenta:
	// se identifica con un número (el orden en que apareció) y tiene su propio sentido de avance
	// (1 = derecha, -1 = izquierda)
	private final int celda;
	private int siguienteFila = 0;
	private final Map<Integer, Integer> direccionFila = new HashMap<>();

	public GameLogic(Shulker jugador, int ronda, int puntuacion, int velX, int velY, int cantEnemigos, int maxEnPantalla, Texture[] texEnemigos) {
		this.jugador = jugador;
		this.ronda = ronda;
		this.puntuacion = puntuacion;
		this.velX = velX;
		this.velY = velY;
		this.cantEnemigos = cantEnemigos;
		this.maxEnPantalla = maxEnPantalla;
		this.texEnemigos = texEnemigos;
		this.pendientes = cantEnemigos;

		int mayorLado = 0;
		for (int lado : GameConfig.ENEMIGO_LADOS) mayorLado = Math.max(mayorLado, lado);
		this.celda = mayorLado + GameConfig.ENEMIGO_DISTANCIA_MIN;

		generarLineas(0); // la primera línea aparece de inmediato
	}

	// Hace aparecer una nueva línea cada INTERVALO_LINEA_MS, mientras queden enemigos por aparecer
	// y la línea completa quepa dentro del máximo de enemigos en pantalla
	private void generarLineas(float delta) {
		if (pendientes <= 0) return;
		msDesdeLinea += delta * 1000;

		// si no queda ningún enemigo en pantalla, la siguiente línea sale sin esperar
		if (!mobs1.isEmpty() && msDesdeLinea < GameConfig.INTERVALO_LINEA_MS) return;

		int cantidad = Math.min(pendientes, Math.min(GameConfig.ENEMIGOS_POR_FILA, maxEnPantalla));
		if (mobs1.size() + cantidad > maxEnPantalla) return; // no cabe: espera a que haya menos enemigos

		crearLinea(cantidad);
		pendientes -= cantidad;
		msDesdeLinea = 0;
	}

	// Crea una fila de enemigos arriba a la izquierda. Desde ahí se mueve por su cuenta
	private void crearLinea(int cantidad) {
		int yAparicion = GameConfig.ALTO_MUNDO - GameConfig.FORMACION_MARGEN - celda;
		
		if (!mobs1.isEmpty()) {
			// baja la formación lo justo para que la fila nueva no se superponga con la más alta
			float techo = 0;
			for (Enemigo e : mobs1) techo = Math.max(techo, e.getArea().y + e.getArea().height);
			int empuje = (int) Math.ceil(techo - (yAparicion - GameConfig.ENEMIGO_DISTANCIA_MIN / 2));
			if (empuje > 0) desplazar(0, -empuje);
		}
		
		int fila = siguienteFila++;
		direccionFila.put(fila, 1); // la fila nueva parte avanzando hacia la derecha
		
		for (int c = 0; c < cantidad; c++) {
			int tipo = azar.nextInt(texEnemigos.length);
			int lado = GameConfig.ENEMIGO_LADOS[tipo];
			// cada enemigo queda centrado dentro de su celda
			int x = GameConfig.FORMACION_MARGEN + c * celda + (celda - lado) / 2;
			int y = yAparicion + (celda - lado) / 2;
			// la velocidad la maneja la fila (moverFila), no cada enemigo
			Enemigo e = new Enemigo(x, y, lado, 0, 0, texEnemigos[tipo]);
			e.setFila(fila);
			mobs1.add(e);
		}
	}
	
	// Mueve cada fila por separado: avanza en horizontal y, al llegar a un borde, baja un escalón
	// y cambia de sentido. El límite de cada fila se mide con SU enemigo más cercano a cada pared,
	// así una fila con pocos enemigos recorre más pantalla y baja menos seguido.
	private void moverFormacion() {
		// agrupa los enemigos vivos por fila, de la más antigua (la que va más abajo) a la más nueva
		Map<Integer, ArrayList<Enemigo>> filas = new TreeMap<>();
		for (Enemigo e : mobs1) {
			ArrayList<Enemigo> grupo = filas.get(e.getFila());
			if (grupo == null) {
				grupo = new ArrayList<>();
				filas.put(e.getFila(), grupo);
			}
			grupo.add(e);
		}
		direccionFila.keySet().retainAll(filas.keySet()); // olvida las filas que ya no tienen enemigos
		
		for (Map.Entry<Integer, ArrayList<Enemigo>> par : filas.entrySet())
			moverFila(par.getKey(), par.getValue());
	}
	
	private void moverFila(int fila, ArrayList<Enemigo> enemigos) {
		float izq = Float.MAX_VALUE, der = -Float.MAX_VALUE;
		for (Enemigo e : enemigos) {
			Rectangle a = e.getArea();
			izq = Math.min(izq, a.x);
			der = Math.max(der, a.x + a.width);
		}
		
		float limiteIzq = GameConfig.FORMACION_MARGEN;
		float limiteDer = GameConfig.ANCHO_MUNDO - GameConfig.FORMACION_MARGEN;
		
		int direccion = direccionFila.getOrDefault(fila, 1);
		int dx = direccion * velX;
		boolean tocaBorde = false;
		if (dx > 0 && der + dx >= limiteDer) {
			dx = Math.max(0, (int) (limiteDer - der)); // avanza solo hasta quedar justo en el borde
			direccion = -1;
			tocaBorde = true;
		} else if (dx < 0 && izq + dx <= limiteIzq) {
			dx = Math.min(0, (int) (limiteIzq - izq));
			direccion = 1;
			tocaBorde = true;
		}
		direccionFila.put(fila, direccion);
		
		// al tocar el borde baja un escalón, salvo que su altura se cruce con la de otra fila
		int dy = 0;
		if (tocaBorde && puedeBajar(fila, enemigos, GameConfig.ENEMIGO_ESCALON))
			dy = -GameConfig.ENEMIGO_ESCALON;
		
		for (Enemigo e : enemigos) e.mover(dx, dy);
	}
	
	// Indica si la fila puede bajar 'paso' px sin que su franja de altura se cruce con la de otra fila.
	// Se comparan solo las alturas (no las posiciones laterales): como las filas se mueven de lado,
	// dos filas a la misma altura terminarían atravesándose aunque ahora estén lejos una de otra.
	private boolean puedeBajar(int fila, ArrayList<Enemigo> enemigos, int paso) {
		int holgura = GameConfig.ENEMIGO_DISTANCIA_MIN / 2;
		for (Enemigo e : enemigos) {
			Rectangle a = e.getArea();
			float desde = a.y - paso - holgura;
			float hasta = a.y - paso + a.height + holgura;
			for (Enemigo otro : mobs1) {
				Rectangle o = otro.getArea();
				if (otro.getFila() != fila && desde < o.y + o.height && hasta > o.y) return false;
			}
		}
		return true;
	}
	
	private void desplazar(int dx, int dy) {
		for (Enemigo e : mobs1) e.mover(dx, dy);
	}

	// Un enemigo que llega al fondo se elimina y suma ENEMIGOS_POR_ESCAPE al total pendiente de la ronda
	private void eliminarEscapados() {
		for (int i = mobs1.size() - 1; i >= 0; i--) {
			if (mobs1.get(i).getArea().y <= 0) {
				mobs1.remove(i);
				pendientes += GameConfig.ENEMIGOS_POR_ESCAPE;
			}
		}
	}

	// Avanza fotograma y devuelve cuantos enemigos fueron destruidos por las balas
	public int actualizar(float delta) {
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

		generarLineas(delta);
		moverFormacion();
		eliminarEscapados();

		// Sin rebote entre enemigos: un enemigo más rápido atraviesa al más lento y sigue su ruta

		return destruidos;
	}

	public boolean agregarBala(Bullet b) { return balas.add(b); }
	public boolean rondaCompletada( ) { return mobs1.isEmpty() && pendientes <= 0 && jugador.getVidas() > 0; }
	public boolean juegoTerminado() { return jugador.estaDestruido(); }
	public void eliminarEnemigo(int i) { mobs1.remove(i); }

	public int getPuntuacion() { return puntuacion; }
	public int getRonda() { return ronda; }
	public int getVelX() { return velX; }
	public int getVelY() { return velY; }
	public int getCantEnemigos() { return cantEnemigos; }
	public int getMaxEnPantalla() { return maxEnPantalla; }
	public Shulker getJugador() {return jugador;}
	public ArrayList<Enemigo> getEnemigos() { return mobs1; }
	public ArrayList<Bullet> getBalas() { return balas;}
}
