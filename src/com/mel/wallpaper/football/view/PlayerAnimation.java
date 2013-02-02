package com.mel.wallpaper.football.view;

import com.mel.util.MathUtil;
import com.mel.util.Point;
import com.mel.wallpaper.football.entity.Player;

public enum PlayerAnimation {
	RUN_N, RUN_S, RUN_E, RUN_W, 
	STOP_N, STOP_S, STOP_E, STOP_W,
	SEG_N, SEG_S, SEG_E, SEG_W,
	PAS_N, PAS_S, PAS_E, PAS_W,
	SHOOT_N, SHOOT_S, SHOOT_E, SHOOT_W,
	APLASTADO,
	JUMP_NE,JUMP_NW,JUMP_SE,JUMP_SW
	;
	

	public static PlayerAnimation calculateStopAnimation(PlayerAnimation lastAnimation){
		switch(lastAnimation) {
			case RUN_E: //derecha
				return PlayerAnimation.STOP_E;
			case RUN_W: //izquierda
				return PlayerAnimation.STOP_W; 
			case RUN_N: //arriba
				return PlayerAnimation.STOP_N;
			case RUN_S: //abajo
				return PlayerAnimation.STOP_S; 
			
			default: //parado_s
				return PlayerAnimation.STOP_S;
		}
	}
	
	public static PlayerAnimation calculatePassAnimation(Player p, Point destination){
		double angulo = MathUtil.getAngulo(p.position.getX(), p.position.getY(), destination.getX(), destination.getY());
		
		if(angulo>=0 && angulo<MathUtil.PI_Q){
			return PlayerAnimation.PAS_E;
		}
		
		if(angulo>=MathUtil.PI_Q && angulo<3*MathUtil.PI_Q){
			return PlayerAnimation.PAS_N;
		}
		
		if(angulo>=3*MathUtil.PI_Q && angulo<5*MathUtil.PI_Q){
			return PlayerAnimation.PAS_W;
		}
		
		if(angulo>=5*MathUtil.PI_Q && angulo<7*MathUtil.PI_Q){
			return PlayerAnimation.PAS_S;
		}
		
		if(angulo>=7*MathUtil.PI_Q && angulo<MathUtil.PI_TWICE){
			return PlayerAnimation.PAS_E;
		}
		
		
		throw new RuntimeException("Error calculando angulo!");
	}
	
	public static PlayerAnimation calculateShootAnimation(Player p, Point destination){
		double angulo = MathUtil.getAngulo(p.position.getX(), p.position.getY(), destination.getX(), destination.getY());
		
		if(angulo>=0 && angulo<MathUtil.PI_Q){
			return PlayerAnimation.SHOOT_E;
		}
		
		if(angulo>=MathUtil.PI_Q && angulo<3*MathUtil.PI_Q){
			return PlayerAnimation.SHOOT_N;
		}
		
		if(angulo>=3*MathUtil.PI_Q && angulo<5*MathUtil.PI_Q){
			return PlayerAnimation.SHOOT_W;
		}
		
		if(angulo>=5*MathUtil.PI_Q && angulo<7*MathUtil.PI_Q){
			return PlayerAnimation.SHOOT_S;
		}
		
		if(angulo>=7*MathUtil.PI_Q && angulo<MathUtil.PI_TWICE){
			return PlayerAnimation.SHOOT_E;
		}
		
		
		throw new RuntimeException("Error calculando angulo!");
	}
	
	public static PlayerAnimation calculateRunAnimation(Player p, Point destination){
		double angulo = MathUtil.getAngulo(p.position.getX(), p.position.getY(), destination.getX(), destination.getY());
		
		if(angulo>=0 && angulo<MathUtil.PI_Q){
			return PlayerAnimation.RUN_E;
		}
		
		if(angulo>=MathUtil.PI_Q && angulo<3*MathUtil.PI_Q){
			return PlayerAnimation.RUN_N;
		}
		
		if(angulo>=3*MathUtil.PI_Q && angulo<5*MathUtil.PI_Q){
			return PlayerAnimation.RUN_W;
		}
		
		if(angulo>=5*MathUtil.PI_Q && angulo<7*MathUtil.PI_Q){
			return PlayerAnimation.RUN_S;
		}
		
		if(angulo>=7*MathUtil.PI_Q && angulo<MathUtil.PI_TWICE){
			return PlayerAnimation.RUN_E;
		}
		
		return PlayerAnimation.STOP_S; //OJO!!
		//throw new RuntimeException("Error calculando angulo!");
	}
}
