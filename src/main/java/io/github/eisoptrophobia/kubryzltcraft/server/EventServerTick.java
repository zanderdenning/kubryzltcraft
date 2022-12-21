package io.github.eisoptrophobia.kubryzltcraft.server;

import io.github.eisoptrophobia.kubryzltcraft.ConfigCommon;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.data.WorldSavedDataKubryzltcraftTimer;
import io.github.eisoptrophobia.kubryzltcraft.event.KubryzltcraftTickEvent;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID)
public class EventServerTick {
	
	private static LocalDateTime prev = null;
	private static int gameDay = -1;
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (prev == null) {
			prev = WorldSavedDataKubryzltcraftTimer.readDateTime(ServerLifecycleHooks.getCurrentServer().overworld());
		}
		if (gameDay == -1) {
			gameDay = WorldSavedDataKubryzltcraftTimer.readGameDay(ServerLifecycleHooks.getCurrentServer().overworld());
		}
		LocalDateTime now = LocalDateTime.now(ZoneId.of(ConfigCommon.TIME_ZONE.get())).minusHours(ConfigCommon.DAY_TICK_HOUR.get());
		if (now.getYear() != prev.getYear() || now.getDayOfYear() != now.getDayOfYear()) {
			gameDay ++;
			onDayTick(now);
			onHourTick(now);
			onMinuteTick(now);
		}
		else if (now.getHour() != prev.getHour()) {
			onHourTick(now);
			onMinuteTick(now);
		}
		else if (now.getMinute() != prev.getMinute()) {
			onMinuteTick(now);
		}
		prev = now;
	}
	
	private static void onMinuteTick(LocalDateTime now) {
		WorldSavedDataKubryzltcraftTimer.writeDateTime(ServerLifecycleHooks.getCurrentServer().overworld(), now);
		for (Territory territory : TerritoryManager.getManager().getTerritories()) {
			territory.improveMorale();
		}
		MinecraftForge.EVENT_BUS.post(new KubryzltcraftTickEvent.MinuteTickEvent(now, gameDay));
	}
	
	private static void onHourTick(LocalDateTime now) {
		MinecraftForge.EVENT_BUS.post(new KubryzltcraftTickEvent.HourTickEvent(now, gameDay));
	}
	
	private static void onDayTick(LocalDateTime now) {
		WorldSavedDataKubryzltcraftTimer.writeGameDay(ServerLifecycleHooks.getCurrentServer().overworld(), gameDay);
		MinecraftForge.EVENT_BUS.post(new KubryzltcraftTickEvent.DayTickEvent(now, gameDay));
	}
}