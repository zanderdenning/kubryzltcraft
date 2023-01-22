package io.github.eisoptrophobia.kubryzltcraft.data;

import io.github.eisoptrophobia.kubryzltcraft.ConfigCommon;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneId;

public class WorldSavedDataKubryzltcraftTimer extends WorldSavedData {
	
	private static final String NAME = Kubryzltcraft.MOD_ID + "_timers";
	private int year;
	private int day;
	private int hour;
	private int minute;
	private int gameDay;
	
	public WorldSavedDataKubryzltcraftTimer(String name) {
		super(name);
		load(new CompoundNBT());
	}
	
	public WorldSavedDataKubryzltcraftTimer() {
		this(NAME);
	}
	
	@Override
	public void load(CompoundNBT nbt) {
		CompoundNBT saveData = nbt.getCompound("save_data");
		if (saveData.contains("year")) {
			year = saveData.getInt("year");
			day = saveData.getInt("day");
			hour = saveData.getInt("hour");
			minute = saveData.getInt("minute");
			if (day < 2) {
				day = 2;
			}
		}
		else {
			LocalDateTime now = LocalDateTime.now(ZoneId.of(ConfigCommon.TIME_ZONE.get())).minusHours(ConfigCommon.DAY_TICK_HOUR.get());
			year = now.getYear();
			day = now.getDayOfYear();
			hour = now.getHour();
			minute = now.getMinute();
		}
		gameDay = saveData.getInt("gameDay");
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		CompoundNBT saveData = new CompoundNBT();
		saveData.putInt("year", year);
		saveData.putInt("day", day);
		saveData.putInt("hour", hour);
		saveData.putInt("minute", minute);
		saveData.putInt("gameDay", gameDay);
		nbt.put("save_data", saveData);
		return nbt;
	}
	
	public static LocalDateTime readDateTime(ServerWorld world) {
		WorldSavedDataKubryzltcraftTimer data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftTimer::new, NAME);
		return LocalDateTime.of(Year.of(data.year).atDay(data.day), LocalTime.of(data.hour, data.minute));
	}
	
	public static void writeDateTime(ServerWorld world, LocalDateTime localDateTime) {
		WorldSavedDataKubryzltcraftTimer data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftTimer::new, NAME);
		data.year = localDateTime.getYear();
		data.day = localDateTime.getDayOfYear();
		data.hour = localDateTime.getHour();
		data.minute = localDateTime.getMinute();
		data.setDirty();
	}
	
	public static int readGameDay(ServerWorld world) {
		WorldSavedDataKubryzltcraftTimer data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftTimer::new, NAME);
		return data.gameDay;
	}
	
	public static void writeGameDay(ServerWorld world, int gameDay) {
		WorldSavedDataKubryzltcraftTimer data = world.getDataStorage().computeIfAbsent(WorldSavedDataKubryzltcraftTimer::new, NAME);
		data.gameDay = gameDay;
		data.setDirty();
	}
}