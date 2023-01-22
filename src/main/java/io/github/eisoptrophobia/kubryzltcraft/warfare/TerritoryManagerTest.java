package io.github.eisoptrophobia.kubryzltcraft.warfare;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class TerritoryManagerTest extends TerritoryManager {
	
	private final Territory OUT_OF_BOUNDS = new Territory("out_of_bounds");
	private final Territory[] territories = new Territory[16];
	
	@Override
	public void init() {
		for (int i = 0; i < 4; i ++) {
			for (int j = 0; j < 4; j ++) {
				territories[i * 4 + j] = new Territory("t" + (i * 4 + j));
			}
		}
	}
	
	@Override
	public Collection<Territory> getTerritories() {
		return Arrays.stream(territories).collect(Collectors.toList());
	}
	
	@Override
	public Collection<String> getTerritoryIds() {
		return Arrays.stream(territories).map(Territory::getId).collect(Collectors.toList());
	}
	
	@Override
	public Territory getTerritoryByBlockPos(World world, BlockPos pos) {
		if (pos.getX() < 0 || pos.getZ() < 0 || pos.getX() >= 256 || pos.getZ() >= 256 || world.dimension() != World.OVERWORLD) {
			return OUT_OF_BOUNDS;
		}
		int xPos = pos.getX() / 64;
		int yPos = pos.getZ() / 64;
		return territories[xPos + yPos * 4];
	}
	
	@Override
	public Collection<Territory> getNeighbors(Territory territory) {
		if (territory == OUT_OF_BOUNDS) {
			return Collections.emptySet();
		}
		Set<Territory> neighbors = new HashSet<>();
		int territoryPos = Integer.parseInt(territory.getId().substring(1));
		if (territoryPos % 4 != 0) {
			neighbors.add(territories[territoryPos - 1]);
		}
		if (territoryPos % 4 != 3) {
			neighbors.add(territories[territoryPos + 1]);
		}
		if (territoryPos / 4 != 0) {
			neighbors.add(territories[territoryPos - 4]);
		}
		if (territoryPos / 4 != 3) {
			neighbors.add(territories[territoryPos + 4]);
		}
		return neighbors;
	}
	
	@Override
	public Territory getTerritoryById(String id) {
		if (id.equals(OUT_OF_BOUNDS.getId())) {
			return OUT_OF_BOUNDS;
		}
		return territories[Integer.parseInt(id.substring(1))];
	}
	
	@Override
	public void convert(Territory territory, KubryzltcraftTeam team) {
		KubryzltcraftTeamManager.setTerritoryTeam(territory, team);
	}
}