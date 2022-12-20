package io.github.eisoptrophobia.kubryzltcraft.warfare.team;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.client.ClientSetup;
import io.github.eisoptrophobia.kubryzltcraft.data.WorldSavedDataKubryzltcraftMap;
import io.github.eisoptrophobia.kubryzltcraft.network.MessageKubryzltcraftMapSync;
import io.github.eisoptrophobia.kubryzltcraft.network.Network;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KubryzltcraftTeamManager {
	
	private static final Map<String, KubryzltcraftTeam> teamsById = new HashMap<>();
	public static final KubryzltcraftTeam NEUTRAL = register("neutral", "NEUT", new ResourceLocation(Kubryzltcraft.MOD_ID, "block/kubryzlt/neutral"));
	
	public static KubryzltcraftTeam register(String id, String prefix, ResourceLocation kubryzltTexture) {
		KubryzltcraftTeam team = new KubryzltcraftTeam(id, prefix, kubryzltTexture);
		teamsById.put(id, team);
		return team;
	}
	
	public static Collection<KubryzltcraftTeam> getTeams() {
		return teamsById.values();
	}
	
	public static KubryzltcraftTeam getTeamById(String id) {
		return teamsById.get(id);
	}
	
	public static KubryzltcraftTeam getTerritoryTeamServer(Territory territory) {
		return WorldSavedDataKubryzltcraftMap.readTeam(ServerLifecycleHooks.getCurrentServer().overworld(), territory.getId());
	}
	
	public static KubryzltcraftTeam getTerritoryTeamClient(Territory territory) {
		KubryzltcraftTeam team = ClientSetup.getClientTerritoryData().get(territory.getId());
		if (team == null) {
			return KubryzltcraftTeamManager.NEUTRAL;
		}
		return team;
	}
	
	public static void setTerritoryTeam(Territory territory, KubryzltcraftTeam team) {
		try {
			WorldSavedDataKubryzltcraftMap.writeTeam(ServerLifecycleHooks.getCurrentServer().overworld(), territory.getId(), team);
			Network.CHANNEL.send(PacketDistributor.ALL.noArg(), new MessageKubryzltcraftMapSync(Collections.singletonMap(territory.getId(), team), Collections.emptyMap()));
		}
		catch (Exception e) {}
	}
}