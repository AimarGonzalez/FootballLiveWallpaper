package com.mel.wallpaper.football.entity.commands;


import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.mel.util.MathUtil;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Partido;

public class JumpCommand extends Command
{
	public Point destination;
	protected Point finalDestination;
	public GoalKeeper goalKeeper;
	public IEaseFunction easeFunction = EaseLinear.getInstance();
	
	
	public JumpCommand(PlayerSnapshot player) {
		this((GoalKeeper)player.originalPlayer);
	}
		
	
	public JumpCommand(GoalKeeper player) {
		super(player);
		this.goalKeeper = player;
	}
	
	@Override
	public void execute(Partido p) {
		this.goalKeeper.forceStopMovement();
		
		moveObject();
		
		this.goalKeeper.jump(this.finalDestination);
	}

	protected void moveObject() {
		Point origen = (Point)this.goalKeeper.getPosition().toPoint();
		this.finalDestination = fixDestinationCoordenates(this.destination);
		
		float angle = MathUtil.getAngulo(origen.getX(), origen.getY(), this.finalDestination.getX(), this.finalDestination.getY());
		
		float distance =  origen.distance(this.finalDestination);
		float jumpDistance =  distance/2f;
		if(distance > GoalKeeper.MAX_JUMP_DISTANCE){
			distance = GoalKeeper.MAX_JUMP_DISTANCE;
		}
		
		this.finalDestination.setX(origen.getX()+(float)Math.cos(angle)*jumpDistance-factor*distance/2f); //hacemos que se tire un poco delante de la pelota
		this.finalDestination.setY(origen.getY()+(float)Math.sin(angle)*jumpDistance);
		
		if(origen.distance(this.finalDestination) > GoalKeeper.MAX_JUMP_DISTANCE){
			Debug.d("jump eeeerrooorrr!!");
		}
	}
	
	
	protected Point fixDestinationCoordenates(Point destination){
		//Este metodo solo se puede usar dentro de executeCommand(), NUNCA ANTES pq no tendrias el factor!
		Point fixedDestination = (Point)destination.clone();
		fixedDestination.setLocation(fixedDestination.getX()*factor, fixedDestination.getY()*factor);//aplicamos factor de inversion de campo (-1 o 1)
		return fixedDestination;
	}
	
	
	
}
