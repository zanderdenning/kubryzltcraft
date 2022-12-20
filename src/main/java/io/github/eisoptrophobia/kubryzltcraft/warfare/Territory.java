package io.github.eisoptrophobia.kubryzltcraft.warfare;

import io.github.eisoptrophobia.kubryzltcraft.ConfigCommon;
import io.github.eisoptrophobia.kubryzltcraft.client.ClientSetup;
import io.github.eisoptrophobia.kubryzltcraft.data.WorldSavedDataKubryzltcraftMap;
import io.github.eisoptrophobia.kubryzltcraft.network.MessageKubryzltcraftMapSync;
import io.github.eisoptrophobia.kubryzltcraft.network.Network;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collections;

public class Territory {
	
	private final String id;
	
	public Territory(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public int getMoraleServer() {
		return WorldSavedDataKubryzltcraftMap.readMorale(ServerLifecycleHooks.getCurrentServer().overworld(), id);
	}
	
	public int getMoraleClient() {
		return ClientSetup.getClientMoraleData().getOrDefault(id, ConfigCommon.DEFAULT_MORALE.get());
	}
	
	public void setMorale(int morale) {
		WorldSavedDataKubryzltcraftMap.writeMorale(ServerLifecycleHooks.getCurrentServer().overworld(), id, morale);
		Network.CHANNEL.send(PacketDistributor.ALL.noArg(), new MessageKubryzltcraftMapSync(Collections.emptyMap(), Collections.singletonMap(id, morale)));
	}
	
	public void improveMorale() {
		setMorale(getMoraleServer() + 10);
	}
}