package io.github.eisoptrophobia.kubryzltcraft.server;

import io.github.eisoptrophobia.kubryzltcraft.ConfigCommon;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.event.KubryzltcraftTickEvent;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID)
public class EventServerTick {
	
	private static LocalDateTime prev = LocalDateTime.now(ZoneId.of(ConfigCommon.TIME_ZONE.get())).minusHours(ConfigCommon.DAY_TICK_HOUR.get());
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		LocalDateTime now = LocalDateTime.now(ZoneId.of(ConfigCommon.TIME_ZONE.get())).minusHours(ConfigCommon.DAY_TICK_HOUR.get());
		if (now.getYear() != prev.getYear() || now.getDayOfYear() != now.getDayOfYear()) {
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
		MinecraftForge.EVENT_BUS.post(new KubryzltcraftTickEvent.MinuteTickEvent(now, 0));
		for (Territory territory : TerritoryManager.getManager().getTerritories()) {
			territory.improveMorale();
		}
	}
	
	private static void onHourTick(LocalDateTime now) {
		MinecraftForge.EVENT_BUS.post(new KubryzltcraftTickEvent.HourTickEvent(now, 0));
	}
	
	private static void onDayTick(LocalDateTime now) {
		MinecraftForge.EVENT_BUS.post(new KubryzltcraftTickEvent.DayTickEvent(now, 0));
	}
}