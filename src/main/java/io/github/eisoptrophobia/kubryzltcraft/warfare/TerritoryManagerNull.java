package io.github.eisoptrophobia.kubryzltcraft.warfare;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class TerritoryManagerNull extends TerritoryManager {
	
	@Override
	public void init() {}
	
	@Override
	public Collection<Territory> getTerritories() {
		return Collections.emptySet();
	}
	
	@Override
	public Collection<String> getTerritoryIds() {
		return Collections.emptySet();
	}
	
	@Override
	public Territory getTerritoryByBlockPos(BlockPos pos) {
		return null;
	}
	
	@Override
	public Set<Territory> getNeighbors(Territory territory) {
		return Collections.emptySet();
	}
	
	@Override
	public Territory getTerritoryById(String id) {
		return null;
	}
	
	@Override
	public void convert(Territory territory, KubryzltcraftTeam team) {
	
	}
}