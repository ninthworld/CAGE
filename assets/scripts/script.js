var packages = new JavaImporter(
	Packages.cage.core.engine.Engine,
	Packages.cage.core.scene.SceneManager,
	Packages.cage.core.scene.SceneNode,
	Packages.cage.core.scene.light.PointLight);
	
with(packages) {
	function initialize(engine) {
		light1 = engine.getSceneManager().getRootSceneNode().createPointLight();
		light1.translate(16.0, 72.0, 16.0);
		light1.setDiffuseColor(1.0, 0.0, 0.0);
		light1.setSpecularColor(1.0, 1.0, 1.0);
		light1.setRange(32.0);
		
		light2 = engine.getSceneManager().getRootSceneNode().createPointLight();
		light2.translate(-16.0, 72.0, 16.0);
		light2.setDiffuseColor(0.0, 1.0, 0.0);
		light2.setSpecularColor(1.0, 1.0, 1.0);
		light2.setRange(32.0);
		
		light3 = engine.getSceneManager().getRootSceneNode().createPointLight();
		light3.translate(16.0, 72.0, -16.0);
		light3.setDiffuseColor(0.0, 0.0, 1.0);
		light3.setSpecularColor(1.0, 1.0, 1.0);
		light3.setRange(32.0);
	}
}