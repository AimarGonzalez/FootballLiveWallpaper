package com.mel.wallpaper.football.entity.commands;


import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.EaseSineOut;
import org.andengine.util.modifier.ease.EaseStrongOut;

import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;

public class ControlBallCommand extends MoveCommand
{
	public Player shooter;
	public Ball ball;
	
	
	public ControlBallCommand(PlayerSnapshot player, BallSnapshot ball) {
		this(player.originalPlayer, ball.originalBall);
		MAXIMUM_DISTANCE = Ball.MAX_REACH_MATE_DISTANCE;
	}
	public ControlBallCommand(Player player, Ball ball) {
		super(player, ball, 1f, EaseSineOut.getInstance());
		
		this.shooter = player;
		this.ball = ball;
	}
	
	@Override
	public void execute(Partido p) {
		if(!shooter.isBusy() && !ball.isBusy() && shooter.getPosition().distance(movable.getPosition()) < Ball.HIT_DISTANCE) {
			
			// movemos los 2 hacia el mismo destino
			this.easeFunction = EaseStrongOut.getInstance();
			moveObject(ball, 1f);
			this.easeFunction = EaseLinear.getInstance();
			moveObject(shooter, 1f);	
		}
	}
	
	public void startPassAnimation(){
		shooter.controlBall(getFinalDestination(this.movable));
	}
	
}
