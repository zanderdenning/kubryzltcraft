package io.github.eisoptrophobia.kubryzltcraft.network;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.client.ClientSetup;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MessageKubryzltcraftMapSync {
	
	private Map<String, KubryzltcraftTeam> territoryData;
	private Map<String, Integer> moraleData;
	
	public MessageKubryzltcraftMapSync() {
	
	}
	
	public MessageKubryzltcraftMapSync(Map<String, KubryzltcraftTeam> territoryData, Map<String, Integer> moraleData) {
		this.territoryData = territoryData;
		this.moraleData = moraleData;
	}
	
	public static void encode(MessageKubryzltcraftMapSync message, PacketBuffer buffer) {
		buffer.writeInt(message.territoryData.size());
		for (Map.Entry<String, KubryzltcraftTeam> territory : message.territoryData.entrySet()) {
			buffer.writeUtf(territory.getKey());
			buffer.writeUtf(territory.getValue().getId());
		}
		buffer.writeInt(message.moraleData.size());
		for (Map.Entry<String, Integer> territory : message.moraleData.entrySet()) {
			buffer.writeUtf(territory.getKey());
			buffer.writeInt(territory.getValue());
		}
	}
	
	public static MessageKubryzltcraftMapSync decode(PacketBuffer buffer) {
		HashMap<String, KubryzltcraftTeam> territoryData = new HashMap<>();
		HashMap<String, Integer> moraleData = new HashMap<>();
		int size = buffer.readInt();
		for (int i = 0; i < size; i ++) {
			String name = buffer.readUtf();
			String team = buffer.readUtf();
			territoryData.put(name, KubryzltcraftTeamManager.getTeamById(team));
		}
		int moraleSize = buffer.readInt();
		for (int i = 0; i < moraleSize; i ++) {
			String name = buffer.readUtf();
			Integer morale = buffer.readInt();
			moraleData.put(name, morale);
		}
		return new MessageKubryzltcraftMapSync(territoryData, moraleData);
	}
	
	public static void handle(MessageKubryzltcraftMapSync message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			for (Map.Entry<String, KubryzltcraftTeam> territory : message.territoryData.entrySet()) {
				ClientSetup.getClientTerritoryData().put(territory.getKey(), territory.getValue());
			}
			for (Map.Entry<String, Integer> territory : message.moraleData.entrySet()) {
				ClientSetup.getClientMoraleData().put(territory.getKey(), territory.getValue());
			}
		});
		context.setPacketHandled(true);
	}
}