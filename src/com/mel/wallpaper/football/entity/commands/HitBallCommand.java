package com.mel.wallpaper.football.entity.commands;


import org.andengine.util.modifier.ease.EaseQuartOut;

import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;

public class HitBallCommand extends MoveCommand
{
	public Player shooter;
	public Ball ball;
	
	
	public HitBallCommand(PlayerSnapshot player, BallSnapshot ball) {
		this(player.originalPlayer, ball.originalBall);
		MAXIMUM_DISTANCE = Ball.MAX_REACH_DISTANCE;
	}
	public HitBallCommand(Player player, Ball ball) {
		super(player, ball, 1.4f, EaseQuartOut.getInstance());
		//this.easeFunction = EaseStrongOut.getInstance();
		//this.easeFunction = EaseExponentialOut.getInstance();
		//this.easeFunction = EaseSineOut.getInstance();
		
		this.shooter = player;
		this.ball = ball;
	}
	
	@Override
	public void execute(Partido p) {
		
		if(shooter.canShoot() && ball.isBusy()==false && shooter.getPosition().distance(movable.getPosition()) < Ball.HIT_DISTANCE){
			super.execute(p);
		}
		
		shooter.forceStopMovement();
		startShootAnimation();
	}
	
	public void startShootAnimation(){
		shooter.shootAt(getFinalDestination(this.movable));//consiga darle a la bola o no, se ve que el jugador chuta
	}
	
}
