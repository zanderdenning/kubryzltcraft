package io.github.eisoptrophobia.kubryzltcraft.network;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {
	
	public static final String NETWORK_VERSION = "0.1.0";
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Kubryzltcraft.MOD_ID, "network"),
			() -> NETWORK_VERSION,
			version -> version.equals(NETWORK_VERSION),
			version -> version.equals(NETWORK_VERSION)
	);
	
	public static void init() {
		CHANNEL.registerMessage(0, MessageEdificeBlocks.class, MessageEdificeBlocks::encode, MessageEdificeBlocks::decode, MessageEdificeBlocks::handle);
	}
}