package com.mel.wallpaper.football.entity;


import java.util.ArrayList;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

import com.mel.entityframework.IEntity;
import com.mel.entityframework.IMovable;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.commands.Command;
import com.mel.wallpaper.football.entity.commands.PlayerSnapshot;
import com.mel.wallpaper.football.settings.GameSettings;
import com.mel.wallpaper.football.timer.TimerHelper;
import com.mel.wallpaper.football.view.PlayerAnimation;
import com.mel.wallpaper.football.view.Position;
import com.mel.wallpaper.football.view.SpriteFactory;


public class Player implements IEntity, IMovable
{
	
	
	public static final float DEFAULT_SPEED = 50;
	
	public AnimatedSprite sprite;

	public int dorsal;
	public Point defaultPosition;
	public Point initialPosition;
	public Position position;
	public Point destination;
	public Point shootTarget;
	public Player passTarget;
	public float speed;
	public Rol rol;
	public String textureId;
	
	private boolean isOnShootingCooldown = false;
	private boolean isOnRunningCooldown = false;
	private boolean isOnAplastadoCooldown = false;
	
	public PlayerAnimation lastAnimation = null;
	
	protected PlayerAnimation initialAnimation = null;
	
	private static final float APLASTADO_DURATION = 3f;
	
	public ArrayList<Command> pendingCommands = new ArrayList<Command>();
	
	public enum Rol {
        PORTER("POR"),
        DEFENSA("DEF"),
        MIGCAMPISTA("MIG"),
        DAVANTER("DAV"),
        ESTRELLA("EST");
        
        private final String valor;

        Rol(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return this.valor;
        }
    }	
	
	/* Getters/Setters */
	public Position getPosition(){
		return this.position;
	}
	
	public boolean isAtDefaultPosition() {
		return this.position.distance(this.defaultPosition)<1f;
	}
	
	public boolean isAtInitialPosition() {
		return this.position.distance(this.initialPosition)<1f;
	}
	
	public boolean isGoingToInitialPosition() {
		if(this.destination == null){
			return false;
		}
		
		return this.destination.distance(this.initialPosition)<1f;
	}
	
	public float getSpeed(){
		return this.speed;
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
	
	public boolean isBusy(){
		return isOnShootingCooldown || isOnRunningCooldown || isOnAplastadoCooldown;
	}
	
	public boolean canShoot(){
		return !isOnShootingCooldown && !isOnAplastadoCooldown;
	}
	
	public boolean isAplastado(){
		return this.isOnAplastadoCooldown;
	}
	
	public void addCommand(Command c){
		this.pendingCommands.add(c);
	}
	
	public void clearPendingCommands(){
		this.pendingCommands.clear();
	}
	
	public float getSpriteOffsetX(){
		return this.sprite.getWidth()/2;
	}
	public float getSpriteOffsetY(){
		return this.sprite.getHeight()-5*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR;
	}
	
	public Point getSpriteOffset(){
		Point spriteCenter = new Point(getSpriteOffsetX(), getSpriteOffsetY());
		return spriteCenter;
	}
	
	/* Constructor */
	public Player(int dorsal, float x, float y, float speed, String textureId, PlayerAnimation initialAnimation, Rol r) {
		this(dorsal, new Point(x,y), speed, textureId, initialAnimation, r);
	}

	public Player(int dorsal, float x, float y, String textureId, PlayerAnimation initialAnimation, Rol r){
		this(dorsal, new Point(x,y), DEFAULT_SPEED, textureId, initialAnimation, r);
	}
	
	public Player(int dorsal, Point p, String textureId, PlayerAnimation initialAnimation, Rol r){
		this(dorsal, p, DEFAULT_SPEED, textureId, initialAnimation, r);
	}
	public Player(int dorsal, Point p, float speed, String textureId, PlayerAnimation initialAnimation, Rol r){
		this.dorsal = dorsal;
		this.textureId = textureId;
		this.defaultPosition = p.clone();
		this.initialPosition = calcInitialPosition(this.defaultPosition);
		this.position = new Position(initialPosition);
		this.speed = speed;
		this.rol = r;
		this.initialAnimation = initialAnimation;
		if(textureId != null){
			this.sprite = (AnimatedSprite)SpriteFactory.getMe().newSprite(textureId, getTextureSize(), getTextureSize());
			//sprite.setRotationCenter(getRotationCenter().getX(), getRotationCenter().getY());
			animate(this.initialAnimation);
		}
	}
	
	
	public Point calcInitialPosition(Point pos){
		Point ini = pos.clone();
		ini.setX((ini.getX()+Field.leftWall)/2f); //a mitad de recorrido
		ini.setX(ini.getX()+50f);
		
		//que no este en campo contrario, y que no este dentro de circulo de saque
		while(ini.getX()>-20 || ini.distance(new Point(0,0))<110){
			ini.setX(ini.getX()-10);
		}
		
		return ini;
	}
	
//	private Point calcInitialPosition(Point pos){
//		Point ini = pos.clone();
//			
//		if(ini.getX()>-20){
//			//que no este en campo contrario, y que no este dentro de circulo de saque
//			while(ini.getX()>-20 || ini.distance(new Point(0,0))<110){
//				ini.setX(ini.getX()-10);
//			}
//		}else{
//			ini.setX(ini.getX()*0.5f);
//		}
//		return ini;
//	}
	
	public float getTextureSize(){
		return 40*SpriteFactory.PLAYERS_SPRITE_SCALEFACTOR;
	}
	
	public Point getRotationCenter(){
		return new Point(25,30);
	}
	
	
	/* methods */
	public PlayerSnapshot getSnapshot(int factor){
		PlayerSnapshot p = new PlayerSnapshot(this, factor);
		return p;
	}
	
	public void recycle(){
		this.sprite.detachSelf();
		this.sprite.unregisterEntityModifiers(null);
		this.sprite.unregisterUpdateHandlers(null);
		this.speed = DEFAULT_SPEED;
		this.position.setLocation(0, 0);
	}
	
	
	
	public void goTo(Point destination){
		//Debug.d("player.goTo(): "+(int)destination.getX()+","+(int)destination.getY());
		this.destination = destination;
		animateRun();
		
		float cooldown = 0.01f * position.distance(destination);
		
		this.isOnRunningCooldown = true;
		TimerHelper.startTimer(this.position, Math.min(cooldown, 0.5f),  new ITimerCallback() {                      
            
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
            	setRunningEnd();
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
	
	public void passBallTo(Player destination){
		this.passTarget = destination;
		animatePass();
	}
	
	public void controlBall(Point destination) {
		goTo(destination);
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
		animateMovementEnd();
	}
	
	public void aplastar(){
		if(!GameSettings.getInstance().godsFingerEnabled){
			return;
		}
		
		if(this.isOnAplastadoCooldown){
			return;
		}
		
		//Debug.d("aplastando jugador!");
		this.isOnAplastadoCooldown = true;
		
		forceStopMovement();
		
		animate(PlayerAnimation.APLASTADO);
		TimerHelper.startTimer(this.position, APLASTADO_DURATION,  new ITimerCallback() 
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
		
		TimerHelper.startTimer(this.position, APLASTADO_DURATION-0.5f,  new ITimerCallback()
		{                      
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	int offset = 3;
            	final Path path = new Path(10).to(position.getX()+offset, position.getY())
            								.to(position.getX()-offset, position.getY())
            								.to(position.getX()+offset, position.getY())
            								.to(position.getX()-offset, position.getY())
            								.to(position.getX()+offset, position.getY())
            								.to(position.getX()-offset, position.getY())
            								.to(position.getX()+offset, position.getY())
            								.to(position.getX()-offset, position.getY())
            								.to(position.getX()+offset, position.getY())
            								.to(position.getX(), position.getY());
            	
            	PathModifier moveModifier = new PathModifier(0.5f, path);
        		position.registerEntityModifier(moveModifier);
            }
        });
	}
	
	
	
		
	
	
	
	private void animateRun(){
		PlayerAnimation a = PlayerAnimation.calculateRunAnimation(this, this.destination);
		animate(a);
	}
	
	private void animateMovementEnd(){
		PlayerAnimation a = PlayerAnimation.calculateStopAnimation(this.lastAnimation);
		animate(a);
	}
	
	private void animateStop(){
		animate(this.initialAnimation);
	}
	
	private void animatePass(){
		PlayerAnimation a = PlayerAnimation.calculatePassAnimation(this, this.passTarget.position.toPoint());
		animate(a);
	}
	
	private void animateShoot(){
		PlayerAnimation a = PlayerAnimation.calculateShootAnimation(this, this.shootTarget);
		animate(a);
	}
	
	public void animate(PlayerAnimation a){
		if(this.lastAnimation == a){
			return;
		}
		
		long tileDuration;
		
		//TODO: este codigo habria que convertirlo en un ImageXXXanimationManager, para poder tener players con distintas imagenes (y a su vez distintas animaciones).
		switch(a) {
			case RUN_E: //derecha
				tileDuration =  Math.round(10000/speed);
				sprite.animate(new long[]{tileDuration, tileDuration, tileDuration, tileDuration},new int[]{3,2,1,0}, true); //fila1
				break;
			case RUN_W: //izquierda
				tileDuration =  Math.round(10000/speed);
				sprite.animate(new long[]{tileDuration, tileDuration, tileDuration, tileDuration}, 8, 11, true);  //fila2 
				break;
			case RUN_N: //arriba
				tileDuration =  Math.round(10000/speed);
				sprite.animate(new long[]{tileDuration, tileDuration, tileDuration, tileDuration}, 16, 19, true); //fila3
				break;
			case RUN_S: //abajo
				tileDuration =  Math.round(10000/speed);
				sprite.animate(new long[]{tileDuration, tileDuration, tileDuration, tileDuration}, 24, 27, true); //fila4 
				break;
			case STOP_S: //abajo
				sprite.stopAnimation(32); //fila5
				break;
			case STOP_N: //arriba
				sprite.stopAnimation(34); 
				break;
			case STOP_W: //izquierda
				sprite.stopAnimation(40);  //fila6 
				break;
			case STOP_E: //derecha
				sprite.stopAnimation(42); 
				break;
			case SHOOT_S: //abajo
				sprite.animate(new long[]{500,100},  new int[]{48, 32}, false); //fila7 
				break;
			case SHOOT_W: //izquierda
				sprite.animate(new long[]{500,100}, new int[]{49,40}, false);
				break;
			case SHOOT_E: //derecha
				sprite.animate(new long[]{500,100},  new int[]{50, 42}, false);
				break;
			case SHOOT_N: //arriba
				sprite.animate(new long[]{500,100},  new int[]{51, 34}, false);
				break;
			case PAS_S: //abajo
				sprite.animate(new long[]{350,100},  new int[]{56, 32}, false); //fila8 
				break;
			case PAS_W: //izquierda
				sprite.animate(new long[]{350,100}, new int[]{57,40}, false);
				break;
			case PAS_E: //derecha
				sprite.animate(new long[]{350,100},  new int[]{58, 42}, false);
				break;
			case PAS_N: //arriba
				sprite.animate(new long[]{350,100},  new int[]{59, 34}, false);
				break;
			case APLASTADO:
				if(this.textureId == SpriteFactory.MARC){
					sprite.animate(new long[]{200,200}, new int[]{4, 6}, true);
				}else{
					sprite.stopAnimation(MathUtils.random(4, 6)); //aqui habra que poner un random, y quizas una rotacion?
					//sprite.setRotation(MathUtils.random(0, 360)); //random
				}
				break;
			default: //parado_s
				sprite.stopAnimation(32);  //fila5
		}
		this.lastAnimation = a;
	}
	
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
