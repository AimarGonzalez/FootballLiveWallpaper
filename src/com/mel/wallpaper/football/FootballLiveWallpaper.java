package com.mel.wallpaper.football;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.BaseGameWallpaperService;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.debug.Debug.DebugLevel;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseSineOut;

import android.util.DisplayMetrics;
import android.widget.Toast;

import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.settings.GameSettings;
import com.mel.wallpaper.football.timer.TimerHelper;

public class FootballLiveWallpaper extends BaseGameWallpaperService implements IUpdateHandler
{
	//android.os.Debug.waitForDebugger(); 

	public static final int CAMERA_WIDTH = 480;
	public static final int CAMERA_HEIGHT = 800;
	public static final int NOTIFICATION_BAR_HEIGHT = 0;
	
	public static String PREFERENCES_ID = "mel.football.preferences";

	
	// ===========================================================
	// Fields
	// ===========================================================
	private static FootballLiveWallpaper instance;
	
	
	private FootballGame game;
	Scene footballScene;
	
	ArrayList<Splash> splashes;
	Splash currentSplash;
	int splashCount = 0;
	int fadeOutSpeed = 1;
	
	Sound benjiBSO;
	
	public boolean isPlayingSplash(){
		boolean playing = this.currentSplash != null || this.benjiBSO.getVolume() > 0;
		return playing;
	}
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public FootballLiveWallpaper()
	{
		instance = this;
		
		// mPrefs = this.getSharedPreferences(PREFERENCES_ID, 0); //poner esto en FootballGameSettings
		// mPrefs.registerOnSharedPreferenceChangeListener(this); //poner esto en FootballGameSettings
        // onSharedPreferenceChanged(mPrefs, null); //poner esto en FootballGameSettings
        
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static FootballLiveWallpaper getSharedInstance()
	{	
		return instance;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public EngineOptions onCreateEngineOptions() {
		
		//android.os.Debug.waitForDebugger(); 
		Debug.setDebugLevel(DebugLevel.NONE);
		//Toast.makeText(this, "You move my sprite right round, right round...", Toast.LENGTH_LONG).show();
		Debug.d("toast", "onCreateEngineOptions");

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		//EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
		options.getAudioOptions().setNeedsSound(true);
		options.getAudioOptions().setNeedsMusic(true);
		
		//getApplication().getWallpaperDesiredMinimumHeight()
		//DisplayMetrics metrics = getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
		
		return options;
	}
	

	@Override
	protected synchronized void onResume() {
		Debug.d("toast", "onResume");
		super.onResume();
	}

	
	public void onCreateResources() {
		Debug.d("toast", "onCreateResources");
		this.game = new FootballGame(this.getEngine(), this);
		this.game.onCreateResources();
		
		
		createSplashResources();
		createMediaResources();
		splashes.get(splashCount%splashes.size()).loadTextureAndNext();
	}
	
	public Scene onCreateScene() {
		Debug.d("toast", "onCreateScene");
		
		this.mEngine.registerUpdateHandler(new FPSLogger());
		Scene initialScene = this.game.onCreateScene();
		
		this.footballScene = this.game.footballScene;
		
		return initialScene;
	}

	
	protected void onPopulatedScene() throws Exception {
		Debug.d("toast", "onPopulatedScene");
		this.game.initialize();
	}
	
	
	public void onGameCreated() {
		Debug.d("toast", "onGameCreated");
		super.onGameCreated();
		Debug.d("GameCreated: start FootballGame");
		this.game.onGameCreated();
	}
	
	
	protected void onOffsetsChanged(float pXOffset, float pYOffset, float pXOffsetStep, float pYOffsetStep, int pXPixelOffset, int pYPixelOffset) {
		super.onOffsetsChanged(pXOffset, pYOffset, pXOffsetStep, pYOffsetStep, pXPixelOffset, pYPixelOffset);
		if(this.game != null){
			//Debug.d("offsetX: "+pXOffset);
			//Debug.d("pixelOffsetX: "+pXPixelOffset);
			this.game.onOffsetsChanged(pXOffset);
		}
	}
	
	@Override
	public void onPause() {
		Debug.d("toast", "onPause");
		super.onPause();
	}
	
	@Override
	public void onPauseGame()
    {
		Debug.d("toast", "onPauseGame");
		game.onPauseGame();
		super.onPauseGame();
    }
	
	@Override
	public void onReloadResources(){
		//Toast.makeText(this, "loading resources...", Toast.LENGTH_LONG).show(); //no que peta!
		Debug.d("toast", "onReloadResources");
		super.onReloadResources();
	}
	
	
	@Override
	public synchronized void onResumeGame(){ //esto viene despues de reload resources
		//Toast.makeText(this, "onResumeGame", Toast.LENGTH_LONG).show();
		Debug.d("toast", "onResumeGame");
		
		game.onResumeGame();
		
		super.onResumeGame();
//		TimerHelper.startTimer(mEngine.getScene(), 2f,  new ITimerCallback() {                      
//			public void onTimePassed(final TimerHandler pTimerHandler){
//				restartEngine();
//			}
//		});
	}
	
	public void restartEngine(){
		super.onResumeGame();
	}
	

//	
//	protected void onTap(int pX, int pY) {
//		//super.onTap(pX, pY);
//		if(splashEnabled){
//			Debug.d("football", "tap on " + pX + "x" + pY);
//			launchSplash();
//		}
//	}
	
	public void onTapFromGame(TouchEvent event) {
		//Debug.d("splash", "tap on " + pX + "x" + pY);
		
		if(isPlayingSplash()){
			Debug.d("splash", "removeSplash()");
			removeSplash();
		}else{
			Debug.d("splash", "launchSplash()");
			launchSplash();
		}
	}
	

	private void launchSplash() {
		if(GameSettings.getInstance().splashEnabled == false){
			return;
		}
		
		
		if(isPlayingSplash())
		{
			//Debug.d("splash", "launchSplash - CurrentSplash not null, skipping launch");
			return;
		}
		
		if(splashes == null)
		{
			//Debug.d("splash", "launchSplash - Splashes not yet initialized, skipping launch");
			return;
		}
		
		playMusic();
		
		currentSplash = splashes.get(splashCount%splashes.size());
		splashCount++;

		
		//Debug.d("splash", "launchSplash - Attaching sprite for " + currentSplash.filename);
		this.footballScene.attachChild(currentSplash.loadSprite());
	}
	
	private void removeSplash(){
		if(this.currentSplash != null){
			this.footballScene.detachChild(this.currentSplash.loadSprite());
			this.currentSplash.modifier.reset();
			this.currentSplash = null;
		}
		stopMusic(3);
		
	}
	
	void playMusic(){
		if(GameSettings.getInstance().musicEnabled){
			benjiBSO.setVolume(1.0f);
			benjiBSO.play();
		}
	}
	
	void stopMusic(){
		stopMusic(true, 1);
	}
	void stopMusic(int fadeOutSpeed){
		stopMusic(true, fadeOutSpeed);
	}
	void stopMusic(boolean fadeOut){
		stopMusic(fadeOut, 1);
	}
	void stopMusic(boolean fadingOut, int fadeOutSpeed){
		this.fadeOutSpeed = fadeOutSpeed;
		if(fadingOut){
			this.doFadeOut = true;
			//Debug.d("fadeOut-start");
		}else{
			benjiBSO.stop();
		}
	}


	// ===========================================================
	// Methods
	// ===========================================================

	
	
	private void createSplashResources()
	{
		final int w = CAMERA_WIDTH;
		final int h = CAMERA_HEIGHT;

		final Splash splash1_3 = new Splash("splash1.3.jpg",
											320,239,
											1.7f,
											0,0,
											-401,0,
											null);
		
		final Splash splash3_3 = new Splash("splash3.3.jpg",
											w,h,
											2,
											1.5f,1f,
											50,500,
											null);
		
		splashes = new ArrayList<Splash>() {		
			{	
				add(new Splash("splash1.2.jpg",
											w,h,
											2f,
											1.5f,1f,
											50,50,
											splash1_3));
				add( new Splash("splash2.1.jpg",
								w,h,
								1.7f,
								1f,2f,
								150,120,
								new Splash("splash2.2.jpg",
											w,h,
											1.7f,
											1.6f,1f,
											400,550,
											new Splash("splash2.3.jpg",
														w,h,
														1.7f,
														1f,2f,
														250,475,
														splash1_3))));
				
				add( new Splash("splash3.1.jpg",
								w,h,
								1.5f,
								1.5f,1f,
								300,250,
								new Splash("splash3.2.jpg",
											w,h,
											2,
											1f,2f,
											225,600,
											splash3_3)));
				
				add( new Splash("splash4.1.jpg",
								w,h,
								1.5f,
								2.5f,1.3f,
								300,300,
								new Splash("splash4.2.jpg",
											500,375,
											1.5f,
											0,0,
											-450,0,
											new Splash("splash4.3.jpg",
														w,h,
														1.5f,
														1.5f,1f,
														100,100,
														new Splash("splash4.4.jpg",
																w,h,
																1.5f,
																1.6f,1f,
																400,600,
																null)))));


				add( new Splash("splash6.1.jpg",
								480,360,
								1.7f,
								-100,0,
								-200,0,
								new Splash("splash6.2.jpg",
											320,239,
											1.7f,
											0,0,
											-400,0,
											new Splash("splash6.3.jpg",
														(int)(880*1.5),(int)(800*1.5),
														1.6f,
														-500,-400,
														-500,0,
														splash3_3))));

				add( new Splash("splash8.jpg",
								484,414,
								4,
								3f,1f,
								630,400,
								null));
				
				add( new Splash("splash9.jpg",
								431,321,
								4,
								2f,1f,
								1300,400,
								null));
			}
		};

		//Debug.d("football", "splashes size " + splashes.size());
		
		splashes.get(0).loadTextureAndNext();
		
		for(Splash splash : splashes)
		{
			splashes.get(splashCount%splashes.size()).loadTextureAndNext();
			splashes.get(splashCount%splashes.size()).loadSpriteAndNext();
		}


	}
	
	private void createMediaResources() {
		
		try {
			//MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "sfx/benji-small.ogg").getMediaPlayer().
			benjiBSO = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "sfx/benji-small.ogg");
			benjiBSO.setVolume(0); //AG: lo uso para saber si estoy reproduciendo, sino no se comprobarlo :/
			
			this.mEngine.registerUpdateHandler(this);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	boolean doFadeOut = false;
	
	public void onUpdate(float pSecondsElapsed) {
		if(doFadeOut){
			benjiBSO.setVolume(Math.max(0f, benjiBSO.getVolume()-pSecondsElapsed/2*this.fadeOutSpeed));
			if(benjiBSO.getVolume() == 0f){
				//Debug.d("fadeOut-end");
				benjiBSO.stop();
				doFadeOut = false;
			}
		}
	}
	
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	public class Splash implements IEntityModifierListener{
			
		public String filename;
		public int w,h;
		public float duration = 1.5f;
		public float initialScale, endScale;
		public int x, y, toX, toY;
		
		private IEntityModifier modifier;

		private BitmapTextureAtlas mBitmapTextureAtlasBig;
		private TextureRegion texture;
		private Sprite sprite;
		
		private Splash nextSplash;
		
		public Splash(String filename, int w, int h, float duration, float initialScale, float endScale, int scaleXcenter, int scaleYcenter, Splash next)
		{
			this.filename = filename;
			this.w = w;
			this.h = h;
			this.duration = duration;
			this.initialScale = initialScale;
			this.endScale = endScale;
			this.x = scaleXcenter;
			this.y = scaleYcenter;
			this.nextSplash = next;
			
			modifier = new ScaleAtModifier(duration,initialScale,endScale,scaleXcenter,scaleYcenter,EaseSineOut.getInstance());
			modifier.addModifierListener(this);
		}
		
		public Splash(String filename, int w, int h, float duration, int fromX, int fromY, int toX, int toY, Splash next)
		{
			this.filename = filename;
			this.w = w;
			this.h = h;
			this.duration = duration;
			this.x = fromX;
			this.y = fromY;
			this.toX = toX;
			this.toY = toY;
			this.nextSplash = next;
			
			modifier = new MoveModifier(duration, x, toX, y, toY, EaseSineOut.getInstance());
			modifier.addModifierListener(this);
		}

		public void loadTexture()
		{
			Debug.d("football", "loadTexture " + filename);
			
			if(texture!=null)
				return;
			
			Debug.d("football", "loadTexture, first time");
			
			mBitmapTextureAtlasBig = new BitmapTextureAtlas(instance.getTextureManager(), 512, 512);
			
			texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlasBig,
																			FootballLiveWallpaper.getSharedInstance(),
																			filename,0,0);	
			mBitmapTextureAtlasBig.load();
			
		}
		
		public void loadTextureAndNext()
		{
			loadTexture();
			
			if(nextSplash!=null)
				nextSplash.loadTextureAndNext();
		}
		
		public void loadSpriteAndNext()
		{
			loadSprite();
			
			if(nextSplash!=null)
				nextSplash.loadSpriteAndNext();
		}
		
		public Sprite loadSprite()
		{
			loadTexture();
			
			if(sprite==null)
			{
				Debug.d("football", "loadSprite, first time");
				
				Debug.d("football", "loadSprite old w:" + w + "x h:" + h);
//					while( w!=CAMERA_WIDTH && h!=CAMERA_HEIGHT ) // poor man's scale
				while( h<CAMERA_HEIGHT ) // poor man's scale
				{
					w++; h++;
				}
				Debug.d("football", "loadSprite new w:" + w + " h:" + h);
				
				sprite = new Sprite(0,0,
									w,h, 
									texture,
									instance.getVertexBufferObjectManager());
				
//					texture = null;
//					instance.mBitmapTextureAtlasBig.unload();
//					instance.mBitmapTextureAtlasBig = null;
//					System.gc();
				
				modifier.reset();
				sprite.registerEntityModifier(modifier);
			}
			
			return sprite;
		}
		
		public boolean hasNext() {
			return (nextSplash!=null);
		}
		
		public Splash next() {
			return nextSplash;
		}
		
		public void onModifierFinished(IModifier<IEntity> arg0, IEntity arg1) {
			
			//Debug.d("football", "onModifierFinished " + arg1);
			
			footballScene.detachChild(currentSplash.loadSprite());
			
//				currentSplash.sprite = null;
			currentSplash.modifier.reset();
			
			if(currentSplash.hasNext())
			{
				currentSplash = currentSplash.next();
				
				if(!currentSplash.hasNext()){
					stopMusic();
				}
				
				//Debug.d("football", "attaching sprite for " + currentSplash.filename);
				footballScene.attachChild(currentSplash.loadSprite());
			}
			else
			{
				stopMusic();
				currentSplash = null;
			}
		}

		
		public void onModifierStarted(IModifier<IEntity> arg0, IEntity arg1) {
			//Debug.d("football", "onModifierStarted " + arg1);
		}

	}

	
}
