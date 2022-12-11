package io.github.eisoptrophobia.kubryzltcraft.warfare.team;

import net.minecraft.util.ResourceLocation;

public class KubryzltcraftTeam {
	
	private final String id;
	private final String prefix;
	private final ResourceLocation kubryzltTexture;
	
	public KubryzltcraftTeam(String id, String prefix, ResourceLocation kubryzltTexture) {
		this.id = id;
		this.prefix = prefix;
		this.kubryzltTexture = kubryzltTexture;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public ResourceLocation getKubryzltTexture() {
		return kubryzltTexture;
	}
}