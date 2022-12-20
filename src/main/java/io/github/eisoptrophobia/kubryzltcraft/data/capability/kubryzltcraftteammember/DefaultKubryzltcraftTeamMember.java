package io.github.eisoptrophobia.kubryzltcraft.data.capability.kubryzltcraftteammember;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;

public class DefaultKubryzltcraftTeamMember implements IKubryzltcraftTeamMember {
	
	private KubryzltcraftTeam team;
	
	@Override
	public KubryzltcraftTeam getTeam() {
		return team == null ? KubryzltcraftTeamManager.NEUTRAL : team;
	}
	
	@Override
	public void setTeam(KubryzltcraftTeam team) {
		this.team = team;
	}
}