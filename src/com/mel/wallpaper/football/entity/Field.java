package com.mel.wallpaper.football.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.math.MathUtils;

import com.mel.util.Point;
import com.mel.wallpaper.football.view.Position;

public class Field
{
	public Sprite background;
	public float width;
	public float height;
	
	
	public float paddingLeft;
	public float paddingRight;
	public float paddingTop;
	public float paddingBottom;
	public static  float correccionCampoX;
	public static  float correccionCampoY;
	public static float rightWall;
	public static float leftWall;
	public static float topWall;
	public static float bottomWall;
	
	public static float topGoal;
	public static float bottomGoal;
	public static float goalWidth;
	public static float leftGoalEnd;
	public static float rightGoalEnd;
	
	public Field(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom, Sprite background){
		this.background = background;

		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		
		this.width = background.getWidth()-(paddingLeft+paddingRight);
		this.height = background.getHeight()-(paddingTop+paddingBottom);
		Field.correccionCampoX = paddingLeft+this.width/2;
		Field.correccionCampoY = paddingTop+this.height/2;
		Field.rightWall = this.width/2;
		Field.leftWall = -this.width/2;
		Field.topWall = this.height/2;
		Field.bottomWall = -this.height/2;
	}
	
	public Point getRandomPoint() {
		return getRandomPoint(0);
	}
	
	public Point getRandomPoint(float margin) {
		return getRandomPoint(margin, margin, margin, margin);
	}
	
	public Point getRandomPoint(float leftMargin, float rightMargin, float topMargin, float bottomMargin) {
		return new Point(MathUtils.random(leftWall+leftMargin, rightWall-rightMargin), MathUtils.random(bottomWall+topMargin, topWall-bottomMargin));
	}
	
	public Point getDefensePoint() {
		return new Point(MathUtils.random(leftWall, 0), MathUtils.random(bottomWall, topWall));
	}
	
	public Point getOffensePoint() {
		return new Point(MathUtils.random(0, rightWall), MathUtils.random(bottomWall, topWall));
	}
	
	public Point getRandomPointOut() {
		return new Point(MathUtils.random(leftWall,  rightWall), MathUtils.random(topWall, topWall+100));
	}
	
	public Point getGoalPoint() {
		return new Point(rightWall, 0);
	}

	public Point getLeftGoalPos() {
		return new Point(leftWall, 0);
	}
	public Point getRightGoalPos() {
		return new Point(rightWall, 0);
	}
	
	public static  boolean isOutTouchLine(Point posicio) {
		return (posicio.getY() > topWall || posicio.getY() < bottomWall);
	}
	
	public static boolean isOutGoalLine(Point posicio) {
		return (posicio.getX() > rightWall || posicio.getX() < leftWall);
	}
	
	public static  boolean isOutField(Point posicio) {
		return (isOutTouchLine(posicio) || isOutGoalLine(posicio));
	}
	
	public static  boolean isDefensiveField(Point posicio) {
		return (posicio.getX() < -200 && !isOutField(posicio));
	}

	public static  boolean isOffensiveField(Point posicio) {
		return (posicio.getX() >= 0 && !isOutField(posicio));
	}	
	
	public static  boolean isGoal(Point posicio) {
		return (isInLeftGoal(posicio) || isInRightGoal(posicio));
	}
	
	private static  boolean isInLeftGoal(Point p){
		return (p.getX()<leftWall && p.getX()>leftGoalEnd && p.getY()<topGoal && p.getY()>bottomGoal);
	}
	private static  boolean isInRightGoal(Point p){
		return (p.getX()>rightWall && p.getX()<rightGoalEnd && p.getY()<topGoal && p.getY()>bottomGoal);
	}

	public static  Point cartesianToEngineCoordinates(Position coordenadas){
		return cartesianToEngineCoordinates(coordenadas.toPoint());
	}
	public static  Point cartesianToEngineCoordinates(Point cartesianCoords){
		Point fixedCoord;
		
		float x = correccionCampoX+cartesianCoords.getX();
		float y = correccionCampoY-cartesianCoords.getY();
		
		fixedCoord = new Point(x, y);
		return fixedCoord;
	}
	
	public Point engineToCartesianCoordinates(Point engineCoords){
		Point fixedCoord;
		
		float x = engineCoords.getX()-correccionCampoX;
		float y = correccionCampoY-engineCoords.getY();
		
		fixedCoord = new Point(x, y);
		return fixedCoord;
	}
	
	
}
