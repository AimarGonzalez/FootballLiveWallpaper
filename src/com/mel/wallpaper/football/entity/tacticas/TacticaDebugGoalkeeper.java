package com.mel.wallpaper.football.entity.tacticas;

import java.util.ArrayList;
import java.util.List;

import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.entity.Player.Rol;
import com.mel.wallpaper.football.entity.commands.BallSnapshot;
import com.mel.wallpaper.football.entity.commands.Command;
import com.mel.wallpaper.football.entity.commands.PartidoSnapshot;
import com.mel.wallpaper.football.entity.commands.PlayerSnapshot;
import com.mel.wallpaper.football.view.PlayerAnimation;
import com.mel.wallpaper.football.view.SpriteFactory;

public class TacticaDebugGoalkeeper extends Tactica
{
	private PlayerSnapshot playerAfterBall;
	
	public TacticaDebugGoalkeeper(Field field) {
		super(field);
		playerAfterBall = null;
	}

	public List<Player> getInitialPlayers(int factor) {
		List<Player> players = new ArrayList<Player>();
		
		PlayerAnimation porteroInitialAnimation = null;
		if (factor == 1) {
			porteroInitialAnimation = PlayerAnimation.STOP_E;
		} else {
			porteroInitialAnimation = PlayerAnimation.STOP_W;
		}
		
		players.add(new GoalKeeper(players.size()+1, -470, 0, SpriteFactory.BENJI,porteroInitialAnimation));
		players.add(new Player(players.size()+1, -300, 150, SpriteFactory.BRUCE,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -300, 150, SpriteFactory.BRUCE,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -300, 0, SpriteFactory.NT_BLACK2,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -300, 0, SpriteFactory.NT_BLACK2,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -300, -150, SpriteFactory.NT_BLACK2,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -300, -150, SpriteFactory.NT_BLACK2,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, 0, 200, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, 0, 200, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, 0, 0, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, 0, -200, SpriteFactory.NT_BLACK2,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, 0, -200, SpriteFactory.NT_BLACK2,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, -75, 150, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -75, 100, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -75, 50, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -75, -150, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -75, -100, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -75, -50, SpriteFactory.NT_BROWN,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, -30, 0, 80,  SpriteFactory.OLIVER,PlayerAnimation.STOP_S, Rol.ESTRELLA));		
		
		return players;
	}
	
	/* SituacionPartido es un snapshot del objeto Partido, que lleva
	 * los jugadores agrupados por amigos/enemigos, y con las coordenadas transformadas
	 * para que siempre estés en el lado izquierdo del campo.
	 */
	public List<Command> getCommands(PartidoSnapshot partido) {
		ArrayList<Command> commands = new ArrayList<Command>();
		Command c = null;
		
		// El que esta mes a prop de la pilota va a buscarla
		playerAfterBall = getNearestPlayerToBall(partido);
		if (playerAfterBall != null && !playerAfterBall.busy) {
			c = getGoToBallCommand(playerAfterBall, partido.ball);
			if (c != null) {
				commands.add(c);
			} else {
				playerAfterBall = null;
			}
		}
	
		//create commands
		for (PlayerSnapshot p : partido.myTeam.players) {
			c = null;
			
			if (p.busy || p == playerAfterBall) {
				//Debug.d("nt","after Ball");
				continue;
			}
			
			if (p.rol == Rol.PORTER) {

				c = getGoalKeeperCommand(p, partido);

				/* no acavo d'entendre la intencio d'aquest codi 
				 * Cuidado pq es MOLT important, que si es crea un Command sempre hem de fer commands.addCommand(), pq sino no es corregeix
				 * en veritat la culpa es del framework, que es massa debil, pero de moment ho haurem de vigilar.
				if(comeBack) {
					c = getClearBallCommand(p, partido);
					if(c != null){
						commands.add(c);
					}
					c = getInitialPositionMovementCommand(p);
					if(c != null) {
						comeBack = false;
					}
				} else {
					c = getJumpCommand(p, partido);
					if(c != null){
						comeBack = true;
					}
				}	
				*/		
				
			} else {
				
				if(canHitBall(p, partido.ball)){
					//Debug.d("nt","hit ball");
					// Xutem a porteria
					c = getHitBallToGoalCommand(p, partido);
					
					// Treiem la pilota de l'area de defensa
					if (c == null) {
						c = getDespejeCommand(p, partido);
					}			
					
					// Passem al mes proper		
					if (c == null) {
						PlayerSnapshot p2 = getNearestPlayerToGoal(p, partido);
						if (p2 != null) {
							c = getPassBallToPlayerCommand(p, p2, partido);
						}
					}
					
					// Controlem la pilota direccio porteria
					if (c == null) {
						c = getControlBallToGoalCommand(p, partido);
					}
				}else{
					//Debug.d("nt","stop");
					// Passejem per la posicio inicial
					c = getTacticMovementCommand(p, partido.ball);
				}
			}
		
			if(c != null){
				commands.add(c);
			}
		}
		return commands;
	}
	
	protected Command getGoalKeeperCommand(PlayerSnapshot goalKeeper, PartidoSnapshot partido){
		Command c = null;
		c = getDespejeCommand(goalKeeper, partido);
			
		if(c==null){
			c = getJumpCommand(goalKeeper, partido);
		}
		
		if(c==null){	
			c = getDefaultPositionMovementCommand(goalKeeper);
		}
		
		return c;
	}
	
	
	protected Command getHitBallToGoalCommand(PlayerSnapshot p, PartidoSnapshot t) {
		return super.getHitBallToGoalCommand(p, t);
	}

	protected Command getPassBallToPlayerCommand(PlayerSnapshot p, PlayerSnapshot p2, PartidoSnapshot t) {
		return super.getPassBallToPlayerCommand(p, p2, t);
	}	

	protected Command getControlBallToGoalCommand(PlayerSnapshot p, BallSnapshot b, PartidoSnapshot t) {
		return super.getControlBallToGoalCommand(p, t);
	}
	
	protected Command getGoToBallCommand(PlayerSnapshot p, BallSnapshot ball){
		return super.getGoToBallCommand(p, ball);
	}	
	
	protected PlayerSnapshot getNearestPlayerToGoal(PlayerSnapshot p, PartidoSnapshot t) {
		PlayerSnapshot nearest = null;
		double distGoal = 0;
		for (PlayerSnapshot p2 : t.myTeam.players) {
			if (canReachPlayer(p, p2)) {
				if (nearest == null || p2.position.distance(field.getGoalPoint()) < distGoal) {
					nearest = p2;
					distGoal = p2.position.distance(field.getGoalPoint());
				}
			}
		}
		
		return nearest;
	}
	
	/*
	protected Command reclaimBall(PlayerSnapshot p, PartidoSnapshot t, Command c) {
		if (playerAfterBall != null && playerAfterBall != p.originalPlayer) {
			// Si estic mes a prop, vaig jo i no ell
			if (p.position.distance(t.ball.position) < playerAfterBall.position.distance(t.ball.position)) {
				// TODO Caldria posar que esborres nomes la de Move, pero com fins ara no es poden tenir 2 alhora en aquesta tactica...
				playerAfterBall.clearPendingCommands();
				playerAfterBall.stopMovement();
			// Sino anulo la meva comanda
			} else {
				// TODO Caldria posar que esborres nomes la de Move, pero com fins ara no es poden tenir 2 alhora en aquesta tactica...
				p.originalPlayer.clearPendingCommands();
				return null;
			}			
		}
		playerAfterBall = p.originalPlayer;
		return c;
	}^
	*/
	
//	private Command getStopCommand(PlayerSnapshot p){
//		StopCommand command = new StopCommand(p.originalPlayer, p.originalPlayer);
//		
//		return command;
//	}

}
