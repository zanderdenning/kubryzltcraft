package io.github.eisoptrophobia.kubryzltcraft.network;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.Edifice;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEdificeBlocks {
	
	private Edifice edifice;
	
	public MessageEdificeBlocks() {
	
	}
	
	public MessageEdificeBlocks(Edifice edifice) {
		this.edifice = edifice;
	}
	
	public static void encode(MessageEdificeBlocks message, PacketBuffer buffer) {
		buffer.writeResourceLocation(message.edifice.getRegistryName());
	}
	
	public static MessageEdificeBlocks decode(PacketBuffer buffer) {
		return new MessageEdificeBlocks(Kubryzltcraft.EDIFICE_REGISTRY.getValue(buffer.readResourceLocation()));
	}
	
	public static void handle(MessageEdificeBlocks message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayerEntity player = context.getSender();
			World world = player.getLevel();
			Kubryzltcraft.LOGGER.info(message.edifice.getRegistryName().toString());
		});
		context.setPacketHandled(true);
	}
}