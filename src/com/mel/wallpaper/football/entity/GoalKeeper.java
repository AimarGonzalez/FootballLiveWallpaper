package com.mel.wallpaper.football.entity;


import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.ease.EaseExponentialOut;

import com.mel.entityframework.IMovable;
import com.mel.util.Point;
import com.mel.wallpaper.football.settings.GameSettings;
import com.mel.wallpaper.football.timer.TimerHelper;
import com.mel.wallpaper.football.view.GoalKeeperAnimation;
import com.mel.wallpaper.football.view.PlayerAnimation;
import com.mel.wallpaper.football.view.Position;
import com.mel.wallpaper.football.view.SpriteFactory;


public class GoalKeeper extends Player implements IMovable
{
	
	private static final float	VERTICAL_CENTER	= 35f*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR;
	public static final float GOLAKEEPER_DEFAULT_SPEED = 50;
	public static final float MAX_JUMP_DISTANCE = 30;

	private boolean isOnShootingCooldown = false;
	private boolean isOnRunningCooldown = false;
	private boolean isOnAplastadoCooldown = false;
	private boolean isOnJumpingCooldown = false;
	private boolean isOnStopBallCooldown = false;

	
	/* Getters/Setters */
	public Position getPosition(){
		return this.position;
	}
	
	public float getSpeed(){
		return this.speed;
	}
	
	public boolean getIsOnJumpingCooldown() {
		return this.isOnJumpingCooldown;
	}
	public boolean getIsOnStopBallCooldown() {
		return this.isOnStopBallCooldown;
	}
	
	private void setShootingEnd(){
		this.isOnShootingCooldown = false;
	}
	
	private void setRunningEnd(){
		this.isOnRunningCooldown = false;
	}
	
	private void setAplastadoEnd(){
		this.isOnAplastadoCooldown = false;
	}
	
	private void setJumpingEnd(){
		//Debug.d("gk","setJumpingEnd");
		this.destination = null;
		this.isOnJumpingCooldown = false;
	}
	
	public boolean isBusy(){
		return isOnShootingCooldown || isOnRunningCooldown || isOnAplastadoCooldown || isOnJumpingCooldown;
	}
	
	public boolean canShoot(){
		return !isOnShootingCooldown && !isOnAplastadoCooldown && !isOnJumpingCooldown;
	}
	

	public float getSpriteOffsetX(){
		return this.sprite.getWidth()/2;
	}
	public float getSpriteOffsetY(){
		return this.sprite.getHeight()-VERTICAL_CENTER;
	}
	
	public Point getSpriteOffset(){
		Point spriteCenter = new Point(getSpriteOffsetX(), getSpriteOffsetY());
		return spriteCenter;
	}
	
//	
//	public float getBlockCenterX(){
//		return this.sprite.getWidth()/2;
//	}
//	public float getBlockCenterY(){
//		if(this.isOnJumpingCooldown){
//			switch (lastAnimation) {
//				case JUMP_NE:
//				case JUMP_NW:
//					return this.position.getY()+20; 
//				case JUMP_SE:
//				case JUMP_SW:
//					return this.position.getY()-20; 
//					
//				default:
//					return this.position.getY(); 
//			}
//		}else {
//			return this.position.getY();
//		}
//	}
//	public Point getBlockCenter(){
//		Point spriteCenter = new Point(getBlockCenterX(), getBlockCenterY());
//		return spriteCenter;
//	}
	
	/* Constructor */
	public GoalKeeper(int dorsal, float x, float y, String textureId, PlayerAnimation initialAnimation){
		this(dorsal, new Point(x,y), GoalKeeper.GOLAKEEPER_DEFAULT_SPEED, textureId, initialAnimation, Rol.PORTER);
	}
	
	public GoalKeeper(int dorsal, float x, float y, float speed, String textureId, PlayerAnimation initialAnimation){
		this(dorsal, new Point(x,y), speed, textureId, initialAnimation, Rol.PORTER);
	}
	
	public GoalKeeper(int dorsal, Point p, String textureId, PlayerAnimation initialAnimation){
		this(dorsal, p, GoalKeeper.GOLAKEEPER_DEFAULT_SPEED, textureId, initialAnimation, Rol.PORTER);
	}

	public GoalKeeper(int dorsal, Point p, float speed, String textureId, PlayerAnimation initialAnimation){
		this(dorsal, p, speed, textureId, initialAnimation, Rol.PORTER);
	}
	
	public GoalKeeper(int dorsal, Point p, float speed, String textureId, PlayerAnimation initialAnimation, Rol r){
		super(dorsal, p, speed, textureId, initialAnimation, Rol.PORTER);
	
		this.initialPosition = this.defaultPosition.clone();
	}
	
	public float getTextureSize(){
		return 70*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR;
	}
	
	public Point getRotationCenter(){
		return new Point(35,35);
	}
	
	
	/* methods */
	
		
	public void goTo(Point destination){
		//Debug.d("gk", "goTo()");
		//Debug.d("goTo(): "+(int)destination.getX()+","+(int)destination.getY());
		this.destination = destination;
		animateRun();
		
		this.isOnRunningCooldown = true;
		TimerHelper.startTimer(this.position, 0.3f,  new ITimerCallback() {                      
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
            	setRunningEnd();
            }
        });
	}
	
	public void jump(Point destination){
		this.destination = destination;
		this.isOnJumpingCooldown = true;
		 
		Debug.d("gk", "jump: "+destination);
		
		
		
		if(this.position.distance(this.destination) > 0 ){
			animateJump();
			
			Path jumpPath = new Path(2).to(this.position.getX(), this.position.getY()).to(destination.getX(), destination.getY());
			PathModifier moveModifier = new PathModifier(1f, jumpPath, new IPathModifierListener()
			{
				
				public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
				}
				public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
				}
				public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
				}
				public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {
					setJumpingEnd();
				}
			},EaseExponentialOut.getInstance());
			this.position.registerEntityModifier(moveModifier);
			
		}else{
			TimerHelper.startTimer(this.position, 1f,  new ITimerCallback() {                      
	            public void onTimePassed(final TimerHandler pTimerHandler)
	            {
	            	setJumpingEnd();
	            }
	        });
		}
	}
	
	public boolean canStopBall(Partido partido){
		Ball ball = partido.ball;
		
		//Point ballSpritePosition = partido.field.cartesianToEngineCoordinates(ball.position);
		//if(sprite.contains(ballSpritePosition.getX(), ballSpritePosition.getY())){ //OJO!, contains, que coordenadas pide? del engine?
		if(this.position.distance(ball.position) < Ball.BLOCK_DISTANCE){ //OJO!, contains, que coordenadas pide? del engine?
			return true;
		}else{
			return false;
		}
	}
	
	public void stopBall(){
		this.isOnStopBallCooldown = true;
		TimerHelper.startTimer(this.position, 1.5f,  new ITimerCallback() {                      
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
            	isOnStopBallCooldown = false;
            }
        });
	}
	
	public void shootAt(Point destination){
		this.shootTarget = destination;
		animateShoot();
		
		// Cuan sels hi dona una ordre player estan "busy" una estona. Aixi la animacio de correr no xafa la de xutar.
		this.isOnShootingCooldown = true;
		TimerHelper.startTimer(this.position, 0.5f,  new ITimerCallback() {                      
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
            	setShootingEnd();
            }
        });
	}
	
	public void passBallTo(GoalKeeper destination){
		this.passTarget = destination;
		animatePass();
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
	
	public void aplastar(){
		if(!GameSettings.getInstance().godsFingerEnabled){
			return;
		}
		
		if(this.isOnAplastadoCooldown){
			return;
		}
		
		Debug.d("aplastando jugador!");
		this.isOnAplastadoCooldown = true;
		
		forceStopMovement();
		
		animate(PlayerAnimation.APLASTADO);
		TimerHelper.startTimer(this.position, 3f,  new ITimerCallback() 
		{                      
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	animate(initialAnimation);
            	
            	TimerHelper.startTimer(position, 1f,  new ITimerCallback() {                      
                    
                    public void onTimePassed(final TimerHandler pTimerHandler){
                    	setAplastadoEnd();
                    }
                });
            }
        });
		
		TimerHelper.startTimer(this.position, 2.5f,  new ITimerCallback()
		{                      
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	final Path path = new Path(10).to(position.getX()+2, position.getY())
            								.to(position.getX()-2, position.getY())
            								.to(position.getX()+2, position.getY())
            								.to(position.getX()-2, position.getY())
            								.to(position.getX()+2, position.getY())
            								.to(position.getX()-2, position.getY())
            								.to(position.getX()+2, position.getY())
            								.to(position.getX()-2, position.getY())
            								.to(position.getX()+2, position.getY())
            								.to(position.getX(), position.getY());
            	
            	PathModifier moveModifier = new PathModifier(0.5f, path);
        		position.registerEntityModifier(moveModifier);
            }
        });
	}
	
	
	
		
	
	
	
	private void animateRun(){
		animate(this.initialAnimation);
	}
	
	private void animateJump(){
		PlayerAnimation a = GoalKeeperAnimation.calculateJumpAnimation(this, this.destination, this.initialAnimation);
		animate(a);
	}
	
	
	private void animateStop(){
		animate(this.initialAnimation);
	}
	
	private void animatePass(){
		PlayerAnimation a = GoalKeeperAnimation.calculatePassAnimation(this.initialAnimation);
		animate(a);
	}
	
	private void animateShoot(){
		PlayerAnimation a = GoalKeeperAnimation.calculateShootAnimation(this.initialAnimation);
		animate(a);
	}
	
	public void animate(PlayerAnimation a){
		if(this.lastAnimation == a){
			return;
		}
		
		//sprite.setRotation(0);
		
		//TODO: este codigo habria que convertirlo en un ImageXXXanimationManager, para poder tener players con distintas imagenes (y a su vez distintas animaciones).
		switch(a) {
			case STOP_E: //derecha
				sprite.stopAnimation(0); 
				break;
			case STOP_W: //izquierda
				sprite.stopAnimation(8);  //fila6 
				break;
			case SHOOT_E: //derecha
				sprite.animate(new long[]{500,100}, new int[]{3,0}, false);
				break;
			case SHOOT_W: //izquierda
				sprite.animate(new long[]{500,100}, new int[]{11,8}, false);
				break;
			case PAS_E: //derecha
				sprite.animate(new long[]{200,200,100}, new int[]{1,2,0}, false);
				break;
			case PAS_W: //izquierda
				sprite.animate(new long[]{200,200,100}, new int[]{10,9,8}, false);
				break;
				
			case JUMP_SE: //derecha
				sprite.animate(new long[]{200,400,100}, new int[]{4,5,0}, false);
				break;
			case JUMP_NE: //izquierda
				sprite.animate(new long[]{200,400,100}, new int[]{6,7,0}, false);
				break;
			case JUMP_SW: //derecha
				sprite.animate(new long[]{200,400,100}, new int[]{12,13,8}, false);
				break;
			case JUMP_NW: //izquierda
				sprite.animate(new long[]{200,400,100}, new int[]{14,15,8}, false);
				break;
			
			case APLASTADO:
				sprite.stopAnimation(MathUtils.random(16, 17)); //aqui habra que poner un random, y quizas una rotacion?
				//sprite.setRotation(MathUtils.random(0, 360)); //random
				break;
			default: //parado_s
				animate(initialAnimation);  //fila5
		}
		this.lastAnimation = a;
	}
	
	public void removeOldMovementOrders(){
		this.destination = null;
		this.isOnJumpingCooldown = false;
		this.isOnRunningCooldown = false;
		super.removeOldMovementOrders();

	}
}
