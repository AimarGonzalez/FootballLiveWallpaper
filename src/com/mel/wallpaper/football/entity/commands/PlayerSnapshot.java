package com.mel.wallpaper.football.entity.commands;


import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Player;


public class PlayerSnapshot
{
	
	public final float DEFAULT_SPEED = 20;
	
	public int dorsal;
	public Point position;
	public Point destination;
	public float speed;
	public int factor = 0;
	public boolean busy;
	public boolean canShoot;
	public Player.Rol rol;
	public Point defaultPosition;
	public Point initialPosition;
	
	protected Player originalPlayer; // WARN L'he hagut de posar public per accedirhi des de Tactica, any problem?
	
	public PlayerSnapshot(Player originalPlayer, int factor){
		//WARN! fer clone de tots els objectes que faci falta! Sino liada padre!
		this.dorsal = originalPlayer.dorsal;
		this.factor = factor;
		
		this.position = originalPlayer.position.toPoint();
		this.position.setLocation(this.position.getX()*factor, this.position.getY()*factor); //aplicamos factor de inversion de campo (-1 o 1)
		
		this.defaultPosition = originalPlayer.defaultPosition.clone();
		this.defaultPosition.setLocation(this.defaultPosition.getX()*factor, this.defaultPosition.getY()*factor); //aplicamos factor de inversion de campo (-1 o 1)
		
		this.initialPosition = originalPlayer.initialPosition.clone();
		this.initialPosition.setLocation(this.initialPosition.getX()*factor, this.initialPosition.getY()*factor); //aplicamos factor de inversion de campo (-1 o 1)
		
		this.speed = originalPlayer.speed;
		this.busy = originalPlayer.isBusy();
		this.canShoot = originalPlayer.canShoot();
		this.rol = originalPlayer.rol;
		
		if(originalPlayer.destination != null){
			this.destination = (Point)originalPlayer.destination.clone(); //CLONE!! no olvidar
			this.destination.setLocation(this.destination.getX()*factor, this.destination.getY()*factor); //aplicamos factor de inversion de campo (-1 o 1)
		}

		this.originalPlayer = originalPlayer;
	}
	
	public PlayerSnapshot(PlayerSnapshot ps){
		this.position = (Point) ps.position.clone();
		this.factor = ps.factor;
		this.speed = ps.speed;

		this.originalPlayer = ps.originalPlayer;
	}
	
	public float distanceToDestination(){
		if(this.destination != null){
			return  this.destination.distance(this.position);
		}else{
			return 0f;
		}
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
	
}
