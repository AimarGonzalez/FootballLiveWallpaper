package com.mel.wallpaper.football.view;

import org.andengine.entity.IEntity;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.shape.Shape;
import org.andengine.opengl.shader.PositionColorShaderProgram;
import org.andengine.opengl.vbo.IVertexBufferObject;

import android.util.FloatMath;

import com.mel.util.Point;


public class Position extends Shape
{
	
	public Position(float x, float y){		
		super(x, y, PositionColorShaderProgram.getInstance());		
	}
	
	public Position(Point p){		
		super(p.getX(), p.getY(), PositionColorShaderProgram.getInstance());		
	}
	
	
	public float distance(Position p) {
		float px = p.getX() - this.getX();
		float py = p.getY() - this.getY();
		return (float)FloatMath.sqrt(px * px + py * py);
	}

	public float distance(Point p) {
		float px = p.getX() - this.getX();
		float py = p.getY() - this.getY();
		return (float)FloatMath.sqrt(px * px + py * py);
	}
	 
	public void setLocation(float x, float y) {
	    this.setPosition(x,y);
	}
	
	public void setLocation(IEntity otherEntity) {
	    this.setPosition(otherEntity);
	}
	
	public Point toPoint(){
		return new Point(this.getX(), this.getY());
	}
	
	public boolean collidesWith(IShape pOtherShape) {
		// TODO Auto-generated method stub
		return false;
	}

	public IVertexBufferObject getVertexBufferObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean contains(float pX, float pY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onUpdateVertices() {
		// TODO Auto-generated method stub
		
	}
}
