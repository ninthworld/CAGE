var packages = new JavaImporter(
	Packages.cage.core.engine.Engine,
	Packages.cage.core.render.RenderManager,
	Packages.cage.core.render.stage.LightingRenderStage,
	Packages.cage.core.graphics.texture.TextureCubeMap,
	Packages.cage.core.graphics.texture.Texture2D,
	Packages.cage.core.asset.AssetManager);
	
with(packages) {
	function initialize(engine) {
		var use = 0;		
		switch(use) {
			case 0: {
				engine.getRenderManager().getDefaultLightingRenderStage().setUseAtmosphere(true);
			} break;
			case 1: {
				engine.getRenderManager().getDefaultLightingRenderStage().setUseSkybox(true);
				engine.getRenderManager().getDefaultLightingRenderStage().setSkyboxTexture(engine.getAssetManager().loadCubeMap("skybox"));				
			} break;
			case 2: {
				engine.getRenderManager().getDefaultLightingRenderStage().setUseSkydome(true);
				engine.getRenderManager().getDefaultLightingRenderStage().setSkydomeTexture(engine.getAssetManager().loadTextureFile("skydome/skydome.jpg"));				
			} break;
		}       
	}
}