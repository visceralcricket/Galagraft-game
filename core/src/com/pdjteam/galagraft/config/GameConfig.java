package com.pdjteam.galagraft.config;

public final class GameConfig {
	private GameConfig() {}
	
	// Resoluciones
	public static final int ANCHO_MUNDO = 1200;
	public static final int ALTO_MUNDO = 800;
	
	// Valores iniciales
	public static final int VIDAS_INICIALES = 3;
	public static final int ENEMIGOS_INICIAL = 10;
	public static final int VEL_INICIAL = 1;
	
	// Progresión por cada ronda
	public static final int ENEMIGOS_POR_RONDA = 10;
	public static final int VEL_POR_RONDA = 3;
	
	// Puntajes
	public static final int PUNTOS_POR_ENEMIGO = 10;
	
	// Audio
	public static final float VOLUMEN_MUSICA = 0.1f;
	public static final float VOLUMEN_EXPLOSION = 0.1f;
	
	// Tamaño de los disparos (bala.png mide 745x500, se escala para el juego)
	public static final int BALA_ANCHO = 30;
	public static final int BALA_ALTO = 20;
	
	// Tamaño de cada tipo de enemigo (lado mayor en px), en el mismo orden que las texturas:
	// GRANDE (ghast), MEDIANO (esqueleto), PEQUENO (blaze). Los PNG miden 512x512 y se reducen a este lado.
	public static final int[] ENEMIGO_LADOS = { 110, 80, 80 };
	
	// Separación mínima (px) entre enemigos al aparecer y reintentos para encontrar un lugar libre
	public static final int ENEMIGO_DISTANCIA_MIN = 20;
	public static final int ENEMIGO_INTENTOS_SPAWN = 50;
	
	// Assets
	public static final String SND_EXPLOSION = "explosion.ogg";
	public static final String SND_DANO = "hurt.ogg";
	public static final String SND_DISPARO = "pop-sound.mp3";
	public static final String SND_MUSICA_JUEGO = "piano-loops.wav";
	public static final String TEX_SHULKER = "shulker.png";
	public static final String TEX_BALA = "bala.png";
	public static final String TEX_ENEMIGO_GRANDE = "Ghast.png";
	public static final String TEX_ENEMIGO_MEDIANO = "esqueleto.png";
	public static final String TEX_ENEMIGO_PEQUENO = "Blaze.png";
}
