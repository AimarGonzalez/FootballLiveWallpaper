package com.mel.wallpaper.football.entity.tacticas;

import java.util.ArrayList;
import java.util.List;

import org.andengine.util.debug.Debug;

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

public class TacticaMuppet extends Tactica
{
	private PlayerSnapshot playerAfterBall;
	
	public TacticaMuppet(Field field) {
		super(field);
		playerAfterBall = null;
	}

	public List<Player> getInitialPlayers(int factor) {
		List<Player> players = new ArrayList<Player>();
		

		PlayerAnimation porteroInitialAnimation = null;
		if(factor == 1){
			porteroInitialAnimation = PlayerAnimation.STOP_E;
		}else{
			porteroInitialAnimation = PlayerAnimation.STOP_W;
		}		
		
		players.add(new GoalKeeper(players.size()+1, -470, 0,	SpriteFactory.EDWARNER,porteroInitialAnimation));
		
		players.add(new Player(players.size()+1, -250, 150, 	SpriteFactory.MP_SKINHEAD,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -280, 0, 		SpriteFactory.MP_WHITE,PlayerAnimation.STOP_S, Rol.DEFENSA));
		players.add(new Player(players.size()+1, -250, -150,	SpriteFactory.MP_BLACK,PlayerAnimation.STOP_S, Rol.DEFENSA));
		
		players.add(new Player(players.size()+1, 0, 220, 		SpriteFactory.MP_WHITE,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, 0, 0, 			SpriteFactory.MP_WHITE,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		players.add(new Player(players.size()+1, 0, -220, 		SpriteFactory.MP_BLACK,PlayerAnimation.STOP_S, Rol.MIGCAMPISTA));
		
		players.add(new Player(players.size()+1, 220, 170, 		SpriteFactory.MP_BLACK,PlayerAnimation.STOP_S, Rol.DAVANTER));
		players.add(new Player(players.size()+1, 220, -170, 	SpriteFactory.MP_SKINHEAD,PlayerAnimation.STOP_S, Rol.DAVANTER));
		
		players.add(new Player(players.size()+1, 240, 0, 80, 	SpriteFactory.MARC,PlayerAnimation.STOP_S, Rol.ESTRELLA));
		
		return players;
	}
	
	/* SituacionPartido es un snapshot del objeto Partido, que lleva
	 * los jugadores agrupados por amigos/enemigos, y con las coordenadas transformadas
	 * para que siempre estés en el lado izquierdo del campo.
	 */
	public List<Command> getCommands(PartidoSnapshot partido) {
		ArrayList<Command> commands = new ArrayList<Command>();
		Command c = null;
		
		//PlayerSnapshot ballOwner = getNearestOfBothTeamsPlayerToBall(partido);
		
		// El que esta mes a prop de la pilota va a buscarla
		playerAfterBall = getNearestPlayerToBall(partido);
				
		if (playerAfterBall != null && !playerAfterBall.busy && !canHitBall(playerAfterBall, partido.ball)) {
			c = getGoToBallCommand(playerAfterBall, partido.ball);	
			if (c != null) {
				Debug.d("tactica", playerAfterBall.dorsal+" - gotoBall");
				commands.add(c);
			} else {
				playerAfterBall = null;
			}
		}else{
			playerAfterBall = null;
		}
	
		//create commands
		for (PlayerSnapshot p : partido.myTeam.players) {
			c = null;
			
			if (p == playerAfterBall) {
				//Debug.d("mp","after Ball");
				continue;
			}
			
			if (p.rol == Rol.PORTER) {
				
				c = getGoalKeeperCommand(p, partido);

				/* no acavo d'entendre la intencio d'aquest codi 
				 * Cuidado pq es MOLT important, que si es crea un Command sempre hem de fer commands.addCommand(), pq sino no es corregeix
				 * en veritat la culpa es del framework, que es massa debil, pero de moment ho haurem de vigilar.
				if(comeBack){
					c = getClearBallCommand(p, partido);
					if(c != null){
						commands.add(c); //WARN AG: aixo pot fer que creem 2 commands per aquest jugador, un aqui i l'altre al final de 'for'. Bueno potser no pasa res.. s'executara el primer i ja esta... 
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
					//Debug.d("mp","hit ball");
					
					// Controlem la pilota direccio porteria
					
					
					// Xutem a porteria (nomes si ets Mark Lenders)
					if (p.rol == Rol.ESTRELLA) {				
						c = getHitBallToGoalCommand(p, partido);
					}
					
					// Treiem la pilota de l'area de defensa
					if (c == null) {
						c = getDespejeCommand(p, partido);
					}
					
					
//					// firstControl is mandatory --> 
//					if(c == null && !firstControlDone){
//						firstControlDone = true;
//						c = getControlBallToGoalCommand(p, partido);
//					}
					
					// Passem al mes proper a la porteria (si no ets Mark Lenders)
					if (c == null && p.rol != Rol.ESTRELLA) {
						PlayerSnapshot p2 = getNearestPlayerToGoal(p, partido);
						if (p2 != null) {
							c = getPassBallToPlayerCommand(p, p2, partido);
						}
					}
					
					// Controlem la pilota direccio porteria
					if (c == null) {
						c = getControlBallToGoalCommand(p, partido);
					}
					
				}else if(!p.busy){
					
					//Debug.d("mp","stop");
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
	
	// Retorna Mark Lenders if possible
	protected PlayerSnapshot getNearestPlayerToGoal(PlayerSnapshot p, PartidoSnapshot t) {
		PlayerSnapshot nearest = null;
		double distGoal = 0;
		for (PlayerSnapshot p2 : t.myTeam.players) {
			if (canReachPlayer(p, p2) && p!=p2) {
				if (p2.rol == Rol.ESTRELLA){ 
					return p2;
					
				}else if(p2.position.getX()>p.position.getX()){
				
					if (nearest == null || p2.position.distance(field.getGoalPoint()) < distGoal) {
						nearest = p2;
						distGoal = p2.position.distance(field.getGoalPoint());
					}
				}
			}
		}

		return nearest;
	}
	
//	private Command getStopCommand(PlayerSnapshot p){
//		StopCommand command = new StopCommand(p.originalPlayer, p.originalPlayer);
//		
//		return command;
//	}

}
