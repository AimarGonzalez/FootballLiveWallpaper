package com.mel.wallpaper.football.process;

import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;

import android.content.Context;
import android.widget.Toast;

import com.mel.entityframework.Game;
import com.mel.entityframework.Process;
import com.mel.wallpaper.football.entity.Ball;
import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.GoalKeeper;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Partido.Status;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.settings.GameSettings;
import com.mel.wallpaper.football.timer.TimerHelper;



public class GameProcess extends Process
{
	private Partido partido;
	private Ball ball;
	private List<Player> players;
	
	private Context toastBoard;
	private Engine engine;
	private Scene footballScene;
	private Scene loadingScene;
	
	
	public GameProcess(Engine engine, Scene footballScene, Scene loadingScene){
		this.engine = engine;
		this.footballScene = footballScene;
		this.loadingScene = loadingScene;
	}
	
	@Override
	public void onAddToGame(Game game){
		this.partido = (Partido)game.getEntity(Partido.class);
		this.ball = (Ball)game.getEntity(Ball.class);

		this.players = (List<Player>) game.getEntities(GoalKeeper.class);
		this.players.addAll(game.getEntities(Player.class));
	}
	
	@Override
	public void onRemoveFromGame(Game game){
		if(players != null){
			players.clear();
			players = null;
		}
		
		this.ball = null;
		this.partido = null;
	}
	
	@Override
	public void update(){
		
		switch(this.partido.status){
			
			
			case INITIAL_STATE:
				break;
				
			case INTRO:
				if(this.engine.getScene()!=this.footballScene){
					this.engine.setScene(this.footballScene);
				}
				
				partido.status = Partido.Status.WAIT_A_SECOND;
				TimerHelper.startTimer(this.engine.getScene(), 0.5f,  new ITimerCallback() {                      
		            public void onTimePassed(final TimerHandler pTimerHandler){
		            	partido.status = Partido.Status.RESUME_GAME;
		            }
		        });
				break;
			
			case PAUSE:
				if(GameSettings.getInstance().loadingScreenEnabled && this.engine.getScene()!=this.loadingScene){
					this.engine.setScene(this.loadingScene);
				}
				
				//teleportPlayersToInitialPositions();
				//forceBallToCenter();
				break;
				
			case RESUME_GAME:
				if(this.engine.getScene()!=this.footballScene){
					this.engine.setScene(this.footballScene);
				}
				
				//disimula el tiempo de carga de recursos a GPU
				partido.status = Partido.Status.WAIT_A_SECOND;
				TimerHelper.startTimer(this.engine.getScene(), 1.5f,  new ITimerCallback() {                      
		            public void onTimePassed(final TimerHandler pTimerHandler){
		            	partido.status = Partido.Status.PLAYING;
		            }
		        });
				//partido.status = Partido.Status.PLAYING;
				break;
			
			case PLAYING:
				
				if(isGol()){
					this.partido.status = Partido.Status.GOAL_CINEMATIC;
					break;
				}
				
				if(isBallOutOfBounds()){
					this.partido.status = Partido.Status.PERFORM_OUTSIDE;
					break;
				}
				
				break;
				
			case GOAL_CINEMATIC:
				this.partido.status = Partido.Status.GOTO_INITIAL_POS;
				break;
				
			case PERFORM_OUTSIDE:
				this.partido.status = Partido.Status.GOTO_INITIAL_POS;
				break;
				
			case GOTO_INITIAL_POS:
				if(isBallStopped() && areAllPlayersAtInitialPosition()){
					forceBallToCenter();
					partido.status = Partido.Status.RESUME_GAME;
				}
				break;
			
			
			case WAIT_A_SECOND:
			default:
		}
		
		
	}
	
	private void teleportPlayersToInitialPositions(){
		for(Player player : this.players){
			player.position.setLocation(player.initialPosition.getX(), player.initialPosition.getY());
			player.forceStopMovement();
		}
	}
	
	private boolean isBallStopped(){
		return this.ball.destination==null;
	}
	
	private boolean isGol(){
		return Field.isGoal(this.ball.position.toPoint());
	}

	private boolean isBallOutOfBounds(){
		return Field.isOutField(this.ball.position.toPoint());
	}
	
	
	private boolean areAllPlayersAtInitialPosition(){
		for(Player p : this.players){
			if(!p.isAtInitialPosition()){
				return false;
			}
		}
		return true;
	}
	
	private void forceBallToCenter(){
		this.ball.forceStopMovement();
		this.ball.position.setPosition(0,0);
	}



	
}
