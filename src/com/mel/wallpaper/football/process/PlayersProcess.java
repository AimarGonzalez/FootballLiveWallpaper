package com.mel.wallpaper.football.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mel.entityframework.Game;
import com.mel.entityframework.Process;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.entity.commands.Command;
import com.mel.wallpaper.football.entity.commands.MoveCommand;
import com.mel.wallpaper.football.entity.commands.PartidoSnapshot;
import com.mel.wallpaper.football.entity.commands.PlayerSnapshot;
import com.mel.wallpaper.football.entity.commands.StopCommand;
import com.mel.wallpaper.football.entity.tacticas.Tactica;



public class PlayersProcess extends Process
{
	private Partido partido;
	private List<Player> players;
	
	
	public PlayersProcess(Partido partido){
	}
	
	@Override
	public void onAddToGame(Game game){
		this.partido = (Partido)game.getEntity(Partido.class);

		this.players = (List<Player>) game.getEntities(GoalKeeper.class);
		this.players.addAll(game.getEntities(Player.class));
	}
	
	@Override
	public void onRemoveFromGame(Game game){
		if(players != null){
			players.clear();
			players = null;
		}
		
		this.partido = null;
	}
	
	@Override
	public void update(){
		
		List<Command> allFixedCommands = new ArrayList<Command>();;
		
		switch(this.partido.status){
			
			case PLAYING:
				getIACommands(allFixedCommands);
				break;
				
			case GOTO_INITIAL_POS:
				forcePlayersToInitialPosition(allFixedCommands);
				break;
				
			default:
		}
		
		//deberiamos agrupar comman
		if(allFixedCommands.size()>0){
			executeCommandsByRandomPlayer();
		}
		
	}
	
	private void getIACommands(List<Command> allCommands) {
		PartidoSnapshot snapshot0 = this.partido.getSnapshot(0, 1);
		PartidoSnapshot snapshot1 = this.partido.getSnapshot(1, -1);
		List<Command> commands;
		
		Tactica tactica;
		tactica= partido.teams[0].tactica;
		commands = tactica.getCommands(snapshot0);
		allCommands.addAll(commands);
		
		tactica= partido.teams[1].tactica;
		commands = tactica.getCommands(snapshot1);
		applyVisitorFactor(commands); //aplicamos factor solo al visitante!
		allCommands.addAll(commands);
	}

	private void forcePlayersToInitialPosition(List<Command> allCommands) {
		PartidoSnapshot snapshot0 = this.partido.getSnapshot(0, 1);
		PartidoSnapshot snapshot1 = this.partido.getSnapshot(1, -1);
		List<Command> commands;
		
		commands = goToInitialPositionCommands(snapshot0.myTeam.players);
		allCommands.addAll(commands);
		
		commands = goToInitialPositionCommands(snapshot1.myTeam.players);
		applyVisitorFactor(commands); //aplicamos factor solo al visitante!
		allCommands.addAll(commands);
	}
	
	private List<Command> goToInitialPositionCommands(List<PlayerSnapshot> teamPlayers) {
		List<Command> gotoInitCommands = new ArrayList<Command>();
		
		for (PlayerSnapshot player : teamPlayers) {
			if(player.isAtInitialPosition()){
				gotoInitCommands.add(new StopCommand(player));
				
			}else if(!player.isGoingToInitialPosition()){
				MoveCommand c = new MoveCommand(player, player);
				c.destination = player.initialPosition;
				gotoInitCommands.add(c);
			}
		}			
		
		return gotoInitCommands;
	}

	
	private void executeCommandsByRandomPlayer(){
		Collections.shuffle(this.players);
		for(Player player:this.players){
			for(Command c:player.pendingCommands){
				c.execute(this.partido);
			}
			player.clearPendingCommands();
		}
	}
	
	
	
	private void applyVisitorFactor(List<Command> commands){
		for(Command command:commands){
			command.factor = -1;
		}
	}
	
}
