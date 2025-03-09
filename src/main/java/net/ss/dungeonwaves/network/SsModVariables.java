package net.ss.dungeonwaves.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.ss.dungeonwaves.DungeonWavesMod;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SsModVariables {
    @SubscribeEvent
    public static void init (FMLCommonSetupEvent event) {
        DungeonWavesMod.addNetworkMessage(SavedDataSyncMessage.class, SavedDataSyncMessage::buffer, SavedDataSyncMessage::new, SavedDataSyncMessage::handler);
    }


    @Mod.EventBusSubscriber
    public static class EventBusVariableHandlers {
        @SubscribeEvent
        public static void onPlayerLoggedIn (PlayerEvent.PlayerLoggedInEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                SavedData mapdata = MapVariables.get(event.getEntity().level());
                SavedData worlddata = WorldVariables.get(event.getEntity().level());
                if (mapdata != null)
                    DungeonWavesMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(0, mapdata));
                if (worlddata != null)
                    DungeonWavesMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
            }
        }

        @SubscribeEvent
        public static void onPlayerChangedDimension (PlayerEvent.PlayerChangedDimensionEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                SavedData worlddata = WorldVariables.get(event.getEntity().level());
                if (worlddata != null)
                    DungeonWavesMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
            }
        }
    }

    public static class WorldVariables extends SavedData {
        public static final String DATA_NAME = "ss_worldvars";

        public static WorldVariables load (CompoundTag tag) {
            WorldVariables data = new WorldVariables();
            data.read(tag);
            return data;
        }

        public void read (CompoundTag nbt) {
        }

        @Override
        public @NotNull CompoundTag save (@NotNull CompoundTag nbt) {
            return nbt;
        }

        public void syncData (LevelAccessor world) {
            this.setDirty();
            if (world instanceof Level level && !level.isClientSide())
                DungeonWavesMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
        }

        static WorldVariables clientSide = new WorldVariables();

        public static WorldVariables get (LevelAccessor world) {
            if (world instanceof ServerLevel level) {
                return level.getDataStorage().computeIfAbsent(WorldVariables::load, WorldVariables::new, DATA_NAME);
            } else {
                return clientSide;
            }
        }
    }

    public static class MapVariables extends SavedData {
        public static final String DATA_NAME = "ss_mapvars";

        public double summonPoints = 100.0;
        public boolean hasChosenMode = false;
        public long dungeonSeed = 0;
        public int wave = 0;
        public boolean inCombat = false;
        public int enemyCount = 0;
        public boolean merchantGone;
        public boolean isRestarting = false;

        // ✅ Thêm biến theo dõi nhóm đã chọn
        public boolean[] selectedGroups = new boolean[3]; // [0] = vũ khí, [1] = thuốc, [2] = relics

        public static MapVariables load(CompoundTag tag) {
            MapVariables data = new MapVariables();
            data.read(tag);
            return data;
        }

        public void read(CompoundTag nbt) {
            summonPoints = nbt.getDouble("summonPoints");
            hasChosenMode = nbt.getBoolean("hasChosenMode");
            dungeonSeed = nbt.getLong("dungeonSeed");
            wave = nbt.getInt("wave");
            inCombat = nbt.getBoolean("inCombat");
            enemyCount = nbt.getInt("enemyCount");
            merchantGone = nbt.getBoolean("merchantGone");
            isRestarting = nbt.getBoolean("isRestarting");

            // ✅ Đọc dữ liệu selectedGroups từ NBT
            for (int i = 0; i < 3; i++) {
                selectedGroups[i] = nbt.getBoolean("selectedGroup" + i);
            }
        }

        @Override
        public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
            nbt.putDouble("summonPoints", summonPoints);
            nbt.putBoolean("hasChosenMode", hasChosenMode);
            nbt.putLong("dungeonSeed", dungeonSeed);
            nbt.putInt("wave", wave);
            nbt.putBoolean("inCombat", inCombat);
            nbt.putInt("enemyCount", enemyCount);
            nbt.putBoolean("merchantGone", merchantGone);
            nbt.putBoolean("isRestarting", isRestarting);

            // ✅ Lưu dữ liệu selectedGroups vào NBT
            for (int i = 0; i < 3; i++) {
                nbt.putBoolean("selectedGroup" + i, selectedGroups[i]);
            }

            return nbt;
        }

        public void syncData(LevelAccessor world) {
            this.setDirty();
            if (world instanceof Level && !world.isClientSide())
                DungeonWavesMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
        }

        static MapVariables clientSide = new MapVariables();

        public static MapVariables get(LevelAccessor world) {
            if (world instanceof ServerLevel serverLevel) {
                return serverLevel.getDataStorage().computeIfAbsent(MapVariables::load, MapVariables::new, DATA_NAME);
            } else {
                return clientSide;
            }
        }
    }

    public static class SavedDataSyncMessage {
        private final int type;
        private SavedData data;

        public SavedDataSyncMessage (FriendlyByteBuf buffer) {
            this.type = buffer.readInt();
            CompoundTag nbt = buffer.readNbt();
            if (nbt != null) {
                this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
                if (this.data instanceof MapVariables mapVariables)
                    mapVariables.read(nbt);
                else if (this.data instanceof WorldVariables worldVariables)
                    worldVariables.read(nbt);
            }
        }

        public SavedDataSyncMessage (int type, SavedData data) {
            this.type = type;
            this.data = data;
        }

        public static void buffer (SavedDataSyncMessage message, FriendlyByteBuf buffer) {
            buffer.writeInt(message.type);
            if (message.data != null)
                buffer.writeNbt(message.data.save(new CompoundTag()));
        }

        public static void handler (SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
                    if (message.type == 0)
                        MapVariables.clientSide = (MapVariables) message.data;
                    else
                        WorldVariables.clientSide = (WorldVariables) message.data;
                }
            });
            context.setPacketHandled(true);
        }
    }
}
