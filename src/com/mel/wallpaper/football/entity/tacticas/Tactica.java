package com.mel.wallpaper.football.entity.tacticas;

import java.util.List;

import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import com.mel.util.MathUtil;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.entity.Player.Rol;
import com.mel.wallpaper.football.entity.commands.BallSnapshot;
import com.mel.wallpaper.football.entity.commands.Command;
import com.mel.wallpaper.football.entity.commands.ControlBallCommand;
import com.mel.wallpaper.football.entity.commands.DespejeBallCommand;
import com.mel.wallpaper.football.entity.commands.HitBallCommand;
import com.mel.wallpaper.football.entity.commands.JumpCommand;
import com.mel.wallpaper.football.entity.commands.MoveCommand;
import com.mel.wallpaper.football.entity.commands.PartidoSnapshot;
import com.mel.wallpaper.football.entity.commands.PassBallCommand;
import com.mel.wallpaper.football.entity.commands.PlayerSnapshot;

public abstract class Tactica
{
	
	protected Field field;
	public Tactica(Field field){
		this.field = field;
	}
	
	public abstract List<Player> getInitialPlayers(int factor);
	/* SituacionPartido es un snapshot del objeto Partido, que lleva
	 * los jugadores agrupados por amigos/enemigos, y con las coordenadas transformadas
	 * para que siempre estés en el lado izquierdo del campo.
	 */
	public abstract List<Command> getCommands(PartidoSnapshot partido);
	
	protected boolean canHitBall(PlayerSnapshot p, BallSnapshot b) {
		return p.canShoot && p.position.distance(b.position) < Ball.HIT_DISTANCE;
	}

	protected boolean canJumpToBall(PlayerSnapshot p, BallSnapshot b) {
		return !p.busy && p.position.distance(b.position) < Ball.JUMP_DISTANCE && !pelotaVienePorElCentro(p,b);
	}
	
	protected boolean pelotaVienePorElCentro(PlayerSnapshot p, BallSnapshot b) {
		return Math.abs(p.position.getX()-b.position.getX()) < 10;
	}
	
	protected boolean canReachGoal(PlayerSnapshot p) {
		return p.position.distance(this.field.getGoalPoint()) < Ball.MAX_REACH_DISTANCE-100f;
	}
	
	protected boolean canReachPlayer(PlayerSnapshot p, PlayerSnapshot p2) {
		if(p == p2){
			return false; //no puedes pasartela a ti mismo!
		}

		double distancia = p.position.distance(p2.position);
		return (distancia < Ball.MAX_REACH_MATE_DISTANCE && distancia > Ball.MIN_REACH_DISTANCE);
	}
	
	protected PlayerSnapshot getNearestPlayerToBall(PartidoSnapshot t) {
		PlayerSnapshot nearest = null;
		double minDistance = 0;
		
		Point futureBallPosition = getFutureBallPosition(t.ball, null);
		
		for (PlayerSnapshot p : t.myTeam.players) {
			if (nearest == null || p.position.distance(futureBallPosition) < minDistance) {
				if (p.rol == Rol.PORTER) continue;
				nearest = p;
				minDistance = p.position.distance(futureBallPosition);
			}
		}
		
		return nearest;
	}
	
	protected PlayerSnapshot getNearestOfBothTeamsPlayerToBall(PartidoSnapshot t) {
		PlayerSnapshot nearest = null;
		double minDistance = 0;
		for (PlayerSnapshot p : t.myTeam.players) {
			if (nearest == null || p.position.distance(t.ball.position) < minDistance) {
				if (p.rol == Rol.PORTER) continue;
				nearest = p;
				minDistance = p.position.distance(t.ball.position);
			}
		}
		
		for (PlayerSnapshot p : t.enemyTeam.players) {
			if (nearest == null || p.position.distance(t.ball.position) < minDistance) {
				if (p.rol == Rol.PORTER) continue;
				nearest = p;
				minDistance = p.position.distance(t.ball.position);
			}
		}
		
		return nearest;
	}	
	
	protected Point getTacticPosition(PlayerSnapshot p, BallSnapshot ball) {
		Point tacticPos = new Point(p.defaultPosition.getX()+ball.position.getX()*0.2f, p.defaultPosition.getY());
		tacticPos =  MathUtil.getRandomPointFromArea(tacticPos, 50f);
		return tacticPos;
	}
	
	protected Command getHitBallToGoalCommand(PlayerSnapshot p, PartidoSnapshot t) {
		HitBallCommand command = null;
		if (canHitBall(p, t.ball) && canReachGoal(p)) {
			command = new HitBallCommand(p, t.ball);
			System.out.printf(this.field.getGoalPoint().toString());
			command.destination = MathUtil.getFurtherPoint(p.position, this.field.getGoalPoint(), 2f);
		}
		return command;
	}
	
	protected Command getPassBallToPlayerCommand(PlayerSnapshot p, PlayerSnapshot p2, PartidoSnapshot t) {
		PassBallCommand command = null;
		if (canHitBall(p, t.ball) && canReachPlayer(p, p2)) {
			Debug.d("tactica","passCommand");
			command = new PassBallCommand(p, t.ball, p2);
			Point destino = p2.position.clone();
			
			float distance = p.position.distance(p2.position);
			if(destino.getX()>0){
				destino.setX(Math.min(destino.getX() + distance*0.2f, Field.rightWall-50));
			}
			
			command.destination = MathUtil.getFurtherPoint(p.position, destino, 1.1f);
		}
		return command;
	}	
	
	protected Command getControlBallToGoalCommand(PlayerSnapshot p, PartidoSnapshot t) {
		ControlBallCommand command = null;
		if (canHitBall(p, t.ball)) {
			command = new ControlBallCommand(p, t.ball);
			Point porteria = this.field.getGoalPoint();
			float direccio = MathUtil.getAngulo(p.position.getX(), p.position.getY(), porteria.getX(), porteria.getY());
			command.destination = MathUtil.getPuntDesti(p.position, direccio, p.speed); //usamos speed como distancia, pq se trata de llevar la pelota "pegada a los pies"
		}
		return command;
	}
	
	protected Command getGoToBallCommand(PlayerSnapshot p, BallSnapshot ball) {
		MoveCommand command = null;
		if (!Field.isOutField(ball.position) && !canHitBall(p, ball)) {
			command = new MoveCommand(p);
			command.destination = getFutureBallPosition(ball, p);
		}
		return command;
	}
	
	protected Point getFutureBallPosition(BallSnapshot ball, PlayerSnapshot p){
		Point futurePosition = null;
		if(ball.destination == null){
			return ball.position.clone();
		}else{
			futurePosition = MathUtil.getFurtherPoint(ball.position, ball.destination, 0.4f);
			if(p != null && p.position.distance(futurePosition) < 20){
				return ball.position.clone(); //ya ha llegado a linea de corte, ves a por la bola.
			}else{
				return futurePosition;
			}
		}
	}
	
	protected Command getTacticMovementCommand(PlayerSnapshot p, BallSnapshot b) {
		if (!p.busy && p.destination == null) {
			if (p.position.distance(p.defaultPosition)>60 || MathUtils.random(0, 100) <= 2) {
				MoveCommand mc = new MoveCommand(p);
				mc.destination = getTacticPosition(p, b);
				return mc;
			} else {
				//simplement no hacer nada...
				//return new StopCommand(p); 
			}
		}
		return null;
	}
	
	protected Command getDefaultPositionMovementCommand(PlayerSnapshot p) {
		MoveCommand command = null;
		if (!p.busy && p.destination == null && !p.isAtDefaultPosition()) {
			command = new MoveCommand(p);
			command.destination = p.defaultPosition;
		}
		return command;
	}
	
	protected Command getHitBallToRandomPositionCommand(PlayerSnapshot p, PartidoSnapshot t){
		HitBallCommand command = null;
		if(canHitBall(p, t.ball) && Field.isDefensiveField(t.ball.position)) {
			command = new HitBallCommand(p, t.ball);
			command.destination = this.field.getRandomPoint(40);
		}
		return command;
	}	
	
	protected Command getDespejeCommand(PlayerSnapshot p, PartidoSnapshot t){
		HitBallCommand command = null;
		if(canHitBall(p, t.ball) && Field.isDefensiveField(t.ball.position)) {
			Debug.d("tactica","despejeCommand");
			command = new DespejeBallCommand(p, t.ball);
			command.destination = this.field.getRandomPoint(300,40,40,40);
		}
		return command;
	}	
	
	protected Command getJumpCommand(PlayerSnapshot p, PartidoSnapshot t) {
		JumpCommand command = null;
		if (canJumpToBall(p, t.ball)) {
			command = new JumpCommand(p);
			command.destination = t.ball.position;
		}
		return command;
	}
	
/*	
	private Command getJumpCommand(PlayerSnapshot p, Point destination){
		JumpCommand command = null;
		if(p.busy==false && p.destination == null){
			command = new JumpCommand(p);
			command.destination = destination;
		}
		return command;
	}
	*/
	
	
}
