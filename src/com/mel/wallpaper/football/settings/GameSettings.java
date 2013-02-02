package com.mel.wallpaper.football.settings;

public class GameSettings
{
	
	public static final String SPLASH_KEY = "splash_enabled";
	public static final String MUSIC_KEY = "music_enabled";
	public static final String GODSFINGER_KEY = "godsFinger_enabled";
	
	public static String PREFERENCES_ID = "mel.football.preferences";
	
	
	public boolean splashEnabled;
	public boolean musicEnabled;
	public boolean godsFingerEnabled;
	public boolean loadingScreenEnabled = false;
	
	
	private static GameSettings instance;
	
	public static GameSettings getInstance(){
		if(instance == null){
			instance = new GameSettings();
		}
		
		return instance;
	}
	
	private GameSettings(){
		
	}
}
