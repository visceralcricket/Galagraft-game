package com.pdjteam.galagraft.interfaces;

import com.badlogic.gdx.math.Rectangle;

public interface Colisionable {

	Rectangle getAreaColision();
	
    boolean verificarColision(Colisionable otro);
    
    void alColisionar(Colisionable otro);
    
    boolean estaActiva();
}
