package com.mel.wallpaper.football;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.mel.entityframework.Game;
import com.mel.wallpaper.football.entity.Field;
import com.mel.wallpaper.football.entity.Partido;
import com.mel.wallpaper.football.process.BallPhisicsProcess;
import com.mel.wallpaper.football.process.GameProcess;
import com.mel.wallpaper.football.process.PlayersProcess;
import com.mel.wallpaper.football.process.RenderBallsProcess;
import com.mel.wallpaper.football.process.RenderPlayersProcess;
import com.mel.wallpaper.football.process.TouchProcess;
import com.mel.wallpaper.football.settings.GameSettings;
import com.mel.wallpaper.football.settings.GameSettingsActivity;
import com.mel.wallpaper.football.timer.TimerHelper;
import com.mel.wallpaper.football.view.SpriteFactory;

public class FootballGame implements SharedPreferences.OnSharedPreferenceChangeListener
{

	public Camera camera;
	public Engine engine;
	public Context context;
	
	public Scene loadingScene;
	public Scene footballScene;
	
	public Game game;
	
	private Partido partido;
	
	private GameProcess gameProcess;
	private TouchProcess touchProcess;
	private BallPhisicsProcess ballPhisicsProcess;
	private PlayersProcess playersCommandsProcess;
	private RenderPlayersProcess renderPlayersProcess;
	private RenderBallsProcess renderBallsProcess;
	

	private float screenOffsetX = 0;
	private float gameOffsetX = 0;
	
	private Sprite loadingBackground;
	private Sprite background;
	
	private float backgroundScaleFactor;

	//public UpdateTicker;
	
	public FootballGame(Engine engine, ContextWrapper context){
		this.engine = engine;
		this.camera = engine.getCamera();
		this.context = context;
		
		SharedPreferences mPrefs;
		mPrefs = context.getSharedPreferences(GameSettings.PREFERENCES_ID, 0);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(mPrefs, null);
	}
	
	public void onCreateResources(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		SpriteFactory.getMe().context = this.context;
		SpriteFactory.getMe().engine = this.engine;
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.EDWARNER,"fb_goalkeeper_richardi_pantalon_llarg.png", 512, 512, 4, 5);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MARC,"fb_player-maped_lenders.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_SKINHEAD,"fb_player-maped_skinhead.png", 512, 512, 8, 8);
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_WHITE,"fb_player-maped_white_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_BLACK,"fb_player-maped_black_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_BLACK2,"fb_player-maped_black_hair2.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_BROWN,"fb_player-maped_generic1.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.MP_RED,"fb_player-maped_redhead_hair.png", 512, 512, 8, 8);
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.BENJI,"fb_goalkeeper_benji_pantalon_llarg.png", 512, 512, 4, 5);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.OLIVER,"fb_player-newteam_oliver.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.BRUCE,"fb_player-newteam_bruce.png", 512, 512, 8, 8);
		
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_BLACK,"fb_player-newteam_black_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_BLACK2,"fb_player-newteam_black_hair2.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_BROWN,"fb_player-newteam_generic1.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.NT_RED,"fb_player-newteam_redhead_hair.png", 512, 512, 8, 8);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.DEFAULT_PLAYER_TEXTURE,"fb_player.png", 512, 512, 8, 8);
		
		
		SpriteFactory.getMe().registerTexture("background","field-final2.png", 2048, 1024);
		SpriteFactory.getMe().registerTexture(SpriteFactory.GOAL_RIGHT,"porteria_derecha.png", 64, 128);
		SpriteFactory.getMe().registerTexture(SpriteFactory.GOAL_LEFT,"porteria_izquierda.png", 64, 128);
		SpriteFactory.getMe().registerTiledTexture(SpriteFactory.BALL,"fb_ball.png", 128, 128, 4, 4);
		
		
		//TODO: Cargar el fondo del campo correctamente 
		//this.grassBackground = new RepeatingSpriteBackground(this.camera.getWidth(), this.camera.getHeight(), this.engine.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.context.getAssets(), "gfx/background_grass.png"), this.engine.getVertexBufferObjectManager());
				
		this.background = getBackground();
		this.loadingBackground = getBackground();
		updateBackgroundPosition();
		
		

	}
	
	public Scene onCreateScene(){
		this.loadingScene = new Scene();
		this.loadingScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		this.loadingScene.attachChild(this.loadingBackground);
		
		this.footballScene = new Scene();
		this.footballScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		this.footballScene.attachChild(this.background);
		
		if(GameSettings.getInstance().loadingScreenEnabled){
			return this.loadingScene;
		}else{
			return this.footballScene;
		}
	}
	

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		GameSettings settings = GameSettings.getInstance();
		settings.godsFingerEnabled = sharedPreferences.getBoolean(GameSettings.GODSFINGER_KEY, true);
		settings.splashEnabled = sharedPreferences.getBoolean(GameSettings.SPLASH_KEY, true);
		settings.musicEnabled = settings.splashEnabled && sharedPreferences.getBoolean(GameSettings.MUSIC_KEY, true);
		
		Debug.d("settings", "onSharedPreferenceChanged()");
		Debug.d("settings", "godsFingerEnabled: "+settings.godsFingerEnabled);
		Debug.d("settings", "splashEnabled: "+settings.splashEnabled);
		Debug.d("settings", "musicEnabled: "+settings.musicEnabled);
	}
	
	
	public void onOffsetsChanged(float screenOffsetX){
		this.screenOffsetX = screenOffsetX;
		updateBackgroundPosition();
	}
	
	public void onGameCreated(){
		
		// start engine
		this.engine.registerUpdateHandler(new IUpdateHandler() {
			public void onUpdate(final float pSecondsElapsed) {
				game.update();
			}

			public void reset() {}
		});
		
		startGame();
	}
	
	public void onPauseGame(){
		pauseGame();
	}
	
	public void onResumeGame(){
		resumeGame();
	}
	
	
	
	
	private Sprite getBackground(){
		ITextureRegion texture = SpriteFactory.getMe().getTexture("background");
		float visibleScreenHeight = FootballLiveWallpaper.CAMERA_HEIGHT - FootballLiveWallpaper.NOTIFICATION_BAR_HEIGHT;
		float visibleScreenWidth = visibleScreenHeight*texture.getWidth()/texture.getHeight();
		
		this.backgroundScaleFactor = visibleScreenHeight/texture.getHeight();
		
		Sprite fieldSprite = SpriteFactory.getMe().newSprite("background",  visibleScreenWidth, visibleScreenHeight);
        return fieldSprite;
	}
	
	
	private float calcCenterX(){
		return (this.camera.getWidth() - this.background.getWidth()) / 2 - this.gameOffsetX; 
	}
	private float calcCenterY(){
		return (this.camera.getHeight() - this.background.getHeight() + FootballLiveWallpaper.NOTIFICATION_BAR_HEIGHT) / 2;
	}
	
	
	
	public void initialize(){
		//initialize model
		float sf = this.backgroundScaleFactor;
		Field field = new Field(sf*52f, sf*52f, sf*92f, sf*52f, this.background); //TODO: cambiar esto por un campo horizontal mas largo
		Field loadingField = new Field(sf*52f, sf*52f, sf*92f, sf*52f, this.loadingBackground); //TODO: cambiar esto por un campo horizontal mas largo
		addGoals(field);
		addGoals(loadingField);
		
		
		
		//TESTING DIMENSIONES CAMPO
		//this.background.attachChild(new Rectangle(500,field.offsetY, 40, 40, this.engine.getVertexBufferObjectManager()));
		//this.background.attachChild(new Rectangle(field.offsetX+field.width/2, field.offsetY+field.height/2, 40, 40, this.engine.getVertexBufferObjectManager()));

		//initialize entity framework
		this.partido = new Partido(field);
		this.game = new Game();
		this.game.addEntity(partido);
		this.game.addEntities(partido.teams[0].players);
		this.game.addEntities(partido.teams[1].players);
		this.game.addEntity(partido.ball);
		
		
		
		this.gameProcess = new GameProcess(this.engine, this.footballScene, this.loadingScene);
		this.touchProcess = new TouchProcess(partido,this.footballScene, this.context);
		this.ballPhisicsProcess = new BallPhisicsProcess(partido);
		this.playersCommandsProcess = new PlayersProcess(partido);
		this.renderPlayersProcess = new RenderPlayersProcess(this.background);
		this.renderBallsProcess = new RenderBallsProcess(this.background);
		
		game.addProcess(this.gameProcess, 1);
		game.addProcess(this.touchProcess, 10);
		game.addProcess(this.ballPhisicsProcess, 20);
		game.addProcess(this.playersCommandsProcess, 21);
		game.addProcess(this.renderPlayersProcess, 98);
		game.addProcess(this.renderBallsProcess, 99);
		
		
	}
	
	
	
	private void updateBackgroundPosition(){
		if(this.background!=null){
			this.gameOffsetX = getGameOffset(screenOffsetX, this.background);
			//Debug.d("gameOffset: "+this.gameOffsetX);
			this.background.setPosition(calcCenterX(), calcCenterY());
		}
		
		if(this.loadingBackground!=null){
			this.gameOffsetX = getGameOffset(screenOffsetX, this.loadingBackground);
			//Debug.d("gameOffset: "+this.gameOffsetX);
			this.loadingBackground.setPosition(calcCenterX(), calcCenterY());
		}
	}
	
	private float getGameOffset(float screenOffsetX, RectangularShape background) {
		//float offsetTotalRange = (this.background.getWidth()*0.5f);
		//float gameOffset = offsetTotalRange*(screenOffsetX-0.5f)
		float offsetTotalRange = (background.getWidth()-this.camera.getWidth());
		float gameOffset = offsetTotalRange*(screenOffsetX-0.5f);
		return gameOffset;
	}
	
	private void addGoals(Field field){
		float goalWidth = 36f;
		float goalHeight = 114f;
		
		Field.topGoal = goalHeight/2f;
		Field.bottomGoal = -goalHeight/2f;
		Field.goalWidth = goalWidth;
		
		Field.leftGoalEnd = Field.leftWall-goalWidth+5;
		Field.rightGoalEnd = Field.rightWall+goalWidth-5;;
		
		Sprite goal_izq = SpriteFactory.getInstance().newSprite(SpriteFactory.GOAL_LEFT, goalWidth, goalHeight);
		Sprite goal_der = SpriteFactory.getInstance().newSprite(SpriteFactory.GOAL_RIGHT, goalWidth, goalHeight);
		
		float x;
		float y;
		
		//left goal
		x = 5 + field.paddingLeft - goalWidth;
		y = Field.correccionCampoY - goalHeight/2;
		goal_izq.setPosition(x, y);
		goal_izq.setZIndex(9999);

		//right goal
		x = field.background.getWidth() - field.paddingRight - 5;
		y = Field.correccionCampoY - goalHeight/2;
		goal_der.setPosition(x, y);
		goal_der.setZIndex(9999);
		
		field.background.attachChild(goal_izq);
		field.background.attachChild(goal_der);
		
	}

	
	
	
	// GAME LIFE CYCLE
	private void startGame(){
		
		if(partido.status == Partido.Status.INITIAL_STATE){
			
			// wait for resources to load and start playing
//			partido.status = Partido.Status.LOADING;
//			TimerHelper.startTimer(this.engine.getScene(), 2f,  new ITimerCallback() {                      
//				public void onTimePassed(final TimerHandler pTimerHandler){
//					partido.status = Partido.Status.INTRO;
//				}
//			});
			
			
			partido.status = Partido.Status.INTRO;
		}
	}
	
	private void pauseGame(){
		if(partido.status != Partido.Status.PAUSE){
			partido.status = Partido.Status.PAUSE;
			
			this.footballScene.setChildrenIgnoreUpdate(true);
			
			if(GameSettings.getInstance().loadingScreenEnabled){
				engine.setScene(loadingScene);
			}
		}
	}
	
	private void resumeGame(){
		
		if(partido.status == Partido.Status.PAUSE){
			
			this.footballScene.setChildrenIgnoreUpdate(false);

			// wait for resources to load and start playing
//			partido.status = Partido.Status.LOADING;
//			TimerHelper.startTimer(engine.getScene(), 2f,  new ITimerCallback() {                      
//				public void onTimePassed(final TimerHandler pTimerHandler){
//					partido.status = Partido.Status.RESUME_GAME;
//				}
//			});
			
			
			partido.status = Partido.Status.RESUME_GAME;
		}
	}
	
	
}
