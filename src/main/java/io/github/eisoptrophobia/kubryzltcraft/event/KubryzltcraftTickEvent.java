package io.github.eisoptrophobia.kubryzltcraft.event;

import net.minecraftforge.eventbus.api.Event;

import java.time.LocalDateTime;

public class KubryzltcraftTickEvent extends Event {
	
	private final LocalDateTime localDateTime;
	private final int gameDay;
	
	public KubryzltcraftTickEvent(LocalDateTime localDateTime, int gameDay) {
		this.localDateTime = localDateTime;
		this.gameDay = gameDay;
	}
	
	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}
	
	public int getGameDay() {
		return gameDay;
	}
	
	public static class MinuteTickEvent extends KubryzltcraftTickEvent {
		
		public MinuteTickEvent(LocalDateTime localDateTime, int gameDay) {
			super(localDateTime, gameDay);
		}
	}
	
	public static class HourTickEvent extends KubryzltcraftTickEvent {
		
		public HourTickEvent(LocalDateTime localDateTime, int gameDay) {
			super(localDateTime, gameDay);
		}
	}
	
	public static class DayTickEvent extends KubryzltcraftTickEvent {
		
		public DayTickEvent(LocalDateTime localDateTime, int gameDay) {
			super(localDateTime, gameDay);
		}
	}
}