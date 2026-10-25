package com.pdjteam.galagraft;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.pdjteam.galagraft.config.GameConfig;

import com.pdjteam.galagraft.view.PantallaMenu;



public class Galagraft extends Game {
	private String nombreJuego = "Galagraft";
	private SpriteBatch batch;
	private BitmapFont font;
	private int highScore;

	public void create() {
		highScore = 0;
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal(GameConfig.FUENTE));
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		Screen ss = new PantallaMenu(this);
		this.setScreen(ss);
	}

	public void render() {
		super.render();
	}

	public void dispose() {
		if(screen != null) screen.dispose();
		
		batch.dispose();
		font.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public BitmapFont getFont() {
		return font;
	}

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}
}