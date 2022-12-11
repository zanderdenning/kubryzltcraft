package io.github.eisoptrophobia.kubryzltcraft.warfare;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class TerritoryManagerTest extends TerritoryManager {
	
	private Territory testTerritory;
	
	@Override
	public void init() {
		testTerritory = new Territory("test", KubryzltcraftTeamManager.NEUTRAL);
	}
	
	@Override
	public Collection<Territory> getTerritories() {
		return Collections.singletonList(testTerritory);
	}
	
	@Override
	public Territory getTerritoryByBlockPos(BlockPos pos) {
		return testTerritory;
	}
	
	@Override
	public Set<Territory> getNeighbors(Territory territory) {
		return Collections.emptySet();
	}
	
	@Override
	public Territory getTerritoryById(String id) {
		return testTerritory;
	}
	
	@Override
	public void convert(Territory territory, KubryzltcraftTeam team) {
		territory.setTeam(team);
	}
}