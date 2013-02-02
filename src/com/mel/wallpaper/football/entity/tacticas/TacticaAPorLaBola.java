package com.mel.wallpaper.football.entity.tacticas;

import java.util.ArrayList;
import java.util.List;

import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.entity.Player.Rol;
import com.mel.wallpaper.football.entity.commands.BallSnapshot;
import com.mel.wallpaper.football.entity.commands.Command;
import com.mel.wallpaper.football.entity.commands.HitBallCommand;
import com.mel.wallpaper.football.entity.commands.PartidoSnapshot;
import com.mel.wallpaper.football.entity.commands.PlayerSnapshot;
import com.mel.wallpaper.football.view.PlayerAnimation;
import com.mel.wallpaper.football.view.SpriteFactory;

public class TacticaAPorLaBola extends Tactica
{
	
	public TacticaAPorLaBola(Field field) {
		super(field);
	}

	public List<Player> getInitialPlayers(int factor){
		List<Player> players = new ArrayList<Player>();
		
		PlayerAnimation porteroInitialAnimation = null;
		if(factor == 1){
			porteroInitialAnimation = PlayerAnimation.STOP_E;
		}else{
			porteroInitialAnimation = PlayerAnimation.STOP_W;
		}
		
		players.add(new GoalKeeper(players.size()+1, -300, 0, SpriteFactory.BENJI, porteroInitialAnimation));
		players.add(new Player(players.size()+1, 0, 200, SpriteFactory.MP_WHITE,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, 200, -200, SpriteFactory.MP_WHITE,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, 200, 200, SpriteFactory.MP_WHITE,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -200, 200, SpriteFactory.MP_WHITE,PlayerAnimation.STOP_S, Rol.DAVANTER));
		
		return players;
	}
	
	/* SituacionPartido es un snapshot del objeto Partido, que lleva
	 * los jugadores agrupados por amigos/enemigos, y con las coordenadas transformadas
	 * para que siempre estés en el lado izquierdo del campo.
	 */
	public List<Command> getCommands(PartidoSnapshot partido){
		ArrayList<Command> commands = new ArrayList<Command>();
		Command c = null;
		
		//create commands
		for(PlayerSnapshot p:partido.myTeam.players){
			if(p.busy == true){
				continue;
			}
			
			c = hitRandomBall(p, partido.ball);
			
			if(c == null){
				c = getGoToBallCommand(p, partido.ball);
			}
			
			if(c != null) {
				commands.add(c);
			}
		}
		
		return commands;
	}
	
		
	private Command hitRandomBall(PlayerSnapshot p, BallSnapshot b){
		HitBallCommand command = null;
		if(!b.busy && canHitBall(p, b)) {
			command = new HitBallCommand(p, b);
			command.destination = this.field.getRandomPoint();
		}
		
		return command;
	}
	
//	private Command getStopCommand(PlayerSnapshot p){
//		StopCommand command = new StopCommand(p.originalPlayer, p.originalPlayer);
//		
//		return command;
//	}

}
