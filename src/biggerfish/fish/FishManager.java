package biggerfish.fish;

import cage.core.asset.AssetManager;
import cage.core.model.ExtModel;

import java.util.List;

public class FishManager {

    public static final int ANCHOVY_TYPE       = 0;
    public static final int CLOWNFISH_TYPE     = 1;
    public static final int TILAPIA_TYPE       = 2;
    public static final int TUNA_TYPE          = 3;
    public static final int NUM_FISH_TYPES     = 4;

    public static final int GREATWHITE_TYPE    = 4;
    public static final int HAMMERHEAD_TYPE    = 5;


    public static final float ANCHOVY_MASS = 0.2f;
    public static final float CLOWNFISH_MASS = 1.0f;
    public static final float TILAPIA_MASS = 10.0f;
    public static final float TUNA_MASS = 40.0f;

    public static final float PLAYER_MASS = 0.3f;

    private AssetManager assetManager;
    private List<ExtModel> animationModels;

    private ExtModel anchovyModel;
    private ExtModel clownfishModel;
    private ExtModel tilapiaModel;
    private ExtModel tunaModel;
    private ExtModel greatWhiteModel;
    private ExtModel hammerheadModel;

    public FishManager(AssetManager assetManager, List<ExtModel> animationModels) {
        this.assetManager = assetManager;
        this.animationModels = animationModels;

        anchovyModel = assetManager.loadColladaModelFile("fish/anchovy/anchovy.dae");
        anchovyModel.getMesh(0).getMaterial().setDiffuse(1.0f, 1.0f, 1.0f);
        anchovyModel.getMesh(0).getMaterial().setDiffuse(assetManager.loadTextureFile("fish/anchovy/anchovy_color.png"));
        anchovyModel.getMesh(0).getMaterial().setSpecular(0.8f, 0.8f, 0.8f);
        anchovyModel.getMesh(0).getMaterial().setSpecular(assetManager.loadTextureFile("fish/anchovy/anchovy_spec.png"));
        anchovyModel.getMesh(0).getMaterial().setShininess(8.0f);
        animationModels.add(anchovyModel);

        clownfishModel = assetManager.loadColladaModelFile("fish/clownfish/clownfish.dae");
        clownfishModel.getMesh(0).getMaterial().setDiffuse(1.0f, 1.0f, 1.0f);
        clownfishModel.getMesh(0).getMaterial().setDiffuse(assetManager.loadTextureFile("fish/clownfish/clownfish_color.png"));
        clownfishModel.getMesh(0).getMaterial().setSpecular(0.5f, 0.5f, 0.5f);
        clownfishModel.getMesh(0).getMaterial().setSpecular(assetManager.loadTextureFile("fish/clownfish/clownfish_spec.png"));
        clownfishModel.getMesh(0).getMaterial().setShininess(8.0f);
        animationModels.add(clownfishModel);

        tilapiaModel = assetManager.loadColladaModelFile("fish/tilapia/tilapia.dae");
        tilapiaModel.getMesh(0).getMaterial().setDiffuse(1.0f, 1.0f, 1.0f);
        tilapiaModel.getMesh(0).getMaterial().setDiffuse(assetManager.loadTextureFile("fish/tilapia/tilapia_color.png"));
        tilapiaModel.getMesh(0).getMaterial().setSpecular(0.8f, 0.8f, 0.8f);
        tilapiaModel.getMesh(0).getMaterial().setSpecular(assetManager.loadTextureFile("fish/tilapia/tilapia_spec.png"));
        tilapiaModel.getMesh(0).getMaterial().setShininess(8.0f);
        animationModels.add(tilapiaModel);

        tunaModel = assetManager.loadColladaModelFile("fish/tuna/tuna.dae");
        tunaModel.getMesh(0).getMaterial().setDiffuse(1.0f, 1.0f, 1.0f);
        tunaModel.getMesh(0).getMaterial().setDiffuse(assetManager.loadTextureFile("fish/tuna/tuna_color.png"));
        tunaModel.getMesh(0).getMaterial().setSpecular(0.4f, 0.4f, 0.4f);
        tunaModel.getMesh(0).getMaterial().setSpecular(assetManager.loadTextureFile("fish/tuna/tuna_spec.png"));
        tunaModel.getMesh(0).getMaterial().setShininess(8.0f);
        animationModels.add(tunaModel);

        greatWhiteModel = createPlayerModel(FishManager.GREATWHITE_TYPE);

        hammerheadModel = createPlayerModel(FishManager.HAMMERHEAD_TYPE);
    }

    public ExtModel createPlayerModel(int type) {
        if(type == FishManager.GREATWHITE_TYPE) {
            ExtModel model = assetManager.loadColladaModelFile("fish/greatwhite/greatwhite.dae", false);
            model.getMesh(0).getMaterial().setDiffuse(1.0f, 1.0f, 1.0f);
            model.getMesh(0).getMaterial().setDiffuse(assetManager.loadTextureFile("fish/greatwhite/greatwhite_color.png"));
            model.getMesh(0).getMaterial().setSpecular(0.4f, 0.4f, 0.4f);
            model.getMesh(0).getMaterial().setSpecular(assetManager.loadTextureFile("fish/greatwhite/greatwhite_spec.png"));
            model.getMesh(0).getMaterial().setShininess(8.0f);
            animationModels.add(model);
            return model;
        }
        else {
            ExtModel model = assetManager.loadColladaModelFile("fish/hammerhead/hammerhead.dae", false);
            model.getMesh(0).getMaterial().setDiffuse(1.0f, 1.0f, 1.0f);
            model.getMesh(0).getMaterial().setDiffuse(assetManager.loadTextureFile("fish/hammerhead/hammerhead_color.png"));
            model.getMesh(0).getMaterial().setSpecular(0.4f, 0.4f, 0.4f);
            model.getMesh(0).getMaterial().setSpecular(assetManager.loadTextureFile("fish/hammerhead/hammerhead_spec.png"));
            model.getMesh(0).getMaterial().setShininess(8.0f);
            animationModels.add(model);
            return model;
        }
    }

    public ExtModel getAnchovyModel() {
        return anchovyModel;
    }

    public ExtModel getClownfishModel() {
        return clownfishModel;
    }

    public ExtModel getTilapiaModel() {
        return tilapiaModel;
    }

    public ExtModel getTunaModel() {
        return tunaModel;
    }

    public ExtModel getGreatWhiteModel() {
        return greatWhiteModel;
    }

    public ExtModel getHammerheadModel() {
        return hammerheadModel;
    }

    public ExtModel getFishModel(int type) {
        switch (type) {
            case CLOWNFISH_TYPE: return getClownfishModel();
            case TILAPIA_TYPE: return getTilapiaModel();
            case TUNA_TYPE: return getTunaModel();
            case GREATWHITE_TYPE: return getGreatWhiteModel();
            case HAMMERHEAD_TYPE: return getHammerheadModel();
            case ANCHOVY_TYPE: default: return getAnchovyModel();
        }
    }
}
