package com.mel.wallpaper.football.process;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.shape.Shape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import android.content.Context;
import android.widget.Toast;

import com.mel.entityframework.Game;
import com.mel.entityframework.Process;
import com.mel.util.Point;
import com.mel.wallpaper.football.FootballLiveWallpaper;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.entity.Player;
import com.mel.wallpaper.football.settings.GameSettings;
import com.mel.wallpaper.football.view.SpriteFactory;

public class TouchProcess extends Process implements IOnSceneTouchListener
{
	private Partido partido;
	private Scene scene;
	private Context toastBoard;
	private RectangularShape touchMarker;
	private TouchEvent lastTouch;
	
	private int TOUCH_RATIO = 45;
	
	public TouchProcess(Partido partido, Scene scene, Context context){
		this.partido = partido;
		this.scene = scene;
		this.toastBoard = context;
		this.touchMarker = SpriteFactory.getInstance().newBall(6, 6);
		this.touchMarker.setColor(Color.RED);
	}
	
	
	@Override
	public void onAddToGame(Game game){
		//inicializar listeners (touch, accelerometer?, keyboard?)
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setOnSceneTouchListener(this);
	}
	
	@Override
	public void onRemoveFromGame(Game game){
		scene.setTouchAreaBindingOnActionDownEnabled(false);
		scene.setOnSceneTouchListener(null);
		
	}
	
	@Override
	public void update(){
		
		if(this.lastTouch != null){
			processLastTouch(this.lastTouch);
			this.lastTouch = null;
		}
		
	}
	
	
	public boolean processLastTouch(TouchEvent touchEvent) {
		FootballLiveWallpaper flWalpaper = FootballLiveWallpaper.getSharedInstance();
		
		//printTouchDebuger(touchEvent);
		
		if(flWalpaper.isPlayingSplash()){
			flWalpaper.onTapFromGame(touchEvent);
			return true;
		}
		

		if(GameSettings.getInstance().godsFingerEnabled){
			List<Player> touchedPlayers = getPlayersUnderTouch(touchEvent, TOUCH_RATIO);
		
			if(touchedPlayers.size() > 0){
				for(Player p:touchedPlayers){
					p.aplastar();
				}
				return true;
			}
		}
		
		flWalpaper.onTapFromGame(touchEvent);
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
		if(this.touchMarker != null){
			if(!this.touchMarker.hasParent()){
				//partido.field.background.attachChild(this.touchMarker);
				this.scene.attachChild(this.touchMarker);
			}
			
			//float[] pointOnField = partido.field.background.convertSceneToLocalCoordinates(touchEvent.getX(), touchEvent.getY());
			float[] pointOnField = {touchEvent.getX(), touchEvent.getY()};
			
			this.touchMarker.setPosition(pointOnField[0]-touchMarker.getRotationCenterX(), pointOnField[1]-touchMarker.getRotationCenterY());
		}
		
	}
	
	
	
	
	/* TOUCH LISTENERS */
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		pSceneTouchEvent.set(pSceneTouchEvent.getX(), pSceneTouchEvent.getY()+10);
		if(this.partido.status == Partido.Status.PLAYING){
			this.lastTouch = pSceneTouchEvent;
			return true;
		}else{
			//Toast.makeText(this.toastBoard, "Be gentle, let the players catch their breath!", Toast.LENGTH_LONG).show();
			//HAY QUE ENCONTRAR COMO PINTAR EL TEXTO EN PANTALLA!!
			return false;
		}
	}
	
	
	

}
