package io.github.eisoptrophobia.kubryzltcraft.warfare;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;

public class Territory {
	
	private final String id;
	private KubryzltcraftTeam team;
	
	public Territory(String id, KubryzltcraftTeam team) {
		this.id = id;
		this.team = team;
	}
	
	public String getId() {
		return id;
	}
	
	public KubryzltcraftTeam getTeam() {
		return team;
	}
	
	public void setTeam(KubryzltcraftTeam team) {
		this.team = team;
	}
}