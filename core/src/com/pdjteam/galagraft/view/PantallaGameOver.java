package com.pdjteam.galagraft.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture;
import com.pdjteam.galagraft.Galagraft;
import com.pdjteam.galagraft.config.*;


public class PantallaGameOver implements Screen {

	private Galagraft game;
	private StretchViewport viewport;
	private OrthographicCamera camera;
	private Texture fondo;
	private Texture fondoTitulo;
	private Texture fondoTexto;
	private int puntuacion;

	public PantallaGameOver(Galagraft game, int puntuacion) {
		this.game = game;
		this.puntuacion = puntuacion;
        
		camera = new OrthographicCamera();
		viewport = new StretchViewport(GameConfig.ANCHO_MUNDO, GameConfig.ALTO_MUNDO, camera);
		fondo = new Texture(Gdx.files.internal(GameConfig.WPR_GAMEOVER));
		fondoTitulo = new Texture(Gdx.files.internal(GameConfig.WPR_GAMEOVER_TXT1));
		fondoTitulo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		fondoTexto = new Texture(Gdx.files.internal(GameConfig.WPR_GAMEOVER_TXT2));
		fondoTexto.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		viewport.apply();
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);
		
		game.getBatch().begin();
		
		// Fondo Game Over
		game.getBatch().draw(fondo, 0, 0, GameConfig.ANCHO_MUNDO, GameConfig.ALTO_MUNDO);
		
		// Tamaños y posiciones de los textos
		float anchoTit = GameConfig.ANCHO_MUNDO * 0.5f;
		float altoTit = anchoTit * fondoTitulo.getHeight() / fondoTitulo.getWidth();
		
		float anchoTxt = GameConfig.ANCHO_MUNDO * 0.85f;
		float altoTxt = anchoTxt * fondoTexto.getHeight() / fondoTexto.getWidth();
		
		float separacion = 70;

		float altoBloque = altoTit + separacion + altoTxt;
		float yTxt = (GameConfig.ALTO_MUNDO - altoBloque) / 2;
		float yTit = yTxt + altoTxt + separacion;
		float yPuntaje = yTxt - 50;
		
		// Textos del fondo
		game.getBatch().draw(fondoTitulo, (GameConfig.ANCHO_MUNDO - anchoTit) / 2, yTit, anchoTit, altoTit);
		game.getBatch().draw(fondoTexto, (GameConfig.ANCHO_MUNDO - anchoTxt) / 2, yTxt, anchoTxt, altoTxt);
		game.getFont().getData().setScale(1.5f);
		game.getFont().draw(game.getBatch(), "Puntaje: " + puntuacion, 0, yPuntaje, GameConfig.ANCHO_MUNDO, Align.center, false);
		game.getFont().draw(game.getBatch(), "Mejor puntaje: " + game.getHighScore(), 0, yPuntaje - 50, GameConfig.ANCHO_MUNDO, Align.center, false);
		game.getFont().getData().setScale(1f);
		
		game.getBatch().end();

		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
			Screen ss = new PantallaJuego(game, 1, GameConfig.VIDAS_INICIALES, 0, GameConfig.VEL_INICIAL, GameConfig.VEL_INICIAL, GameConfig.ENEMIGOS_INICIAL, GameConfig.ENEMIGOS_MAX_PANTALLA_INICIAL);
			game.setScreen(ss);
			dispose();
		}
	}
 
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		fondo.dispose();
		fondoTitulo.dispose();
		fondoTexto.dispose();
		// TODO Auto-generated method stub
		
	}
   
}