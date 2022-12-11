package io.github.eisoptrophobia.kubryzltcraft.warfare.team;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
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
}