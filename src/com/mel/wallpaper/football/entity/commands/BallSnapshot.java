package com.mel.wallpaper.football.entity.commands;


import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Ball;

public class BallSnapshot
{
	public Point position;
	public Point destination;
	public float speed;
	public int factor;
	public boolean busy;
	
	protected Ball originalBall;
	
	public BallSnapshot(Ball originalBall, int factor){
		//WARN! fer clone de tots els objectes que faci falta! Sino liada padre!
		this.position = originalBall.position.toPoint();
		this.position.setLocation(this.position.getX()*factor, this.position.getY()*factor); //aplicamos factor de inversion de campo (-1 o 1)
		this.factor = factor;
		this.busy = originalBall.isFliying();

		if(originalBall.destination != null){
			this.destination = originalBall.destination.clone(); //CLONE!! no olvidar
			this.destination.setLocation(this.destination.getX()*factor, this.destination.getY()*factor);
		}

		this.originalBall = originalBall;
	}
}
