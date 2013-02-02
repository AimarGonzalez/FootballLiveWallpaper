package com.mel.wallpaper.football.entity;

import java.util.List;

import com.mel.entityframework.IEntity;
import com.mel.wallpaper.football.entity.tacticas.Tactica;

public class Team implements IEntity
{
	public List<Player> players;
	public int score; //no se si usaremos esto :P
	private int factor;
	public Tactica tactica;
	
	public String camisetaJugador;
	public String camisetaPortero;
	
	public Team(int index, Tactica tactica){
		this.factor = index==0?1:-1;
		this.tactica = tactica;
		players = getInitialPlayers();
	}
	
	private List<Player> getInitialPlayers(){
		List<Player> initialPlayers = tactica.getInitialPlayers(factor);
		if(factor==-1){
			for(Player player:initialPlayers){
				player.position.setLocation(player.position.getX()*factor, player.position.getY()*factor);
				player.defaultPosition.setLocation(player.defaultPosition.getX()*factor, player.defaultPosition.getY()*factor);
				player.initialPosition.setLocation(player.initialPosition.getX()*factor, player.initialPosition.getY()*factor);
			}
		}
		return initialPlayers;
	}
	
	public void recycle(){
		this.players = null;
		this.tactica = null;
	}
}
