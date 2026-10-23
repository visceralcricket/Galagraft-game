package com.pdjteam.galagraft.config;

public final class GameConfig {
	private GameConfig() {}
	
	// Resoluciones: el campo completo es la zona donde aparecen los enemigos (antes 1200x800)
	public static final int ANCHO_MUNDO = 1800;
	public static final int ALTO_MUNDO = 1200;
	
	// Valores iniciales
	public static final int VIDAS_INICIALES = 3;
	public static final int ENEMIGOS_INICIAL = 10;
	public static final int VEL_INICIAL = 5;
	public static final int MAX_BALAS = 2;
	
	// Progresión por cada ronda
	public static final int ENEMIGOS_POR_RONDA = 10;
	public static final int VEL_POR_RONDA = 3;
	
	// Puntajes
	public static final int PUNTOS_POR_ENEMIGO = 10;
	
	// Audio
	public static final float VOLUMEN_MUSICA = 0.5f;
	public static final float VOLUMEN_EXPLOSION = 0.1f;
	
	// Probabilidad (0 a 1) de que al disparar suene disparoJ en vez del sonido normal
	public static final float PROB_DISPARO_J = 0.1f;
	
	// Tamaño de los disparos (bala.png mide 745x500, se escala para el juego)
	public static final int BALA_ANCHO = 30;
	public static final int BALA_ALTO = 20;
	
	// Tamaño de cada tipo de enemigo (lado mayor en px), en el mismo orden que las texturas:
	// GRANDE (ghast), MEDIANO (esqueleto), PEQUENO (blaze). Los PNG miden 512x512 y se reducen a este lado.
	public static final int[] ENEMIGO_LADOS = { 110, 80, 80 };
	
	// Formación de enemigos (estilo Galaga): aparecen en filas desde la esquina superior izquierda,
	// avanzan en bloque hacia un lado y, al tocar el borde, bajan un escalón y cambian de sentido
	public static final int ENEMIGOS_POR_FILA = 10;
	public static final int ENEMIGO_DISTANCIA_MIN = 20; // separación (px) entre enemigos de la formación
	public static final int FORMACION_MARGEN = 20;      // distancia (px) a los bordes laterales y superior
	public static final int ENEMIGO_ESCALON = 60;       // cuánto bajan (px) al tocar un borde
	
	// Assets
	public static final String FUENTE = "minecraftia.fnt";
	public static final String SND_EXPLOSION = "explosion.ogg";
	public static final String SND_DANO = "hurt.ogg";
	public static final String SND_DISPARO = "shulker_sound.mp3";
	public static final String SND_DISPARO_J = "disparoJ.mp3";
	public static final String SND_MUSICA_JUEGO = "music.mp3";
	public static final String TEX_SHULKER = "shulker.png";
	public static final String TEX_BALA = "bala.png";
	public static final String TEX_ENEMIGO_GRANDE = "Ghast.png";
	public static final String TEX_ENEMIGO_MEDIANO = "esqueleto.png";
	public static final String TEX_ENEMIGO_PEQUENO = "Blaze.png";
	public static final String WPR_GAMEOVER = "game_over_screen.png";
	public static final String WPR_GAMEOVER_TXT1 = "gameover1.png";
	public static final String WPR_GAMEOVER_TXT2= "gameover2.png";
}
