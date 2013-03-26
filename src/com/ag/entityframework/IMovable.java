package com.ag.entityframework;

import com.ag.util.Point;
import com.ag.wallpaper.football.view.Position;

public interface IMovable
{
	public Position getPosition();
	
	public float getSpeed();
	
	public void goTo(Point destination);
	
	public void endMovement();

	public void forceStopMovement();
	
	public void removeOldMovementOrders();
	
}
