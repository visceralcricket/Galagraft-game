package com.pdjteam.galagraft.view;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.pdjteam.galagraft.Galagraft;
import com.pdjteam.galagraft.engine.entities.*;
import com.pdjteam.galagraft.engine.powerups.CatalogoPotenciadores;
import com.pdjteam.galagraft.strategies.DisparoTriplePowerUpStrategy;
import com.pdjteam.galagraft.config.*;

public class PantallaJuego implements Screen {

	private Galagraft game;
	private OrthographicCamera camera;
	private FitViewport viewport;
	private SpriteBatch batch;
	
	private GameLogic logica;
	private Texture[] texEnemigos;
	
	private Texture fondo;
	private Sound explosionSound;
	private Music gameMusic;
	private Texture texShulker;
	private Texture texBala;
	private Texture texFlecha;
	private Texture texCargaFuego;
	private CatalogoPotenciadores catalogoPotenciadores;
	private Sound sndDano;
	private Sound sndDisparo;
	private Sound sndDisparoJ;
	private Sound sndEnemigoHerido;
	private boolean pausado = false;
	private boolean recursosLiberados = false;

	public PantallaJuego(Galagraft game, int ronda, int vidas, int puntuacion, int velXEnemigos, int velYEnemigos, int cantEnemigos, int maxEnPantalla) {
		this.game = game;
		this.batch = game.getBatch();
		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(GameConfig.ANCHO_MUNDO, GameConfig.ALTO_MUNDO, camera);
		
		explosionSound = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_EXPLOSION));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal(GameConfig.SND_MUSICA_JUEGO));
		fondo = new Texture(Gdx.files.internal(GameConfig.WPR_GAMEPLAY));
		
		gameMusic.setLooping(true);
		gameMusic.setVolume(GameConfig.VOLUMEN_MUSICA);
		gameMusic.play();
		
		texShulker = new Texture(Gdx.files.internal(GameConfig.TEX_SHULKER));
		texBala = new Texture(Gdx.files.internal(GameConfig.TEX_BALA));
		texFlecha = new Texture(Gdx.files.internal(GameConfig.TEX_FLECHA));
		texCargaFuego = new Texture(Gdx.files.internal(GameConfig.TEX_CARGA_FUEGO));
		catalogoPotenciadores = new CatalogoPotenciadores();
		texEnemigos = new Texture[] {
				new Texture(Gdx.files.internal(GameConfig.TEX_ENEMIGO_GRANDE)),
				new Texture(Gdx.files.internal(GameConfig.TEX_ENEMIGO_MEDIANO)),
				new Texture(Gdx.files.internal(GameConfig.TEX_ENEMIGO_PEQUENO))
		};
		sndDano = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_DANO));
		sndDisparo = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_DISPARO));
		sndDisparoJ = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_DISPARO_J));
		sndEnemigoHerido = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_ENEMIGO_HERIDO));
		
		Shulker jugador = Shulker.inicializarInstancia(
				GameConfig.ANCHO_MUNDO / 2f - 32f, 
				60f, 
				texShulker, 
				texBala, 
				sndDisparo,
				sndDisparoJ, 
				sndDano
		);
		jugador.setVidaMaxima(Math.max(vidas, GameConfig.VIDAS_INICIALES));
		jugador.setVida(vidas);
		jugador.setPuntaje(puntuacion);

		logica = new GameLogic(
				ronda,
				puntuacion,
				velXEnemigos,
				velYEnemigos,
				cantEnemigos,
				maxEnPantalla,
				texEnemigos,
				texFlecha,
				texCargaFuego,
				catalogoPotenciadores,
				sndEnemigoHerido
		);
	}
    
	public void dibujaEncabezado() {
		CharSequence str = "Vidas: " + logica.getJugador().getVida() + " Ronda: " + logica.getRonda()
				+ " | Arma: " + logica.getJugador().getEstrategiaDisparo().getNombreEstrategia();
		game.getFont().getData().setScale(1f);		
		game.getFont().draw(batch, str, 10, 30);
		game.getFont().draw(batch, "Score:" + logica.getPuntuacion(), GameConfig.ANCHO_MUNDO - 250, 30);
		game.getFont().draw(batch, "HighScore:" + game.getHighScore(), GameConfig.ANCHO_MUNDO / 2f - 100, 30);
	}
	
	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
			return;
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
			pausado = !pausado;
			if(pausado) gameMusic.pause();
			else gameMusic.play();
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		if(!pausado) {
			if(logica.actualizar(delta) > 0) {
		        explosionSound.play(GameConfig.VOLUMEN_EXPLOSION);
		    }
		}
		
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f); 
		batch.draw(fondo, 0, 0, GameConfig.ANCHO_MUNDO, GameConfig.ALTO_MUNDO);
		batch.setColor(1f, 1f, 1f, 1f); 
		dibujaEncabezado();

		for(Proyectil b : logica.getBalas()) {
			b.dibujar(batch);
		}

		for(Potenciador pw : logica.getPotenciadores()) {
			pw.dibujar(batch);
		}

		logica.getJugador().dibujar(batch);

		List<Enemigo> enemigos = logica.getEnemigos();
		for(Enemigo e : enemigos) {
			e.dibujar(batch);
		}

		if(pausado) {
			game.getFont().draw(batch, "JUEGO EN PAUSA - [P] Continuar | [ESC] Salir", 0, GameConfig.ALTO_MUNDO / 2f, GameConfig.ANCHO_MUNDO, Align.center, false);
		}
		batch.end();
	    
		if(!pausado && logica.juegoTerminado()) {
			if(logica.getPuntuacion() > game.getHighScore())
				game.setHighScore(logica.getPuntuacion());
			Screen ss = new PantallaGameOver(game, logica.getPuntuacion());
			game.setScreen(ss);
			dispose();
			return;
		}

		if(!pausado && logica.rondaCompletada()) {
			GameLogic siguienteLogica = logica.crearSiguienteRonda(texEnemigos, texFlecha, texCargaFuego);
			Screen ss = new PantallaJuego(
					game,
					siguienteLogica.getRonda(),
					logica.getJugador().getVida(),
					siguienteLogica.getPuntuacion(),
					siguienteLogica.getVelX(),
					siguienteLogica.getVelY(),
					siguienteLogica.getCantEnemigos(),
					siguienteLogica.getMaxEnPantalla()
			);
			game.setScreen(ss);
			dispose();
		}
	}
    
	public boolean agregarBala(Proyectil bb) {
		return logica.agregarBala(bb);
	}
    
	public boolean puedeDisparar() {
		return logica.puedeDisparar();
	}
	
	@Override
	public void show() {}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		if(recursosLiberados) return;
		recursosLiberados = true;
		explosionSound.dispose();
		gameMusic.dispose();
		texShulker.dispose();
		texBala.dispose();
		texFlecha.dispose();
		texCargaFuego.dispose();
		catalogoPotenciadores.dispose();
		for(Texture t : texEnemigos) t.dispose();
		sndDano.dispose();
		sndDisparo.dispose();
		sndDisparoJ.dispose();
		sndEnemigoHerido.dispose();
		fondo.dispose();
	}
}