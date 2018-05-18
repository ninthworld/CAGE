package biggerfish.audio;

import org.joml.Vector3fc;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class AudioManager {

    private long deviceId;
    private long contextId;
    private List<Audio> audios;

    public AudioManager() {
        this.deviceId = alcOpenDevice((ByteBuffer)null);
        ALCCapabilities caps = ALC.createCapabilities(this.deviceId);

        this.contextId = alcCreateContext(this.deviceId, (int[])null);
        alcMakeContextCurrent(this.contextId);
        AL.createCapabilities(caps);

        this.audios = new ArrayList<>();
    }

    public void destroy() {
        for(Audio audio : audios) {
            audio.destroy();
        }
        alcCloseDevice(deviceId);
    }

    public Audio createAudio(String file) {
        Audio audio = new Audio(file);
        audios.add(audio);
        return audio;
    }

    public void setListenerPosition(Vector3fc position) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            position.get(buffer);
            alListenerfv(AL_POSITION, buffer);
        }
    }
}
