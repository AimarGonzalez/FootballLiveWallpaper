package com.mel.wallpaper.football.entity;



import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

import com.mel.entityframework.IEntity;
import com.mel.entityframework.IMovable;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.commands.BallSnapshot;
import com.mel.wallpaper.football.timer.TimerHelper;
import com.mel.wallpaper.football.view.PlayerAnimation;
import com.mel.wallpaper.football.view.Position;
import com.mel.wallpaper.football.view.SpriteFactory;

public class Ball implements IEntity, IMovable
{
	
	public static final float BLOCK_DISTANCE = 20f; //distancia para que el portero bloquee
	public static final float DESPEJE_DISTANCE = 42f; //distancia para que el portero despeje
	
	public static final float HIT_DISTANCE = 20f;
	public static final float JUMP_DISTANCE = 60f;
	public static final float MAX_REACH_MATE_DISTANCE = 220f;
	public static final float MAX_REACH_DISTANCE = 350f;
	public static final float MIN_REACH_DISTANCE = 70f;
	
	public static final float DEFAULT_SPEED = 65f;
	
	public Position position;
	public float speed;
	public AnimatedSprite sprite;
	
	public Point origin;
	public Point destination;
	
	public boolean isOnVColisionCooldown = false;
	public boolean isOnHColisionCooldown = false;
	
	/* Getters/Setters */
	public Position getPosition() {
		return this.position;
	}
	public float getSpeed() {
		return this.speed;
	}
	public void goTo(Point destination) {
		this.origin = position.toPoint();
		this.destination = destination;
		//Debug.d("ball.goTo(): "+(int)destination.getX()+","+(int)destination.getY());
		animateMove();
	}
	
	public void forceStopMovement(){
		removeOldMovementOrders();
		animateStop();
	}
	
	public void endMovement(){
		//testing code
		if(this.position.getEntityModifierCount() > 1){
			Debug.d("ball","ALERT: paramos animacion jugador y tenemos MODIFIERS acumulados: "+this.position.getEntityModifierCount());
		}// testing code
		
		this.destination = null;
		animateStop();
	}
	
	public float getSpriteOffsetX(){
		return this.sprite.getWidth()/2;
	}
	public float getSpriteOffsetY(){
		return this.sprite.getHeight()/2;
	}
	
	public Point getSpriteOffset(){
		Point spriteCenter = new Point(getSpriteOffsetX(), getSpriteOffsetY());
		return spriteCenter;
	}
	
	/* Constructor */
	public Ball(float x, float y, String textureId){
		this(new Position(x,y), textureId);
	}
	public Ball(Position p, String textureId){
		this.position = p;
		this.speed = Ball.DEFAULT_SPEED;
		this.sprite = (AnimatedSprite) SpriteFactory.getMe().newSprite(SpriteFactory.BALL, 19*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR, 19*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR);
	}
	
	
	/* Methods */
	public BallSnapshot getSnapshot(int factor){
		BallSnapshot p = new BallSnapshot(this, factor);
		return p;
	}
	
	public void recycle(){
		this.sprite.detachSelf();
		this.sprite.unregisterEntityModifiers(null);
		this.sprite.unregisterUpdateHandlers(null);
		this.speed = 0;
		this.position.setLocation(0, 0);
	}
	
	public boolean isBusy(){
		return isFliying();
	}
	
	public boolean isFliying(){
		if(this.origin != null && this.destination !=null){
			float distanciaRecorrida = getTraveledDistance();
			float distanciaTotal = getTotalDistance();
			return distanciaTotal > 80 && distanciaRecorrida/distanciaTotal < 0.3f;
		}
		return false;
	}
	
	public float getTraveledDistance(){
		return this.position.distance(this.origin);
	}
	
	public float getTotalDistance(){
		return this.origin.distance(this.destination);
	}
	
	public boolean isMoving(){
		return this.destination != null;
	}
	
	public void startCollisionVertical(){
		isOnVColisionCooldown = true;
		TimerHelper.startTimer(this.position, 1f,  new ITimerCallback() {                      
            
            public void onTimePassed(final TimerHandler pTimerHandler){
            	endColisionVertical();
            }
        });
	}
	
	private void endColisionVertical(){
		isOnVColisionCooldown = false;
	}
	
	public void startCollisionHorizontal(){
		isOnHColisionCooldown = true;
		TimerHelper.startTimer(this.position, 1f,  new ITimerCallback() {                      
            
            public void onTimePassed(final TimerHandler pTimerHandler){
            	endColisionHorizontal();
            }
        });
	}
	
	private void endColisionHorizontal(){
		isOnHColisionCooldown = false;
	}
	
	public void animateMove(){
		
		//Debug.d("ball", "animate - MOVE!");
		long tileDuration = 1000;
		float speedFactor = 0f;
		if(getTotalDistance() - getTraveledDistance() > 2){
			speedFactor = getTraveledDistance()/getTotalDistance();
			tileDuration = 15 + (long)(speedFactor*100) + (long)(1000f/getTotalDistance()); //un maximo / segun distancia recorida / segun fuerza chute
		}
		
		sprite.animate(new long[]{tileDuration, tileDuration, tileDuration, tileDuration, tileDuration}, 0, 4, true);
		
		TimerHelper.startTimer(this.position, Math.max(0.5f, 5f*(float)tileDuration/1000f),  new ITimerCallback() 
		{                      
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	if(destination != null){
            		animateMove();
            	}
            } 
        });
	}
	
	public void animateStop(){
		int stopTile = MathUtils.random(0, 4);
		//Debug.d("ball", "animate - STOP! ("+stopTile+")");
		sprite.stopAnimation(stopTile);
	}
	
	
	
	/* HELPERS */
	public void removeOldMovementOrders(){
		this.destination = null;
		this.position.unregisterEntityModifiers(new IEntityModifier.IEntityModifierMatcher()
		{
			public boolean matches(IModifier<org.andengine.entity.IEntity> pObject) {
				boolean matches = pObject.getClass().equals(PathModifier.class) || pObject.getClass().equals(MoveModifier.class);
				return matches;
			}
		});
	}
	
	
}
