package com.mel.wallpaper.football.entity.commands;


import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.mel.entityframework.IMovable;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;

public class StopCommand extends Command
{
	public Point destination;
	public IMovable movable;
	public IEaseFunction easeFunction = EaseLinear.getInstance();
	
	public StopCommand(PlayerSnapshot player) {
		this(player.originalPlayer, player.originalPlayer);
	}
	
	public StopCommand(PlayerSnapshot player, PlayerSnapshot movableSnapshot) {
		this(player.originalPlayer, movableSnapshot.originalPlayer);
	}
	
	public StopCommand(PlayerSnapshot player, BallSnapshot movableSnapshot) {
		this(player.originalPlayer, movableSnapshot.originalBall);
	}	
	
	public StopCommand(Player player, IMovable movable) {
		super(player);
		this.movable = movable;
	}
	
	@Override
	public void execute(Partido p) {
		this.movable.forceStopMovement();
	}
	
	
	
}
