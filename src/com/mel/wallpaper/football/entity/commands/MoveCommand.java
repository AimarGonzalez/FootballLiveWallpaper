package com.mel.wallpaper.football.entity.commands;


import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.mel.entityframework.IMovable;
import com.mel.util.MathUtil;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;

public class MoveCommand extends Command
{
	public Point destination;
	public Point finalDestination;
	public IMovable movable;
	public IEaseFunction easeFunction;
	public float speedFactor;
	
	
	private static final IEaseFunction DEFAULT_EASE_FUNCTION = EaseLinear.getInstance();
	private static final float DEFAULT_SPEED_FACTOR = 1f;
	protected float MAXIMUM_DISTANCE = 0f;;
	
	public MoveCommand(PlayerSnapshot player) {
		this(player.originalPlayer, player.originalPlayer);
	}
	
	public MoveCommand(PlayerSnapshot player, PlayerSnapshot movableSnapshot) {
		this(player.originalPlayer, movableSnapshot.originalPlayer);
	}
	
	public MoveCommand(PlayerSnapshot player, BallSnapshot movableSnapshot) {
		this(player.originalPlayer, movableSnapshot.originalBall);
	}
	
	public MoveCommand(Player player, IMovable movable) {
		this(player, movable, DEFAULT_SPEED_FACTOR, DEFAULT_EASE_FUNCTION);
	}
	
	public MoveCommand(Player player, IMovable movable, float speedFactor, IEaseFunction easeFunction) {
		super(player);
		this.movable = movable;
		this.speedFactor = speedFactor;
		this.easeFunction = easeFunction;
	}

	@Override
	public void execute(Partido p) {
		moveObject(this.movable, this.speedFactor);
	}

	protected void moveObject(final IMovable currentMovable, float currentSpeedFactor) {
		currentMovable.removeOldMovementOrders();
		
		Point origien = (Point)currentMovable.getPosition().toPoint();
		Point destino = getFinalDestination(currentMovable);
		
		float distance =  origien.distance(destino);
		// Protegim el PathModifier de que generi NaN quan origen = desti
		if (distance > 0) {
			float duration = distance/(currentMovable.getSpeed()*currentSpeedFactor);
			
			Path path = new Path(2).to(origien.getX(), origien.getY()).to(destino.getX(), destino.getY()); 
			PathModifier moveModifier = new PathModifier(duration, path, new IPathModifierListener()
			{
				public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
				}
				
				public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
				}
				
				public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
				}
				
				public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {
					currentMovable.endMovement(); 
					//ejemplo de codigo seguro para autoquitarse un modifier al terminar;
	//			    engine.runOnUpdateThread(new Runnable(){
	//                    @Override
	//                    public void run(){
	//                    		movable.stopMovement();
	//                    }
	//				});
				}
			},easeFunction);
			currentMovable.getPosition().registerEntityModifier(moveModifier);
			
			startMoveAnimation(currentMovable);
		}
		
	}
	
	private void startMoveAnimation(IMovable currentMovable){
		currentMovable.goTo(getFinalDestination(currentMovable));
	}
	
	protected Point getFinalDestination(IMovable currentMovable){
		if(this.finalDestination == null){
			this.finalDestination =  calcFinalDestination(currentMovable);
		}
		return this.finalDestination;
	}
	
	protected Point calcFinalDestination(IMovable currentMovable){
		//Este metodo solo se puede usar dentro de executeCommand(), NUNCA ANTES pq no tendrias el factor!
		Point finalDestination = (Point)this.destination.clone();
		finalDestination.setLocation(finalDestination.getX()*factor, finalDestination.getY()*factor);//aplicamos factor de inversion de campo (-1 o 1)
		
		float distance = currentMovable.getPosition().distance(finalDestination);
		// limitar a distancia maxima
		if(MAXIMUM_DISTANCE > 0f && distance > MAXIMUM_DISTANCE){ //acortamos el destino segun la distancia maxima a la que se puede xutar la pelota
			finalDestination = MathUtil.getPuntDesti(currentMovable.getPosition().toPoint(), finalDestination, MAXIMUM_DISTANCE);
			distance = MAXIMUM_DISTANCE;
		}
		
		// agregar dispersion
		if(currentMovable instanceof Ball && (this instanceof HitBallCommand || this instanceof PassBallCommand)){
			finalDestination.setX(finalDestination.getX()+distance*MathUtils.random(0f,+0.08f)*MathUtils.randomSign());
			finalDestination.setY(finalDestination.getY()+distance*MathUtils.random(0f,+0.08f)*MathUtils.randomSign());
		}
		
		return finalDestination;
	}
	
	
	
}
