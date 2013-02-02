package com.mel.entityframework;


public class Process implements Comparable<Process>
{
	protected int priority = 0;

	/**
	 * Called just after the system is added to the game, before any calls to the update method.
	 * Override this method to add your own functionality.
	 * 
	 * @param game The game the system was added to.
	 */
	public void onAddToGame(Game game){
		
	}
	
	/**
	 * Called just after the system is removed from the game, after all calls to the update method.
	 * Override this method to add your own functionality.
	 * 
	 * @param game The game the system was removed from.
	 */
	public void onRemoveFromGame(Game game){
		
	}
	
	/**
	 * After the system is added to the game, this method is called every frame until the system
	 * is removed from the game. Override this method to add your own functionality.
	 * 
	 * <p>If you need to perform an action outside of the update loop (e.g. you need to change the
	 * systems in the game and you don't want to do it while they're updating) add a listener to
	 * the game's updateComplete signal to be notified when the update loop completes.</p>
	 * 
	 * @param time The duration, in seconds, of the frame.
	 */
	public void update(){
		
	}

	public int compareTo(Process another) {
		if(this.priority>another.priority){
			return 1;
		}else if(this.priority<another.priority){
			return -1;
		}else{
			return 0;
		}
	}
}
