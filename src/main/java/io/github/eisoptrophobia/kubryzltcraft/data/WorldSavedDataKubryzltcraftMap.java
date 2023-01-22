package io.github.eisoptrophobia.kubryzltcraft.data;

import com.mojang.datafixers.util.Pair;
import io.github.eisoptrophobia.kubryzltcraft.ConfigCommon;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.EdificeUtils;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.ModEdifices;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;
import java.util.stream.Collectors;

public class WorldSavedDataKubryzltcraftMap extends WorldSavedData {
	
	private static final String NAME = Kubryzltcraft.MOD_ID + "_map";
	private final HashMap<String, KubryzltcraftTeam> territoryData = new HashMap<>();
	private final HashMap<String, Integer> moraleData = new HashMap<>();
	private final HashMap<UUID, Pair<World, BlockPos>> edificeLocations = new HashMap<>();
	private final HashMap<UUID, EdificeUtils.StatusData> edificeStatuses = new HashMap<>();
	private final HashMap<String, Set<UUID>> kubryzltEdifices = new HashMap<>();
	
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
			if (territoryData.contains("edifices")) {
				ListNBT edifices = territoryData.getList("edifices", Constants.NBT.TAG_STRING);
				for (INBT edifice : edifices) {
					Kubryzltcraft.LOGGER.info(edifice.getAsString());
					UUID uuid = UUID.fromString(edifice.getAsString());
					addKubryzltEdifice(territory, uuid);
				}
			}
		}
		CompoundNBT edificeData = nbt.getCompound("edifice_data");
		for (String key : edificeData.getAllKeys()) {
			UUID uuid = UUID.fromString(key);
			CompoundNBT edifice = edificeData.getCompound(key);
			for (ServerWorld world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
				if (world.dimension().getRegistryName().toString().equals(edifice.getString("world"))) {
					setEdificeLocation(uuid, world, new BlockPos(edifice.getInt("x"), edifice.getInt("y"), edifice.getInt("z")));
					break;
				}
			}
			setEdificeStatus(uuid, EdificeUtils.StatusData.load(edifice.getCompound("status")));
		}
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		CompoundNBT saveData = new CompoundNBT();
		for (String territory : TerritoryManager.getManager().getTerritoryIds()) {
			CompoundNBT territoryData = new CompoundNBT();
			territoryData.putString("team", getTerritoryTeam(territory).getId());
			territoryData.putInt("morale", getTerritoryMorale(territory));
			ListNBT edifices = new ListNBT();
			for (UUID uuid : getKubryzltEdifices(territory)) {
				edifices.add(StringNBT.valueOf(uuid.toString()));
			}
			territoryData.put("edifices", edifices);
			saveData.put(territory, territoryData);
		}
		nbt.put("save_data", saveData);
		CompoundNBT edificeData = new CompoundNBT();
		Set<UUID> edifices = new HashSet<>();
		edifices.addAll(edificeLocations.keySet());
		edifices.addAll(edificeStatuses.keySet());
		for (UUID uuid : edifices) {
			CompoundNBT edifice = new CompoundNBT();
			Pair<World, BlockPos> location = getEdificeLocation(uuid);
			edifice.putString("world", location.getFirst().dimension().getRegistryName().toString());
			edificeData.putInt("x", location.getSecond().getX());
			edificeData.putInt("y", location.getSecond().getY());
			edificeData.putInt("z", location.getSecond().getZ());
			edificeData.put("status", getEdificeStatus(uuid).save());
			edificeData.put(uuid.toString(), edifice);
		}
		nbt.put("edifice_data", edificeData);
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
	
	public static Pair<World, BlockPos> readEdificeLocation(ServerWorld world, UUID uuid) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		return data.getEdificeLocation(uuid);
	}
	
	public static void writeEdificeLocation(ServerWorld world, UUID uuid, World edificeWorld, BlockPos pos) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		data.setEdificeLocation(uuid, edificeWorld, pos);
		data.setDirty();
	}
	
	public static void deleteEdificeLocationStatus(ServerWorld world, UUID uuid) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		data.removeEdificeLocationStatus(uuid);
		data.setDirty();
	}
	
	public static Set<Map.Entry<UUID, Pair<World, BlockPos>>> searchEdificeLocations(ServerWorld world, World edificeWorld, BlockPos pos, int radius) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		return data.edificeLocations.entrySet().stream().filter(e -> {
			Pair<World, BlockPos> position = e.getValue();
			return position.getFirst().dimension().equals(edificeWorld.dimension()) && position.getSecond().distManhattan(pos) <= radius;
		}).collect(Collectors.toSet());
	}
	
	public static EdificeUtils.StatusData readEdificeStatus(ServerWorld world, UUID uuid) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		return data.getEdificeStatus(uuid);
	}
	
	public static void writeEdificeStatus(ServerWorld world, UUID uuid, EdificeUtils.StatusData statusData) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		data.setEdificeStatus(uuid, statusData);
		data.setDirty();
	}
	
	public static Set<UUID> readKubryzltEdifices(ServerWorld world, String id) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		return data.getKubryzltEdifices(id);
	}
	
	public static void writeKubryzltEdifice(ServerWorld world, String id, UUID edifice) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		data.addKubryzltEdifice(id, edifice);
		data.setDirty();
	}
	
	public static void deleteKubryzltEdifice(ServerWorld world, String id, UUID edifice) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		data.removeKubryzltEdifice(id, edifice);
		data.setDirty();
	}
	
	public static void cleanKubryzltEdifices(ServerWorld world, UUID edifice) {
		WorldSavedDataKubryzltcraftMap data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftMap::new, NAME);
		for (Set<UUID> edifices : data.kubryzltEdifices.values()) {
			edifices.remove(edifice);
		}
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
	
	private Pair<World, BlockPos> getEdificeLocation(UUID uuid) {
		return edificeLocations.getOrDefault(uuid, new Pair<>(ServerLifecycleHooks.getCurrentServer().overworld(), BlockPos.ZERO));
	}
	
	private void setEdificeLocation(UUID uuid, World world, BlockPos pos) {
		edificeLocations.put(uuid, new Pair<>(world, pos));
	}
	
	private void removeEdificeLocationStatus(UUID uuid) {
		edificeLocations.remove(uuid);
		edificeStatuses.remove(uuid);
	}
	
	private EdificeUtils.StatusData getEdificeStatus(UUID uuid) {
		EdificeUtils.StatusData statusData = edificeStatuses.get(uuid);
		if (statusData == null) {
			EdificeUtils.StatusData out = new EdificeUtils.StatusData(ModEdifices.NULL.get());
			out.setObstructed();
			return out;
		}
		return statusData;
	}
	
	private void setEdificeStatus(UUID uuid, EdificeUtils.StatusData statusData) {
		edificeStatuses.put(uuid, statusData);
	}
	
	private Set<UUID> getKubryzltEdifices(String id) {
		return kubryzltEdifices.getOrDefault(id, new HashSet<>());
	}
	
	private void addKubryzltEdifice(String id, UUID uuid) {
		Set<UUID> edifices = getKubryzltEdifices(id);
		edifices.add(uuid);
		kubryzltEdifices.put(id, edifices);
	}
	
	private void removeKubryzltEdifice(String id, UUID uuid) {
		Set<UUID> edifices = getKubryzltEdifices(id);
		edifices.remove(uuid);
		kubryzltEdifices.put(id, edifices);
	}
}