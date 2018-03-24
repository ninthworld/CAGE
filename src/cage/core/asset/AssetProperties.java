package cage.core.asset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AssetProperties {

    private Properties properties;
    private Path propertiesFile;
    private Path fontsPath;
    private Path imagesPath;
    private Path modelsPath;
    private Path shadersPath;
    private Path texturesPath;

    public AssetProperties(Path propertiesFile) {
        this.properties = new Properties();
        this.propertiesFile = propertiesFile;
        load();
    }

    public void load() {
        try {
            if (Files.exists(propertiesFile)) {
                properties.load(Files.newInputStream(propertiesFile));
            } else {
                Files.createDirectories(propertiesFile.getParent());
                Files.createFile(propertiesFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean notifySave = false;
        if(!properties.containsKey("assets.fonts.path")) {
            properties.setProperty("assets.fonts.path", "assets/fonts/");
            notifySave = true;
        }
        fontsPath = Paths.get(properties.getProperty("assets.fonts.path"));

        if(!properties.containsKey("assets.images.path")) {
            properties.setProperty("assets.images.path", "assets/images/");
            notifySave = true;
        }
        imagesPath = Paths.get(properties.getProperty("assets.images.path"));

        if(!properties.containsKey("assets.models.path")) {
            properties.setProperty("assets.models.path", "assets/models/");
            notifySave = true;
        }
        modelsPath = Paths.get(properties.getProperty("assets.models.path"));

        if(!properties.containsKey("assets.shaders.path")) {
            properties.setProperty("assets.shaders.path", "assets/shaders/");
            notifySave = true;
        }
        shadersPath = Paths.get(properties.getProperty("assets.shaders.path"));

        if(!properties.containsKey("assets.textures.path")) {
            properties.setProperty("assets.textures.path", "assets/textures/");
            notifySave = true;
        }
        texturesPath = Paths.get(properties.getProperty("assets.textures.path"));

        if(notifySave) {
            save();
        }
    }

    public void save() {
        try {
            properties.store(Files.newBufferedWriter(propertiesFile), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key, "");
    }

    public Path getValuePath(String key) {
        return Paths.get(getValue(key));
    }

    public void setDefault(String key, String defaultValue) {
        if(!properties.containsKey(key)) {
            properties.setProperty(key, defaultValue);
            save();
        }
    }

    public Path getFontsPath() {
        return fontsPath;
    }

    public Path getImagesPath() {
        return imagesPath;
    }

    public Path getModelsPath() {
        return modelsPath;
    }

    public Path getShadersPath() {
        return shadersPath;
    }

    public Path getTexturesPath() {
        return texturesPath;
    }
}
