package com.mel.wallpaper.football.view;

import com.mel.util.Point;
import com.mel.wallpaper.football.entity.GoalKeeper;

public class GoalKeeperAnimation {
	
	
	public static PlayerAnimation calculatePassAnimation(PlayerAnimation initialAnimation){
		
		switch (initialAnimation) {
			case STOP_E:
				return PlayerAnimation.PAS_E;
			case STOP_W:
				return PlayerAnimation.PAS_W;
			default:
				return initialAnimation;
		}
		
	}
	
	public static PlayerAnimation calculateShootAnimation(PlayerAnimation initialAnimation){
		switch (initialAnimation) {
			case STOP_E:
				return PlayerAnimation.SHOOT_E;
			case STOP_W:
				return PlayerAnimation.SHOOT_W;
			default:
				return initialAnimation;
		}
		
	}
	public static PlayerAnimation calculateJumpAnimation(GoalKeeper gk, Point destination, PlayerAnimation initialAnimation){
		float direction = destination.getY() - gk.position.getY();
		switch (initialAnimation) {
			case STOP_E:
				if(direction > 0){
					return PlayerAnimation.JUMP_NE;
				}else if(direction < 0){
					return PlayerAnimation.JUMP_SE;
				}
				break;
			case STOP_W:
				if(direction > 0){
					return PlayerAnimation.JUMP_NW;
				}else if(direction < 0){
					return PlayerAnimation.JUMP_SW;
				}
				break;
			default:
				return initialAnimation;
		}
		
		return initialAnimation;
	}
	
	
	
	
}
