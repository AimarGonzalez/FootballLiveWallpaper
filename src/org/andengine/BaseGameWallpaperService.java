package org.andengine;

import org.andengine.entity.scene.Scene;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;

public abstract class BaseGameWallpaperService extends BaseLiveWallpaperService
{
	
	
	
	
	protected abstract void onCreateResources() throws Exception;
	protected abstract Scene onCreateScene() throws Exception ;
	protected abstract void onPopulatedScene() throws Exception ;

	public final void onCreateResources(final OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		this.onCreateResources();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public final void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		final Scene scene = this.onCreateScene();

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	public final void onPopulateScene(final Scene pScene, final OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		this.onPopulatedScene();
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	
	

	
}
