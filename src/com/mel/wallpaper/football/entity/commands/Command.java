package com.mel.wallpaper.football.entity.commands;

import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;

public abstract class Command
{
	public Player player;
	public int factor = 1; //sobreescrito por el Framework (PlayersProcess)
	
	public Command(Player player){
		this.player = player;
		if(this.player != null){
			this.player.addCommand(this);
		}
	}
	
	public abstract void execute(Partido partido);
	
}
