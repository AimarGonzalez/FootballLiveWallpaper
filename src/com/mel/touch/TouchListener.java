package com.mel.touch;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.Shape;
import org.andengine.input.touch.TouchEvent;

import com.mel.util.Point;
import com.mel.wallpaper.football.FootballLiveWallpaper;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.settings.GameSettings;

public class TouchListener implements IOnSceneTouchListener
{
	private int TOUCH_RATIO = 40;
	
	private Partido partido;
	private Shape touchPoint;
	public TouchListener(Partido partido){
		this.partido = partido;
		
//		touchPoint = SpriteFactory.getInstance().newBall(3, 3);
//		touchPoint.setColor(1, 0, 0);
//		touchPoint.setZIndex(10000);
	}
	
	
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		pSceneTouchEvent.set(pSceneTouchEvent.getX(), pSceneTouchEvent.getY()+10);
		
		FootballLiveWallpaper flWalpaper = FootballLiveWallpaper.getSharedInstance();
		
		printTouchDebuger(pSceneTouchEvent);
		
		if(flWalpaper.isPlayingSplash()){
			flWalpaper.onTapFromGame(pSceneTouchEvent);
			return true;
		}
		

		if(GameSettings.getInstance().godsFingerEnabled){
			List<Player> touchedPlayers = getPlayersUnderTouch(pSceneTouchEvent,TOUCH_RATIO);
		
			if(touchedPlayers.size() > 0){
				for(Player p:touchedPlayers){
					p.aplastar();
				}
				return true;
			}
		}
		
		flWalpaper.onTapFromGame(pSceneTouchEvent);
		return true;
	}
	
	private List<Player> getPlayersUnderTouch(TouchEvent pSceneTouchEvent, int touchRatio){
		ArrayList<Player> touchedPlayers = new ArrayList<Player>();
		Point spriteCenter = null;
		for(Player p:this.partido.teams[0].players){
			spriteCenter = new Point(p.sprite.getSceneCenterCoordinates());
			if(spriteCenter.distance(pSceneTouchEvent.getX(), pSceneTouchEvent.getY()) < TOUCH_RATIO){
				touchedPlayers.add(p);
			}
		}
		
		for(Player p:this.partido.teams[1].players){
			spriteCenter = new Point(p.sprite.getSceneCenterCoordinates());
			if(spriteCenter.distance(pSceneTouchEvent.getX(), pSceneTouchEvent.getY()) < TOUCH_RATIO){
				touchedPlayers.add(p);
			}
		}
		
		return touchedPlayers;
	}
	
	
	private void printTouchDebuger(TouchEvent touchEvent){
		if(this.touchPoint != null){
			if(!this.touchPoint.hasParent()){
				partido.field.background.attachChild(this.touchPoint);
			}
			
			float[] pointOnField = partido.field.background.convertSceneToLocalCoordinates(touchEvent.getX(), touchEvent.getY());
			
			this.touchPoint.setPosition(pointOnField[0]-touchPoint.getRotationCenterX(), pointOnField[1]-touchPoint.getRotationCenterY());
		}
		
	}
	
}
