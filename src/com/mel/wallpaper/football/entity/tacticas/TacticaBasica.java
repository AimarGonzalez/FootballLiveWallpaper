package com.mel.wallpaper.football.entity.tacticas;

import java.util.ArrayList;
import java.util.List;

import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.entity.Player.Rol;
import com.mel.wallpaper.football.entity.commands.Command;
import com.mel.wallpaper.football.entity.commands.JumpCommand;
import com.mel.wallpaper.football.entity.commands.MoveCommand;
import com.mel.wallpaper.football.entity.commands.PartidoSnapshot;
import com.mel.wallpaper.football.entity.commands.PlayerSnapshot;
import com.mel.wallpaper.football.view.PlayerAnimation;
import com.mel.wallpaper.football.view.SpriteFactory;

public class TacticaBasica extends Tactica
{
	private boolean comeback = false;
	
	public TacticaBasica(Field field) {
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
		
		players.add(new GoalKeeper(players.size()+1, 0, 0, SpriteFactory.BENJI,porteroInitialAnimation));
		players.add(new Player(players.size()+1, -50, 0, SpriteFactory.OLIVER,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -50, 0, SpriteFactory.BRUCE,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -50, 0, SpriteFactory.NT_BLACK,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -50, -20, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, -50, 20, SpriteFactory.NT_BLACK,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -50, 20, SpriteFactory.NT_BLACK2,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, -50, 20, SpriteFactory.NT_RED,PlayerAnimation.STOP_S, Rol.DEFENSA));
		
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
			if(p.rol == Player.Rol.PORTER){
				if(comeback){
					c = getMoveCommand(p, p.defaultPosition);
					if(c!=null){
						//Debug.d("moveDistance: "+p.position.distance(p.initialPosition));
						comeback = false;
					}
				}else{
					c = getRandomJumpCommand(p);
					if(c!=null){
						comeback = true;
					}
				}
			}else{
				c = getRandomMovementCommand(p);
			}
			
			
			if(c != null){
				commands.add(c);
			}
		}
		
		
		
		return commands;
	}
	
	private Command getRandomMovementCommand(PlayerSnapshot p){
		return getMoveCommand(p,this.field.getRandomPoint(50));
	}
	
	
	private Command getRandomJumpCommand(PlayerSnapshot p){
		return getJumpCommand(p,this.field.getRandomPoint(50));
	}
	
	private Command getMoveCommand(PlayerSnapshot p, Point destination){
		MoveCommand command = null;
		if(p.busy==false && p.destination == null){
			command = new MoveCommand(p, p);
			command.destination = destination;
			
		}
		
		return command;
	}
	
	private Command getJumpCommand(PlayerSnapshot p, Point destination){
		JumpCommand command = null;
		if(p.busy==false && p.destination == null){
			command = new JumpCommand(p);
			command.destination = destination;
		}
		return command;
	}

}
