package com.pdjteam.galagraft;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pdjteam.galagraft.view.PantallaMenu;



public class Galagraft extends Game {
	private String nombreJuego = "Galagraft";
	private SpriteBatch batch;
	private BitmapFont font;
	private int highScore;
	public static final int ANCHO_MUNDO = 1200;
	public static final int ALTO_MUNDO = 800; // Hace más sentido decir ALTURA_MUNDO

	public void create() {
		highScore = 0;
		batch = new SpriteBatch();
		font = new BitmapFont(); // usa Arial font x defecto
		font.getData().setScale(2f);
		Screen ss = new PantallaMenu(this);
		this.setScreen(ss);
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
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