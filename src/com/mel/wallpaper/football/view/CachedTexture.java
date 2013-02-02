package com.mel.wallpaper.football.view;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.ITextureRegion;

public class CachedTexture
{
	public String uid;
	public ITextureRegion region;
	public ITexture atlas;
	
	public CachedTexture(String uid, ITextureRegion textureRegion, ITexture atlas){
		this.uid = uid;
		this.region = textureRegion;
		this.atlas = atlas;
	}
	
	@Override
	public boolean equals(Object obj) {
		return uid.equals(((CachedTexture)obj).uid);
	}
}
