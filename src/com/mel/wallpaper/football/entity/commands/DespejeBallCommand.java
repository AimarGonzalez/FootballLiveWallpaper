package com.mel.wallpaper.football.entity.commands;


import com.mel.wallpaper.football.entity.Partido;

public class DespejeBallCommand extends HitBallCommand
{
	
	
	public DespejeBallCommand(PlayerSnapshot player, BallSnapshot ball) {
		super(player, ball);
	}
	
	@Override
	public void execute(Partido p) {
		MAXIMUM_DISTANCE = 700f;
		super.execute(p);
	}
	
	
}
