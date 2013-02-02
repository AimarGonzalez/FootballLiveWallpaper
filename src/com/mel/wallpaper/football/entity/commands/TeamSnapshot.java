package com.mel.wallpaper.football.entity.commands;

import java.util.ArrayList;
import java.util.List;

public class TeamSnapshot
{
	public List<PlayerSnapshot> players;
	public int score; //no se si usaremos esto :P
	public int factor;
	
	public TeamSnapshot(int factor){
		this.factor = factor;
		this.players = new ArrayList<PlayerSnapshot>();
	}
}
