package com.mel.wallpaper.football.entity.commands;

import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.entity.Team;

public class PartidoSnapshot
{
	public TeamSnapshot myTeam;
	public TeamSnapshot enemyTeam;
	
	public BallSnapshot ball;
	
	public PartidoSnapshot(){
	}
	
	public void setMyTeam(Team t, int factor){
		this.myTeam = buildTeamSnapshot(t, factor);
	}
	
	public void setEnemyTeam(Team t, int factor){
		this.enemyTeam = buildTeamSnapshot(t, factor);
	}
	
	public TeamSnapshot buildTeamSnapshot(Team originalTeam, int factor){
		TeamSnapshot t = new TeamSnapshot(factor);
		for(Player p:originalTeam.players){
			t.players.add(p.getSnapshot(factor));
		}
		return t;
	}
	
	public void setBall(Ball originalBall, int factor){
		this.ball = originalBall.getSnapshot(factor);
	}
	
	
}
