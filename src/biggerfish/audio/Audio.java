package biggerfish.audio;

import org.joml.Vector3fc;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Paths;

import static org.lwjgl.openal.AL10.*;

public class Audio {

    private int bufferId;
    private int sourceId;

    public Audio(String file) {
        int[] buffers = new int[1];
        alGenBuffers(buffers);
        this.bufferId = buffers[0];

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channels = stack.mallocInt(1);
            IntBuffer sampleRate = stack.mallocInt(1);
            ShortBuffer rawAudio = STBVorbis.stb_vorbis_decode_filename(Paths.get("assets/audio/" + file).toString(), channels, sampleRate);
            alBufferData(bufferId, (channels.get() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16), rawAudio, sampleRate.get());
        }

        this.sourceId = alGenSources();
        alSourcei(this.sourceId, AL_BUFFER, this.bufferId);
    }

    public void destroy() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    public void play() {
        alSourcePlay(sourceId);
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void rewind() {
        alSourceRewind(sourceId);
    }

    public void setLooped(boolean looped) {
        alSourcei(sourceId, AL_LOOPING, (looped ? AL_TRUE : AL_FALSE));
    }

    public void setPosition(Vector3fc position) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            position.get(buffer);
            alSourcefv(sourceId, AL_POSITION, buffer);
        }
    }

    public void setMaxDistance(float distance) {
        alSourcef(sourceId, AL_REFERENCE_DISTANCE, distance / 16.0f);
        alSourcef(sourceId, AL_MAX_DISTANCE, distance);
    }
}
