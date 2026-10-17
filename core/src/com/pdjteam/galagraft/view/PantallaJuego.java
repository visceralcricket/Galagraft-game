package com.pdjteam.galagraft.view;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pdjteam.galagraft.Galagraft;
import com.pdjteam.galagraft.engine.entities.*;
import com.pdjteam.galagraft.config.*;


public class PantallaJuego implements Screen {

	private Galagraft game;
	private OrthographicCamera camera;
	private FitViewport viewport;
	private SpriteBatch batch;
	
	private GameLogic logica;
	private Texture[] texEnemigos;
	
	private Sound explosionSound;
	private Music gameMusic;
	private Texture texShulker;
	private Texture texBala;
	private Sound sndDano;
	private Sound sndDisparo;

	public PantallaJuego(Galagraft game, int ronda, int vidas, int puntuacion, int velXEnemigos, int velYEnemigos, int cantEnemigos) {
		
		this.game = game;
		batch = game.getBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(GameConfig.ANCHO_MUNDO, GameConfig.ALTO_MUNDO, camera);
		//inicializar assets; musica de fondo y efectos de sonido
		explosionSound = Gdx.audio.newSound(Gdx.files.internal(GameConfig.SND_EXPLOSION));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal(GameConfig.SND_MUSICA_JUEGO));
		
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
		
		
	    // cargar imagen de la nave, 64x64   
	    Shulker jugador = new Shulker(GameConfig.ANCHO_MUNDO/2-50,30, texShulker, sndDano, texBala, sndDisparo);
	    jugador.setVidas(vidas);
        //crear asteroides
        logica = new GameLogic(jugador, ronda, puntuacion, velXEnemigos, velYEnemigos, cantEnemigos, texEnemigos);
	}
    
	public void dibujaEncabezado() {
		CharSequence str = "Vidas: " + logica.getJugador().getVidas() +" Ronda: " + logica.getRonda();
		game.getFont().getData().setScale(2f);		
		game.getFont().draw(batch, str, 10, 30);
		game.getFont().draw(batch, "Score:" + logica.getPuntuacion(), GameConfig.ANCHO_MUNDO-150, 30);
		game.getFont().draw(batch, "HighScore:" + game.getHighScore(), GameConfig.ANCHO_MUNDO/2-100, 30);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		if (logica.actualizar() > 0) {
			explosionSound.play(GameConfig.VOLUMEN_EXPLOSION);
		}
		
        batch.begin();
		dibujaEncabezado();
	    //dibujar balas
	    for (Bullet b : logica.getBalas()) {       
	        b.draw(batch);
	    }
	    logica.getJugador().draw(batch, this);
	    //dibujar asteroides y manejar colision con nave
	    ArrayList<Esqueleto> enemigos = logica.getEnemigos();
	    for (int i = 0; i < enemigos.size(); i++) {
	    	Esqueleto e = enemigos.get(i);
	    	e.draw(batch);
		    //perdió vida o game over
	        if (logica.getJugador().checkCollision(e)) {
		       //asteroide se destruye con el choque             
	           logica.eliminarEnemigo(i);
	           i--;
            }   	  
  	    }
	    
	    if (logica.juegoTerminado()) {
  			if (logica.getPuntuacion() > game.getHighScore())
  				game.setHighScore(logica.getPuntuacion());
	    	Screen ss = new PantallaGameOver(game);
  			game.setScreen(ss);
  			dispose();
  		}
	    batch.end();
	    //nivel completado
	    if (logica.rondaCompletada()) {
			Screen ss = new PantallaJuego(game, logica.getRonda()+1, logica.getJugador().getVidas(),
					logica.getPuntuacion(), logica.getVelX() + GameConfig.VEL_POR_RONDA, 
					logica.getVelY() + GameConfig.VEL_POR_RONDA, logica.getCantEnemigos() + GameConfig.ENEMIGOS_POR_RONDA);
			game.setScreen(ss);
			dispose();
		}
	    	 
	}
    
    public boolean agregarBala(Bullet bb) {
    	return logica.agregarBala(bb);
    }
	
	@Override
	public void show() {
		gameMusic.play();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		
	}

	@Override
	public void pause() {
		gameMusic.pause();
		
	}

	@Override
	public void resume() {
		gameMusic.play();
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		explosionSound.dispose();
		gameMusic.dispose();
		texShulker.dispose();
		texBala.dispose();
		for (Texture t : texEnemigos) t.dispose();
		sndDano.dispose();
		sndDisparo.dispose();
	}
   
}
