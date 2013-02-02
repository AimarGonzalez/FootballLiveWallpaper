package com.mel.wallpaper.football.process;

import java.util.List;

import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;

import com.mel.entityframework.Game;
import com.mel.entityframework.Process;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.Partido;

public class RenderBallsProcess extends Process
{
	private Field field;

	private List<Ball> balls;
	private Sprite canvas;
	
	public RenderBallsProcess(Sprite canvas){
		this.canvas = canvas;
	}
	
	
	//final Shape miniball = SpriteFactory.getInstance().newBall(3, 3);
	final Shape miniball = null; //disabled
	
	@Override
	public void onAddToGame(Game game){
		Partido partido = (Partido)game.getEntity(Partido.class);
		this.field = partido.field;
		
		this.balls = (List<Ball>) game.getEntities(Ball.class);
		for(Ball b : this.balls){
			this.canvas.attachChild(b.position);
			this.canvas.attachChild(b.sprite);
		}
		
		if(miniball != null){
			this.canvas.attachChild(miniball);
		}
	}
	
	@Override
	public void onRemoveFromGame(Game game){
		if(balls != null){
			balls.clear();
			balls = null;
		}
		
		canvas = null;
		field = null;
		
	}
	
	@Override
	public void update(){
		
		
		for(Ball ball : this.balls){
			
			Point ballCenter= Field.cartesianToEngineCoordinates(ball.position);
			Point fixedCoord = new Point(ballCenter.getX()-ball.getSpriteOffsetX(), ballCenter.getY()-ball.getSpriteOffsetY());
			ball.sprite.setPosition(fixedCoord.getX(), fixedCoord.getY());
			//Ordenar profundidad en el campo
			ball.sprite.setZIndex(1000-(int)ball.position.getY());

			if(miniball != null){
				miniball.setPosition(ballCenter.getX()-1, ballCenter.getY()-1);
				miniball.setColor(1, 0, 0);
				miniball.setZIndex(10000);
			}
			
			
		}
		
		this.canvas.sortChildren();
		
	}

}
