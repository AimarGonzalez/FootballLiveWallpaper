package com.mel.wallpaper.football.entity.commands;


import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;

public class PassBallCommand extends HitBallCommand
{
	public Player receiver;
	
	public PassBallCommand(PlayerSnapshot player, BallSnapshot ball, PlayerSnapshot target) {
		this(player.originalPlayer, ball.originalBall, target.originalPlayer);
	}
	
	public PassBallCommand(Player player, Ball ball, Player target) {
		super(player, ball);
		this.receiver = target;
	}
	
	@Override
	public void execute(Partido p) {
		if(shooter.canShoot() && !ball.isBusy() && shooter.getPosition().distance(movable.getPosition()) < Ball.HIT_DISTANCE) {
			super.execute(p);
		}
		
		shooter.forceStopMovement();		
		startPassAnimation();
	}
		
	public void startPassAnimation(){
		shooter.passBallTo(receiver);
	}
	
}
