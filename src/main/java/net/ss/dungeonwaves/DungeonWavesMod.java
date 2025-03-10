package net.ss.dungeonwaves;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.ss.dungeonwaves.init.SsModEntities;
import net.ss.dungeonwaves.init.SsModItems;
import net.ss.dungeonwaves.init.SsModMenus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod("dungeon_waves")
public class DungeonWavesMod {
	public static final Logger LOGGER = LogManager.getLogger(DungeonWavesMod.class);
	public static final String MODID = "dungeon_waves";

	public DungeonWavesMod() {
		LOGGER.info("DungeonWavesMod is initializing...");
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		SsModEntities.REGISTRY.register(bus);
		SsModMenus.REGISTRY.register(bus);
		SsModItems.REGISTRY.register(bus);
		LOGGER.info("DungeonWavesMod initialization completed.");
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		LOGGER.debug("Registering network message: " + messageType.getSimpleName());
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	// ✅ Optimized task system
	private static final ConcurrentHashMap<String, TaskData> taskQueue = new ConcurrentHashMap<>();

	public static void queueServerWork(String taskName, int tickDelay, Runnable action) {
		LOGGER.debug("Queueing server work: " + taskName + " with delay: " + tickDelay);
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
			// ✅ Prevent continuous delay loops
			if (!taskQueue.containsKey(taskName)) {
				taskQueue.put(taskName, new TaskData(tickDelay, action));
				LOGGER.info("Task '{}' added to the queue.", taskName);
			} else {
				LOGGER.warn("⚠ Task '{}' already exists in the queue, not adding again.", taskName);
			}
		}
	}

	// ✅ Check if task exists
	public static boolean hasServerWork(String taskName) {
		LOGGER.debug("Checking if task exists: " + taskName);
		return taskQueue.containsKey(taskName);
	}

	public static void cancelServerWork(String taskName) {
		LOGGER.info("Cancelling task: " + taskName);
		taskQueue.remove(taskName);
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			taskQueue.forEach((name, task) -> {
				task.ticksRemaining--;
				if (task.ticksRemaining <= 0) {
					task.action.run();
					taskQueue.remove(name);
				}
			});
		}
	}

	private static class TaskData {
		int ticksRemaining;
		Runnable action;

		TaskData(int ticks, Runnable action) {
			LOGGER.debug("Creating new TaskData with ticks: " + ticks);
			this.ticksRemaining = ticks;
			this.action = action;
		}
	}
}
