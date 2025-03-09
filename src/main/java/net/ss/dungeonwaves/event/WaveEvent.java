package net.ss.dungeonwaves.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all wave-related events.
 */
public class WaveEvent extends Event {
    private final ServerLevel world;
    private final int waveNumber;

    public WaveEvent(ServerLevel world, int waveNumber) {
        this.world = world;
        this.waveNumber = waveNumber;
    }

    public ServerLevel getWorld() {
        return world;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    /**
     * Triggered when a wave ends.
     */
    public static class End extends WaveEvent {
        public End(ServerLevel world, int waveNumber) {
            super(world, waveNumber);
        }
    }
}
