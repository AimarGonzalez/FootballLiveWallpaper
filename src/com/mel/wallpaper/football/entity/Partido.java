package com.mel.wallpaper.football.entity;

import com.mel.entityframework.IEntity;
import com.mel.wallpaper.football.entity.commands.PartidoSnapshot;
import com.mel.wallpaper.football.entity.tacticas.TacticaMuppet;
import com.mel.wallpaper.football.entity.tacticas.TacticaNewTeam;


public class Partido implements IEntity
{
	public Team[] teams;
	public Ball ball;
	public Field field;
	
	public static enum Status{
		INITIAL_STATE, LOADING,INTRO, GOTO_INITIAL_POS, RESUME_GAME, WAIT_A_SECOND, PLAYING, GOAL_CINEMATIC, PERFORM_OUTSIDE, PAUSE
	}
	public static enum Event{
		PAUSE, RESUME, SHOW_INTRO, START_PLAYING, GOAL, BALL_OUT_OF_BOUNDS
	}
	
	public Status status = Partido.Status.INITIAL_STATE;
		
	
	public Partido(Field field){
		this.teams = new Team[2];
		//this.teams[0] = new Team(0,	new TacticaAPorLaBola(field), 	SpriteFactory.DEFAULT_PLAYER_TEXTURE, null);
		//this.teams[0] = new Team(0,	new TacticaLaura(field));
		//this.teams[0] = new Team(0,	new TacticaPasmaos(field));
		//this.teams[1] = new Team(1,	new TacticaDebugGoalkeeper(field));
		this.teams[0] = new Team(0,	new TacticaNewTeam(field));
		this.teams[1] = new Team(1,	new TacticaMuppet(field));
		this.ball = new Ball(0, 0, "bola");
		this.field = field;
	}
	
	
	public PartidoSnapshot getSnapshot(int myTeamIndex, int factor){
		int enemyTeamIndex = (myTeamIndex+1)%2;
		
		PartidoSnapshot ps = new PartidoSnapshot();
		ps.setMyTeam(teams[myTeamIndex], factor);
		ps.setEnemyTeam(teams[enemyTeamIndex], factor);
		ps.setBall(this.ball, factor);
		
		return ps;
	}
	
	
	public void recycle(){
		this.status = Partido.Status.INITIAL_STATE;
		
		//mover los jugadores a posicion inicial
	}
	
	
		
}
