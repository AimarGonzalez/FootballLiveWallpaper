package com.mel.wallpaper.football.timer;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;

public class TimerHelper
{
	public static void startTimer(Entity entity, float delaySecs, ITimerCallback timerCallback ){
		entity.registerUpdateHandler(new TimerHandler(delaySecs, timerCallback));
	}
}
