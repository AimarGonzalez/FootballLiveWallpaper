package com.mel.entityframework;

import com.mel.util.Point;
import com.mel.wallpaper.football.view.Position;

public interface IMovable
{
	public Position getPosition();
	
	public float getSpeed();
	
	public void goTo(Point destination);
	
	public void endMovement();

	public void forceStopMovement();
	
	public void removeOldMovementOrders();
	
}
