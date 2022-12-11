package io.github.eisoptrophobia.kubryzltcraft.data;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;

public interface IKubryzltcraftTeamMember {
	
	KubryzltcraftTeam getTeam();
	
	void setTeam(KubryzltcraftTeam team);
}