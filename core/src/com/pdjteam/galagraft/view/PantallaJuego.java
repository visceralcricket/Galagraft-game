package com.pdjteam.galagraft.view;

import java.util.ArrayList;
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
import com.pdjteam.galagraft.strategies.DisparoTriplePowerUpStrategy;
import com.pdjteam.galagraft.config.*;

public class PantallaJuego implements Screen {

	private Galagraft game;
	private OrthographicCamera camera;
	private FitViewport viewport;
	private SpriteBatch batch;
	
	private GameLogic logica;
	private Texture[] texEnemigos;
	private List<Proyectil> proyectiles;
	
	private Texture fondo;
	private Sound explosionSound;
	private Music gameMusic;
	private Texture texShulker;
	private Texture texBala;
	private Sound sndDano;
	private Sound sndDisparo;
	private Sound sndDisparoJ;
	private boolean pausado = false;
	private boolean recursosLiberados = false;

	public PantallaJuego(Galagraft game, int ronda, int vidas, int puntuacion, int velXEnemigos, int velYEnemigos, int cantEnemigos, int maxEnPantalla) {
		this.game = game;
		this.batch = game.getBatch();
		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(GameConfig.ANCHO_MUNDO, GameConfig.ALTO_MUNDO, camera);
		this.proyectiles = new ArrayList<>();
		
		explosionSound = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_EXPLOSION));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal(GameConfig.SND_MUSICA_JUEGO));
		fondo = new Texture(Gdx.files.internal(GameConfig.WPR_GAMEPLAY));
		
		gameMusic.setLooping(true);
		gameMusic.setVolume(GameConfig.VOLUMEN_MUSICA);
		gameMusic.play();
		
		texShulker = new Texture(Gdx.files.internal(GameConfig.TEX_SHULKER));
		texBala = new Texture(Gdx.files.internal(GameConfig.TEX_BALA));
		texEnemigos = new Texture[] {
				new Texture(Gdx.files.internal(GameConfig.TEX_ENEMIGO_GRANDE)),
				new Texture(Gdx.files.internal(GameConfig.TEX_ENEMIGO_MEDIANO)),
				new Texture(Gdx.files.internal(GameConfig.TEX_ENEMIGO_PEQUENO))
		};
		sndDano = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_DANO));
		sndDisparo = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_DISPARO));
		sndDisparoJ = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_DISPARO_J));
		
		Shulker jugador = Shulker.inicializarInstancia(
				GameConfig.ANCHO_MUNDO / 2f - 40f, 
				60f, 
				texShulker, 
				texBala, 
				sndDisparoJ, 
				sndDano, 
				proyectiles
		);
		jugador.setVidaMaxima(Math.max(vidas, GameConfig.VIDAS_INICIALES));
		jugador.setVida(vidas);
		jugador.setPuntaje(puntuacion);

		logica = new GameLogic(jugador, ronda, puntuacion, velXEnemigos, velYEnemigos, cantEnemigos, maxEnPantalla, texEnemigos);
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
		// cierre seguro sin crashear OpenGL
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			gameMusic.stop();
			Gdx.app.exit();
			return;
		}

		// Pausa con tecla P
		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
			pausado = !pausado;
			if (pausado) gameMusic.pause();
			else gameMusic.play();
		}

		// Power-Up (Strategy GM-7) con tecla E
		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			logica.getJugador().aplicarPowerUp(new DisparoTriplePowerUpStrategy(), 10f);
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		if (!pausado) {
			logica.getJugador().actualizar(delta);
			if (logica.actualizar(delta) > 0) {
				explosionSound.play(GameConfig.VOLUMEN_EXPLOSION);
			}
		}
		
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f); 
		batch.draw(fondo, 0, 0, GameConfig.ANCHO_MUNDO, GameConfig.ALTO_MUNDO);
		batch.setColor(1f, 1f, 1f, 1f); 
		dibujaEncabezado();

		// dibujar proyectiles solo una vez
		for (Proyectil b : logica.getBalas()) {
			b.dibujar(batch);
		}

		logica.getJugador().dibujar(batch);

		ArrayList<Enemigo> enemigos = logica.getEnemigos();
		for (int i = 0; i < enemigos.size(); i++) {
			Enemigo e = enemigos.get(i);
			e.dibujar(batch);
			if (!pausado && logica.getJugador().verificarColision(e)) {
				logica.getJugador().alColisionar(e);
				logica.eliminarEnemigo(i);
				i--;
			}   	  
		}

		if (pausado) {
			game.getFont().draw(batch, "JUEGO EN PAUSA - [P] Continuar | [ESC] Salir", 0, GameConfig.ALTO_MUNDO / 2f, GameConfig.ANCHO_MUNDO, Align.center, false);
		}
		batch.end();
	    
		if (!pausado && logica.juegoTerminado()) {
			if (logica.getPuntuacion() > game.getHighScore())
				game.setHighScore(logica.getPuntuacion());
			game.setScreen(new PantallaGameOver(game, logica.getPuntuacion()));
			return;
		}

		if (!pausado && logica.rondaCompletada()) {
			Screen ss = new PantallaJuego(game, logica.getRonda() + 1, logica.getJugador().getVida(),
					logica.getPuntuacion(),
					Math.min(GameConfig.VEL_MAX, logica.getVelX() + GameConfig.VEL_POR_RONDA),
					Math.min(GameConfig.VEL_MAX, logica.getVelY() + GameConfig.VEL_POR_RONDA),
					Math.round(logica.getCantEnemigos() * (1 + GameConfig.CRECIMIENTO_ENEMIGOS)),
					Math.round(logica.getMaxEnPantalla() * (1 + GameConfig.CRECIMIENTO_MAX_PANTALLA)));
			game.setScreen(ss);
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
	public void hide() { dispose(); }

	@Override
	public void dispose() {
		if (recursosLiberados) return;
		recursosLiberados = true;
		explosionSound.dispose();
		gameMusic.dispose();
		texShulker.dispose();
		texBala.dispose();
		for (Texture t : texEnemigos) t.dispose();
		sndDano.dispose();
		sndDisparo.dispose();
		sndDisparoJ.dispose();
		fondo.dispose();
	}
}