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
	public static final float VOLUMEN_MUSICA = 0.3f;
	public static final float VOLUMEN_EXPLOSION = 0.3f;
	
	// Assets
	public static final String SND_EXPLOSION = "explosion.ogg";
	public static final String SND_DANO = "hurt.ogg";
	public static final String SND_DISPARO = "pop-sound.mp3";
	public static final String SND_MUSICA_JUEGO = "piano-loops.wav";
	public static final String TEX_SHULKER = "MainShip3.png";
	public static final String TEX_BALA = "Rocket2.png";
	public static final String TEX_ENEMIGO_GRANDE = "aGreyLarge.png";
	public static final String TEX_ENEMIGO_MEDIANO = "aGreyMedium4.png";
	public static final String TEX_ENEMIGO_PEQUENO = "aGreySmall.png";
}
