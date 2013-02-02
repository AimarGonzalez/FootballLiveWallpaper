package com.mel.util;

import com.mel.wallpaper.football.view.Position;


public class Point
{
	private float x;
	private float y;
	
	
	
	public float getX() {
		return x;
	}
	public void setX(float x) {	
		this.x = x;
	}
	public float getY() {	
		return y;
	}
	public void setY(float y) {		
		this.y = y;
	}
	
	public Point(float x, float y){		
		this.x = x;
		this.y = y;
	}
	
	public Point(float[] coords){		
		this.x = coords[0];
		this.y = coords[1];
	}
	
	public float distance(float px, float py) {
		px -= getX();
		py -= getY();
		return (float)Math.sqrt(px * px + py * py);
	}
	
	public void setLocation(float px, float py) {		
		this.x = px;
		this.y = py;
	}
	
	/**
	 * Returns the distance from this <code>Point2D</code> to a
	 * specified <code>Point2D</code>.
	 *
	 * @param pt the specified point to be measured
	 *           against this <code>Point2D</code>
	 * @return the distance between this <code>Point2D</code> and
	 * the specified <code>Point2D</code>.
	 * @since 1.2
	 */
	public float distance(Point pt) {
		float px = pt.getX() - this.getX();
		float py = pt.getY() - this.getY();
		return (float) Math.sqrt(px * px + py * py);
	}
	
	public float distance(Position pos) {
		return distance(pos.toPoint());
	}
	
	public Point clone(){		
		return new Point(this.x, this.y);
	}
	
	public boolean equals(Point p){
		return (this.getX() == p.getX() && this.getY() == p.getY());
	}
	
	public String toString(){
		return "["+this.getX()+","+this.getY()+"]";
	}
	
	
}
