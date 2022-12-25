package io.github.eisoptrophobia.kubryzltcraft.warfare;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Collection;
import java.util.Set;

public abstract class TerritoryManager extends ForgeRegistryEntry<TerritoryManager> {
	
	private static TerritoryManager manager;
	
	public abstract void init();
	
	public abstract Collection<Territory> getTerritories();
	
	public abstract Collection<String> getTerritoryIds();
	
	public abstract Territory getTerritoryByBlockPos(World world, BlockPos pos);
	
	public abstract Set<Territory> getNeighbors(Territory territory);
	
	public abstract Territory getTerritoryById(String id);
	
	public abstract void convert(Territory territory, KubryzltcraftTeam team);
	
	public static TerritoryManager getManager() {
		return manager;
	}
	
	public static void setManager(TerritoryManager territoryManager) {
		manager = territoryManager;
	}
}