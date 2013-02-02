package com.mel.wallpaper.football.view;

import java.util.HashMap;
import java.util.Map;

import org.andengine.engine.Engine;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import android.content.Context;

public class SpriteFactory
{
	
	public final static float PLAYERS_SPRITE_SCALEFACTOR = 1.3f;
	
	public final static String DEFAULT_PLAYER_TEXTURE = "player";

	public final static String OLIVER = "oliver";
	public final static String BENJI = "benji";
	public final static String BRUCE = "bruce";
	
	public final static String NT_BLACK = "NT_BLACK";
	public final static String NT_WHITE = "NT_WHITE";
	public final static String NT_BLOND = "NT_BLOND";
	public final static String NT_BROWN = "NT_BROWN";
	public final static String NT_BLACK2 = "NT_BLACK2";
	public final static String NT_RED = "NT_RED";

	
	
	public final static String MARC = "marc";
	public final static String EDWARNER = "ed";
	public final static String MP_SKINHEAD = "MP_SKINHEAD";
	
	public final static String MP_BLACK = "MP_BLACK";
	public final static String MP_WHITE = "MP_WHITE";
	public final static String MP_BLOND = "MP_BLOND";
	public final static String MP_BROWN = "MP_BROWN";
	public final static String MP_BLACK2 = "MP_BLACK2";
	public final static String MP_RED = "MP_RED";

	public final static String GOAL_LEFT = "GOAL_LEFT";
	public final static String GOAL_RIGHT = "GOAL_RIGHT";
	public final static String BALL = "ball";

	public Context context;
	public Engine engine;
	
	// Singleton stuff
	private static SpriteFactory instance;

	private SpriteFactory(){
	}
	
	public static SpriteFactory getMe(){
		return getInstance();
	}
	public static SpriteFactory getInstance(){
		if(instance == null){
			instance = new SpriteFactory();
		}
		return instance;
	}
	
	// Other stuff
	public Map<String, CachedTexture> textureDictionary = new HashMap<String, CachedTexture>();
	public Map<String, CachedTexture> tiledTextureDictionary = new HashMap<String, CachedTexture>();
	
	public void registerTexture(String uid, String fileName, int altasWidht, int atlasHeigh){
//		CachedTexture texture = getTexture(uid); 
//		if(texture!=null){
//			texture.atlas.load(); //load(), pq seguramente se trata del preview del escritorio, y reusamos las texturas, pero el engine tiene que cargarlas de nuevo
//			return;
//		}
		
		BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), altasWidht, atlasHeigh, TextureOptions.BILINEAR);
		//BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), altasWidht, atlasHeigh, TextureOptions.NEAREST);
		ITextureRegion region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, context, fileName, 0, 0);
		atlas.load();
		
		registerTexture(uid,  region, atlas);
	}
	
	public void registerTiledTexture(String uid, String fileName, int altasWidht, int atlasHeigh, int rows, int columns){
//		CachedTexture texture = getTexture(uid); 
//		if(texture!=null){
//			texture.atlas.load(); //load(), pq seguramente se trata del preview del escritorio, y reusamos las texturas, pero el engine tiene que cargarlas de nuevo
//			return;
//		}
		
		BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), altasWidht, atlasHeigh, TextureOptions.BILINEAR);
		//BitmapTextureAtlas atlas = new BitmapTextureAtlas(engine.getTextureManager(), altasWidht, atlasHeigh, TextureOptions.NEAREST);
		ITiledTextureRegion region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(atlas, context, fileName, 0, 0, rows, columns);
		atlas.load();
		registerTiledTexture(uid,  region, atlas);
	}
	
	public void registerTexture(String uid, ITextureRegion textureRegion, ITexture atlas){
		this.textureDictionary.put(uid, new CachedTexture(uid, textureRegion, atlas));
	}
	
	public void registerTiledTexture(String uid, ITiledTextureRegion textureRegion, ITexture atlas ){
		this.tiledTextureDictionary.put(uid, new CachedTexture(uid, textureRegion, atlas));
	}
	
	
	public ITextureRegion getTexture(String uid){
		CachedTexture ct = getCachedTexture(uid);
		if(ct == null){
			return null;
		}else{
			return ct.region;
		}
	}
	
	public CachedTexture getCachedTexture(String uid){
		CachedTexture texture = this.textureDictionary.get(uid);
		if(texture == null){
			texture = this.tiledTextureDictionary.get(uid);
		}
		
		return texture;
	}
	
	
	public Sprite newSprite(String textureId, float width, float height){
		return newSprite(textureId, 0, 0, width, height);
	}
	
	public Sprite newSprite(String textureId, float x, float y, float width, float height){
		CachedTexture texture = textureDictionary.get(textureId);
		Sprite sprite = null;
		if(texture == null){
			texture = tiledTextureDictionary.get(textureId);
			//AnimatedSprite
			sprite = new AnimatedSprite(x, y, width, height, (ITiledTextureRegion)texture.region, engine.getVertexBufferObjectManager());
		}else{
			//Sprite
			sprite = new Sprite(x, y, width, height, texture.region, engine.getVertexBufferObjectManager());
		}
		
		return sprite;
	}
	
	public RectangularShape newBall(float width, float height){
		return new Rectangle(0,0,width,height, engine.getVertexBufferObjectManager());
	}
	
	
}
