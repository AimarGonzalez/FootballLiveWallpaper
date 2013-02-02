package com.mel.wallpaper.football.process;

import java.util.List;

import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.ease.EaseSineOut;

import com.mel.entityframework.Game;
import com.mel.entityframework.Process;
import com.mel.util.MathUtil;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.commands.MoveCommand;



public class BallPhisicsProcess extends Process
{
	private Partido partido;
	private List<Ball> balls;
	private List<GoalKeeper> porteros;
	
	public BallPhisicsProcess(Partido partido){
		
	}
	
	@Override
	public void onAddToGame(Game game){
		this.porteros = (List<GoalKeeper>) game.getEntities(GoalKeeper.class);
		this.balls = (List<Ball>) game.getEntities(Ball.class);
		this.partido = (Partido)game.getEntity(Partido.class);
	}
	
	@Override
	public void onRemoveFromGame(Game game){
		if(porteros != null){
			porteros.clear();
			porteros = null;
		}
		
		if(balls != null){
			balls.clear();
			balls = null;
		}
		
		this.partido = null;
	}
	
	@Override
	public void update(){
		
		checkPorteroDetieneBalones();
		
		checkBallsCollisions();
		
	}
	
	
	private void checkPorteroDetieneBalones() {
		for(Ball ball : this.balls){
			checkPorteroDetieneBalon(ball);
		}
	}
	
	private void checkPorteroDetieneBalon(Ball ball) {
		// Per parar la pilota quan el porter la toca
		for(GoalKeeper portero : this.porteros){
			tryToStopBall(ball, portero);
		}
	}

	private void tryToStopBall(Ball ball, GoalKeeper porter0) {
		// DEBUG
		//if(porter0.canStopBall(this.partido)){
		//	Debug.d("portero", "porter0.getIsOnJumpingCooldown()="+porter0.getIsOnJumpingCooldown()+", porter0.getIsOnStopBallCooldown()="+porter0.getIsOnStopBallCooldown());
		//}
		if (porter0.getIsOnJumpingCooldown() && !porter0.getIsOnStopBallCooldown() && porter0.canStopBall(this.partido)) { //canStopBall() no funciona, pq Sprite.contains() tampoco!!!!
			//Debug.d("portero", "readyToStop!");
			porter0.stopBall();
			if (MathUtils.random(0, 100) < 85) { //el portero puede fallar también :P
				ball.forceStopMovement();
			}
		}
	}
	
	private void checkBallsCollisions(){
		for(Ball ball : this.balls){
			checkBallCollides(ball);
		}
	}
	
	private void checkBallCollides(Ball ball){
		if(ball.destination == null || !Field.isOutGoalLine(ball.position.toPoint())){
			return;
		}
		
		Point newDestination = null;
		float distP = calcDistanceToPoste(ball);
		
		if(distP<10){
			Debug.d("colision", "no es possible!!");
			
		}else if(collidesWithHorizontalNet(ball)){
			Debug.d("colision", "colision Horizontal!!");
			newDestination = new Point(ball.destination.getX(), ball.position.getY()-(ball.destination.getY()-ball.position.getY()));
			newDestination = MathUtil.getFurtherPoint(ball.position.toPoint(), newDestination, 0.15f, Field.goalWidth-25);
			ball.startCollisionHorizontal();
			
		}else if(collidesWithVerticalNet(ball)){
			Debug.d("colision", "colision Vertical!!");
			newDestination = new Point(ball.position.getX()-(ball.destination.getX()-ball.position.getX()), ball.destination.getY());
			newDestination = MathUtil.getFurtherPoint(ball.position.toPoint(), newDestination, 0.15f, Field.goalWidth-25);
			ball.startCollisionVertical();
		}
		
		if(newDestination != null){
			ball.forceStopMovement();
			MoveCommand mc = new MoveCommand(null, ball, 0.5f, EaseSineOut.getInstance());
			mc.destination = newDestination;
			mc.execute(this.partido);
		}
	}
	
	private float calcDistanceToPoste(Ball ball){
		return 999;
	}
	
	private boolean collidesWithHorizontalNet(Ball ball){
		if(ball.isOnHColisionCooldown) return false;
		
		float distHN = calcDistanceHorizontalNet(ball);
		if(MathUtils.isInBounds(Field.leftGoalEnd, Field.leftWall, ball.position.getX()) 
				&& Math.abs(distHN) < 5f){
			return true;
		}
		
		if(MathUtils.isInBounds(Field.rightWall, Field.rightGoalEnd, ball.position.getX()) 
				&& Math.abs(distHN) < 5f){
			return true;
		}
		
		return false;
	}

	
	private boolean collidesWithVerticalNet(Ball ball){
		if(ball.isOnVColisionCooldown) return false;
		
		float distVN = calcDistanceVerticalNet(ball);
		if(MathUtils.isInBounds(Field.bottomGoal, Field.topWall, ball.position.getY()) 
				&& Math.abs(distVN) < 5f){
			return true;
		}
		
		if(MathUtils.isInBounds(Field.bottomGoal, Field.topWall, ball.position.getY()) 
				&& Math.abs(distVN) < 5f){
			return true;
		}
		
		return false;
	}
	
	
	
	private float calcDistanceHorizontalNet(Ball ball){
		float distTopNet = ball.position.getY()-Field.topGoal;
		float distBotNet = ball.position.getY()-Field.bottomGoal;
		
		if(Math.abs(distTopNet) < Math.abs(distBotNet)){
			return distTopNet;
		}else{
			return distBotNet;
		}
	}
	
	private float calcDistanceVerticalNet(Ball ball){
		float distLeftNet = ball.position.getX()-(Field.leftGoalEnd);
		float distRightNet = ball.position.getX()-(Field.rightGoalEnd);
		
		if(Math.abs(distLeftNet) < Math.abs(distRightNet)){
			return distLeftNet;
		}else{
			return distRightNet;
		}
	}
	
}
