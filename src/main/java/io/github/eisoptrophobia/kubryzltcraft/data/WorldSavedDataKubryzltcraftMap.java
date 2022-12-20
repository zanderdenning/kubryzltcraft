package io.github.eisoptrophobia.kubryzltcraft.data;

import io.github.eisoptrophobia.kubryzltcraft.ConfigCommon;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;

public class WorldSavedDataKubryzltcraftMap extends WorldSavedData {
	
	private static final String NAME = Kubryzltcraft.MOD_ID + "_map";
	private final HashMap<String, KubryzltcraftTeam> territoryData = new HashMap<>();
	private final HashMap<String, Integer> moraleData = new HashMap<>();
	
	public WorldSavedDataKubryzltcraftMap(String name) {
		super(name);
	}
	
	public WorldSavedDataKubryzltcraftMap() {
		this(NAME);
	}
	
	@Override
	public void load(CompoundNBT nbt) {
		CompoundNBT saveData = nbt.getCompound("save_data");
		for (String territory : TerritoryManager.getManager().getTerritoryIds()) {
			CompoundNBT territoryData = saveData.getCompound(territory);
			if (territoryData.isEmpty()) {
				continue;
			}
			String team = territoryData.getString("team");
			if (!team.isEmpty()) {
				KubryzltcraftTeam kubryzltcraftTeam = KubryzltcraftTeamManager.getTeamById(team);
				if (kubryzltcraftTeam == null) {
					continue;
				}
				setTerritoryTeam(territory, kubryzltcraftTeam);
			}
			if (territoryData.contains("morale")) {
				int morale = territoryData.getInt("morale");
				setTerritoryMorale(territory, morale);
			}
		}
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		CompoundNBT saveData = new CompoundNBT();
		for (String territory : TerritoryManager.getManager().getTerritoryIds()) {
			CompoundNBT territoryData = new CompoundNBT();
			territoryData.putString("team", getTerritoryTeam(territory).getId());
			territoryData.putInt("morale", getTerritoryMorale(territory));
			saveData.put(territory, territoryData);
		}
		nbt.put("save_data", saveData);
		return nbt;
	}
	
	public static KubryzltcraftTeam readTeam(ServerWorld world, String id) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		return data.getTerritoryTeam(id);
	}
	
	public static void writeTeam(ServerWorld world, String id, KubryzltcraftTeam team) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		data.setTerritoryTeam(id, team);
		data.setDirty();
	}
	
	public static int readMorale(ServerWorld world, String id) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		return data.getTerritoryMorale(id);
	}
	
	public static void writeMorale(ServerWorld world, String id, int morale) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		data.setTerritoryMorale(id, morale);
		data.setDirty();
	}
	
	private KubryzltcraftTeam getTerritoryTeam(String id) {
		return territoryData.getOrDefault(id, KubryzltcraftTeamManager.NEUTRAL);
	}
	
	private void setTerritoryTeam(String id, KubryzltcraftTeam team) {
		territoryData.put(id, team);
	}
	
	private int getTerritoryMorale(String id) {
		return moraleData.getOrDefault(id, ConfigCommon.DEFAULT_MORALE.get());
	}
	
	private void setTerritoryMorale(String id, int morale) {
		moraleData.put(id, morale);
	}
}