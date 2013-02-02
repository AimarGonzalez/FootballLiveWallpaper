package com.mel.wallpaper.football.process;

import java.util.List;

import org.andengine.entity.sprite.Sprite;

import com.mel.entityframework.Game;
import com.mel.entityframework.Process;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.view.PlayerAnimation;
import com.mel.wallpaper.football.view.SpriteFactory;

public class RenderPlayersProcess extends Process
{
	private Field field;

	private List<Player> players;
	private Sprite canvas;
	
	public RenderPlayersProcess(Sprite canvas){
		this.canvas = canvas;
	}
	
	@Override
	public void onAddToGame(Game game){
		
		this.field = ((Partido)game.getEntity(Partido.class)).field;

		this.players = (List<Player>) game.getEntities(GoalKeeper.class);
		this.players.addAll(game.getEntities(Player.class));		
	
		for(Player p : this.players){
			this.canvas.attachChild(p.position);
			this.canvas.attachChild(p.sprite);
		}
	}
	
	@Override
	public void onRemoveFromGame(Game game){
		if(players != null){
			players.clear();
			players = null;
		}
		
		canvas = null;
		field = null;
	}
	
	@Override
	public void update(){
		
		
		for(Player player : this.players){
			Point playerCenter = Field.cartesianToEngineCoordinates(player.position);
			Point fixedCoord = new Point(playerCenter.getX()-player.getSpriteOffsetX(), playerCenter.getY()-player.getSpriteOffsetY());
			player.sprite.setPosition(fixedCoord.getX(), fixedCoord.getY());
			
			//Ordenar jugadores segun profundidad en el campo
			if(player.isAplastado() && player.lastAnimation==PlayerAnimation.APLASTADO && player.textureId!=SpriteFactory.MARC){
				player.sprite.setZIndex(1);
			}else{
				player.sprite.setZIndex(1000-(int)player.position.getY());
			}
		}
		
		this.canvas.sortChildren();
		
	}
	
}
