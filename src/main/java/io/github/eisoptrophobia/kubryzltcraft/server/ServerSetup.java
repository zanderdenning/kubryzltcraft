package io.github.eisoptrophobia.kubryzltcraft.server;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class ServerSetup {
	
	private static MinecraftServer server;
	
	public static void setup(FMLDedicatedServerSetupEvent event) {
	
	}
	
	@SubscribeEvent
	public static void onFMLServerStarting(FMLServerStartingEvent event) {
		server = event.getServer();
	}
	
	public static MinecraftServer getServer() {
		return server;
	}
}