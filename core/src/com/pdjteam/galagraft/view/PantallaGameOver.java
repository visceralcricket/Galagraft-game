package com.pdjteam.galagraft.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pdjteam.galagraft.Galagraft;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.Align;


public class PantallaGameOver implements Screen {

	private Galagraft game;
	private FitViewport viewport;
	private OrthographicCamera camera;

	public PantallaGameOver(Galagraft game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		viewport = new FitViewport(Galagraft.ANCHO_MUNDO, Galagraft.ALTO_MUNDO, camera);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		viewport.apply();
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);

		game.getBatch().begin();
		game.getFont().draw(game.getBatch(), "Game Over !!!", 0, 400, Galagraft.ANCHO_MUNDO, Align.center, false);
		game.getFont().draw(game.getBatch(), "Pincha en cualquier lado para reiniciar ...", 0, 300, Galagraft.ANCHO_MUNDO, Align.center, false);
	
		game.getBatch().end();

		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
			Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
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
		// TODO Auto-generated method stub
		
	}
   
}